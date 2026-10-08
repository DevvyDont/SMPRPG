package xyz.devvydont.smprpg.entity.base;

import io.papermc.paper.registry.keys.SoundEventKeys;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.components.DamageTracker;
import xyz.devvydont.smprpg.entity.interfaces.IDamageTrackable;
import xyz.devvydont.smprpg.entity.slayer.SlayerBossInstance;
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent;
import xyz.devvydont.smprpg.events.CustomItemDropRollEvent;
import xyz.devvydont.smprpg.services.AttributeService;
import xyz.devvydont.smprpg.services.ChatService;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils;
import xyz.devvydont.smprpg.util.scoreboard.BossSidebar;

import java.time.Duration;
import java.util.*;

public abstract class BossInstance<T extends LivingEntity> extends LeveledEntity<T> implements IDamageTrackable, Listener {

    private final DamageTracker _tracker = new DamageTracker();

    private record PlayerDamageEntry (Player player, int damage){}

    public static long INFINITE_TIME_LIMIT = Long.MAX_VALUE;

    // The bossbar attached to this boss to update whenever possible. It is possible this will be null.
    @Nullable protected BossBar bossBar = null;

    // The packet-driven sidebar shown to involved players. Players stay on the main scoreboard at all times.
    private BossSidebar scoreboard;

    // The task that is responsible for the AI/decisions that the entity makes based on certain conditions and
    // scoreboard updates.
    BukkitTask entityBrainTask = null;

    /*
     * The System.currentTimeMillis() timestamp that this boss should "wipe" at. A wipe will simply get rid of the boss
     * and kill all involved players with no loot rewards. If the timestamp is 0, it is either not set or we shouldn't
     * have this behavior.
     */
    private long wipeTimestamp = 0;

    /*
     * A collection of players who are ACTIVELY participating. Walking away and/or dying will remove them from this.
     * This is used to know who we should "wipe" in the event time runs out.
     */
    private final Map<UUID, Player> activelyInvolvedPlayers = new HashMap<>();

    /**
     * All bosses can be spawned by players in some way. This map will represent the contributions towards spawning,
     * from 0-1. If a player solely spawns a boss, they have their odds doubled. Helping spawn a boss also contributes
     * but not as much.
     */
    private final Map<UUID, Double> spawnContribution = new HashMap<>();

    /**
     * The escalating stages a timed boss moves through once its timer expires. Rather than instantly wiping the
     * party, the boss first enrages (a stacking strength buff) and then dooms (an unhealable, escalating max-HP
     * drain on everyone still fighting). The party is only fully removed once every member is dead or gone.
     */
    public enum WipePhase {
        ACTIVE,
        ENRAGED,
        DOOMED
    }

    // How long the enrage stage lasts before doom sets in.
    private static final long ENRAGE_DURATION_MS = 2 * 60 * 1000L;
    // How often the boss gains another strength stack during enrage/doom.
    private static final long ENRAGE_STACK_INTERVAL_MS = 15 * 1000L;
    // Additive damage gained per stack. +0.5 == +50% of the boss's base damage per stack.
    private static final double ENRAGE_STRENGTH_PER_STACK = 0.5;

    // How long doom takes to drain a freshly-doomed player from full health down to the floor.
    private static final int DOOM_DRAIN_SECONDS = 50;
    // The max HP a doomed player is drained down to (and held at). At 1, any hit from anything kills them, so a
    // wipe becomes purely about getting hit rather than the drain finishing the job itself.
    private static final double DOOM_MIN_HP = 1.0;

    private static final NamespacedKey ENRAGE_STRENGTH_KEY = new NamespacedKey("smprpg", "boss_enrage_strength");
    private static final NamespacedKey DOOM_DRAIN_KEY = new NamespacedKey("smprpg", "boss_doom_drain");

    private WipePhase wipePhase = WipePhase.ACTIVE;
    // Timestamp the boss became enraged. Drives both the enrage countdown and the (continuous) strength stacking,
    // which keeps climbing through doom.
    private long enrageStartedAt = 0;
    private int enrageStacks = 0;
    // Per-player doom bookkeeping. A present key means the player is being drained; the value is the max HP they
    // had when doom first touched them, which anchors a deterministic drain rate.
    private final Map<UUID, Double> doomBaseHp = new HashMap<>();
    private final Map<UUID, Double> doomDrained = new HashMap<>();


    public BossInstance(Entity entity) {
        super(entity);
    }

    public BossInstance(T entity) {
        super(entity);
    }

    /**
     * Broadcasts the [PLAYER] has summoned x! chat message when we spawn in.
     * Dynamically handles complex spawning scenarios with multiple players as well.
     * @param players The players who summoned the boss.
     */
    public void broadcastSpawnedByPlayers(List<Player> players) {

        // First, make a prefix component. If it was one player, just put their name.
        // If it was two players, put x and y.
        // If it was more, do a comma separated list with the last player being and. for example, x, y and z.
        Component names;
        ChatService chatService = SMPRPG.getService(ChatService.class);
        if (players.size() == 1) {
            names = chatService.getPlayerDisplay(players.getFirst());
        } else if (players.size() == 2) {
            names = Component.text()
                    .append(chatService.getPlayerDisplay(players.get(0)))
                    .append(Component.text(" and "))
                    .append(chatService.getPlayerDisplay(players.get(1)))
                    .build();
        } else {
            // More than 2 players: x, y, and z
            names = Component.empty();
            for (int i = 0; i < players.size(); i++) {
                if (i > 0) {
                    if (i == players.size() - 1) {
                        names = names.append(Component.text(" and "));
                    } else {
                        names = names.append(Component.text(", "));
                    }
                }
                names = names.append(chatService.getPlayerDisplay(players.get(i)));
            }
        }

        Bukkit.broadcast(ComponentUtils.alert(ComponentUtils.merge(
                names,
                ComponentUtils.create(" summoned the "),
                getPowerComponent(),
                ComponentUtils.SPACE,
                getNameComponent(),
                ComponentUtils.create("!")
        ), NamedTextColor.DARK_PURPLE));

        if (!this._entity.getType().equals(EntityType.WITHER))
            Audience.audience(Bukkit.getOnlinePlayers()).playSound(net.kyori.adventure.sound.Sound.sound().type(SoundEventKeys.ENTITY_WITHER_SPAWN).volume(.2f).build());
    }

    /**
     * Gets the spawn contribution map. Represents player IDs to a weight on how much they contributed to spawn.
     * The intended use is to have values from 0-1, representing 0-100% luck boost, but if you want spawning to be
     * more impactful you can go beyond 1.
     * @return The spawn contribution map.
     */
    public Map<UUID, Double> getSpawnContribution() {
        return spawnContribution;
    }

    /**
     * Add spawn contribution weight for a player. Adds to their already present contribution weight.
     * @param player The player to add.
     * @param contribution The amount of weight to add.
     */
    public void addSpawnContribution(Player player, double contribution) {
        addSpawnContribution(player.getUniqueId(), contribution);
    }

    /**
     * Add spawn contribution weight for a player. Adds to their already present contribution weight.
     * @param playerId The player to add.
     * @param contribution The amount of weight to add.
     */
    public void addSpawnContribution(UUID playerId, double contribution) {
        var val = spawnContribution.getOrDefault(playerId, 0.0);
        spawnContribution.put(playerId, val + contribution);
    }

    @Override
    public DamageTracker getDamageTracker() {
        return _tracker;
    }

    /*
     * Default time in seconds for how long this boss has to be defeated. Use INFINITE_TIME_LIMIT to have an infinite one.
     */
    public long getTimeLimit() {
        return INFINITE_TIME_LIMIT;
    }

    /*
     * A time limit does not start when the boss spawns. It starts when damage is first applied or when this method is
     * called.
     */
    public void startDefaultTimeLimit() {

        // If we have an unlimited time limit, set the timestamp to be never ending
        if (getTimeLimit() == INFINITE_TIME_LIMIT) {
            wipeTimestamp = INFINITE_TIME_LIMIT;
            return;
        }

        // If we do have a time limit already set, also don't do anything (this may change)
        if (wipeTimestamp != 0)
            return;

        // We do have a time limit, set the timestamp
        _entity.setRemoveWhenFarAway(false);
        _entity.setPersistent(false);
        wipeTimestamp = System.currentTimeMillis() + (getTimeLimit() * 1000L);
    }

    /*
     * Call from elsewhere to manually add a time limit to a boss.
     */
    public void setManualTimeLimit(int seconds) {
        wipeTimestamp = System.currentTimeMillis() + (seconds * 1000L);
    }

    public int getSecondsLeft() {
        return (int) (wipeTimestamp - System.currentTimeMillis()) / 1000;
    }

    /*
     * Creates a boss bar that should be used for this boss. When implementing this method, null can be returned if
     * a custom boss bar is not needed. Ender Dragons and Withers by default already have boss bars in vanilla, so
     * these entities can have null boss bars.
     */
    @Nullable
    public abstract BossBar createBossBar();

    /*
     * Given how much time is left on the boss and the current server tick, considers playing a heartbeat.
     */
    public void considerHeartbeat() {

        // Too much time?
        int secondsLeft = getSecondsLeft();
        if (secondsLeft > 30)
            return;

        // Bad tick? only do even ticks if this is second 30 or less, then every tick in the final moments
        boolean isSecondTick = Bukkit.getServer().getCurrentTick() % 20 == 0;
        boolean isEvenTick = secondsLeft % 2 == 0;
        if (!isSecondTick)
            return;
        if (secondsLeft > 15 && !isEvenTick)
            return;
        if (secondsLeft < 0)
            return;

        // Play it
        for (Player player : getActivelyInvolvedPlayers())
            player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1.5f, .7f);
    }

    /*
     * Method that runs every tick called by the entityBrainTask BukkitTask. Can be extended to add more functionality
     * beyond just simply updating the scoreboard.
     */
    public void tick() {

        if (!_entity.isValid()) {
            cleanupBrainTickTask();
            return;
        }

        // If the boss bar is defined, update the progress and the name display
        if (bossBar != null) {
            Component name = _entity.customName();
            if (name != null)
                bossBar.name(name);
            bossBar.progress((float) Math.clamp(getHealthPercentage(), 0f, 1f));
        }

        // We only need to update the scoreboard a couple times a second unless we are running low on time.
        int updateFreq = getSecondsLeft() > 60 ? 20 : 2;
        if (Bukkit.getCurrentTick() % updateFreq == 0)
            updateScoreboard();

        considerHeartbeat();

        // Drive the escalating wipe mechanics (timer -> enrage -> doom) instead of an instant party kill.
        tickWipeMechanics();
    }

    // =====================================================================================================
    //  Wipe escalation: enrage -> doom
    // =====================================================================================================

    /**
     * @return The current escalation stage of this boss's wipe timer.
     */
    public WipePhase getWipePhase() {
        return wipePhase;
    }

    /**
     * @return The boss's current outgoing damage multiplier from enrage stacks (1.0 == no buff).
     */
    public double getDamageMultiplier() {
        return 1.0 + ENRAGE_STRENGTH_PER_STACK * enrageStacks;
    }

    /**
     * @return Whether this boss has a real, finite wipe deadline (as opposed to being unlimited).
     */
    private boolean hasWipeDeadline() {
        return wipeTimestamp != 0 && wipeTimestamp != INFINITE_TIME_LIMIT;
    }

    /**
     * Advances the wipe escalation each brain tick. While ACTIVE, watches for the deadline and kicks off enrage.
     * While ENRAGED/DOOMED, keeps the strength stacks climbing, transitions to doom, and drains doomed players.
     * If the party has emptied out at any point past the deadline, the boss simply despawns.
     */
    private void tickWipeMechanics() {

        long now = System.currentTimeMillis();

        if (wipePhase == WipePhase.ACTIVE) {
            if (!hasWipeDeadline() || now < wipeTimestamp)
                return;
            // Deadline reached. With nobody left to punish, just clean up the boss.
            if (getActivelyInvolvedPlayers().isEmpty()) {
                wipe();
                return;
            }
            enterEnraged();
            return;
        }

        // ENRAGED or DOOMED: if everyone has died or fled, the fight is over.
        if (getActivelyInvolvedPlayers().isEmpty()) {
            wipe();
            return;
        }

        updateEnrageStacks(now);

        if (wipePhase == WipePhase.ENRAGED && now - enrageStartedAt >= ENRAGE_DURATION_MS)
            enterDoomed();

        if (wipePhase == WipePhase.DOOMED)
            tickDoomDrain();
    }

    /**
     * Transitions the boss into the enrage stage: a 2-minute window where it gains a stacking strength buff.
     */
    private void enterEnraged() {
        wipePhase = WipePhase.ENRAGED;
        enrageStartedAt = System.currentTimeMillis();
        enrageStacks = 0;
        applyEnrageStrength();
        announcePhase(
                ComponentUtils.create("ENRAGED!", NamedTextColor.RED, TextDecoration.BOLD),
                ComponentUtils.create("It grows stronger with every passing moment...", NamedTextColor.GOLD),
                Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f
        );
    }

    /**
     * Transitions the boss into the doom stage: strength keeps climbing and everyone still fighting begins to lose
     * max HP until they are wiped. There is no escaping it.
     */
    private void enterDoomed() {
        wipePhase = WipePhase.DOOMED;
        announcePhase(
                ComponentUtils.create("DOOMED!", NamedTextColor.DARK_RED, TextDecoration.BOLD),
                ComponentUtils.create("Your life is being torn away. There is no escape.", NamedTextColor.RED),
                Sound.ENTITY_WITHER_SPAWN, 0.7f
        );
    }

    /**
     * Recomputes how many strength stacks the boss should have based on how long it has been enraged, applying the
     * buff and firing audible feedback whenever a new stack lands.
     */
    private void updateEnrageStacks(long now) {
        int target = (int) ((now - enrageStartedAt) / ENRAGE_STACK_INTERVAL_MS);
        if (target <= enrageStacks)
            return;
        enrageStacks = target;
        applyEnrageStrength();
        onEnrageStackIncreased();
    }

    /**
     * Applies the current enrage stacks to the boss as a single transient strength modifier. Using ADD_SCALAR
     * means the boss deals base * (1 + 0.5 * stacks), composing cleanly with the base value set elsewhere.
     */
    private void applyEnrageStrength() {
        if (!(_entity instanceof LivingEntity living))
            return;
        AttributeInstance attack = living.getAttribute(Attribute.ATTACK_DAMAGE);
        if (attack == null)
            return;
        attack.removeModifier(ENRAGE_STRENGTH_KEY);
        if (enrageStacks <= 0)
            return;
        attack.addTransientModifier(new AttributeModifier(
                ENRAGE_STRENGTH_KEY,
                ENRAGE_STRENGTH_PER_STACK * enrageStacks,
                AttributeModifier.Operation.ADD_SCALAR
        ));
    }

    /**
     * Plays a rising-pitch cue to the party each time the boss gains a stack, so the escalation is audible.
     */
    private void onEnrageStackIncreased() {
        float pitch = (float) Math.min(2.0, 0.5 + enrageStacks * 0.1);
        for (Player player : getActivelyInvolvedPlayers())
            player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_OPEN_SHUTTER, 1f, pitch);
    }

    /**
     * Shows a dramatic title and plays a sound to everyone still fighting when a wipe stage begins.
     */
    private void announcePhase(Component title, Component subtitle, Sound sound, float pitch) {
        Title shown = Title.title(title, subtitle,
                Title.Times.times(Duration.ofMillis(300), Duration.ofMillis(2500), Duration.ofMillis(500)));
        for (Player player : getActivelyInvolvedPlayers()) {
            player.showTitle(shown);
            player.playSound(player.getLocation(), sound, 1.5f, pitch);
        }
    }

    /**
     * Drains a tick's worth of max HP from every player still in the fight. Iterates a snapshot because wiping a
     * player kills them, which mutates the involved-players map mid-loop.
     */
    private void tickDoomDrain() {
        for (Player player : new ArrayList<>(getActivelyInvolvedPlayers())) {
            if (!player.isOnline() || player.isDead())
                continue;
            drainDoomed(player);
        }
    }

    /**
     * Applies one tick of doom to a single player: shrinks their max HP toward {@link #DOOM_MIN_HP} at a rate that
     * reaches the floor in {@link #DOOM_DRAIN_SECONDS}, then holds them there. They are never killed by the drain
     * itself; at 1 max HP any hit finishes them, so the wipe depends entirely on getting hit.
     */
    private void drainDoomed(Player player) {

        UUID id = player.getUniqueId();
        AttributeInstance hp = player.getAttribute(Attribute.MAX_HEALTH);
        if (hp == null)
            return;

        // First doom tick for this player: capture their current max HP as the drain baseline.
        double base = doomBaseHp.computeIfAbsent(id, key -> {
            hp.removeModifier(DOOM_DRAIN_KEY);
            return hp.getValue();
        });

        // Total HP to strip is everything above the floor; pace it so the floor is reached in DOOM_DRAIN_SECONDS,
        // then clamp so the drain holds at the floor instead of continuing past it.
        double drainCeiling = Math.max(0, base - DOOM_MIN_HP);
        double drained = Math.min(drainCeiling, doomDrained.getOrDefault(id, 0.0) + drainCeiling / (DOOM_DRAIN_SECONDS * 20.0));
        doomDrained.put(id, drained);

        hp.removeModifier(DOOM_DRAIN_KEY);
        if (drained > 0)
            hp.addTransientModifier(new AttributeModifier(DOOM_DRAIN_KEY, -drained, AttributeModifier.Operation.ADD_NUMBER));

        // Keep current health pinned under the shrinking ceiling so the squeeze is felt immediately.
        double newMax = hp.getValue();
        if (player.getHealth() > newMax)
            player.setHealth(Math.max(DOOM_MIN_HP, newMax));

        refreshHealthScale(player);

        if (Bukkit.getServer().getCurrentTick() % 10 == 0)
            player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1f, 0.5f);
    }

    /**
     * Lifts doom off a single player: removes the max-HP drain modifier and forgets their bookkeeping so a future
     * fight (or a respawn) starts clean.
     */
    private void clearDoom(Player player) {
        doomBaseHp.remove(player.getUniqueId());
        doomDrained.remove(player.getUniqueId());
        AttributeInstance hp = player.getAttribute(Attribute.MAX_HEALTH);
        if (hp != null)
            hp.removeModifier(DOOM_DRAIN_KEY);
        refreshHealthScale(player);
    }

    /**
     * Re-syncs the number of hearts the client renders to the player's (possibly drained) max HP.
     */
    private void refreshHealthScale(Player player) {
        player.setHealthScale(SMPRPG.getService(EntityService.class).getPlayerInstance(player).getHealthScale());
    }

    /**
     * Resets all wipe escalation back to a clean slate: stops the strength buff on the boss and lifts doom off any
     * afflicted player. Called when the boss is (re)initialised and when it is torn down.
     */
    private void resetWipeMechanics() {
        wipePhase = WipePhase.ACTIVE;
        enrageStartedAt = 0;
        enrageStacks = 0;

        if (_entity instanceof LivingEntity living) {
            AttributeInstance attack = living.getAttribute(Attribute.ATTACK_DAMAGE);
            if (attack != null)
                attack.removeModifier(ENRAGE_STRENGTH_KEY);
        }

        for (UUID id : new ArrayList<>(doomBaseHp.keySet())) {
            Player player = Bukkit.getPlayer(id);
            if (player != null)
                clearDoom(player);
        }
        doomBaseHp.clear();
        doomDrained.clear();
    }

    /*
     * Gets all the players who are ACTIVELY participating who are currently online. Players that distance themselves
     * or die will be removed from this
     */
    public Collection<Player> getActivelyInvolvedPlayers() {
        return activelyInvolvedPlayers.values();
    }

    /*
     * Call this method to "wipe" the players involved and get rid of the boss. This is usually only called when
     * the timer expires and the players did not kill the boss in time.
     */
    public void wipe() {

        cleanupBrainTickTask();
        cleanupScoreboard();
        if (bossBar != null) {
            bossBar.removeViewer(Audience.audience(Bukkit.getOnlinePlayers()));
        }
        bossBar = null;

        // If the entity is already removed, we prob already did this.
        if (!_entity.isValid())
            return;

        // Copy the collection first: setHealth(0) fires PlayerDeathEvent synchronously, which removes the
        // player from activelyInvolvedPlayers and would otherwise mutate the map we are iterating over.
        for (Player player : new ArrayList<>(getActivelyInvolvedPlayers())) {
            // Kill everyone
            player.setHealth(0);
        }

        Bukkit.broadcast(ComponentUtils.alert(ComponentUtils.merge(
            ComponentUtils.create("The "),
            getPowerComponent(),
            ComponentUtils.SPACE,
            getNameComponent(),
            ComponentUtils.create(" has reigned victorious and wiped out those who challenged it...")
        )));

        for (Player p : Bukkit.getOnlinePlayers())
            p.playSound(p.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1, .1f);

        // Dragons cannot be .removed() otherwise the game will freak out...
        if (!(_entity instanceof EnderDragon))
            _entity.remove();
    }

    @Override
    public TextColor getNameColor() {
        return NamedTextColor.DARK_PURPLE;
    }

    @Override
    public int getInvincibilityTicks() {
        return 0;
    }

    private TextColor getPlaceColor(int rank) {
        return switch (rank) {
            case 1 -> NamedTextColor.YELLOW;
            case 2 -> NamedTextColor.WHITE;
            case 3 -> TextColor.color(0x65350F);  // Brown
            default -> NamedTextColor.GRAY;
        };
    }

    private String getPlaceth(int rank) {
        return switch (rank % 9) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }

    public List<Component> getRankingsComponents() {
        ArrayList<Component> rankings = new ArrayList<>();
        List<PlayerDamageEntry> entries = new ArrayList<>();
        for (var entry : this.getDamageTracker().getPlayerDamageTracker().entrySet())
            entries.add(new PlayerDamageEntry(entry.getKey(), entry.getValue()));
        entries.sort((o1, o2) -> {
            if (o1.damage() == o2.damage())
                return 0;
            return o1.damage() > o2.damage() ? -1 : 1;
        });
        for (int i = 0; i < entries.size(); i++) {
            PlayerDamageEntry entry = entries.get(i);
            Player player = entry.player();
            int damage = entry.damage();
            int place = i + 1;
            var name = SMPRPG.getService(ChatService.class).getPlayerDisplay(player);
            rankings.add(ComponentUtils.merge(
                ComponentUtils.create(place + getPlaceth(place) + ": ", getPlaceColor(place)),
                name,
                ComponentUtils.create(" - "),
                ComponentUtils.create(MinecraftStringUtils.formatNumber(damage), NamedTextColor.RED),
                ComponentUtils.create(String.format(" (%d%%)", (int)(damage/getMaxHp()*100)), NamedTextColor.DARK_GRAY)
            ));
        }
        return rankings;
    }

    private void updateScoreboard() {

        if (scoreboard == null) {
            cleanupScoreboard();
            return;
        }

        List<Component> lines = new ArrayList<>();

        // Add HP description, 3 lines
        lines.add(ComponentUtils.EMPTY);
        lines.add(ComponentUtils.create("Boss Health: ").append(getHealthComponent()));
        lines.add(ComponentUtils.EMPTY);

        // Add damage rankings (7 lines max!)
        List<Component> rankings = getRankingsComponents();
        lines.addAll(rankings.subList(0, Math.min(8, rankings.size())));

        // Add some information, 2 lines
        lines.add(ComponentUtils.EMPTY);
        lines.add(ComponentUtils.merge(
            ComponentUtils.create("Deal "),
            ComponentUtils.create(MinecraftStringUtils.formatNumber(getDamageRequirement()), NamedTextColor.RED),
            ComponentUtils.create(" damage for "),
            ComponentUtils.create("MAX LOOT", NamedTextColor.LIGHT_PURPLE),
            ComponentUtils.SYMBOL_EXCLAMATION
        ));

        // Append the wipe section appropriate to the current escalation stage.
        appendWipeStatusLines(lines);

        // With default settings so far, we are using 12 lines. We have space for another 3 if desired
        scoreboard.setLines(lines);
    }

    /**
     * Appends the timer / enrage / doom section of the sidebar based on the current wipe stage.
     */
    private void appendWipeStatusLines(List<Component> lines) {
        switch (wipePhase) {
            case ACTIVE -> appendTimeLeftLine(lines);
            case ENRAGED -> appendEnrageLines(lines);
            case DOOMED -> appendDoomedLines(lines);
        }
    }

    /**
     * The standard pre-deadline countdown, ramping through green/red/flashing as time runs out.
     */
    private void appendTimeLeftLine(List<Component> lines) {

        int secondsLeft = Math.max(0, getSecondsLeft());
        long msLeft = wipeTimestamp - System.currentTimeMillis() - (secondsLeft * 1000L);
        if (secondsLeft >= 1000 || secondsLeft <= 0)
            return;

        lines.add(ComponentUtils.EMPTY);

        // Color is always green by default
        NamedTextColor timeColor = NamedTextColor.GREEN;
        String timestring = String.format("%d:%02d", secondsLeft / 60, secondsLeft % 60);
        // If there is less than a minute left, we turn red.
        if (secondsLeft < 60) {
            timeColor = NamedTextColor.RED;
            timestring = String.format("%02d.%d", secondsLeft, msLeft / 100);
        }

        // If there is less than 30 seconds left even seconds are red
        if (secondsLeft < 30 && secondsLeft % 2 == 1)
            timeColor = NamedTextColor.DARK_GRAY;

        // If there is less than 15 seconds half of a tick is one color and the other half is the other
        if (secondsLeft < 10)
            timeColor = Bukkit.getServer().getCurrentTick() % 20 >= 10 ? NamedTextColor.RED : NamedTextColor.DARK_GRAY;

        lines.add(ComponentUtils.create("Time Left: ").append(ComponentUtils.create(timestring, timeColor)));
    }

    /**
     * The enrage section: a flashing ENRAGED banner, a countdown to doom, and the live damage multiplier.
     */
    private void appendEnrageLines(List<Component> lines) {
        lines.add(ComponentUtils.EMPTY);
        boolean flash = Bukkit.getServer().getCurrentTick() % 20 >= 10;
        lines.add(ComponentUtils.create("ENRAGED!!!", flash ? NamedTextColor.RED : NamedTextColor.DARK_RED, TextDecoration.BOLD));
        long remaining = Math.max(0, ENRAGE_DURATION_MS - (System.currentTimeMillis() - enrageStartedAt));
        lines.add(ComponentUtils.merge(
                ComponentUtils.create("Doom in: "),
                formatThreatTimer(remaining)
        ));
        lines.add(damageMultiplierLine());
    }

    /**
     * The doom section: a flashing DOOMED banner, flavor text, and the still-climbing damage multiplier.
     */
    private void appendDoomedLines(List<Component> lines) {
        lines.add(ComponentUtils.EMPTY);
        boolean flash = Bukkit.getServer().getCurrentTick() % 20 >= 10;
        lines.add(ComponentUtils.create("DOOMED!!!", flash ? NamedTextColor.DARK_RED : NamedTextColor.BLACK, TextDecoration.BOLD));
        lines.add(ComponentUtils.create("Your soul is being drained...", NamedTextColor.DARK_GRAY));
        lines.add(damageMultiplierLine());
    }

    /**
     * Renders the boss's current outgoing damage multiplier, e.g. "Damage: 5.0x".
     */
    private Component damageMultiplierLine() {
        return ComponentUtils.merge(
                ComponentUtils.create("Damage: "),
                ComponentUtils.create(String.format("%.1fx", getDamageMultiplier()), NamedTextColor.RED, TextDecoration.BOLD)
        );
    }

    /**
     * Formats a threatening countdown: m:ss normally, ss.d in the final minute, flashing in the final seconds.
     */
    private Component formatThreatTimer(long ms) {
        int secondsLeft = (int) (ms / 1000);
        long msLeft = ms - secondsLeft * 1000L;
        NamedTextColor color = NamedTextColor.RED;
        String text = String.format("%d:%02d", secondsLeft / 60, secondsLeft % 60);
        if (secondsLeft < 60)
            text = String.format("%02d.%d", secondsLeft, msLeft / 100);
        if (secondsLeft < 10)
            color = Bukkit.getServer().getCurrentTick() % 20 >= 10 ? NamedTextColor.RED : NamedTextColor.DARK_GRAY;
        return ComponentUtils.create(text, color, TextDecoration.BOLD);
    }

    @Override
    public void updateAttributes() {
        updateBaseAttribute(AttributeWrapper.STRENGTH, this._config.getBaseDamage());
        updateBaseAttribute(AttributeWrapper.HEALTH, this._config.getBaseHealth());
        updateBaseAttribute(AttributeWrapper.ARMOR, 0);
    }

    @Override
    public double getHalfHeartValue() {
        return Math.max(2, getMaxHp() / 300.0);
    }

    @Override
    public double getDamageRatioRequirement() {
        return .20;
    }

    @Override
    public int getMinecraftExperienceDropped() {
        // return super.getMinecraftExperienceDropped() * 18;
        return 0;
    }

    @Override
    public double getSkillExperienceMultiplier() {
        return 40;
    }

    @Override
    public void setup() {
        super.setup();
        cleanupBrainTickTask();
        cleanupScoreboard();
        resetWipeMechanics();
        scoreboard = new BossSidebar(getPowerComponent().append(ComponentUtils.SPACE).append(getNameComponent()));
        heal();
        bossBar = createBossBar();
        cleanupBrainTickTask();
        entityBrainTask = Bukkit.getScheduler().runTaskTimer(_plugin, this::tick, 1, 1);
        _entity.setPersistent(true);
    }

    @Override
    public void cleanup() {
        super.cleanup();

        cleanupBrainTickTask();
        cleanupScoreboard();
        resetWipeMechanics();
        if (bossBar != null)
            for (Player p : Bukkit.getOnlinePlayers())
                bossBar.removeViewer(p);
        bossBar = null;
    }

    private void cleanupScoreboard() {
        if (scoreboard == null)
            return;
        scoreboard.cleanup();
        scoreboard = null;
    }

    private void cleanupBrainTickTask() {
        if (entityBrainTask != null) {
            entityBrainTask.cancel();
            entityBrainTask = null;
        }
    }

    /*
     * When this boss dies, announce it to chat
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBossDeath(EntityDeathEvent event) {

        if (!_entity.equals(event.getEntity()))
            return;

        cleanupBrainTickTask();

        if (this.getDamageTracker().getPlayerDamageTracker().isEmpty() || event.getEntity().getKiller() == null)
            return;

        Player player = event.getEntity().getKiller();

        // We died!!!
        Bukkit.broadcast(ComponentUtils.create("-----------------------------"));
        Bukkit.broadcast(getPowerComponent().append(ComponentUtils.SPACE).append(getNameComponent()).append(ComponentUtils.create(" Defeated!")));
        Bukkit.broadcast(ComponentUtils.EMPTY);
        var winnerChatInfo = SMPRPG.getService(ChatService.class).getPlayerInfo(player);
        Bukkit.broadcast(ComponentUtils.create(winnerChatInfo.prefix(), NamedTextColor.WHITE).append(ComponentUtils.create(player.getName(), winnerChatInfo.nameColor())).append(ComponentUtils.create(" dealt the final blow!")));
        Bukkit.broadcast(ComponentUtils.EMPTY);
        for (Component component : getRankingsComponents())
            Bukkit.broadcast(component);
        Bukkit.broadcast(ComponentUtils.create("-----------------------------"));

        for (Player p : Bukkit.getOnlinePlayers())
            p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, .2f, 1);
    }

    /*
     * When a player damages this entity, set them as an involved player and set their scoreboard.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {

        if (!_entity.equals(event.getEntity()))
            return;

        if (!(event.getDamageSource().getCausingEntity() instanceof Player player))
            return;

        if (scoreboard == null)
            return;

        activelyInvolvedPlayers.put(player.getUniqueId(), player);
        startDefaultTimeLimit();
        scoreboard.display(player);
        if (bossBar != null)
            bossBar.addViewer(player);
    }

    /*
     * When a player dies, remove them from this scoreboard and the involved players of this entity.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getPlayer();

        // Always drop a dying player as a viewer, even one who only ever got near the boss and never dealt
        // damage. Such proximity viewers are not in activelyInvolvedPlayers, so the involved-player check
        // below would skip them and leak them as a sidebar viewer: the client tears the objective down on
        // respawn while the server still believes it is shown. That desyncs the re-show guards (the sidebar
        // never reappears) and races a stale refresh into a protocol-error disconnect.
        if (bossBar != null)
            bossBar.removeViewer(player);
        if (scoreboard != null && scoreboard.showing(player))
            scoreboard.hide(player);

        // However they died, lift any doom drain so they don't respawn with a shrunken health bar.
        clearDoom(player);

        var removed = activelyInvolvedPlayers.remove(player.getUniqueId());
        if (removed == null)
            return;

        // If this is the last player that was involved, we need to wipe.
        if (activelyInvolvedPlayers.isEmpty())
            wipe();
    }

    /*
     * Respawning resets the client's scoreboard state, which would drop our packet-driven sidebar. Re-send it
     * to any player who is still being shown this boss's sidebar.
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (scoreboard != null && scoreboard.showing(event.getPlayer()))
            scoreboard.refresh(event.getPlayer());
    }

    /*
     * If a player has moved 200 blocks away from this entity, remove them from the involved players.
     */
    @EventHandler
    public void onPlayerDistanced(PlayerMoveEvent event) {

        // Doom is inescapable: once the fight is doomed, players cannot drop out of it by walking away. Leaving
        // the involved set here would also let the boss despawn out from under players who are still being drained.
        if (wipePhase == WipePhase.DOOMED)
            return;

        if (!event.getPlayer().getWorld().equals(_entity.getWorld()))
            return;

        if (!event.hasChangedBlock())
            return;

        if (!_entity.isDead() && event.getPlayer().getLocation().distance(_entity.getLocation()) < 200) {
            if (bossBar != null)
                bossBar.addViewer(event.getPlayer());
            if (scoreboard != null && !scoreboard.showing(event.getPlayer()))
                scoreboard.display(event.getPlayer());
            return;
        }

        if (bossBar != null)
            bossBar.removeViewer(event.getPlayer());

        activelyInvolvedPlayers.remove(event.getPlayer().getUniqueId());

        if (scoreboard == null)
            return;

        if (!scoreboard.showing(event.getPlayer()))
            return;

        scoreboard.hide(event.getPlayer());
    }

    /*
     * If a player quits, remove them from the involved players.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        activelyInvolvedPlayers.remove(event.getPlayer().getUniqueId());
        if (bossBar != null)
            bossBar.removeViewer(event.getPlayer());
        // Drop them as a sidebar viewer so we don't try to send packets to an offline player.
        if (scoreboard != null && scoreboard.showing(event.getPlayer()))
            scoreboard.hide(event.getPlayer());
        // Clear any doom drain so a logged-off player isn't left with a modified health bar.
        clearDoom(event.getPlayer());
    }

    /**
     * Never let a player take PVP damage or deal PVP damage if they are in the fight.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPVPDuringBoss(CustomEntityDamageByEntityEvent event) {

        // Only listen to PVP.
        if (!(event.dealer instanceof Player dealer))
            return;

        if (!(event.damaged instanceof Player damaged))
            return;

        // We know this is PVP. If either character is participating in this fight, cancel.
        if (activelyInvolvedPlayers.containsKey(dealer.getUniqueId()) || activelyInvolvedPlayers.containsKey(damaged.getUniqueId()))
            event.setCancelled(true);
    }

    /**
     * When we take damage, limit the max damage we can take so high level players can't obliterate us.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void __onReceiveDamage(EntityDamageByEntityEvent event) {

        if (!event.getEntity().equals(_entity))
            return;

        // Slayer bosses don't have damage caps.
        if (this instanceof SlayerBossInstance<?>)
            return;

        // Only living entities have damage caps.
        if (!(event.getDamager() instanceof LivingEntity damager))
            return;

        // We want a DPS cap, not just a raw damage cap. Use default attack speed of 4.
        var attackSpeed = 4.0;
        var attackSpeedAttribute = AttributeService.getInstance().getAttribute(damager, AttributeWrapper.ATTACK_SPEED);
        if (attackSpeedAttribute != null)
            attackSpeed = attackSpeedAttribute.getValue();

        // Clamp attack speed so nothing crazy happens...
        attackSpeed = Math.max(.5, Math.min(16, attackSpeed));

        var capMultiplier = 4.0 / attackSpeed;

        var cap = this.getHalfHeartValue() * capMultiplier;
        var dmg = event.getFinalDamage();
        if (dmg <= cap)
            return;

        // We need to tone it back...
        var curve = 0.5;
        var reduced = cap + Math.pow(dmg-cap, curve);
        double scale = reduced / dmg;
        event.setDamage(dmg * scale);
    }

    /**
     * When we roll for drops, check if the player involved has a spawn contribution boost.
     */
    @EventHandler
    public void __onRollForDrops(CustomItemDropRollEvent event) {

        if (!event.source.equals(this))
            return;

        // This loot roll involves us! Check if the player rolling is present.
        var contributionRating = getSpawnContribution().get(event.player.getUniqueId());
        if (contributionRating == null)
            return;

        // This player contributed to our spawn. Let's boost the chance. By default, we say that for every .01
        // contribution rating, they get a 1% boost.
        var newChance = event.chance * (contributionRating+1);
        event.chance = newChance;
    }
}
