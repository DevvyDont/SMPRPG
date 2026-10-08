package xyz.devvydont.smprpg.items.behaviors

import net.momirealms.craftengine.bukkit.block.BukkitBlockManager
import net.momirealms.craftengine.bukkit.item.behavior.BlockItemBehavior
import net.momirealms.craftengine.core.entity.player.InteractionResult
import net.momirealms.craftengine.core.item.behavior.ItemBehaviorFactory
import net.momirealms.craftengine.core.pack.Pack
import net.momirealms.craftengine.core.pack.PendingConfigSection
import net.momirealms.craftengine.core.plugin.config.ConfigConstants
import net.momirealms.craftengine.core.plugin.config.ConfigSection
import net.momirealms.craftengine.core.util.Key
import net.momirealms.craftengine.core.world.BlockHitResult
import net.momirealms.craftengine.core.world.BlockPos
import net.momirealms.craftengine.core.world.context.BlockPlaceContext
import net.momirealms.craftengine.core.world.context.UseOnContext
import xyz.devvydont.smprpg.items.behaviors.context.RopePlaceContext
import java.nio.file.Path

class RopeBlockItemBehavior(blockId: Key): BlockItemBehavior(blockId) {

    override fun useOnBlock(context: UseOnContext): InteractionResult {
        val blockPlaceCtx = BlockPlaceContext(context)
        if (context.player != null) {
            // Just place like normal if we are sneaking.
            if (context.player!!.isSneaking)
                return this.place(blockPlaceCtx)

            var currY = context.clickedPos.y
            while (context.world.getBlock(context.clickedPos.x, currY, context.clickedPos.z).id() == context.world.getBlock(context.clickedPos).id()) {
                currY--
                if (currY < blockPlaceCtx.world.worldHeight().minBuildHeight)
                    return InteractionResult.FAIL
                val blockAt = context.world.getBlock(context.clickedPos.x, currY, context.clickedPos.z)
                if (blockAt.id() != context.world.getBlock(context.clickedPos).id()) {
                    val ropePlaceCtx = RopePlaceContext(UseOnContext(context.world, context.player, context.hand, context.item,
                        BlockHitResult(
                            context.hitResult.location(),
                            context.hitResult.direction(),
                            BlockPos(context.clickedPos.x, currY + 1, context.clickedPos.z),
                            context.hitResult.isInside,
                            context.hitResult.isWorldBorderHit
                        )))
                    return this.place(ropePlaceCtx)
                }
            }
        }
        return super.useOnBlock(context)
    }

    companion object {
        val FACTORY = Factory()

        class Factory : ItemBehaviorFactory<RopeBlockItemBehavior> {
            override fun create(pack: Pack, path: Path, key: Key, section: ConfigSection): RopeBlockItemBehavior {
                val blockValue = section.getNonNullValue("block", ConfigConstants.ARGUMENT_SECTION)
                if (blockValue.`is`(MutableMap::class.java)) {
                    BukkitBlockManager.instance().blockParser().addPendingConfigSection(PendingConfigSection(pack, path, key, blockValue.getAsSection()))
                    return RopeBlockItemBehavior(key)
                } else {
                    return RopeBlockItemBehavior(blockValue.getAsIdentifier())
                }
            }
        }
    }
}