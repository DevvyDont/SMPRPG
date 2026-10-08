package xyz.devvydont.smprpg.items.blueprints.resources.crafting

import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineBlueprint
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService

class PlatinumIngot(itemService: ItemService, type: CustomItemType) :
    CraftEngineBlueprint(itemService, type), ISellable {
    override val itemClassification: ItemClassification
        /**
         * Determine what type of item this is.
         */
        get() = ItemClassification.MATERIAL

    override fun getWorth(item: ItemStack): Int {
        return 450 * item.amount
    }
}
