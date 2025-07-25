package xyz.devvydont.smprpg.entity.bosses;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.effects.services.SpecialEffectService;
import xyz.devvydont.smprpg.effects.tasks.OverheatingEffect;
import xyz.devvydont.smprpg.effects.tasks.TetheredEffect;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.base.CustomBossInstance;
import xyz.devvydont.smprpg.entity.base.LeveledEntity;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;
import xyz.devvydont.smprpg.util.items.QuantityLootDrop;
import xyz.devvydont.smprpg.util.particles.ParticleUtil;

import java.util.*;

public class BlazeBoss extends CustomBossInstance<Blaze> implements Listener {

    private enum BlazeBossPhase {
        STARTING,
        DEFAULT,
        MOBBING,
    }

    private final static double GOAL_SCALE = 7.5;
    private final static double CHARGE_PROGRESS_PER_TICK = .003;
    private final static int MAX_MINIONS = 10;

    /*
     * A collection of minions spawned by the boss. Used so we don't spawn too many, to use in instances where we want
     * to track them for certain mechanics.
     */
    private final Map<UUID, LeveledEntity<?>> minions = new HashMap<>();

    // The current tick this boss has been alive for, used for certain aspects during the fight
    private int tick = 0;

    private BlazeBossPhase phase = BlazeBossPhase.STARTING;

    public BlazeBoss(Entity entity, CustomEntityType type) {
        super(entity, type);
    }

    public BlazeBoss(Blaze entity, CustomEntityType type) {
        super(entity, type);
    }

    @Override
    public @Nullable BossBar createBossBar() {
        return BossBar.bossBar(ComponentUtils.EMPTY, 1.0f, BossBar.Color.RED, BossBar.Overlay.NOTCHED_20, Set.of(BossBar.Flag.DARKEN_SCREEN));
    }

    @Override
    public void tick() {
        super.tick();
        tick++;

        // Check on all the minions. If they are either too far or not alive, clear them
        for (var entry : minions.entrySet()) {

            var uuid = entry.getKey();
            var minion = entry.getValue();

            if (!minion.getEntity().isValid()) {
                minions.remove(uuid);
                continue;
            }

            if (minion.getEntity() instanceof LivingEntity living && living.getHealth() <= 0) {
                minions.remove(uuid);
                continue;
            }

            if (minion.getEntity().getLocation().distance(_entity.getLocation()) > 100) {
                minions.remove(uuid);
                minion.getEntity().remove();
            }

            // If this minion has been alive for 20 ticks, enable its AI. We spawned it with disabled AI to prevent spawn damage.
            if (minion.getEntity() instanceof LivingEntity minionLiving && minion.getEntity().getTicksLived() > 20)
                minionLiving.setAI(true);
        }

        // The main logic of the boss.
        // If there are at least 5 mobs alive, enter the mobbing phase
        if (minions.size() >= (MAX_MINIONS/2))
            phase = BlazeBossPhase.MOBBING;

        // If the blaze cannot be hit, set its bar color to white, otherwise red.
        if (bossBar != null)
            bossBar.color(_entity.isInvulnerable() ? BossBar.Color.WHITE : BossBar.Color.RED);

        // If we are in the mobbing phase, we are invulnerable
        if (phase == BlazeBossPhase.MOBBING) {
            _entity.setInvulnerable(true);
            getEntity().setAI(false);

            // Only spawn particles every so often.
            if (tick % 5 == 0)
                for (var minion : minions.values())
                    ParticleUtil.spawnParticlesBetweenTwoPoints(Particle.ENCHANTED_HIT, _entity.getWorld(), getEntity().getEyeLocation().toVector(), minion.getEntity().getLocation().add(0, 1, 0).toVector(), 50);

            // No minions left? Back to default
            if (minions.isEmpty())
                phase = BlazeBossPhase.DEFAULT;

            return;
        }

        // If we are in the default phase, don't do anything. This is basically the "DPS" phase
        // We can also move from one phase to another here
        if (phase == BlazeBossPhase.DEFAULT) {
            _entity.setInvulnerable(false);
            getEntity().setAI(true);
            return;
        }

        // If we are in the starting phase, we are charging up similarly to how the wither does.
        if (phase == BlazeBossPhase.STARTING) {

            double chargeProgress = tick * CHARGE_PROGRESS_PER_TICK + .001;

            _entity.setInvulnerable(true);
            getEntity().setAI(false);
            setHealthPercentage(Math.min(chargeProgress, 1.0));
            updateBaseAttribute(AttributeWrapper.SCALE, chargeProgress * GOAL_SCALE);

            // Are we done charging?
            if (chargeProgress >= 1.0) {
                _entity.setInvulnerable(false);
                getEntity().setAI(true);
                phase = BlazeBossPhase.DEFAULT;
                _entity.getWorld().strikeLightningEffect(getEntity().getEyeLocation());
                _entity.getWorld().playSound(_entity.getLocation(), Sound.ENTITY_BLAZE_DEATH, 10f, .25f);
            }
            return;
        }

    }

    @Override
    public long getTimeLimit() {
        return 60L * 5L;
    }

    @Override
    public void setup() {
        super.setup();
        this.updateBaseAttribute(AttributeWrapper.ARMOR, 0);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        for (var leveledEntity : minions.values().stream().toList())
            if (leveledEntity.getEntity().isValid() && leveledEntity.getEntity() instanceof LivingEntity living)
                living.damage(999_999_999);
        minions.clear();
    }

    @Override
    public void updateAttributes() {
        super.updateAttributes();
        updateBaseAttribute(AttributeWrapper.KNOCKBACK_RESISTANCE, 1);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(

                // Common drops
                new QuantityLootDrop(ItemService.generate(CustomItemType.CHILI_PEPPER), 1, 3, this),
                new QuantityLootDrop(ItemService.generate(Material.BLAZE_ROD), 2, 6, this),
                new QuantityLootDrop(ItemService.generate(Material.IRON_INGOT), 2, 6, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.PREMIUM_BLAZE_ROD), 2, this),

                // Pity system drops
                new ChancedItemDrop(ItemService.generate(CustomItemType.ENCHANTED_IRON), 20, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ENCHANTED_IRON_BLOCK), 90, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.IRON_SINGULARITY), 2000, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ENCHANTED_BLAZE_ROD), 25, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.SCORCHING_STRING), 90, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.BOILING_INGOT), 20, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.INFERNO_REMNANT), 40, this),
                new QuantityLootDrop(ItemService.generate(CustomItemType.INFERNO_RESIDUE), 1, 2, this),

                // Gear drops
                new ChancedItemDrop(ItemService.generate(CustomItemType.INFERNO_HELMET), 100, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.INFERNO_CHESTPLATE), 120, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.INFERNO_LEGGINGS), 110, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.INFERNO_BOOTS), 100, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.INFERNO_SABER), 120, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.INFERNO_SHORTBOW), 120, this),

                new ChancedItemDrop(ItemService.generate(CustomItemType.SMOLDERING_CORE), 100, this),

                // Chance to summon again
                new ChancedItemDrop(ItemService.generate(CustomItemType.INFERNO_ARROW), 20, this)
        );
    }

    public int getMinionLimit() {
        return phase == BlazeBossPhase.MOBBING ? MAX_MINIONS : (MAX_MINIONS/2);
    }

    private void spawnMinion(Location location) {
        var mob = SMPRPG.getService(EntityService.class).spawnCustomEntity(CustomEntityType.PHOENIX, location);
        if (mob == null || mob.getEntity() == null)
            return;

        minions.put(mob.getEntity().getUniqueId(), mob);
    }

    private void handleFireballHitPlayer(Player player, boolean fromBoss) {

        // If this player already has an ailment, no need to do anything.
        if (SMPRPG.getService(SpecialEffectService.class).hasEffect(player))
            return;

        // If they have fire resistance, make them overheat.
        if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            var effect = new OverheatingEffect(SMPRPG.getService(SpecialEffectService.class), player, 10);
            var msg = ComponentUtils.merge(
                    ComponentUtils.create("From "),
                    getNameComponent(), ComponentUtils.create(": "),
                    ComponentUtils.create("\"Your mortal alchemy reeks of fear. Let the fire you sought to tame now consume you.\"", NamedTextColor.RED)
            ).hoverEvent(HoverEvent.showText(ComponentUtils.merge(
                    ComponentUtils.create("You were punished for being under the effect of "),
                    ComponentUtils.create("Fire Resistance", NamedTextColor.GOLD),
                    ComponentUtils.create(" when being hit by a "),
                    ComponentUtils.create("Phoenix fireball", NamedTextColor.RED),
                    ComponentUtils.create(". While under the effect of the "),
                    effect.getNameComponent(),
                    ComponentUtils.create(" ailment, you suffer from constant damage and eventually will have your "),
                    ComponentUtils.create("Fire Resistance", NamedTextColor.GOLD),
                    ComponentUtils.create(" replaced with "),
                    ComponentUtils.create("Poison", NamedTextColor.DARK_GREEN),
                    ComponentUtils.create(".")
            )));
            player.sendMessage(msg);
            player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, 1, .5f);
            SMPRPG.getService(SpecialEffectService.class).giveEffect(player, effect);
            return;
        }

        // If this is the boss, tether them.
        if (fromBoss) {
            var effect = new TetheredEffect(SMPRPG.getService(SpecialEffectService.class), player, getEntity(), 5);
            var msg = ComponentUtils.merge(
                    ComponentUtils.create("From "),
                    getNameComponent(), ComponentUtils.create(": "),
                    ComponentUtils.create("\"The fire has marked you. Now, it must draw you home. Do not avert your gaze.\"", NamedTextColor.RED)
            ).hoverEvent(HoverEvent.showText(ComponentUtils.merge(
                ComponentUtils.create("The "),
                    getNameComponent(),
                    ComponentUtils.create(" managed to hit you directly with a "),
                    ComponentUtils.create("Phoenix fireball", NamedTextColor.RED),
                    ComponentUtils.create("! If you do not remove the "),
                    effect.getNameComponent(),
                    ComponentUtils.create(" ailment quickly, you will be "),
                    ComponentUtils.create("sent flying to your demise", NamedTextColor.DARK_RED),
                    ComponentUtils.create("!")
            )));
            player.sendMessage(msg);
            SMPRPG.getService(SpecialEffectService.class).giveEffect(player, effect);
        }

    }

    /*
     * When our boss shoots a fireball and it explodes, it will summon a normal phoenix enemy
     * Also, if it hits a player directly, they get "tethered"
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void __onBossFireballExplode(ProjectileHitEvent event) {

        // Did a minion hit the player?
        for (var minion : minions.values())
            if (minion.getEntity().equals(event.getEntity().getShooter()) && event.getHitEntity() instanceof Player player)
                handleFireballHitPlayer(player, false);

        // Ignore fireballs not shot by the boss
        if (!_entity.equals(event.getEntity().getShooter()))
            return;

        // Did we hit a player directly? if so, we need to tether them.
        if (event.getHitEntity() instanceof Player hitPlayer)
            handleFireballHitPlayer(hitPlayer, true);

        // Do we have too many minions?
        if (minions.size() >= getMinionLimit())
            return;

        // RNG check
        if (Math.random() < .5)
            return;

        // This fireball is from our blaze boss.
        Location spawn = event.getEntity().getLocation();
        var mob = SMPRPG.getService(EntityService.class).spawnCustomEntity(CustomEntityType.PHOENIX, spawn);
        if (mob == null || mob.getEntity() == null)
            return;

        // Disable its AI at first so it doesn't deal damage immediately.
        if (mob.getEntity() instanceof LivingEntity livingMinion)
            livingMinion.setAI(false);

        minions.put(mob.getEntity().getUniqueId(), mob);
    }

    /*
     * When a player damages the boss....
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void __onDamageBoss(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof LivingEntity living))
            return;

        // Was the damaged entity the boss?
        if (!event.getEntity().equals(_entity))
            return;

        // Is the boss already under half HP?
        if (getHealthPercentage() <= .5)
            return;

        double threshold = getMaxHp() / 2;
        // Are we hitting the half hp threshold?
        if (!(getEntity().getHealth() - event.getDamage() < threshold))
            return;

        // Set the damage to hit the threshold exactly and spawn a bunch of minions
        event.setDamage(living.getHealth()-threshold);

        if (getActivelyInvolvedPlayers().isEmpty())
            return;

        getEntity().getWorld().playSound(getEntity().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 10f, .2f);

        int minionsSpawned = 0;

        while (minionsSpawned < 10) {
            for (Player p : getActivelyInvolvedPlayers()) {
                spawnMinion(p.getEyeLocation().subtract(p.getLocation().getDirection().normalize().multiply(3).add(new Vector(Math.random() * 6 - 3, 0, Math.random() * 6 - 3))));
                minionsSpawned++;
            }
        }

        phase = BlazeBossPhase.MOBBING;
    }



    /*
     * When a minion dies, spawn a fire particle trail, and damage the boss
     */
    @EventHandler
    private void __onMinionDeath(EntityDeathEvent event) {

        // Was the killed entity a minion?
        if (!minions.containsKey(event.getEntity().getUniqueId()))
            return;

        // Remove the reference and handle any logic
        minions.remove(event.getEntity().getUniqueId());
        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 2);

        // If we were in the mobbing phase and this is the last minion, set phase back to default
        if (phase == BlazeBossPhase.MOBBING && minions.isEmpty())
            phase = BlazeBossPhase.DEFAULT;

        // Was there a cause?
        if (event.getDamageSource().getCausingEntity() == null || event.getDamageSource().getDirectEntity() == null)
            return;

        // Damage the boss for how much health the minion had
        double damage = event.getEntity().getAttribute(Attribute.MAX_HEALTH).getValue();
        boolean invulnState = _entity.isInvulnerable();
        _entity.setInvulnerable(false);
        getEntity().damage(damage, DamageSource.builder(DamageType.MAGIC).withCausingEntity(event.getDamageSource().getCausingEntity()).withDirectEntity(event.getDamageSource().getDirectEntity()).build());
        _entity.setInvulnerable(invulnState);
        ParticleUtil.spawnParticlesBetweenTwoPoints(Particle.FLAME, event.getEntity().getWorld(), getEntity().getEyeLocation().toVector(), event.getEntity().getEyeLocation().toVector(), 100);
    }
}
