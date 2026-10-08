package xyz.devvydont.smprpg.block.behaviors

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior
import net.momirealms.craftengine.bukkit.block.behavior.BukkitFallableBlock
import net.momirealms.craftengine.core.block.BlockDefinition
import net.momirealms.craftengine.core.block.behavior.BlockBehavior
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
import net.momirealms.craftengine.core.plugin.config.ConfigSection
import net.momirealms.craftengine.core.sound.SoundData
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.block.data.CraftBlockData
import org.bukkit.craftbukkit.entity.CraftFallingBlock
import xyz.devvydont.smprpg.events.block.AscendingBlockRegisterEvent
import java.util.*


class AscendingBlockBehavior(blockDefinition: BlockDefinition,
                             val hurtAmount: Float,
                             val maxHurt: Int,
                             landSound: SoundData?,
                             destroySound: SoundData?): BukkitBlockBehavior(blockDefinition), BukkitFallableBlock {

    override fun onPlace(thisBlock: Any?, args: Array<Any?>) {
        val world = args[1] as ServerLevel
        val blockPos = args[2]
        world.scheduleTick(blockPos as BlockPos, thisBlock as Block, 2)
    }

    override fun updateShape(thisBlock: Any?, args: Array<Any?>): Any? {
        val world = args[`updateShape$level`] as ServerLevel
        val blockPos = args[`updateShape$blockPos`]
        world.scheduleTick(blockPos as BlockPos, thisBlock as Block, 2)
        return args[0]
    }

    override fun tick(thisBlock: Any, args: Array<out Any?>?) {
        val net_serverLevel = args!![1] as ServerLevel
        val net_blockPos = args[2] as BlockPos

        val world = net_serverLevel.world
        val loc = Location(world, net_blockPos.x.toDouble(), net_blockPos.y.toDouble(), net_blockPos.z.toDouble())

        val aboveLoc = BlockPos(loc.x.toInt(), loc.y.toInt() + 1, loc.z.toInt())
        val aboveState = net_serverLevel.getBlockState(aboveLoc)
        val isFree = net.minecraft.world.level.block.FallingBlock.isFree(aboveState)
        if (!isFree) return

        val net_blockState = args[0] as BlockState
        val startLoc = loc.clone()
        startLoc.x += 0.5
        startLoc.y += 0.0784
        startLoc.z += 0.5
        val fallingBlock = world.spawnFallingBlock(startLoc, CraftBlockData.createData(net_blockState))
        net_serverLevel.world.setBlockData(loc, Material.AIR.createBlockData())
        if (hurtAmount > 0 && maxHurt > 0) (fallingBlock as CraftFallingBlock).handle.setHurtsEntities(hurtAmount, maxHurt)

        AscendingBlockRegisterEvent(fallingBlock).callEvent()
    }

    companion object {
        val FACTORY = Factory()

        class Factory : BlockBehaviorFactory<BlockBehavior> {
            override fun create(block: BlockDefinition, section: ConfigSection): AscendingBlockBehavior {
                val hurtAmount = section.getFloat("hurt-amount", -1f)
                val hurtMax = section.getInt("max-hurt", -1)
                val sounds = section.getSection("sounds")
                var landSound: SoundData? = null
                var destroySound: SoundData? = null
                if (sounds != null) {
                    landSound = sounds.getValue(
                        "land",
                        { v ->
                            SoundData.fromConfig(
                                v,
                                SoundData.SoundValue.FIXED_1,
                                SoundData.SoundValue.RANGED_0_9_1
                            )
                        })
                    destroySound = sounds.getValue(
                        "destroy",
                        { v ->
                            SoundData.fromConfig(
                                v,
                                SoundData.SoundValue.FIXED_1,
                                SoundData.SoundValue.RANGED_0_9_1
                            )
                        })
                }
                return AscendingBlockBehavior(block, hurtAmount, hurtMax, landSound, destroySound)
            }
        }
    }
}