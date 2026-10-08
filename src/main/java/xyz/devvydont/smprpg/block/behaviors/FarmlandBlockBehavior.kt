package xyz.devvydont.smprpg.block.behaviors

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import net.momirealms.craftengine.bukkit.block.behavior.AbstractCanSurviveBlockBehavior
import net.momirealms.craftengine.bukkit.block.behavior.FenceGateBlockBehavior
import net.momirealms.craftengine.bukkit.util.BlockStateUtils
import net.momirealms.craftengine.core.block.BlockDefinition
import net.momirealms.craftengine.core.block.behavior.BlockBehavior
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
import net.momirealms.craftengine.core.block.behavior.PrioritizedFallOnHandler
import net.momirealms.craftengine.core.plugin.config.ConfigSection
import net.momirealms.craftengine.core.util.Key
import net.momirealms.craftengine.libraries.nbt.CompoundTag
import net.momirealms.craftengine.libraries.nbt.IntTag
import net.momirealms.craftengine.libraries.nbt.Tag
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.block.impl.CraftFenceGate
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.player.PlayerInteractEvent
import xyz.devvydont.smprpg.util.craftengine.CraftEngineHelpers
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class FarmlandBlockBehavior(blockDefinition: BlockDefinition,
                            val revertBlockKey: Key,
                            val tagsCanHydrate: Set<Key>,
                            val statesCanHydrate: Set<Key>): AbstractCanSurviveBlockBehavior(blockDefinition, 0), PrioritizedFallOnHandler {

    override fun canSurvive(
        thisBlock: Any?,
        state: Any?,
        world: Any?,
        blockPos: Any?
    ): Boolean {
        val craftWorld = (world as ServerLevel).world
        if (!canFarmlandSurvive(arrayOf(craftWorld, blockPos)))
            revertToDirt(CraftEngineHelpers.getLocationFromBlockPos(blockPos as BlockPos, craftWorld))  // We aren't going to break the block, so just revert it and always return true.
        return true
    }

    fun canFarmlandSurvive(args: Array<out Any?>?) : Boolean {
        val craftWorld = args!![0] as CraftWorld
        val nms_pos = args[1] as BlockPos

        val bukkitBlock = craftWorld.getBlockAt(CraftEngineHelpers.getLocationFromBlockPos(nms_pos, craftWorld))
        val blockLoc = bukkitBlock.location
        val blockAbove = blockLoc.world.getBlockAt(blockLoc.x.toInt(), (blockLoc.y + 1).toInt(), blockLoc.z.toInt())

        if (CraftEngineBlocks.isCustomBlock(blockAbove)) {
            val nms_AboveState = BlockStateUtils.getBlockState(blockAbove) as BlockState  // We are going to use NMS state to work with lower level settings
            val isGate = CraftEngineBlocks.getCustomBlockState(blockAbove)!!.behavior().getFirst(FenceGateBlockBehavior::class.java) != null
            return (!nms_AboveState.isSolid || isGate)
        }
        else {
            val aboveState = blockAbove.state
            return (!aboveState.type.isSolid || aboveState.blockData is CraftFenceGate || aboveState.type == Material.MOVING_PISTON)
        }
    }

    fun shouldMaintainFarmland(block: Block) : Boolean {
        return BlockStateUtils.isTag(block.blockData, Key.of("minecraft:maintains_farmland"))
    }

    fun revertToDirt(blockLoc: Location) {
        CraftEngineBlocks.place(blockLoc, revertBlockKey, false)
    }

    fun isNearMoisture(blockLoc: Location) : Boolean {
        val radius = 4
        for (x in -radius..radius) {
            for (z in -radius..radius) {
                val blockAt = blockLoc.world.getBlockAt((blockLoc.x + x).toInt(), blockLoc.y.toInt(), (blockLoc.z + z).toInt())
                val ceBlock = BukkitAdaptor.adapt(blockAt)
                for (tag in tagsCanHydrate)
                    if (BlockStateUtils.isTag(blockAt.blockData, tag)) return true
                for (state in statesCanHydrate)
                    if (ceBlock.id().equals(state)) return true
            }
        }
        return false
    }

    // BlockState state, ServerLevel level, BlockPos pos, RandomSource random
    override fun randomTick(thisBlock: Any?, args: Array<out Any?>?) {
        val nms_state = args!![0] as BlockState
        val nms_level = args[1] as ServerLevel
        val nms_pos = args[2] as BlockPos

        val bukkitBlock = nms_level.world.getBlockAt(CraftEngineHelpers.getLocationFromBlockPos(nms_pos, nms_level.world))
        if (CraftEngineBlocks.isCustomBlock(bukkitBlock)) {
            val ceBlockState = CraftEngineBlocks.getCustomBlockState(bukkitBlock)!!.customBlockState()
            var moisture = ceBlockState.getProperty<Int>("moisture")
            val blockLoc = CraftEngineHelpers.getLocationFromBlockPos(nms_pos, nms_level.world)
            if (isNearMoisture(blockLoc) || (Key.of("minecraft:water") in statesCanHydrate && nms_level.isRainingAt(nms_pos))) {
                moisture = min(moisture + 1, 7)
            }
            else {
                if (moisture == 0) {
                    if (!shouldMaintainFarmland(bukkitBlock.getRelative(BlockFace.UP))) {
                        revertToDirt(bukkitBlock.location)
                        return
                    }
                }
                else moisture = max(moisture - 1, 0)
            }
            val properties = CompoundTag(mutableMapOf(Pair("moisture", IntTag(moisture) as Tag)))
            CraftEngineBlocks.place(blockLoc, blockDefinition.id(), properties, false)
        }
    }

    // 1.21.5+ Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance
    override fun fallOn(thisBlock: Any?, args: Array<out Any?>?) {
        super.fallOn(thisBlock, args)
        val nms_level = args!![0] as Level
        val nms_pos = args[2] as BlockPos
        val nms_entity = args[3] as Entity
        val fallDist = args[4] as Double

        val craftEntity = nms_entity.bukkitEntity
        val craftWorld = craftEntity.location.world
        val craftBlock = nms_level.world.getBlockAt(CraftEngineHelpers.getLocationFromBlockPos(nms_pos, nms_level.world))

        if (Random.nextFloat().toDouble() < fallDist - 0.5 &&
            craftEntity is org.bukkit.entity.LivingEntity &&
            (craftEntity is CraftPlayer || craftWorld.gameRules.contains<Any>(org.bukkit.GameRules.MOB_GRIEFING)) &&
            craftEntity.boundingBox.widthX * craftEntity.boundingBox.widthZ * craftEntity.boundingBox.height > 0.512f
        ) {
            var event: org.bukkit.event.Cancellable
            if (craftEntity is CraftPlayer) {
                event = PlayerInteractEvent(craftEntity, Action.PHYSICAL, null, craftBlock, BlockFace.UP)
            }
            else {
                event = EntityInteractEvent(craftEntity, craftBlock)
            }
            event.callEvent()

            if (event.isCancelled)
                return

            revertToDirt(CraftEngineHelpers.getLocationFromBlockPos(nms_pos, craftEntity.world))
        }
    }

    override fun tick(thisBlock: Any?, args: Array<out Any?>?) {
        val world = (args!![1] as ServerLevel).world
        if (!canFarmlandSurvive(arrayOf(world, args[2] as BlockPos))) {
            val loc = CraftEngineHelpers.getLocationFromBlockPos(args[2] as BlockPos, world)
            revertToDirt(loc)
        }
    }

    companion object {
        val FACTORY = Factory()

        class Factory : BlockBehaviorFactory<BlockBehavior> {
            override fun create(block: BlockDefinition, section: ConfigSection): FarmlandBlockBehavior {
                val revertBlockKey = Key.of(section.getNonNullString("reverted-block"))
                val tagsCanHydrate = mutableSetOf<Key>()
                val statesCanHydrate = mutableSetOf<Key>()
                for (tagStr in section.getStringList("hydration-block-tags")) {
                   tagsCanHydrate += Key.of(tagStr)
                }
                for (stateStr in section.getStringList("hydration-blocks")) {
                    statesCanHydrate += Key.of(stateStr)
                }
                return FarmlandBlockBehavior(block, revertBlockKey, tagsCanHydrate, statesCanHydrate)
            }
        }
    }
}