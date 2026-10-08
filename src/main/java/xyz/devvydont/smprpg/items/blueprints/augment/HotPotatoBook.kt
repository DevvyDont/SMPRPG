package xyz.devvydont.smprpg.items.blueprints.augment

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.function.Consumer

class HotPotatoBook(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    ISellable, IHeaderDescribable {
    /**
     * Determine what type of item this is.
     */
    override val itemClassification: ItemClassification get() = ItemClassification.AUGMENT_STONE

    /**
     * Given this item stack, how much should it be able to sell for?
     * Keep in mind that the size of the stack needs to considered as well!
     *
     * @param item The item that can be sold.
     * @return The worth of the item.
     */
    override fun getWorth(item: ItemStack): Int {
        return 19_686 * item.amount
    }

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        itemStack.editMeta(Consumer { meta: ItemMeta? -> meta!!.setMaxStackSize(1) })
    }

    override fun getHeader(itemStack: ItemStack): List<Component> {
        return listOf(
            ComponentUtils.create("Combine with a weapon in an anvil up to 10"),
            ComponentUtils.merge(
                ComponentUtils.create("times to add "),
                ComponentUtils.create("+2 Strength", NamedTextColor.RED),
                ComponentUtils.create(", or")
            ),
            ComponentUtils.create("combine with an armor piece"),
            ComponentUtils.merge(
                ComponentUtils.create("to add "),
                ComponentUtils.create("+4 Health", NamedTextColor.RED),
                ComponentUtils.create(" and "),
                ComponentUtils.create("+2 Defense", NamedTextColor.GREEN)
            ),
        )
    }

    companion object {
        val HOT_POTATO_BOOK_KEY = NamespacedKey(SMPRPG.plugin, "hot_potato_book_modifier")
        const val MAX_HOT_POTATO_BOOKS = 10
        const val DEFENSE_BONUS = 2
        const val HEALTH_BONUS =  4
        const val STRENGTH_BONUS = 2

        fun addHotPotatoBookToItem(item: ItemStack) {
            val numBooks = item.persistentDataContainer.getOrDefault(HOT_POTATO_BOOK_KEY, PersistentDataType.INTEGER, 0)
            item.editPersistentDataContainer { pdc -> pdc.set(HOT_POTATO_BOOK_KEY, PersistentDataType.INTEGER, numBooks + 1) }
        }
    }
}
