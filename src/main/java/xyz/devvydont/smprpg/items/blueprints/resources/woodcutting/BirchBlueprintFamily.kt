package xyz.devvydont.smprpg.items.blueprints.resources.woodcutting

import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.extensions.calculateCompressedWorth

class BirchBlueprintFamily(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    ISellable {
    override val itemClassification: ItemClassification get() = ItemClassification.MATERIAL

    override fun getWorth(itemStack: ItemStack): Int {
        return this.calculateCompressedWorth(itemStack)
    }
}
