package xyz.devvydont.smprpg.items.behaviors.context

import net.momirealms.craftengine.core.util.Direction
import net.momirealms.craftengine.core.world.BlockPos
import net.momirealms.craftengine.core.world.context.BlockPlaceContext
import net.momirealms.craftengine.core.world.context.UseOnContext

class RopePlaceContext(context: UseOnContext): BlockPlaceContext(context) {
    private val belowBlockPos = hitResult.blockPos().relative(Direction.DOWN)

    override fun getClickedPos(): BlockPos {
        return if (this.replacingClickedBlock()) { super.getClickedPos() } else { belowBlockPos }
    }

    override fun canPlace(): Boolean {
        return this.level.getBlock(belowBlockPos).canBeReplaced(this);
    }
}