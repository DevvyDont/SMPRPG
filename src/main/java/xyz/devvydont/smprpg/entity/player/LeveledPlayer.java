package xyz.devvydont.smprpg.entity.player;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.EntityGlobals;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.LeveledEntity;
import xyz.devvydont.smprpg.entity.components.EntityConfiguration;
import xyz.devvydont.smprpg.entity.player.settings.PlayerSettings;
import xyz.devvydont.smprpg.events.skills.SkillExperiencePostGainEvent;
import xyz.devvydont.smprpg.items.interfaces.IAttributeItem;
import xyz.devvydont.smprpg.services.*;
import xyz.devvydont.smprpg.skills.SkillInstance;
import xyz.devvydont.smprpg.skills.SkillType;
import xyz.devvydont.smprpg.util.persistence.KeyStore;
import xyz.devvydont.smprpg.util.persistence.PDCAdapters;
import xyz.devvydont.smprpg.util.attributes.AttributeUtil;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils;
import xyz.devvydont.smprpg.util.formatting.Symbols;

import java.util.Collection;
import java.util.List;

public class LeveledPlayer extends LeveledEntity<Player> implements Listener {

    public static int MANA_REGENERATE_FREQUENCY = 10;

    // Used as a shortcut for skill modification
    private final SkillInstance combatSkill;
    private final SkillInstance miningSkill;
    private final SkillInstance fishingSkill;
    private final SkillInstance farmingSkill;
    private final SkillInstance woodcuttingSkill;
    private final SkillInstance magicSkill;
    private final SkillInstance slayerSkill;

    private BukkitTask _manaRegenerateTask;
    private double _mana = 0;

    // The player's personal HUD preferences. Cached here so it can be read cheaply, including from the
    // asynchronous action bar task. Loaded from the player's PDC on setup and re-saved when changed via the menu.
    private PlayerSettings _settings = new PlayerSettings();

    // The last values we wrote to the (purely visual) experience bar, so updateExperienceBar() can be called
    // freely on a fast cadence without re-sending identical packets to the client.
    private int _lastExpBarLevel = Integer.MIN_VALUE;
    private float _lastExpBarProgress = Float.NaN;

    // The skill this player most recently gained experience in, used by the "last skill" experience bar modes.
    // Null until they gain any skill experience this session, in which case those modes fall back to the average.
    @Nullable
    private SkillInstance _lastGainedSkill = null;

    // The player's attack-strength charge (0.0-1.0), polled once per tick. Recent Paper builds reset
    // the live attack-strength ticker before the damage event fires, so reading it there yields ~0.
    // Polling lets us recover the charge the player actually had when they swung. We keep the last two
    // ticks so the damage/crit logic can read the value from just before the attack reset the ticker.
    private BukkitTask _attackChargePollTask;
    private float _currentTickAttackCharge = 1f;
    private float _previousTickAttackCharge = 1f;

    // How often (in ticks) we refresh the below-name HP display, and how many segments its bar has.
    private static final int BELOW_NAME_UPDATE_FREQUENCY = 2;
    private static final int HEALTH_BAR_SEGMENTS = 10;
    private BukkitTask _belowNameHealthTask;
    // Last values we rendered to the below-name display, so we can skip redundant scoreboard writes.
    private int _lastBelowNameHp = Integer.MIN_VALUE;
    private int _lastBelowNameMaxHp = Integer.MIN_VALUE;
    private int _lastBelowNameAbsorption = Integer.MIN_VALUE;

    public LeveledPlayer(SMPRPG plugin, Player entity) {
        super(entity);

        var skillService = SMPRPG.getService(SkillService.class);
        // Skill shortcuts
        this.combatSkill = skillService.getNewSkillInstance(entity, SkillType.COMBAT);
        this.miningSkill = skillService.getNewSkillInstance(entity, SkillType.MINING);
        this.fishingSkill = skillService.getNewSkillInstance(entity, SkillType.FISHING);
        this.farmingSkill = skillService.getNewSkillInstance(entity, SkillType.FARMING);
        this.woodcuttingSkill = skillService.getNewSkillInstance(entity, SkillType.WOODCUTTING);
        this.magicSkill = skillService.getNewSkillInstance(entity, SkillType.MAGIC);
        this.slayerSkill = skillService.getNewSkillInstance(entity, SkillType.SLAYER);

        this._config = EntityConfiguration.PLAYER;
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.HUMANOID);

        super.setup();

        // Load the player's saved HUD preferences before anything that reads them (tasks, exp bar) starts.
        _settings = _entity.getPersistentDataContainer()
                .getOrDefault(KeyStore.PLAYER_SETTINGS, PDCAdapters.PLAYER_SETTINGS, new PlayerSettings());

        startManaTask();
        startAttackChargePollTask();
        startBelowNameHealthTask();

        // Initialize the (purely visual) experience bar to reflect the player's settings.
        updateExperienceBar();

        // Vanilla food is disabled (see __onHungerChange), so crank up the regen rates to avoid the vanilla
        // hunger system ever interfering with our custom healing.
        _entity.setSaturatedRegenRate(160);
        _entity.setUnsaturatedRegenRate(160);
    }

    /**
     * The player's personal HUD preferences. Mutating the returned instance and then calling
     * {@link PlayerSettings#save(org.bukkit.persistence.PersistentDataHolder)} persists the change.
     * @return The cached settings instance for this player.
     */
    public PlayerSettings getSettings() {
        return _settings;
    }

    /**
     * Re-renders the vanilla experience bar to reflect this player's current settings. The bar is purely a
     * visual on this server, so this is the single authority that writes to it (all vanilla experience gains
     * are suppressed in {@link #__onVanillaExperienceGain}). The number and the bar fill are configured
     * independently, so this resolves each from settings. Redundant writes are skipped, so this is safe to
     * call on a fast cadence.
     */
    public void updateExperienceBar() {
        int level = computeExperienceBarNumber();
        float progress = computeExperienceBarProgress();

        if (level == _lastExpBarLevel && Float.compare(progress, _lastExpBarProgress) == 0)
            return;
        _lastExpBarLevel = level;
        _lastExpBarProgress = progress;

        _entity.setLevel(level);
        _entity.setExp(progress);
    }

    /**
     * Resolves the number to show on the experience bar from this player's settings.
     * @return The level number to display.
     */
    private int computeExperienceBarNumber() {
        return switch (_settings.getExperienceBarNumber()) {
            case MANA_PERCENT -> {
                double max = getMaxMana();
                yield max <= 0 ? 0 : (int) Math.round(Math.clamp(_mana / max, 0, 1) * 100);
            }
            case MANA -> (int) Math.round(_mana);
            case POWER_RATING -> getLevel();
            case SKILL_AVERAGE -> (int) getAverageSkillLevel();
            // Fall back to the average until the player has gained skill experience this session.
            case LAST_SKILL -> _lastGainedSkill != null ? _lastGainedSkill.getLevel() : (int) getAverageSkillLevel();
            case HIDDEN -> 0;  // Vanilla draws no number when the level is 0.
        };
    }

    /**
     * Resolves the fill (0.0-1.0) of the experience bar from this player's settings.
     * @return The bar progress to display.
     */
    private float computeExperienceBarProgress() {
        return switch (_settings.getExperienceBarFill()) {
            case MANA -> {
                double max = getMaxMana();
                yield max <= 0 ? 0f : (float) Math.clamp(_mana / max, 0, 1);
            }
            case SKILL_AVERAGE -> skillAverageProgress();
            // Fall back to the average progress until the player has gained skill experience this session.
            case LAST_SKILL -> _lastGainedSkill != null ? skillProgressFraction(_lastGainedSkill) : skillAverageProgress();
            case HIDDEN -> 0f;
        };
    }

    /**
     * The fractional progress (0.0-1.0) toward the player's next average skill level. Creeps up as skill
     * experience is earned.
     * @return The average progress fraction.
     */
    private float skillAverageProgress() {
        double average = getAverageSkillLevel();
        return (float) (average - Math.floor(average));
    }

    /**
     * The fractional progress (0.0-1.0) through a single skill's current level.
     * @param skill The skill to measure.
     * @return The progress fraction, or full when the skill is maxed.
     */
    private float skillProgressFraction(SkillInstance skill) {
        int into = skill.getExperienceProgress();
        int band = into + skill.getExperienceForNextLevel();
        if (band <= 0)
            return 1f;  // Max level (or no further progression) — render the bar as full.
        return (float) Math.clamp((double) into / band, 0, 1);
    }

    public void regenerateMana() {
        var max = getMaxMana();
        this._mana += getMaxMana() / 100;
        this._mana = Math.min(Math.max(0, _mana), max);

        // Refresh the experience bar on this periodic tick. It de-dupes redundant writes internally, so this
        // cheaply keeps mana-driven, power-rating (gear changes), and skill displays current without spamming
        // packets when nothing has actually changed.
        updateExperienceBar();
    }

    public double getMana() {
        return _mana;
    }

    /**
     * The player's attack-strength charge (0.0-1.0) from just before their most recent attack reset
     * the attack-strength ticker. Use this instead of the live {@link Player#getAttackCooldown()}
     * during damage events, where recent Paper builds have already reset the ticker to ~0.
     * Returns the higher of the last two polled values so the pre-attack charge survives regardless
     * of whether the per-tick poll ran before or after the attack within the tick.
     */
    public float getLastMeleeAttackCharge() {
        return Math.max(_currentTickAttackCharge, _previousTickAttackCharge);
    }

    private void pollAttackCharge() {
        _previousTickAttackCharge = _currentTickAttackCharge;
        _currentTickAttackCharge = getPlayer().getAttackCooldown();
    }

    public double getMaxMana() {
        var mana = SMPRPG.getService(AttributeService.class).getOrCreateAttribute(_entity, AttributeWrapper.INTELLIGENCE);
        return mana.getValue();
    }

    public void useMana(int cost) {

        if (this.getPlayer().getGameMode().isInvulnerable())
            return;

        this._mana -= cost;
        this._mana = Math.max(0, this._mana);
    }

    public void gainMana(int gained) {

        if (this.getPlayer().getGameMode().isInvulnerable())
            return;

        this._mana += gained;
        this._mana = Math.min(this._mana, getMaxMana());
    }

    public ProfileDifficulty getDifficulty() {
        return SMPRPG.getService(DifficultyService.class).getDifficulty(getPlayer());
    }

    public SkillInstance getCombatSkill() {
        return combatSkill;
    }

    public SkillInstance getMiningSkill() {
        return miningSkill;
    }

    public SkillInstance getFishingSkill() {
        return fishingSkill;
    }

    public SkillInstance getFarmingSkill() {
        return farmingSkill;
    }

    public SkillInstance getWoodcuttingSkill() {
        return woodcuttingSkill;
    }

    public SkillInstance getMagicSkill() {
        return magicSkill;
    }

    public SkillInstance getSlayerSkill() {
        return slayerSkill;
    }

    public Collection<SkillInstance> getSkills() {
        return List.of(
                getCombatSkill(),
                getMiningSkill(),
                getFishingSkill(),
                getFarmingSkill(),
                getWoodcuttingSkill(),
                getMagicSkill(),
                getSlayerSkill()
        );
    }

    public double getAverageSkillLevel() {
        double sum = 0;
        for (SkillInstance skill : getSkills())
            sum += skill.getLevel();
        return sum / getSkills().size();
    }

    @Override
    public int getInvincibilityTicks() {
        return 20;
    }

    @Override
    public String getClassKey() {
        return "player";
    }

    @Override
    public EntityType getDefaultEntityType() {
        return EntityType.PLAYER;
    }

    @Override
    public String getEntityName() {
        return _entity.getName();
    }

    @Override
    public int getLevel() {

        // If we haven't fully initialized yet we cannot get a good calculation yet
        if (getCombatSkill() == null)
            return 0;

        // Using average level of gear and skills on this player determine how strong they are
        int factor = 0;
        double total = 0;

        // First skills
        for (SkillInstance skill : getSkills())
            total += skill.getLevel();
        factor += getSkills().size();

        // Now gear
        Player p = getPlayer();
        ItemStack[] gear = {p.getInventory().getHelmet(), p.getInventory().getChestplate(),
                p.getInventory().getLeggings(), p.getInventory().getBoots(), p.getInventory().getItemInMainHand(),
                p.getInventory().getItemInOffHand()};

        for (ItemStack item : gear) {

            if (item == null || item.getType().equals(Material.AIR))
                continue;

            var blueprint = SMPRPG.getService(ItemService.class).getBlueprint(item);
            if (!(blueprint instanceof IAttributeItem attributable))
                continue;

            total += attributable.getPowerRating() + AttributeUtil.getPowerBonus(item.getItemMeta());
            factor += 1;
        }

        // The factor cannot be any less than all the skills we have + 4 pieces of armor (exclude holding nothing in our hands)
        factor = Math.max(factor, getSkills().size() + 4);
        return (int) (total / factor);
    }

    /**
     * Calculates the number of half-hearts to render for the player, based on max HP.
     * Display rules:
     * - 100 HP = 1 full row = 20 half-hearts
     * - 1000 HP = 2 full rows = 40 half-hearts
     * - 2500 HP = 3 full rows = 60 half-hearts (cap)
     * The scale is calculated in tiers:
     * - Below 100 HP: 1 half-heart per 5 HP
     * - Between 100–1000 HP: 1 half-heart per 45 HP above 100 (starts from 20)
     * - Above 1000 HP: 1 half-heart per 500 HP above 1000 (starts from 40)
     * Always rounded up to the nearest even number (Minecraft only displays full hearts).
     * @return The health scale (number of half-hearts) to display to the client (min 2, max 60).
     */
    public int getHealthScale() {
        float hp = (float) getMaxHp();
        int scale;

        if (hp < 100) {
            scale = Math.round(hp / 5f); // 20 at 100 HP
        } else if (hp < 1000) {
            scale = 20 + Math.round((hp - 100) / 45f); // 40 at 1000 HP
        } else {
            scale = 40 + Math.round((hp - 1000) / 500f); // Half heart every 500HP
        }

        // Round down to nearest even number to avoid half-hearts
        if (scale % 2 != 0)
            scale--;

        return Math.clamp(scale, 2, 60);
    }

    /**
     * The current value of HP this player's half of heart is HP wise
     * This amount of HP is used a lot for damage such as fall damage, burning, and regeneration values
     * @return The amount of HP this player's half heart is currently worth.
     */
    @Override
    public double getHalfHeartValue() {
        return getMaxHp() / getHealthScale();
    }

    @Override
    public void updateAttributes() {

        // Update max health to 100 while maintaining their current HP
        double percent = getHealthPercentage();
        updateBaseAttribute(AttributeWrapper.HEALTH, this._config.getBaseHealth());

        if (percent > .01)
            setHealthPercentage(percent);

        // Set misc default base attributes that players should have
        updateBaseAttribute(AttributeWrapper.STRENGTH, this._config.getBaseDamage());
        updateBaseAttribute(AttributeWrapper.REGENERATION, getDifficulty() == ProfileDifficulty.HARD ? 50 : 100);
        updateBaseAttribute(AttributeWrapper.INTELLIGENCE, getDifficulty() == ProfileDifficulty.HARD ? 50 : 100);
        updateBaseAttribute(AttributeWrapper.LUCK, 100);
        updateBaseAttribute(AttributeWrapper.DEFENSE, 0);
        updateBaseAttribute(AttributeWrapper.CRITICAL_CHANCE, 0);
        updateBaseAttribute(AttributeWrapper.CRITICAL_DAMAGE, getDifficulty() == ProfileDifficulty.HARD ? 25 : 50);
        updateBaseAttribute(AttributeWrapper.SWEEPING, .05);

        updateBaseAttribute(AttributeWrapper.MINING_FORTUNE, 0);
        updateBaseAttribute(AttributeWrapper.FARMING_FORTUNE, 0);
        updateBaseAttribute(AttributeWrapper.WOODCUTTING_FORTUNE, 0);

        updateBaseAttribute(AttributeWrapper.FISHING_RATING, 0);
        updateBaseAttribute(AttributeWrapper.FISHING_CREATURE_CHANCE, 0);
        updateBaseAttribute(AttributeWrapper.FISHING_TREASURE_CHANCE, 0);

        updateBaseAttribute(AttributeWrapper.MINING_SPEED, 0.0);
        updateBaseAttribute(AttributeWrapper.LEGACY_MINING_SPEED, -100.0);
        updateBaseAttribute(AttributeWrapper.AIRBORNE_MINING, 0.2);



        // Make sure we aren't overloading their UI with hearts
        getPlayer().setHealthScale(getHealthScale());
        getPlayer().setHealthScaled(true);

        // Make them only start with a fraction of their mana, to prevent abusing mana restoration from re-logging.
        this._mana = getMaxMana() / 5;
    }

    private Team getNametagTeam() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Player player = getPlayer();
        String teamKey = player.getUniqueId().toString();
        Team team = scoreboard.getTeam(teamKey);
        if (team == null)
            team = scoreboard.registerNewTeam(teamKey);
        team.addPlayer(player);
        return team;
    }

    @Override
    public void updateNametag() {
        Team team = getNametagTeam();
        var chatInformation = SMPRPG.getService(ChatService.class).getPlayerInfo(getPlayer());
        Component newPrefix = ComponentUtils.powerLevelPrefix(getLevel()).append(ComponentUtils.SPACE);
        team.prefix(newPrefix);
        if (!chatInformation.prefix().isEmpty())
            team.suffix(Component.text(" " + chatInformation.prefix().stripTrailing(), NamedTextColor.WHITE));
        else
            team.suffix(null);
        team.color(NamedTextColor.nearestTo(getDifficulty().Color));
    }

    @Override
    public void cleanup() {
        super.cleanup();
        killManaTask();
        killAttackChargePollTask();
        killBelowNameHealthTask();
        clearBelowNameHealthScore();
    }

    @Override
    public void setLevel(int level) {
        // Does nothing
    }

    @Override
    public void resetLevel() {
        // Does nothing
    }

    @Override
    public void dimNametag() {
        // Does nothing
    }

    @Override
    public void brightenNametag() {
        // does nothing
    }

    @Override
    public EntityConfiguration getDefaultConfiguration() {
        var difficulty = getDifficulty();
        return EntityConfiguration.builder()
                .withLevel(0)
                .withHealth(difficulty == ProfileDifficulty.HARD ? 50 : 100)
                .withDamage(difficulty == ProfileDifficulty.HARD ? 2 : 5)
                .build();
    }

    public Player getPlayer() {
        return _entity;
    }

    @Override
    public boolean hasVanillaDrops() {
        return true;
    }

    private void killManaTask() {
        if (_manaRegenerateTask != null)
            _manaRegenerateTask.cancel();
        _manaRegenerateTask = null;
    }

    private void startManaTask() {
        killManaTask();
        _manaRegenerateTask = Bukkit.getScheduler().runTaskTimer(SMPRPG.getPlugin(), this::regenerateMana, 0, MANA_REGENERATE_FREQUENCY);
    }

    private void killAttackChargePollTask() {
        if (_attackChargePollTask != null)
            _attackChargePollTask.cancel();
        _attackChargePollTask = null;
    }

    private void startAttackChargePollTask() {
        killAttackChargePollTask();
        _attackChargePollTask = Bukkit.getScheduler().runTaskTimer(SMPRPG.getPlugin(), this::pollAttackCharge, 0, 1);
    }

    private void killBelowNameHealthTask() {
        if (_belowNameHealthTask != null)
            _belowNameHealthTask.cancel();
        _belowNameHealthTask = null;
    }

    private void startBelowNameHealthTask() {
        killBelowNameHealthTask();
        // Force the next tick to render, in case the backing objective was recreated under us.
        _lastBelowNameHp = Integer.MIN_VALUE;
        _lastBelowNameMaxHp = Integer.MIN_VALUE;
        _lastBelowNameAbsorption = Integer.MIN_VALUE;
        _belowNameHealthTask = Bukkit.getScheduler().runTaskTimer(
                SMPRPG.getPlugin(), this::updateBelowNameHealthDisplay, 0, BELOW_NAME_UPDATE_FREQUENCY);
    }

    /**
     * Returns the objective backing the below-name HP display, or {@code null} if it isn't set up yet.
     */
    @Nullable
    private Objective getBelowNameHealthObjective() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        return scoreboard.getObjective(EntityService.BELOW_NAME_HEALTH_OBJECTIVE);
    }

    /**
     * Refreshes this player's styled HP readout in the below-name scoreboard slot.
     *
     * <p>The below-name slot is global on the server, but on our Minecraft version (26.1.x) the client
     * draws it under every entity that has a custom name. To keep it player-only we render it through a
     * DUMMY objective (see {@link EntityService}) and write a score here for players exclusively; entities
     * without a score render nothing. We replace the raw score number with a fully styled component via
     * {@link NumberFormat#fixed(Component)}, which is what lets us color HP and append a bar.
     *
     * <p>Skips the scoreboard write when the rendered values are unchanged so we aren't spamming packets.
     */
    private void updateBelowNameHealthDisplay() {
        int hp = clampDisplayHealth(getTotalHp());
        int maxHp = (int) Math.ceil(getMaxHp());
        int absorption = (int) Math.ceil(getAbsorptionHealth());

        if (hp == _lastBelowNameHp && maxHp == _lastBelowNameMaxHp && absorption == _lastBelowNameAbsorption)
            return;
        _lastBelowNameHp = hp;
        _lastBelowNameMaxHp = maxHp;
        _lastBelowNameAbsorption = absorption;

        Objective objective = getBelowNameHealthObjective();
        if (objective == null)
            return;

        Score score = objective.getScore(getPlayer().getName());
        score.setScore(hp);  // The value is hidden behind the fixed format below; we set it for sanity only.
        score.numberFormat(NumberFormat.fixed(buildBelowNameHealthComponent()));
    }

    /**
     * Removes this player's below-name HP score, e.g. when they log out, so it doesn't linger.
     */
    private void clearBelowNameHealthScore() {
        Objective objective = getBelowNameHealthObjective();
        if (objective != null)
            objective.getScoreboard().resetScores(getPlayer().getName());
        _lastBelowNameHp = Integer.MIN_VALUE;
        _lastBelowNameMaxHp = Integer.MIN_VALUE;
        _lastBelowNameAbsorption = Integer.MIN_VALUE;
    }

    /**
     * Builds the styled below-name HP component, e.g. {@code 1,847/2,000 ❤ +250 ▰▰▰▰▰▰▰▰▱▱}. The current
     * HP is colored by how much is missing (reusing {@link EntityGlobals#getChatColorFromHealth}),
     * absorption is shown in gold, and the bar fills by health percentage.
     */
    private Component buildBelowNameHealthComponent() {
        double totalHp = getTotalHp();
        double maxHp = getMaxHp();
        int hp = clampDisplayHealth(totalHp);
        int maxHpInt = (int) Math.ceil(maxHp);
        TextColor hpColor = EntityGlobals.getChatColorFromHealth(totalHp, maxHp);

        Component readout = ComponentUtils.merge(
                ComponentUtils.create(MinecraftStringUtils.formatNumber(hp), hpColor),
//                ComponentUtils.create("/"),  // Uncomment for max HP number
//                ComponentUtils.create(MinecraftStringUtils.formatNumber(maxHpInt), NamedTextColor.GREEN),  // Uncomment for max HP number
                ComponentUtils.create(" " + Symbols.HEART, NamedTextColor.DARK_RED)
        );

        double absorption = getAbsorptionHealth();
        return ComponentUtils.merge(readout, ComponentUtils.create(" "), buildHealthBar(totalHp, maxHp, absorption, hpColor));
    }

    /**
     * Builds a fixed-width health bar whose filled portion reflects the current health percentage. The
     * filled segments take the same color as the HP number; empty segments are dark gray. A living player
     * always shows at least one filled segment, and the bar only reads as full when health is actually at
     * (or above) max.
     */
    private Component buildHealthBar(double hp, double maxHp, double absorption, TextColor filledColor) {
        double percent = maxHp <= 0 ? 0 : Math.clamp(hp / maxHp, 0, 1);
        int filled = (int) Math.round(percent * HEALTH_BAR_SEGMENTS);
        if (filled <= 0 && hp > 0)
            filled = 1;
        if (filled >= HEALTH_BAR_SEGMENTS && percent < 1.0)
            filled = HEALTH_BAR_SEGMENTS - 1;

        var absorptionBar = absorption > 0 ?
                ComponentUtils.create(String.format("+%d", (int)(Math.ceil(getPlayer().getAbsorptionAmount() / 2.0))), NamedTextColor.AQUA) :
                ComponentUtils.EMPTY;

        return ComponentUtils.merge(
                ComponentUtils.create(Symbols.HEALTH_BAR_FILLED.repeat(filled), filledColor),
                ComponentUtils.create(Symbols.HEALTH_BAR_EMPTY.repeat(HEALTH_BAR_SEGMENTS - filled), NamedTextColor.DARK_GRAY),
                absorptionBar
        );
    }

    /**
     * Rounds health for display: never shows 0 while the player is technically alive, and never shows a
     * fractional heart's worth of HP as 0.
     */
    private int clampDisplayHealth(double health) {
        if (health <= 0)
            return 0;
        if (health < 1)
            return 1;
        return (int) Math.ceil(health);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void __onEntityAddToWorld(EntityAddToWorldEvent event) {

        if (event.getEntity().equals(_entity))
            updateNametag();

    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void __onJoin(PlayerJoinEvent event) {

        if (!event.getPlayer().equals(getPlayer()))
            return;

        updateNametag();
        updateExperienceBar();
    }

    /**
     * Keeps the experience bar in sync the instant the player earns skill experience, so progress is
     * visualized in real time rather than only on the next periodic refresh.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void __onSkillExperienceGained(SkillExperiencePostGainEvent event) {

        if (!event.getPlayer().equals(getPlayer()))
            return;

        _lastGainedSkill = event.getSkill();
        updateExperienceBar();
    }

    /**
     * The experience bar is purely a visual that we drive ourselves, so we suppress all vanilla experience
     * gains (orbs, thrown bottles, smelting, etc.) to keep the bar under our exclusive control.
     */
    @EventHandler
    private void __onVanillaExperienceGain(PlayerExpChangeEvent event) {

        if (!event.getPlayer().equals(getPlayer()))
            return;

        event.setAmount(0);
    }

    /**
     * Prevents hunger from depleting, essentially disabling vanilla food.
     */
    @EventHandler
    private void __onHungerChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);  // Cancel Vanilla event

        if (event.getEntity() == getPlayer()) {
            var item = event.getItem();
            if (item != null) {
                var foodComp = item.getData(DataComponentTypes.FOOD);
                if (foodComp != null) {
                    float foodRestoreAmt = foodComp.nutrition();
                    @Nullable AttributeInstance maxHp = getPlayer().getAttribute(Attribute.MAX_HEALTH);
                    if (maxHp != null)  // Sanity, should never be the case though.
                        getPlayer().heal((foodRestoreAmt / 100.0f) * maxHp.getValue());

                    if (foodComp.saturation() > 0) {
                        var max = getMaxMana();
                        this._mana += foodComp.saturation();
                        this._mana = Math.min(Math.max(0, _mana), max);
                    }
                }
            }

            event.getEntity().setFoodLevel(20);  // Force food level to be maxed out at all times.
            event.getEntity().setSaturation(20);
        }
    }

    public void refillMana() {
        _mana = getMaxMana();
    }
}
