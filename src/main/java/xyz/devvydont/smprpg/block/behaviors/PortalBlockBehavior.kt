package xyz.devvydont.smprpg.block.behaviors

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior
import net.momirealms.craftengine.core.block.BlockDefinition
import net.momirealms.craftengine.core.block.UpdateFlags
import net.momirealms.craftengine.core.block.behavior.BlockBehavior
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
import net.momirealms.craftengine.core.plugin.config.ConfigSection
import net.momirealms.craftengine.core.sound.SoundData
import net.momirealms.craftengine.core.util.Direction
import net.momirealms.craftengine.core.util.Key
import net.momirealms.craftengine.core.world.BlockPos
import net.momirealms.craftengine.libraries.nbt.CompoundTag
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.PortalType
import org.bukkit.block.BlockFace
import org.bukkit.craftbukkit.entity.CraftLivingEntity
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.event.entity.EntityPortalEnterEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.util.persistence.KeyStore


class PortalBlockBehavior(blockDefinition: BlockDefinition,
                          val dimensionKey : NamespacedKey,
                          val ambientSound : SoundData,
                          val transitionSound : SoundData,
                          val travelSound : SoundData,
                          val instantTravel : Boolean) : BukkitBlockBehavior(blockDefinition) {

    // BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston
    override fun neighborChanged(thisBlock: Any?, args: Array<out Any?>?) {
        super.neighborChanged(thisBlock, args)
        val world = (args!![1] as Level).world
        val blockPos = args[2] as net.minecraft.core.BlockPos
        val block = world.getBlockAt(Location(world, blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble()))
        var faces = mutableSetOf(BlockFace.UP, BlockFace.DOWN)
        var dir = CraftEngineBlocks.getCustomBlockState(block)!!.customBlockState().getProperty<Direction>("facing")
        if (dir != null) {
            if (dir == Direction.WEST || dir == Direction.EAST) {
                faces.add(BlockFace.NORTH)
                faces.add(BlockFace.SOUTH)
            }
            else {
                faces.add(BlockFace.EAST)
                faces.add(BlockFace.WEST)
            }
        }

        var gottaBreak = false
        val ceBlock = BukkitAdaptor.adapt(block)
        if (ceBlock.customBlockState()!!.customBlockState().getProperty<Boolean>("placed") == false) {
            val tag = CompoundTag()
            tag.putBoolean("placed", true)
            CraftEngineBlocks.place(block.location, ceBlock.customBlockState()!!.with(tag), UpdateFlags.UPDATE_NONE, false)
            return
        }

        for (face in faces) {
            val relBlock = block.getRelative(face)
            val relCeBlock = BukkitAdaptor.adapt(relBlock)
            val relBlockKey = relCeBlock.blockState().ownerId()
            if (relBlockKey == Key("minecraft", "air")) {
                gottaBreak = true
            }
            if (gottaBreak)
                continue
        }
        if (gottaBreak) {
            CraftEngineBlocks.remove(block, null, false, false, true)
        }
    }

    override fun entityInside(thisBlock: Any?, args: Array<out Any?>?) {
        super.entityInside(thisBlock, args)
        if (!instantTravel) {  // This should be instant travel, it isn't currently for debug purposes
            val entity = args!![3] as Entity
            val craftEntity = entity.bukkitEntity
            val nms_blockPos = args[2] as net.minecraft.core.BlockPos
            val blockLoc = Location((args[1] as Level).world, nms_blockPos.x.toDouble(), nms_blockPos.y.toDouble(), nms_blockPos.z.toDouble())

            // TODO: Very temporary, find a proper way to teleport them to a safe location.
            val overworld = Bukkit.getWorld(NamespacedKey("minecraft", "overworld"))
            val loc = craftEntity.location.clone()
            if (loc.world != overworld) {
                if (craftEntity is CraftPlayer)
                    craftEntity.teleport(overworld!!.spawnLocation)
                else {
                    val spawnLoc = (craftEntity as CraftPlayer).respawnLocation ?: overworld!!.spawnLocation
                    craftEntity.teleport(spawnLoc)
                }
            }
            else {
                val destWorld = Bukkit.getWorld(dimensionKey)
                loc.world = destWorld
                if (dimensionKey == KeyStore.DIM_AETHER) {
                    loc.y = destWorld!!.maxHeight + 10.0
                    val portalEvent = EntityPortalEnterEvent(craftEntity, blockLoc, PortalType.CUSTOM)
                    portalEvent.callEvent()
                    if (!portalEvent.isCancelled) {
                        craftEntity.teleport(loc)
                        if (craftEntity is CraftLivingEntity) {
                            craftEntity.addPotionEffect(
                                PotionEffect(
                                    PotionEffectType.SLOW_FALLING,
                                    20 * 20,
                                    0,
                                    true,
                                    true
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun randomTick(thisBlock: Any?, args: Array<out Any?>?) {
        super.tick(thisBlock, args)

        val serverLevel = args!![1] as ServerLevel

        val blockPos = args[2] as net.minecraft.core.BlockPos
        val ceBlockPos = BlockPos.of(blockPos.asLong())

        val world = serverLevel.world

        val loc = Location(world, ceBlockPos.x.toDouble(), ceBlockPos.y.toDouble(), ceBlockPos.z.toDouble())

        world.playSound(loc, ambientSound.id.toString(), ambientSound.volume.get(), ambientSound.pitch.get())
    }

    companion object {
        val FACTORY = Factory()

        class Factory : BlockBehaviorFactory<BlockBehavior> {
            override fun create(block: BlockDefinition, section: ConfigSection): PortalBlockBehavior {
                val dimensionComps = section.getString("dimension", "minecraft:overworld").split(":")
                val dimensionKey : NamespacedKey = NamespacedKey(dimensionComps[0], dimensionComps[1])
                val sounds = section.getSection("sounds")
                val ambientSound = SoundData.of(sounds.getKey("ambient-sound"), SoundData.SoundValue.FIXED_1, SoundData.SoundValue.fixed(1f))
                val transitionSound = SoundData.of(sounds.getKey("transition-sound"), SoundData.SoundValue.FIXED_1, SoundData.SoundValue.fixed(1f))
                val travelSound = SoundData.of(sounds.getKey("travel-sound"), SoundData.SoundValue.FIXED_1, SoundData.SoundValue.fixed(1f))
                val instantTravel : Boolean = section.getBoolean("instant", false)
                return PortalBlockBehavior(block, dimensionKey, ambientSound, transitionSound, travelSound, instantTravel)
            }
        }
    }
}