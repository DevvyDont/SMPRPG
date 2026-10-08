package xyz.devvydont.smprpg.block.behaviors

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior
import net.momirealms.craftengine.bukkit.util.LocationUtils
import net.momirealms.craftengine.core.block.BlockDefinition
import net.momirealms.craftengine.core.block.UpdateFlags
import net.momirealms.craftengine.core.block.behavior.BlockBehavior
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
import net.momirealms.craftengine.core.block.property.IntegerProperty
import net.momirealms.craftengine.core.plugin.config.ConfigSection
import org.bukkit.block.BlockFace
import xyz.devvydont.smprpg.skills.listeners.FarmingExperienceListener
import xyz.devvydont.smprpg.util.world.ChunkUtil
import kotlin.math.min
import kotlin.random.Random

class TickAcceleratorBlockBehavior(blockDefinition: BlockDefinition,
                                   val chance : Float,
                                   val mode : String): BukkitBlockBehavior(blockDefinition) {

    override fun randomTick(thisBlock: Any?, args: Array<out Any?>?) {
        super.randomTick(thisBlock, args)
        val blockAbovePos = LocationUtils.above(args!![2]) as BlockPos
        val level = args[1] as ServerLevel
        val blockState = level.getBlockState(blockAbovePos)
        val bukkitBlock = level.world.getBlockAt(blockAbovePos.x, blockAbovePos.y, blockAbovePos.z)
        val block = blockState.block

        var numTicks = 0
        var chanceDupe = chance
        while (chanceDupe >= 1.0) {
            numTicks += 1
            chanceDupe -= 1.0f
        }
        if (Random.nextFloat() <= chanceDupe)
            numTicks += 1

        if (numTicks == 0) { return }

        when (mode) {
            "any", "all" -> { blockState.randomTick(level, blockAbovePos, RandomSource.create()) }
            "crop" -> {
                // For crops, our "chance" field becomes an extra growth stage tick.
                val ceBlock = BukkitAdaptor.adapt(bukkitBlock)
                if (ceBlock.isCustom) {
                    val ceBlockState = ceBlock.customBlockState() ?: return
                    var propertyToCheck = "age"
                    if (ceBlockState.customBlockState().hasProperty("age")) { propertyToCheck = "age" }
                    else if (ceBlockState.customBlockState().hasProperty("stage")) { propertyToCheck = "stage"}
                    else { return }

                    val ageProperty = ceBlockState.getProperty<Int>("age") as IntegerProperty
                    if (ceBlockState.get<Int>(ageProperty) + numTicks >= ageProperty.max)
                        ChunkUtil.markBlockSkillValid(bukkitBlock)
                    ceBlock.customBlockState()!!.setCustomBlockState(ceBlockState.customBlockState().withProperty(propertyToCheck, min(ceBlockState.get<Int>(ageProperty) + numTicks, ageProperty.max).toString()))
                }
                else {
                    val ceBlockState = ceBlock.blockState()
                    var propertyToCheck = "age"
                    if (ceBlockState.hasProperty("age")) { propertyToCheck = "age" }
                    else if (ceBlockState.hasProperty("stage")) { propertyToCheck = "stage"}
                    else { return }

                    val currAge = ceBlockState.getProperty<Int>(propertyToCheck)
                    val maxAge = FarmingExperienceListener.getVanillaCropMaxAge(bukkitBlock.type)
                    val newAge = min(currAge + numTicks, maxAge)
                    if (newAge >= maxAge)
                        ChunkUtil.markBlockSkillValid(bukkitBlock)
                    ceBlock.world().setBlockState(ceBlock.x(), ceBlock.y(), ceBlock.z(),
                        ceBlock.blockState().withProperty(propertyToCheck,
                            min(currAge + numTicks, maxAge).toString()),
                        UpdateFlags.UPDATE_ALL)

                }
            }
        }
    }

    companion object {
        val FACTORY = Factory()

        class Factory : BlockBehaviorFactory<BlockBehavior> {
            override fun create(block: BlockDefinition, section: ConfigSection): TickAcceleratorBlockBehavior {
                val chance : Float = section.getFloat("chance")
                val mode : String = section.getNonNullString("mode") // Any, Crop
                return TickAcceleratorBlockBehavior(block, chance, mode)
            }
        }
    }
}