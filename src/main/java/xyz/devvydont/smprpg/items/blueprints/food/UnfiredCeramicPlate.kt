package xyz.devvydont.smprpg.items.blueprints.sets.inferno

import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService

class UnfiredCeramicPlate(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    IModelOverridden, ISellable {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM

    override fun getDisplayKey(): Key? { return IModelOverridden.ofItemTypeInDirectory(customItemType, "materials") }

    override fun getWorth(item: ItemStack): Int { return item.amount * 3}
}
