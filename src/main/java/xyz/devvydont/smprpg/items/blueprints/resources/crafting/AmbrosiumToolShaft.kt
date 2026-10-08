package xyz.devvydont.smprpg.items.blueprints.resources.crafting

import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService

class AmbrosiumToolShaft(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    ISellable, IModelOverridden {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM

    override fun getWorth(itemStack: ItemStack): Int { return 5_220 * itemStack.amount }

    override fun getDisplayKey(): Key? { return IModelOverridden.ofItemTypeInDirectory(customItemType, "materials") }
}
