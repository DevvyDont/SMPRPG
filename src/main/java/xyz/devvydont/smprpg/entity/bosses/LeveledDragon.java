package xyz.devvydont.smprpg.entity.bosses;

import com.destroystokyo.paper.event.entity.EnderDragonFireballHitEvent;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.services.SpecialEffectService;
import xyz.devvydont.smprpg.effects.tasks.DisintegratingEffect;
import xyz.devvydont.smprpg.entity.base.BossInstance;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;
import xyz.devvydont.smprpg.entity.components.EntityConfiguration;
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;
import xyz.devvydont.smprpg.util.items.QuantityLootDrop;
import xyz.devvydont.smprpg.util.particles.ParticleUtil;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.Collection;
import java.util.List;

public class LeveledDragon extends BossInstance<EnderDragon> implements Listener {

    /**
     * A number from 0-.9999 that determines how intense crystal placement luck is at first.
     */
    public static final double BASE_CRYSTAL_LUCK = .5;

    /**
     * How much luck multiplier you "cap out at".
     */
    public static final double CRYSTAL_LUCK_SOFT_CAP = 4;

    private boolean wasSummoned = false;

    public LeveledDragon(EnderDragon entity) {
        super(entity);
    }

    @Override
    public void setup() {
        super.setup();
        this.updateBaseAttribute(AttributeWrapper.ARMOR, 0);
        this.updateBaseAttribute(AttributeWrapper.KNOCKBACK_RESISTANCE, 100);
        this.updateBaseAttribute(AttributeWrapper.REGENERATION, 5);
    }

    @Override
    public @Nullable BossBar createBossBar() {
        return null;
    }

    @Override
    public long getTimeLimit() {
        return wasSummoned ? 60*5 : INFINITE_TIME_LIMIT;
    }

    @Override
    public String getClassKey() {
        return VanillaEntity.VANILLA_CLASS_KEY;
    }

    @Override
    public EntityType getDefaultEntityType() {
        return EntityType.ENDER_DRAGON;
    }

    @Override
    public String getEntityName() {
        return "Ender Dragon";
    }

    public void setSummoned(boolean summoned) {
        this.wasSummoned = summoned;
    }

    @Override
    public EntityConfiguration getDefaultConfiguration() {
        return EntityConfiguration.builder()
                .withLevel(wasSummoned ? 50 : 40)
                .withHealth(wasSummoned ? 3_000_000 : 1_000_000)
                .withDamage(wasSummoned ? 1250 : 500)
                .build();
    }

    private TNTPrimed makeTnt(World world, Location loc, int ticks, Vector velocity, float yield, BlockData data) {
        TNTPrimed tnt = (TNTPrimed) world.spawnEntity(loc, EntityType.TNT);
        tnt.setFuseTicks(ticks);
        tnt.setVelocity(velocity);
        tnt.setYield(yield);
        tnt.setBlockData(data);
        tnt.setSource(_entity);
        return tnt;
    }

    @Override
    public void cleanup() {
        super.cleanup();

        // Since this is a dragon, we need a bit more care. Trigger the normal death, but make sure nobody gets credit.
        getDamageTracker().clear();
        _entity.setPhase(EnderDragon.Phase.DYING);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void doFireballSpawn(EnderDragonFireballHitEvent event)
    {

        // Only perform this action if the source is from us.
        if (!_entity.equals(event.getEntity().getShooter()))
            return;

        var world = event.getEntity().getWorld();
        var loc = event.getEntity().getLocation();
        var tnt = makeTnt(world, loc, 20, new Vector(0, 1.0, 0), 5.0f, Material.AMETHYST_BLOCK.createBlockData());
        tnt.setFireTicks(100);
        for (int i = 0; i <= 3; i++) {
            double x = 0;
            double z = 0;
            switch (i) {
                case 0: x = 0.25; z = 0; break;
                case 1: x = -0.25; z = 0; break;
                case 2: x = 0; z = 0.25; break;
                case 3: x = 0; z = -0.25; break;
            }
            var velocity = new Vector(x, 1.0, z);
            makeTnt(world, loc, 80, velocity, 5.0f, Material.WHITE_GLAZED_TERRACOTTA.createBlockData());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void __onTakeCloudDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player player))
            return;

        var specialEffectService = SMPRPG.getService(SpecialEffectService.class);
        if (specialEffectService.hasEffect(player))
            return;

        // Check if our entity was responsible for the damage.
        if (!_entity.equals(event.getDamageSource().getCausingEntity()))
            return;

        // Check if the direct entity involved was the area effect cloud.
        if (!(event.getDamageSource().getDirectEntity() instanceof AreaEffectCloud cloud))
            return;

        specialEffectService.giveEffect(player, new DisintegratingEffect(specialEffectService, player, DisintegratingEffect.SECONDS));
    }

    /**
     * When putting drops here, consider the fact that crystal contribution makes a HUGE impact on drop luck.
     * If someone places optimal crystals to summon, these odds are more or less slashed by a factor of 4.
     */
    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new ChancedItemDrop(ItemService.generate(CustomItemType.ELDERFLAME_HELMET), 850, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ELDERFLAME_CHESTPLATE), 1000, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ELDERFLAME_LEGGINGS), 900, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ELDERFLAME_BOOTS), 850, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ELDERFLAME_DAGGER), 1000, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.VOID_RELIC), 1000, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.TRANSMISSION_WAND), 1000, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.DRACONIC_CRYSTAL), 400, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.DRAGON_SCALES), 4, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ENCHANTED_ENDER_PEARL), 50, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.PREMIUM_ENDER_PEARL), 5, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.SUMMONING_CRYSTAL), 150, this),
                new QuantityLootDrop(ItemService.generate(Material.ENDER_PEARL), 1, 3, this),
                new QuantityLootDrop(ItemService.generate(CustomItemType.ENDERIOS), 1, 2, this)
        );
    }

    @Override
    public void wipe() {

        var battle = _entity.getWorld().getEnderDragonBattle();
        if (battle != null) {
            battle.generateEndPortal(true);
            battle.resetCrystals();
        }

        super.wipe();
    }

    /**
     * When we take damage and there's still crystals up, give us some reduction.
     * @param event
     */
    @EventHandler
    public void onDamageReceivedWhileCrystalsActive(CustomEntityDamageByEntityEvent event) {

        if (!(event.damaged.equals(_entity)))
            return;

        var dragonBattle = _entity.getWorld().getEnderDragonBattle();
        if (dragonBattle == null)
            return;

        updateBaseAttribute(AttributeWrapper.DEFENSE, dragonBattle.getHealingCrystals().size() * 150);

        for (var crystal : dragonBattle.getHealingCrystals())
            ParticleUtil.spawnParticlesBetweenTwoPoints(Particle.SOUL_FIRE_FLAME, crystal.getWorld(), crystal.getLocation().toVector(), _entity.getLocation().toVector(), 100);
    }

    /**
     * When we take damage and there's still crystals up, give us some reduction.
     * @param event
     */
    @EventHandler
    public void onHealWithCrystalsActive(EntityRegainHealthEvent event) {

        if (!(event.getEntity().equals(_entity)))
            return;

        var dragonBattle = _entity.getWorld().getEnderDragonBattle();
        if (dragonBattle == null)
            return;

        updateBaseAttribute(AttributeWrapper.DEFENSE, dragonBattle.getHealingCrystals().size() * 150);
        event.setAmount(dragonBattle.getHealingCrystals().size() * 500);
    }

    @EventHandler
    public void onAttemptTeleportDuringDragonFight(EntityPortalEnterEvent event) {

        if (!event.getEntity().getWorld().getEnvironment().equals(World.Environment.THE_END))
            return;

        if (!event.getPortalType().equals(PortalType.ENDER))
            return;

        var dragonBattle = _entity.getWorld().getEnderDragonBattle();
        if (dragonBattle == null)
            return;

        if (dragonBattle.getEnderDragon() != null) {
            event.setCancelled(true);
            return;
        }

        if (!dragonBattle.getRespawnPhase().equals(DragonBattle.RespawnPhase.NONE)) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCrystalDestroyed(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof EnderCrystal crystal))
            return;

        var battle = crystal.getWorld().getEnderDragonBattle();
        if (battle == null)
            return;

        if (battle.getEnderDragon() == null)
            return;

        if (!battle.getEnderDragon().equals(_entity))
            return;

        var found = false;
        for (var healingCrystal : battle.getHealingCrystals())
            if (healingCrystal.equals(crystal))
                found = true;

        if (!found)
            return;

        if (!(event.getDamageSource().getCausingEntity() instanceof Player player))
            return;

        // Found a crystal in this fight that was destroyed by a player!
        getDamageTracker().addDamageDealtByEntity(player, (int)(getHalfHeartValue() * 7.5));
    }

    /**
     * When a dragon's TNT explodes, we should create another pool of his toxic gas where it blew up.
     */
    @EventHandler
    public void onDragonTntExplosion(EntityExplodeEvent event) {

        if (!(event.getEntity() instanceof TNTPrimed tnt))
            return;

        if (!_entity.equals(tnt.getSource()))
            return;

        var cloud = event.getEntity().getWorld().spawn(event.getLocation(), AreaEffectCloud.class);
        cloud.setColor(Color.PURPLE);
        cloud.setSource(_entity);
        cloud.setParticle(Particle.DRAGON_BREATH);
        cloud.setBasePotionType(PotionType.HARMING);
        cloud.setOwnerUniqueId(_entity.getUniqueId());
        cloud.setDuration((int) TickTime.seconds(30));
    }

}
