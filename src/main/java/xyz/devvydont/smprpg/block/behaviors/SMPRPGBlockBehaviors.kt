package xyz.devvydont.smprpg.block.behaviors

import net.momirealms.craftengine.core.block.behavior.BlockBehaviors
import net.momirealms.craftengine.core.util.Key
import xyz.devvydont.smprpg.SMPRPG

class SMPRPGBlockBehaviors : BlockBehaviors() {

    init {
        SMPRPG.plugin.logger.info("Block behaviors registered!")
    }
    
    companion object {
        val ACCELERATOR = register(Key.from("smprpg:accelerator"), AcceleratorBlockBehavior.FACTORY)
        val ASCENDING = register(Key.from("smprpg:ascending_block"), AscendingBlockBehavior.FACTORY)
        val COOKING_POT = register(Key.from("smprpg:cooking_pot"), CookingPotBlockBehavior.FACTORY)
        val CUTTING_BOARD = register(Key.from("smprpg:cutting_board"), CuttingBoardBehavior.FACTORY)
        val FARMLAND = register(Key.from("smprpg:farmland"), FarmlandBlockBehavior.FACTORY)
        val FREEZER = register(Key.from("smprpg:freezer"), FreezerBlockBehavior.FACTORY)
        val LAUNCH_BLOCK = register(Key.from("smprpg:launch_block"), LaunchBlockBehavior.FACTORY)
        val MENU_INTERACTABLE = register(Key.from("smprpg:menu_interactable"), SMPRPGMenuBlockBehavior.FACTORY)
        val PORTAL = register(Key.from("smprpg:portal"), PortalBlockBehavior.FACTORY)
        val REFORGE_TABLE = register(Key.from("smprpg:reforge_table"), ReforgeTableBehavior.FACTORY)
        val TICK_ACCELERATOR = register(Key.from("smprpg:tick_accelerator"), TickAcceleratorBlockBehavior.FACTORY)
        val TILLABLE = register(Key.from("smprpg:tillable_block"), TillableBlockBehavior.FACTORY)
        val ZERO_GRAVITY = register(Key.from("smprpg:zero_gravity_block"), ZeroGravityBlockBehavior.FACTORY)
    }
}