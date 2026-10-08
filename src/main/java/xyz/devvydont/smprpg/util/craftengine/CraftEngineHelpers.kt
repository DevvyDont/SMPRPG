package xyz.devvydont.smprpg.util.craftengine

import net.minecraft.core.BlockPos
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import net.momirealms.craftengine.core.world.BlockPos as CEBlockPos
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import net.momirealms.craftengine.core.util.Key as CEKey

class CraftEngineHelpers {
    companion object {
        fun getBlockKey(block: Block): CEKey? {
            return if (CraftEngineBlocks.isCustomBlock(block))
                CEKey.of(CraftEngineBlocks.getCustomBlockState(block)!!.customBlockState().asString.replace("\\[.*]".toRegex(), ""))
            else
                return null
        }

        fun getBlockKey(state: BlockState): CEKey? {
            val block = state.block
            return getBlockKey(block)
        }

        /**
         * This method is useful for extracting properties from the block in the key as well,
         * whereas getBlockKey purges any useful property data for the sake of table lookups
         *
         * getBlockKey would return from smprpg:tomato_plant[age=2] : smprpg:tomato_plant
         * getAbsoluteBlockKey would return smprpg:tomato_plant[age=2]
         */
        fun getAbsoluteBlockKey(block: Block): CEKey? {
            return if (CraftEngineBlocks.isCustomBlock(block))
                CEKey.of(CraftEngineBlocks.getCustomBlockState(block)!!.customBlockState().asString)
            else
                null
        }

        fun getLocationFromBlockPos(pos: CEBlockPos, world: World): Location {
            return Location(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        }

        fun getLocationFromBlockPos(pos: BlockPos, world: World): Location {
            return Location(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        }
    }
}