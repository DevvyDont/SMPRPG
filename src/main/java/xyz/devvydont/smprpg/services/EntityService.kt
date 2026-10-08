package xyz.devvydont.smprpg.services

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import io.papermc.paper.scoreboard.numbers.NumberFormat
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance
import xyz.devvydont.smprpg.entity.base.LeveledEntity
import xyz.devvydont.smprpg.entity.base.VanillaEntity
import xyz.devvydont.smprpg.entity.base.listeners.EnderDragonSpawnContributionListener
import xyz.devvydont.smprpg.entity.base.listeners.listeners.SeaCreatureBurnPrevention
import xyz.devvydont.smprpg.entity.bosses.LeveledDragon
import xyz.devvydont.smprpg.entity.bosses.LeveledElderGuardian
import xyz.devvydont.smprpg.entity.bosses.LeveledWarden
import xyz.devvydont.smprpg.entity.bosses.LeveledWither
import xyz.devvydont.smprpg.entity.interfaces.IDamageTrackable
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.entity.vanilla.*
import xyz.devvydont.smprpg.events.LeveledEntitySpawnEvent
import xyz.devvydont.smprpg.listeners.damage.ShulkerDefenseModeFix
import xyz.devvydont.smprpg.listeners.entity.EntityTamingAttributeFix
import xyz.devvydont.smprpg.listeners.entity.TamedEntityFeedFix
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.tasks.PlaytimeTracker
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*
import java.util.function.Consumer

const val ENTITY_CLASS_KEY: String = "entity-class"
const val LEVEL_KEY_STRING: String = "level"

// The radius at which custom entities will be considered when deciding if there are too many already present for natural spawning of custom mobs.
const val CUSTOM_ENTITY_PROXIMITY_SPAWN_RADIUS = 200
// How many entities of the same type that must be near each other to effectively abort natural spawning of a custom mob.
const val CUSTOM_ENTITY_PROXIMITY_SPAWN_LIMIT = 10

class EntityService : IService, Listener {
    private val entityInstances: MutableMap<UUID, LeveledEntity<*>> = HashMap<UUID, LeveledEntity<*>>()
    private val entityResolver: MutableMap<String, CustomEntityType> = HashMap<String, CustomEntityType>()
    private val vanillaEntityHandlers: MutableMap<EntityType, Class<out LeveledEntity<*>>> = HashMap<EntityType, Class<out LeveledEntity<*>>>()

    private val listeners: MutableList<ToggleableListener> = ArrayList<ToggleableListener>()

    private var wellnessCheckTask: BukkitTask? = null

    @Throws(RuntimeException::class)
    override fun setup() {

        // Start tracking playtime.
        PlaytimeTracker.start()

        for (customEntityType in CustomEntityType.entries)
            entityResolver.put(customEntityType.key(), customEntityType)

        plugin.logger.info(String.format("Registered %s custom entity types", entityResolver.size))

        vanillaEntityHandlers.put(EntityType.BAT, LeveledBat::class.java)
        vanillaEntityHandlers.put(EntityType.CHICKEN, LeveledChicken::class.java)
        vanillaEntityHandlers.put(EntityType.COW, LeveledCow::class.java)
        vanillaEntityHandlers.put(EntityType.PIG, LeveledPig::class.java)
        vanillaEntityHandlers.put(EntityType.SHEEP, LeveledSheep::class.java)
        vanillaEntityHandlers.put(EntityType.CAMEL, LeveledCamel::class.java)
        vanillaEntityHandlers.put(EntityType.DONKEY, LeveledDonkey::class.java)
        vanillaEntityHandlers.put(EntityType.HORSE, LeveledHorse::class.java)
        vanillaEntityHandlers.put(EntityType.MULE, LeveledMule::class.java)
        vanillaEntityHandlers.put(EntityType.CAT, LeveledCat::class.java)
        vanillaEntityHandlers.put(EntityType.PARROT, LeveledParrot::class.java)
        vanillaEntityHandlers.put(EntityType.WOLF, LeveledWolf::class.java)
        vanillaEntityHandlers.put(EntityType.ARMADILLO, LeveledArmadillo::class.java)
        vanillaEntityHandlers.put(EntityType.BEE, LeveledBee::class.java)
        vanillaEntityHandlers.put(EntityType.FOX, LeveledFox::class.java)
        vanillaEntityHandlers.put(EntityType.GOAT, LeveledGoat::class.java)
        vanillaEntityHandlers.put(EntityType.LLAMA, LeveledLlama::class.java)
        vanillaEntityHandlers.put(EntityType.OCELOT, LeveledOcelot::class.java)
        vanillaEntityHandlers.put(EntityType.PANDA, LeveledPanda::class.java)
        vanillaEntityHandlers.put(EntityType.POLAR_BEAR, LeveledPolarBear::class.java)
        vanillaEntityHandlers.put(EntityType.RABBIT, LeveledRabbit::class.java)

        vanillaEntityHandlers.put(EntityType.AXOLOTL, LeveledAxolotl::class.java)
        vanillaEntityHandlers.put(EntityType.COD, LeveledCod::class.java)
        vanillaEntityHandlers.put(EntityType.DOLPHIN, LeveledDolphin::class.java)
        vanillaEntityHandlers.put(EntityType.FROG, LeveledFrog::class.java)
        vanillaEntityHandlers.put(EntityType.GLOW_SQUID, LeveledGlowSquid::class.java)
        vanillaEntityHandlers.put(EntityType.NAUTILUS, LeveledNautilus::class.java)
        vanillaEntityHandlers.put(EntityType.PUFFERFISH, LeveledPufferfish::class.java)
        vanillaEntityHandlers.put(EntityType.SALMON, LeveledSalmon::class.java)
        vanillaEntityHandlers.put(EntityType.SQUID, LeveledSquid::class.java)
        vanillaEntityHandlers.put(EntityType.TADPOLE, LeveledTadpole::class.java)
        vanillaEntityHandlers.put(EntityType.TROPICAL_FISH, LeveledTropicalFish::class.java)
        vanillaEntityHandlers.put(EntityType.TURTLE, LeveledTurtle::class.java)

        vanillaEntityHandlers.put(EntityType.MOOSHROOM, LeveledMushroomCow::class.java)
        vanillaEntityHandlers.put(EntityType.SNIFFER, LeveledSniffer::class.java)
        vanillaEntityHandlers.put(EntityType.CREAKING, LeveledCreaking::class.java)

        vanillaEntityHandlers.put(EntityType.IRON_GOLEM, LeveledIronGolem::class.java)
        vanillaEntityHandlers.put(EntityType.COPPER_GOLEM, LeveledCopperGolem::class.java)
        vanillaEntityHandlers.put(EntityType.SNOW_GOLEM, LeveledSnowGolem::class.java)

        vanillaEntityHandlers.put(EntityType.ZOMBIE, LeveledZombie::class.java)
        vanillaEntityHandlers.put(EntityType.SKELETON, LeveledSkeleton::class.java)
        vanillaEntityHandlers.put(EntityType.PARCHED, LeveledParched::class.java)
        vanillaEntityHandlers.put(EntityType.STRAY, LeveledStray::class.java)
        vanillaEntityHandlers.put(EntityType.BOGGED, LeveledBogged::class.java)
        vanillaEntityHandlers.put(EntityType.DROWNED, LeveledDrowned::class.java)
        vanillaEntityHandlers.put(EntityType.SPIDER, LeveledSpider::class.java)
        vanillaEntityHandlers.put(EntityType.CAVE_SPIDER, LeveledSpider::class.java)
        vanillaEntityHandlers.put(EntityType.CREEPER, LeveledCreeper::class.java)
        vanillaEntityHandlers.put(EntityType.HUSK, LeveledHusk::class.java)
        vanillaEntityHandlers.put(EntityType.GHAST, LeveledGhast::class.java)
        vanillaEntityHandlers.put(EntityType.HAPPY_GHAST, LeveledHappyGhast::class.java)
        vanillaEntityHandlers.put(EntityType.HOGLIN, LeveledHoglin::class.java)
        vanillaEntityHandlers.put(EntityType.ZOGLIN, LeveledZoglin::class.java)
        vanillaEntityHandlers.put(EntityType.PIGLIN, LeveledPiglin::class.java)
        vanillaEntityHandlers.put(EntityType.PIGLIN_BRUTE, LeveledPiglinBrute::class.java)
        vanillaEntityHandlers.put(EntityType.ZOMBIFIED_PIGLIN, LeveledZombifiedPiglin::class.java)
        vanillaEntityHandlers.put(EntityType.STRIDER, LeveledStrider::class.java)
        vanillaEntityHandlers.put(EntityType.ENDERMAN, LeveledEnderman::class.java)
        vanillaEntityHandlers.put(EntityType.PHANTOM, LeveledPhantom::class.java)
        vanillaEntityHandlers.put(EntityType.ENDER_DRAGON, LeveledDragon::class.java)
        vanillaEntityHandlers.put(EntityType.WITHER, LeveledWither::class.java)
        vanillaEntityHandlers.put(EntityType.GUARDIAN, LeveledGuardian::class.java)
        vanillaEntityHandlers.put(EntityType.ELDER_GUARDIAN, LeveledElderGuardian::class.java)
        vanillaEntityHandlers.put(EntityType.WARDEN, LeveledWarden::class.java)

        vanillaEntityHandlers.put(EntityType.SKELETON_HORSE, LeveledSkeletonHorse::class.java)
        vanillaEntityHandlers.put(EntityType.ZOMBIE_HORSE, LeveledZombieHorse::class.java)
        vanillaEntityHandlers.put(EntityType.CAMEL_HUSK, LeveledCamelHusk::class.java)
        vanillaEntityHandlers.put(EntityType.ZOMBIE_NAUTILUS, LeveledZombieNautilus::class.java)

        vanillaEntityHandlers.put(EntityType.SLIME, LeveledSizedCube::class.java)
        vanillaEntityHandlers.put(EntityType.MAGMA_CUBE, LeveledSizedCube::class.java)

        vanillaEntityHandlers.put(EntityType.BLAZE, LeveledBlaze::class.java)
        vanillaEntityHandlers.put(EntityType.WITHER_SKELETON, LeveledWitherSkeleton::class.java)

        vanillaEntityHandlers.put(EntityType.BREEZE, LeveledBreeze::class.java)

        vanillaEntityHandlers.put(EntityType.VILLAGER, LeveledVillager::class.java)
        vanillaEntityHandlers.put(EntityType.ZOMBIE_VILLAGER, LeveledZombieVillager::class.java)
        vanillaEntityHandlers.put(EntityType.WANDERING_TRADER, LeveledWanderingTrader::class.java)
        vanillaEntityHandlers.put(EntityType.TRADER_LLAMA, LeveledTraderLlama::class.java)
        vanillaEntityHandlers.put(EntityType.ALLAY, LeveledAllay::class.java)
        vanillaEntityHandlers.put(EntityType.VINDICATOR, LeveledVindicator::class.java)
        vanillaEntityHandlers.put(EntityType.ILLUSIONER, LeveledIllusioner::class.java)
        vanillaEntityHandlers.put(EntityType.WITCH, LeveledWitch::class.java)
        vanillaEntityHandlers.put(EntityType.EVOKER, LeveledEvoker::class.java)
        vanillaEntityHandlers.put(EntityType.VEX, LeveledVex::class.java)
        vanillaEntityHandlers.put(EntityType.RAVAGER, LeveledRavager::class.java)
        vanillaEntityHandlers.put(EntityType.PILLAGER, LeveledPillager::class.java)

        vanillaEntityHandlers.put(EntityType.ARMOR_STAND, LeveledArmorStand::class.java)

        vanillaEntityHandlers.put(EntityType.MANNEQUIN, LeveledMannequin::class.java)
        vanillaEntityHandlers.put(EntityType.BLOCK_DISPLAY, LeveledDisplay::class.java)
        vanillaEntityHandlers.put(EntityType.ITEM_DISPLAY, LeveledDisplay::class.java)
        vanillaEntityHandlers.put(EntityType.TEXT_DISPLAY, LeveledDisplay::class.java)

        plugin.logger.info("Associated ${vanillaEntityHandlers.size} vanilla entities with custom handlers")

        // --- Below-name HP display ---
        // We pin a scoreboard objective to the BELOW_NAME slot so HP renders under player names.
        //
        // This used to use the vanilla `health` criteria. The server auto-tracks a health score for every
        // entity, but on MC 1.21 and earlier the client only *rendered* below-name scores under players,
        // so in practice it was player-only. Minecraft 26.1 "fixed" MC-99647 and now renders below-name
        // scores under EVERY entity that has a custom name -- which is all of our mobs, and it leaks onto
        // displays/items too -- spamming a stray heart number on everything.
        //
        // Workaround while we're on 26.1.x: use a DUMMY objective (no auto-tracking) and write a styled
        // score for players only (see LeveledPlayer#updateBelowNameHealthDisplay), so non-players have no
        // score and render nothing. 26.2 reverts the client behavior and adds the `below_name_distance`
        // attribute to control it, so this can go back to a plain `health` objective once we update past
        // 26.1. We unregister any pre-existing objective first to migrate old `health` objectives in
        // already-generated worlds over to the dummy.
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        scoreboard.getObjective(BELOW_NAME_HEALTH_OBJECTIVE)?.unregister()
        val hpObjective = scoreboard.registerNewObjective(
            BELOW_NAME_HEALTH_OBJECTIVE,
            Criteria.DUMMY,
            ComponentUtils.create("", NamedTextColor.WHITE)
        )
        hpObjective.displaySlot = DisplaySlot.BELOW_NAME

        // Initialize entities that are already loaded
        for (world in Bukkit.getWorlds()) for (entity in world.entities) {

            // Ensures that this entity does not display any sort of information under their name unless
            // we explicitly say so in our class.
            val obj = Bukkit.getScoreboardManager().mainScoreboard.getObjective(BELOW_NAME_HEALTH_OBJECTIVE)
            obj?.getScoreFor(entity)?.numberFormat(NumberFormat.blank())

            // Ignore non living/displays
            if (entity !is LivingEntity && entity !is Display)
                continue

            val leveled = getEntityInstance(entity)

            leveled.updateAttributes()
            leveled.setup()
            trackEntity(leveled)
        }

        wellnessCheckTask = object : BukkitRunnable() {
            override fun run() {
                val invalid = ArrayList<UUID>()
                for (entry in entityInstances.entries)
                    if (!entry.value.getEntity().isValid)
                        invalid.add(entry.key)
                for (id in invalid)
                    removeEntity(id)
            }
        }.runTaskTimer(plugin, TickTime.minutes(5), TickTime.minutes(5))

        listeners.add(EntityTamingAttributeFix()) // Scales entities to owner levels.
        listeners.add(TamedEntityFeedFix()) // Allows tamed entities to heal more efficiently.
        listeners.add(EnderDragonSpawnContributionListener()) // Adds boss contribution weight for boss spawning.
        listeners.add(ShulkerDefenseModeFix()) // Fixes shulkers so they take reduced damage when not peeking.
        listeners.add(SeaCreatureBurnPrevention())  // Sea creatures will not burn in lava.
        for (listener in listeners)
            listener.start()
    }

    override fun cleanup() {
        for (entity in entityInstances.values)
            entity.cleanup()
        entityInstances.clear()
        wellnessCheckTask!!.cancel()
        for (listener in listeners)
            listener.stop()
    }

    fun <T : LeveledEntity<*>> getEntitiesOfClass(clazz: Class<T>): ArrayList<T> {
        val results = ArrayList<T>()
        for (entity in entityInstances.values)
            if (entity.javaClass == clazz)
                results.add(clazz.cast(entity))
        return results
    }

    /**
     * A vanilla entity is being queried, determine if there is a child class of VanillaEntity to use as a
     * handler class. If there is not, then we will use the default VanillaEntity that is compatible with every
     * entity.
     *
     * @param entity The entity to create a new wrapper instance for.
     */
    fun getNewVanillaEntityInstance(entity: Entity): LeveledEntity<*> {
        var ret: LeveledEntity<*>

        // Are we using the vanilla handler? (We don't have a custom class setup for this vanilla type)
        val handler = vanillaEntityHandlers[entity.type]
        if (handler == null) {
            ret = VanillaEntity(entity)
            ret.setup()
            ret.updateAttributes()
            trackEntity(ret)
            return ret
        }

        // Reflection hacks since we have a custom handler
        try {
            val clazz = entity.type.entityClass
            ret = handler.getConstructor(clazz).newInstance(entity)
            ret.setup()
            ret.updateAttributes()
            trackEntity(ret)
            return ret
        } catch (e: Exception) {
            plugin.logger.severe(
                String.format(
                    "Failed to instantiate vanilla class handler %s for entity type %s. Ensure that a constructor exists using the %s class as a parameter.: %s",
                    handler.getName(),
                    entity.type,
                    entity.type.entityClass,
                    e
                )
            )
            ret = VanillaEntity(entity)
            ret.setup()
            ret.updateAttributes()
            trackEntity(ret)
            return ret
        }
    }

    /**
     * Given a living entity, get its LeveledEntity instance
     * If the entity has an instance we are tracking, return it.
     * If the entity is not being tracked, start tracking it by resolving its PDC
     *
     * @param entity The entity instance.
     * @return A wrapper instance.
     */
    fun getEntityInstance(entity: Entity): LeveledEntity<*> {

        // Are we already tracking them?
        val instance = entityInstances[entity.uniqueId]
        if (instance != null)
            return instance

        // Is this a player? We use a pretty barebones instance for players for least amount of interference possible
        if (entity is Player) {
            val leveledPlayer = LeveledPlayer(plugin, entity)
            leveledPlayer.setup()
            trackEntity(leveledPlayer)
            return leveledPlayer
        }

        // Does this entity have an associated entity handler? If no, assume vanilla entity
        val entityClass = entity.persistentDataContainer.get(classNamespacedKey, PersistentDataType.STRING)
        if (entityClass == null || entityClass == VanillaEntity.VANILLA_CLASS_KEY)
            return getNewVanillaEntityInstance(entity)

        // We do have an associated handler
        val type = entityResolver[entityClass]
        if (type == null)
            return getNewVanillaEntityInstance(entity)

        // Create an instance of the handler and track it.
        val leveled: LeveledEntity<*>

        // This might seem confusing, but we have to force the right create() method to be called based on the type.
        if (entity is LivingEntity)
            leveled = type.create(entity)
        else
            leveled = type.create(entity)

        leveled.setup()
        trackEntity(leveled)
        return leveled
    }

    /**
     * Gets the player wrapper to allow you to interact with various custom functions, such as spending mana.
     * @param player The player to retrieve.
     */
    fun getPlayerInstance(player: Player): LeveledPlayer {
        return getEntityInstance(player) as LeveledPlayer
    }

    /**
     * Spawns a custom entity into the world. This is guaranteed to be successful.
     */
    fun spawnCustomEntity(type: CustomEntityType, location: Location): LeveledEntity<*> {
        val entity = location.getWorld().spawnEntity(location, type.Type, CreatureSpawnEvent.SpawnReason.CUSTOM, Consumer { e: Entity ->
                if (e is LivingEntity) {
                    e.equipment?.setItemInMainHand(null)
                    e.equipment?.setItemInOffHand(null)
                    e.equipment?.setHelmet(null)
                    e.equipment?.setChestplate(null)
                    e.equipment?.setLeggings(null)
                    e.equipment?.setBoots(null)
                }
                e.persistentDataContainer.set(classNamespacedKey, PersistentDataType.STRING, type.key())
            })

        return getEntityInstance(entity)
    }

    /**
     * Check if an entity is being tracked by this service.
     * @param entity The entity you want to check.
     * @return True if the entity is being tracked, otherwise False.
     */
    fun isTracking(entity: Entity): Boolean {
        return this.entityInstances.containsKey(entity.uniqueId)
    }

    /**
     * Starts tracking an entity, meaning we register its events (if it has any) and consider it setup.
     */
    private fun trackEntity(entity: LeveledEntity<*>) {
        if (isTracking(entity.getEntity()))
            return

        removeEntity(entity.getEntity().uniqueId)
        entityInstances.put(entity.getEntity().uniqueId, entity)
        if (entity is Listener)
            plugin.server.pluginManager.registerEvents(entity, plugin)
    }

    /**
     * Stops tracking an entity. This entity will no longer respond to wellness checks or react to events.
     * If the entity still exists, it can be thought of as behaving to its vanilla counterpart.
     */
    private fun removeEntity(uuid: UUID?) {
        val removed = entityInstances.remove(uuid)
        if (removed == null)
            return

        removed.cleanup()
        if (removed is Listener)
            HandlerList.unregisterAll(removed)
    }

    /**
     * Called when a creature is spawned for the first time.
     * Makes sure they have proper attributes and we are tracking them
     */
    @Suppress("unused")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onEntitySpawnForTheFirstTime(event: CreatureSpawnEvent) {
        val entity = getEntityInstance(event.getEntity())
        entity.resetLevel()

        val leveledSpawnEvent = LeveledEntitySpawnEvent(entity)
        leveledSpawnEvent.callEvent()

        entity.updateAttributes()
        entity.updateNametag()

        // If this entity is holding/wearing anything, we need to fix their items to have proper stats
        val itemService = SMPRPG.getService(ItemService::class.java)
        val equipment = event.getEntity().equipment
        if (equipment == null)
            return

        equipment.setHelmet(itemService.ensureItemStackUpdated(equipment.helmet))
        equipment.setChestplate(itemService.ensureItemStackUpdated(equipment.chestplate))
        equipment.setLeggings(itemService.ensureItemStackUpdated(equipment.leggings))
        equipment.setBoots(itemService.ensureItemStackUpdated(equipment.boots))
        equipment.setItemInMainHand(itemService.ensureItemStackUpdated(equipment.itemInMainHand))
        equipment.setItemInOffHand(itemService.ensureItemStackUpdated(equipment.itemInOffHand))
    }

    /**
     * Every time an entity is spawned, we need to construct an instance for them, we do this just to make sure
     * that at least every entity has some sort stat initialization
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onEntitySpawn(event: EntityAddToWorldEvent) {

        // Ensures that this entity does not display any sort of information under their name unless
        // we explicitly say so in our class.
        val obj = Bukkit.getScoreboardManager().mainScoreboard.getObjective(BELOW_NAME_HEALTH_OBJECTIVE)
        obj?.getScoreFor(event.entity)?.numberFormat(NumberFormat.blank())

        if (event.getEntity() !is LivingEntity && event.getEntity() !is Display)
            return

        val leveled = getEntityInstance(event.getEntity())
        leveled.updateAttributes()
        trackEntity(leveled)
    }

    @EventHandler
    @Suppress("unused")
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        val leveled = getPlayerInstance(event.getPlayer())
        leveled.updateAttributes()
        trackEntity(leveled)

        // If they are in a non survival mode, full heal them.
        if (event.getPlayer().gameMode.isInvulnerable) {
            leveled.heal()
            leveled.refillMana()
        }

        // Fix every item in their inventory
        for (item in event.getPlayer().inventory.contents)
            if (item != null && item.type != Material.AIR)
                SMPRPG.getService(ItemService::class.java).ensureItemStackUpdated(item)

        // Store first joined. No checking necessary.
        PlaytimeTracker.setFirstSeenIfNotPresent(event.getPlayer())
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onEntityDespawn(event: EntityRemoveFromWorldEvent) {
        removeEntity(event.getEntity().uniqueId)
    }

    // Handle spawning in the secondary name tags on players when they respawn
    @EventHandler(priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onRespawn(event: PlayerPostRespawnEvent) {
        val p = getPlayerInstance(event.getPlayer())
        p.updateNametag()
    }

    // Handle nametag updates and Damage popups for damage events
    @EventHandler(priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onHit(event: EntityDamageEvent) {
        if (event.isCancelled) return

        if (event.getEntity() !is LivingEntity) return

        // Update the entity's nametag
        val leveled = getEntityInstance(event.getEntity())
        object : BukkitRunnable() {
            override fun run() {
                leveled.updateNametag()
            }
        }.runTaskLater(plugin, 1L)
    }

    // Handle nametag updates and Damage popups for healing events
    @EventHandler(priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onRegen(event: EntityRegainHealthEvent) {
        if (event.isCancelled) return

        if (event.getEntity() !is LivingEntity) return

        // Update the entity's nametag
        val leveled = getEntityInstance(event.getEntity())
        object : BukkitRunnable() {
            override fun run() {
                leveled.updateNametag()
            }
        }.runTaskLater(plugin, 1L)
    }

    // Handle nametag updates potion effect events
    @EventHandler
    @Suppress("unused")
    private fun onPotionEffectUpdate(event: EntityPotionEffectEvent) {
        if (event.isCancelled) return

        if (event.getEntity() !is LivingEntity)
            return

        val leveled = getEntityInstance(event.getEntity() as LivingEntity)

        object : BukkitRunnable() {
            override fun run() {
                leveled.updateNametag()
            }
        }.runTaskLater(plugin, 1L)
    }

    // Handle nametag updates when we switch the item in our hand
    @EventHandler
    @Suppress("unused")
    private fun onSwitchItemInHand(event: PlayerItemHeldEvent) {
        val leveled = getEntityInstance(event.getPlayer())
        object : BukkitRunnable() {
            override fun run() {
                leveled.updateNametag()
            }
        }.runTaskLater(plugin, 1L)
    }

    // Handle nametag updates when we switch armor we are wearing
    @EventHandler
    @Suppress("unused")
    private fun onEquipArmor(event: PlayerArmorChangeEvent) {
        val leveled = getEntityInstance(event.getPlayer())
        object : BukkitRunnable() {
            override fun run() {
                leveled.updateNametag()
            }
        }.runTaskLater(plugin, 1L)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onDamageEntity(event: EntityDamageByEntityEvent) {

        if (event.getEntity() !is LivingEntity)
            return

        val living = event.entity as LivingEntity
        val leveled = getEntityInstance(living)
        var dealer = event.damageSource.causingEntity
        if (dealer == null)
            dealer = event.damager

        // Only consider players that dealt damage
        if (dealer !is Player)
            return

        // Don't "brighten" a player's nametag or track damage dealt to them
        if (living is Player)
            return

        // Show the nametag and track the damage dealt if it is a monster
        if (leveled is IDamageTrackable) leveled.getDamageTracker().addDamageDealtByEntity(dealer, event.damage.toInt())
        leveled.brightenNametag()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onSpawn(event: CreatureSpawnEvent) {

        // Ignore non natural spawns
        val naturalReasons = listOf<CreatureSpawnEvent.SpawnReason?>(
            CreatureSpawnEvent.SpawnReason.NATURAL,
            CreatureSpawnEvent.SpawnReason.DEFAULT
        )

        if (!naturalReasons.contains(event.spawnReason))
            return

        // DO NOT do this with bosses. This will break ender dragons.
        if (event.entity is Boss)
            return

        // Determine eligible creatures that can spawn in its place
        val choices: MutableList<CustomEntityType> = ArrayList<CustomEntityType>()
        for (type in CustomEntityType.entries)
            if (type.testNaturalSpawn(event.location))
                choices.add(type)

        // If no choices were found, that means this should be a vanilla mob.
        if (choices.isEmpty())
            return

        // We are making a new entity, so we no longer need to spawn the original one.
        event.isCancelled = true

        // Pick a random entity to make
        val newEntity = choices[(Math.random() * choices.size).toInt()]
        // Filter this enemy so it doesn't flood the world. Ensure there already aren't more than 10 present.
        // The main reason for this existing, is to prevent 1000 iron golems from spawning on the end island since
        // it is not considered a hostile mob.
        var count = 0
        for (nearby in event.location.getNearbyLivingEntities(CUSTOM_ENTITY_PROXIMITY_SPAWN_RADIUS.toDouble())) {
            val custom = getEntityInstance(nearby)
            if (custom is CustomEntityInstance<*> && custom.entityType.equals(newEntity))
                count++
        }
        if (count >= CUSTOM_ENTITY_PROXIMITY_SPAWN_LIMIT)
            return

        val entity = this.spawnCustomEntity(newEntity, event.location)
        entity.setup()
    }

    /**
     * Potential fix for maybe invincible mobs. Don't allow dead things to take damage.
     */
    @EventHandler
    @Suppress("unused")
    private fun onDamageWhileDead(event: EntityDamageEvent) {
        if (event.getEntity().isDead)
            event.isCancelled = true
    }

    companion object {

        // Scoreboard objective used to render HP under player names in the BELOW_NAME slot. See the
        // setup code for why this is a DUMMY objective populated only for players.
        const val BELOW_NAME_HEALTH_OBJECTIVE: String = "hp_objective"

        @JvmStatic
        val classNamespacedKey: NamespacedKey
            get() = NamespacedKey(plugin, ENTITY_CLASS_KEY)

        @JvmStatic
        val levelNamespacedKey: NamespacedKey
            get() = NamespacedKey(plugin, LEVEL_KEY_STRING)
    }
}
