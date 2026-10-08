package xyz.devvydont.smprpg.items.blueprints.resources.woodcutting

import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineCompressibleBlueprint
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService

private const val WORTH_PER_ITEM = 8

class GoldenOakLog(itemService: ItemService, type: CustomItemType) :
    CraftEngineCompressibleBlueprint(itemService, type), ISellable {

    override val itemClassification: ItemClassification get() = ItemClassification.MATERIAL

    override fun getWorth(item: ItemStack): Int = WORTH_PER_ITEM * item.amount
}

