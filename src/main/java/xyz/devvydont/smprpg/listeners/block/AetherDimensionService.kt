package xyz.devvydont.smprpg.listeners.block

import io.papermc.paper.entity.TeleportFlag
import net.kyori.adventure.text.Component
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import net.momirealms.craftengine.libraries.nbt.CompoundTag
import net.momirealms.craftengine.libraries.nbt.StringTag
import net.momirealms.craftengine.libraries.nbt.Tag
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.inventory.MenuType
import org.bukkit.scheduler.BukkitRunnable
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.block.CraftEngineBlockEnums
import xyz.devvydont.smprpg.gui.player.InterfaceStats
import xyz.devvydont.smprpg.gui.player.InterfaceWardrobe
import xyz.devvydont.smprpg.gui.player.MenuStatsRoot
import xyz.devvydont.smprpg.gui.player.SubmenuStatOverview
import xyz.devvydont.smprpg.listeners.advancement.AdvancementTriggerListener
import xyz.devvydont.smprpg.services.IService
import xyz.devvydont.smprpg.util.craftengine.CraftEngineHelpers
import xyz.devvydont.smprpg.util.particles.ParticleUtil
import xyz.devvydont.smprpg.util.persistence.KeyStore
import java.util.*

class AetherDimensionService : IService, Listener {
    private val activeTransitions : MutableMap<UUID, NamespacedKey> = mutableMapOf()

    override fun setup() {
        return
    }

    override fun cleanup() {
        return
    }

    /**
     * Handle falling into overworld transition
     */
    @EventHandler
    private fun onEnterVoid(event: PlayerMoveEvent) {
        val player = event.player
        val location = player.location
        if (location.world.key != AETHER_DIM_KEY) return

        // If we are below world floor
        if (player.location.y <= (player.world.minHeight - 16)) {
            val newLoc = location.clone()
            newLoc.world = Bukkit.getWorld(OVERWORLD_DIM_KEY)
            newLoc.y = newLoc.world.maxHeight.toDouble()
            player.teleport(newLoc, TeleportFlag.Relative.VELOCITY_Y)
            AdvancementTriggerListener.grantSimpleAdvancement(player, NamespacedKey(plugin, "aether/fall_from_aether"), "fall_from_aether")
        }
    }

    /**
     * Lava converts directly to obsidian when placed in the Aether.
     */
    @EventHandler
    private fun onEmptyLavaBucket(event: PlayerBucketEmptyEvent) {
        if (event.block.world.key == AETHER_DIM_KEY) {
            if (event.bucket == Material.LAVA_BUCKET)
                // Set the block to obsidian one tick later.
                object : BukkitRunnable() {
                    override fun run() {
                        val world = event.block.world
                        world.playSound(event.block.location, Sound.BLOCK_LAVA_EXTINGUISH, 1f, 1f)
                        CraftEngineBlocks.place(event.block.location, CraftEngineBlockEnums.AEROGEL.key, false)
                        AdvancementTriggerListener.grantSimpleAdvancement(event.player, NamespacedKey(plugin, "aether/make_aerogel"), "use_lava_bucket")
                        val particleLoc = event.block.location.clone()
                        particleLoc.x = particleLoc.x + 0.5
                        particleLoc.z = particleLoc.z + 0.5
                        world.spawnParticle(
                            Particle.DUST,
                            event.block.location,
                            50,
                            0.5,
                            0.5,
                            0.5,
                            0.1,
                            Particle.DustOptions(Color.BLACK, 2.0f)
                        )
                    }
                }.runTaskLater(plugin, 1L)
        }
    }

    /**
     * Fire extinguishes automatically in the Aether
     */
    @EventHandler
    private fun onFirePlace(event: BlockPlaceEvent) {
        val block = event.block
        if (block.world.key == AETHER_DIM_KEY) {
            if (event.block.type == Material.FIRE) {
                event.isCancelled = true
                val world = block.world
                world.playSound(event.block.location, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 2f)
                val particleLoc = event.block.location.clone()
                particleLoc.x = particleLoc.x + 0.5
                particleLoc.z = particleLoc.z + 0.5
                world.spawnParticle(
                    Particle.DUST,
                    event.block.location,
                    25,
                    0.5,
                    0.5,
                    0.5,
                    0.1,
                    Particle.DustOptions(Color.BLACK, 1.0f)
                )
            }
        }
    }

    /**
     * Torches extinguish automatically in the Aether
     */
    @EventHandler
    private fun onTorchPlace(event: BlockPlaceEvent) {
        val block = event.block

        fun playExtinguishEffect() {
            val world = block.world
            world.playSound(event.block.location, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 2f)
            val particleLoc = event.block.location.clone()
            particleLoc.x = particleLoc.x + 0.5
            particleLoc.z = particleLoc.z + 0.5
            particleLoc.y = particleLoc.y + 0.8
            world.spawnParticle(
                Particle.DUST,
                event.block.location,
                25,
                0.5,
                0.5,
                0.5,
                0.1,
                Particle.DustOptions(Color.BLACK, 1.0f)
            )
        }

        if (block.world.key == AETHER_DIM_KEY) {
            when (event.block.type) {
                Material.TORCH ->
                {
                    playExtinguishEffect()
                    CraftEngineBlocks.place(event.block.location, CraftEngineBlockEnums.EXTINGUISHED_TORCH.key, false)
                }
                Material.WALL_TORCH ->
                {
                    playExtinguishEffect()
                    val blockState = event.block.state
                    val stateData = blockState.blockData.asString.substringAfter('[').dropLast(1)
                    val stateComps = stateData.split("=")
                    val properties = CompoundTag(mutableMapOf(Pair(stateComps[0], StringTag(stateComps[1]) as Tag)))
                    CraftEngineBlocks.place(event.block.location, CraftEngineBlockEnums.EXTINGUISHED_WALL_TORCH.key, properties, false)
                }
                else -> {}
            }
        }
    }

    /**
     * No weather in the Aether (would be weird to have rain above the clouds)
     */
    @EventHandler
    private fun onWeatherChange(event: WeatherChangeEvent) {
        if (event.world == Bukkit.getWorld(KeyStore.DIM_AETHER)) event.isCancelled = true
    }

    /**
     * This behemoth handles portal ignition logic.
     */
    @EventHandler
    private fun onWaterPlace(event: PlayerBucketEmptyEvent) {
        val block = event.block
        val clickedBlock = event.blockClicked
        if (clickedBlock.type != Material.GLOWSTONE) return
        if (block.world.key == KeyStore.DIM_END) return

        fun findPortalAxis(clickedBlock: Block) : Axis {
            val world = block.world
            val bl = clickedBlock.location
            var currAxis = Axis.Y
            if (world.getBlockAt((bl.x+1).toInt(), bl.y.toInt(), bl.z.toInt()).type == Material.GLOWSTONE ||
                world.getBlockAt((bl.x-1).toInt(), bl.y.toInt(), bl.z.toInt()).type == Material.GLOWSTONE) {
                currAxis = Axis.X
            }
            if (world.getBlockAt(bl.x.toInt(), (bl.y).toInt(), bl.z.toInt()+1).type == Material.GLOWSTONE ||
                world.getBlockAt(bl.x.toInt(), (bl.y).toInt(), bl.z.toInt()-1).type == Material.GLOWSTONE) {
                if (currAxis == Axis.X) {
                    // Predicament! We have blocks everywhere around this portal. Prioritize player facing instead.
                    val yaw = event.player.location.yaw
                    if (((yaw <= -45.0) && (yaw >= -135)) || // East
                        ((yaw >= 45) && (yaw <= 135)))  // West
                        return Axis.X
                    else
                        return Axis.Z
                }
                currAxis = Axis.Z
            }
            return currAxis
        }

        val axis = findPortalAxis(clickedBlock)
        if (axis == Axis.Y) return  // Invalid portal

        fun isValidPortal(block: Block, startingBlock: Block, axis: Axis, alreadyFound: Set<Block>?, isFirst: Boolean) : Boolean {
            var found: Set<Block>?
            if (alreadyFound == null) found = mutableSetOf()
            else found = alreadyFound.toMutableSet()

            // Reached end of iteration through frame
            if (block.location.equals(startingBlock.location) && !isFirst) {
                val startY = startingBlock.y
                var currMaxY = startingBlock.location.world.minHeight
                for (fb in found) {
                    if (fb.y > currMaxY)
                        currMaxY = fb.y
                }
                // If portal was ignited from the top, it's invalid even if the shape is.
                if (startY >= currMaxY)
                    return false
                else return true
            }

            val checked = found
            if (axis == Axis.X) {
                val nearbyBlocks = Arrays.stream(
                    arrayOf<Block?>(
                        block.location.add(1.0, 0.0, 0.0).block,
                        block.location.add(1.0, 1.0, 0.0).block,
                        block.location.add(1.0, -1.0, 0.0).block,
                        block.location.add(0.0, 1.0, 0.0).block,
                        block.location.add(0.0, -1.0, 0.0).block,
                        block.location.add(-1.0, 0.0, 0.0).block,
                        block.location.add(-1.0, 1.0, 0.0).block,
                        block.location.add(-1.0, -1.0, 0.0).block
                    )
                ).filter { b: Block? -> !checked.contains(b) }.toArray()
                for (nearbyBlock in nearbyBlocks) {
                    val nb = nearbyBlock as Block?
                    if (nb == null) continue
                    if (nb.type == Material.GLOWSTONE && !found.contains(nb)) {
                        found += nb
                        return isValidPortal(nb, startingBlock, axis, found, false)
                    }
                }
            }
            else if (axis == Axis.Z) {
                val nearbyBlocks = Arrays.stream(
                    arrayOf<Block?>(
                        block.location.add(0.0, 0.0, 1.0).block,
                        block.location.add(0.0, 1.0, 1.0).block,
                        block.location.add(0.0, -1.0, 1.0).block,
                        block.location.add(0.0, 1.0, 0.0).block,
                        block.location.add(0.0, -1.0, 0.0).block,
                        block.location.add(0.0, 0.0, -1.0).block,
                        block.location.add(0.0, 1.0, -1.0).block,
                        block.location.add(0.0, -1.0, -1.0).block
                    )
                ).filter({ b -> !checked.contains(b) }).toArray()
                for (nearbyBlock in nearbyBlocks) {
                    val nb = nearbyBlock as Block?
                    if (nb == null) continue
                    if (nb.type == Material.GLOWSTONE && !found.contains(nb)) {
                        found += nb
                        return isValidPortal(nb, startingBlock, axis, found, false)
                    }
                }
            }
            else throw IllegalArgumentException("Axis cannot be Y")
            return false
        }

        if (isValidPortal(clickedBlock, clickedBlock, axis, null, true)) {
            event.isCancelled = true

            fun fillPortalBlocks(axis: Axis, currBlock: Block, facesToExpand: MutableSet<BlockFace>, filledBlocks: MutableSet<Location>) {
                if (currBlock.location in filledBlocks) return

                var canReplace = false
                var isPortal = false
                if (CraftEngineBlocks.isCustomBlock(currBlock)) {
                    val blockKey = CraftEngineHelpers.getBlockKey(currBlock)
                    if (blockKey == CraftEngineBlockEnums.AETHER_PORTAL.key) {
                        canReplace = true
                        isPortal = true
                    }
                }
                else when (currBlock.type) {
                    Material.WATER, Material.AIR -> canReplace = true
                    else -> {}
                }

                if (canReplace) {
                    when (axis) {
                        Axis.X -> {
                            if (filledBlocks.add(currBlock.location)) {
                                val properties = CompoundTag(mutableMapOf(Pair("facing", StringTag("north") as Tag)))
                                CraftEngineBlocks.place(
                                    currBlock.location,
                                    CraftEngineBlockEnums.AETHER_PORTAL.key,
                                    properties,
                                    false
                                )
                            } else return

                            for (face in facesToExpand) {
                                fillPortalBlocks(axis, currBlock.getRelative(face), facesToExpand, filledBlocks)
                            }
                        }
                        Axis.Z -> {
                            if (filledBlocks.add(currBlock.location)) {
                                val properties = CompoundTag(mutableMapOf(Pair("facing", StringTag("east") as Tag)))
                                CraftEngineBlocks.place(
                                    currBlock.location,
                                    CraftEngineBlockEnums.AETHER_PORTAL.key,
                                    properties,
                                    false
                                )
                            } else return

                            for (face in facesToExpand) {
                                fillPortalBlocks(axis, currBlock.getRelative(face), facesToExpand, filledBlocks)
                            }
                        }
                        else -> {}
                    }
                }
            }
            var facesToExpand = mutableSetOf<BlockFace>()
            when (axis) {
                Axis.X -> {
                    for (face in setOf(BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN)) {
                        if (CraftEngineBlocks.isCustomBlock(block)) {
                            val blockKey = CraftEngineHelpers.getBlockKey(block)
                            if (blockKey == CraftEngineBlockEnums.AETHER_PORTAL.key) {
                                facesToExpand.add(face)
                            }
                        } else when (block.type) {
                            Material.WATER, Material.AIR -> facesToExpand.add(face)
                            else -> {}
                        }
                    }
                }
                Axis.Z -> {
                    for (face in setOf(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN)) {
                        if (CraftEngineBlocks.isCustomBlock(block)) {
                            val blockKey = CraftEngineHelpers.getBlockKey(block)
                            if (blockKey == CraftEngineBlockEnums.AETHER_PORTAL.key) {
                                facesToExpand.add(face)
                            }
                        } else when (block.type) {
                            Material.WATER, Material.AIR -> facesToExpand.add(face)
                            else -> {}
                        }
                    }
                }
                else -> {}
            }
            fillPortalBlocks(axis, block, facesToExpand, mutableSetOf())
        }
    }

    companion object {
        val AETHER_DIM_KEY = NamespacedKey(plugin, "the_aether")
        val OVERWORLD_DIM_KEY = NamespacedKey("minecraft", "overworld")
    }

}