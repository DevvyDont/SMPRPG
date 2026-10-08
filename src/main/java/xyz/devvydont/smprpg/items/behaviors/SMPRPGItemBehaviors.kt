package xyz.devvydont.smprpg.block.behaviors

import net.momirealms.craftengine.core.item.behavior.ItemBehaviors
import net.momirealms.craftengine.core.util.Key
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.behaviors.RopeBlockItemBehavior

class SMPRPGItemBehaviors : ItemBehaviors() {

    init {
        SMPRPG.plugin.logger.info("Item behaviors registered!")
    }

    companion object {
        val ACCELERATOR = register(Key.from("smprpg:rope_block_item"), RopeBlockItemBehavior.FACTORY)
    }
}