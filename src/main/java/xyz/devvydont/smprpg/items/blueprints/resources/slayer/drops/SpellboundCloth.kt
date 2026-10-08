package xyz.devvydont.smprpg.items.blueprints.resources.slayer.drops

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

class SpellboundCloth(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    ISellable, IHeaderDescribable, IModelOverridden {
    /**
     * Determine what type of item this is.
     */
    override val itemClassification: ItemClassification get() = ItemClassification.ITEM

    /**
     * Given this item stack, how much should it be able to sell for?
     * Keep in mind that the size of the stack needs to considered as well!
     *
     * @param item The item that can be sold.
     * @return The worth of the item.
     */
    override fun getWorth(item: ItemStack): Int { return 13_400 * item.amount }

    override fun getHeader(itemStack: ItemStack?): MutableList<Component?> {
        return mutableListOf(
            ComponentUtils.create("Hexed Cloth, refined with Spell Powder"),
            ComponentUtils.create("to remove the curses, and leave behind"),
            ComponentUtils.create("a potent, magical cloth.")
        )
    }

    override fun getDisplayKey(): Key {
        return IModelOverridden.ofItemTypeInDirectory(customItemType, "materials")
    }
}
