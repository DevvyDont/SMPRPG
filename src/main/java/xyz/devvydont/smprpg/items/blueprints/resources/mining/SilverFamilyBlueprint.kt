package xyz.devvydont.smprpg.items.blueprints.resources.mining

import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineCompressibleBlueprint
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.extensions.calculateCompressedWorth

class SilverFamilyBlueprint(itemService: ItemService, type: CustomItemType) :
    CraftEngineCompressibleBlueprint(itemService, type), ISellable {

    override val itemClassification get() = ItemClassification.MATERIAL

    override fun getWorth(item: ItemStack) = calculateCompressedWorth(item)
}
