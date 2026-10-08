package xyz.devvydont.smprpg.items.blueprints.resources.crafting

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class HexedCloth(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    IHeaderDescribable, ISellable, IModelOverridden {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM

    override fun getHeader(itemStack: ItemStack?): MutableList<Component?> {
        return mutableListOf(
            ComponentUtils.create("Tattered rags from a Sea Hag,"),
            ComponentUtils.create("riddled with curses")
        )
    }

    override fun getWorth(item: ItemStack): Int { return 8000 * item.amount }

    override fun getDisplayKey(): Key { return IModelOverridden.ofItemTypeInDirectory(customItemType, "materials") }
}
