package xyz.devvydont.smprpg.services

import com.destroystokyo.paper.ParticleBuilder
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.block.BlockType
import org.bukkit.block.data.Ageable
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.block.CraftEngineBlockEnums
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.farming.CropCritter
import xyz.devvydont.smprpg.items.blueprints.block.SuperSoil
import xyz.devvydont.smprpg.skills.listeners.FarmingExperienceListener
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*
import net.momirealms.craftengine.core.util.Key as CEKey

class CropCritterService: IService, Listener {

    val activeCritters = mutableMapOf<UUID, CropCritter<*>>()
    val spawningCritters = mutableListOf<UUID>()

    override fun setup() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun cleanup() {
        HandlerList.unregisterAll(this)
    }

    /**
     * Spawns Crop Critters based on Player's uprooting stat
     */
    @EventHandler
    fun onBreakCrop(event: BlockBreakEvent) {
        if (spawningCritters.contains(event.player.uniqueId)) return
        if (activeCritters.getOrDefault(event.player.uniqueId, null) != null) {
            val critter = activeCritters.get(event.player.uniqueId)
            if (event.player.location.distance(critter!!.entity!!.location) < 100) {
                event.player.sendMessage(ComponentUtils.error("The critter you dug up is scaring others away!"))
                event.player.playSound(event.player.location, Sound.ENTITY_SILVERFISH_AMBIENT, 1f, 1.25f)
                return
            }
        }

        val attrService = SMPRPG.getService(AttributeService::class.java)
        var playerUprooting = attrService.getOrCreateAttribute(event.player, AttributeWrapper.CRITTER_CHANCE).value
        playerUprooting += SOIL_TO_UPROOTING.getOrDefault(BukkitAdaptor.adapt(event.block.getRelative(BlockFace.DOWN)).id(), 0.0)
        if (playerUprooting > 0.0) {
            val ceBlock = BukkitAdaptor.adapt(event.block)
            val critterType = CROP_TO_CRITTER.getOrDefault(ceBlock.id(), null)
            if (critterType != null) {
                // First check that our crop is mature.
                if (ceBlock.isCustom) {
                    val age = ceBlock.customBlockState()!!.customBlockState().getProperty<Int>("age")
                    if (age != null) {
                        val maxAge = FarmingExperienceListener.getCustomCropMaxAge(ceBlock.id())
                        if (age != maxAge)
                            return
                    }
                }
                else if (event.block.blockData is Ageable) {
                    val data = event.block.blockData as Ageable
                    if (data.age != data.maximumAge)
                        return
                }

                val uprootingReq = CRITTER_UPROOTING_REQ[critterType]
                val nextCritterTimestamp = event.player.persistentDataContainer.getOrDefault(CRITTER_TIMESTAMP_KEY, PersistentDataType.LONG, 0)
                val currTime = System.currentTimeMillis()
                val critterPity = if (nextCritterTimestamp - currTime < 0L) 0.0 else ((nextCritterTimestamp - currTime) / TIME_BETWEEN_CRITTER_SPAWNS.toDouble())
                val spawnRoll = Math.random() * playerUprooting * (1.0 - critterPity)
                if (spawnRoll >= uprootingReq!!) {
                    spawningCritters.add(event.player.uniqueId)
                    event.player.persistentDataContainer.set(CRITTER_TIMESTAMP_KEY, PersistentDataType.LONG, currTime + TIME_BETWEEN_CRITTER_SPAWNS)
                    object : BukkitRunnable() {
                        private var clock = 0
                        private var location = event.block.location.add(0.5, 0.0, 0.5)

                        override fun run() {
                            val player = event.player
                            val locCopy = location
                            val randRoll = Math.random()
                            if (clock % 2 == 0) {
                                player.world.playSound(player.location, Sound.BLOCK_ROOTED_DIRT_BREAK, 1f, (1f + randRoll.toFloat()))
                                player.world.playSound(player.location, Sound.BLOCK_ROOTED_DIRT_BREAK, 1f, (0.5f + randRoll.toFloat()))
                                val crumble = ParticleBuilder(Particle.BLOCK_CRUMBLE)
                                    .location(locCopy)
                                    .count(24)
                                    .data(BlockType.DIRT.createBlockData())
                                    .offset(randRoll / 4.0, 0.0, randRoll / 4.0)
                                    .receivers(32, true)
                                    .spawn()
                            }
                            clock++
                            if (clock > 20) {
                                val critter = SMPRPG.getService(EntityService::class.java).spawnCustomEntity(critterType, location) as CropCritter
                                critter.spawnedBy = event.player.uniqueId
                                val team: Team = CropCritter.team
                                team.addEntity(critter.entity as LivingEntity)
                                critter.entity!!.isGlowing = true
                                activeCritters.put(critter.spawnedBy!!, critter)
                                spawningCritters.remove(event.player.uniqueId)
                                player.world.playSound(location, Sound.BLOCK_CROP_BREAK, 1f, 0.5f)
                                this.cancel()
                            }
                        }
                    }.runTaskTimer(plugin, TickTime.INSTANTANEOUSLY, TickTime.INSTANTANEOUSLY)
                }
            }
        }
    }

    /**
     * Prevent crop critters from trampling farmland
     */
    @EventHandler
    fun onTrampleCrop(event: EntityInteractEvent) {
        if (SMPRPG.getService(EntityService::class.java).getEntityInstance(event.entity) is CropCritter &&
            event.block.type == Material.FARMLAND) { event.isCancelled = true }
    }

    companion object {
        val CROP_TO_CRITTER = mutableMapOf(
            Pair(CEKey.of("minecraft:potatoes"), CustomEntityType.EARTHWORM),
            Pair(CEKey.of("minecraft:wheat"), CustomEntityType.MITE),
            Pair(CraftEngineBlockEnums.ONION_PLANT.key, CustomEntityType.OGRELING),
            Pair(CEKey.of("minecraft:carrots"), CustomEntityType.CARROT_CANNIBAL),
            Pair(CEKey.of("minecraft:melon"), CustomEntityType.MELON_MASHER),
        )
        val CRITTER_UPROOTING_REQ = mutableMapOf(
            Pair(CustomEntityType.EARTHWORM, 200),
            Pair(CustomEntityType.MITE, 200),
            Pair(CustomEntityType.OGRELING, 200),
            Pair(CustomEntityType.CARROT_CANNIBAL, 200),
            Pair(CustomEntityType.MELON_MASHER, 200),
        )
        val SOIL_TO_UPROOTING = mutableMapOf(
            Pair(CEKey.of("minecraft:farmland"), 0.0),
            Pair(CraftEngineBlockEnums.SUPER_SOIL.key, SuperSoil.UPROOTING_BONUS),
        )
        val CRITTER_TIMESTAMP_KEY: NamespacedKey = NamespacedKey(plugin, "next_crop_critter_timestamp")
        val TIME_BETWEEN_CRITTER_SPAWNS: Long = 60 * 5 * 1000L // 5 Minutes
    }
}