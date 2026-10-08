package xyz.devvydont.smprpg.items.blueprints.augment

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.recipe.CraftingBookCategory
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

class BookOfBounties(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
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
            ComponentUtils.create("Combine with an axe, pickaxe, or hoe in an anvil up to 10"),
            ComponentUtils.merge(
                ComponentUtils.create("times to add "),
                ComponentUtils.create("+5 Splintering, Fortune, or Yield", NamedTextColor.GREEN),
            ),
            ComponentUtils.EMPTY,
            ComponentUtils.merge(
                ComponentUtils.create("Combine with a weapon to add "),
                ComponentUtils.create("+2 Luckiness", NamedTextColor.GREEN)
            ),
        )
    }

    companion object {
        val BOUNTY_BOOK_KEY = NamespacedKey(SMPRPG.plugin, "bounty_book_modifier")
        const val MAX_BOUNTY_BOOKS = 10
        const val FORTUNE_BONUS = 5
        const val LUCK_BONUS = 2

        fun addBountyBookToItem(item: ItemStack) {
            val numBooks = item.persistentDataContainer.getOrDefault(BOUNTY_BOOK_KEY, PersistentDataType.INTEGER, 0)
            item.editPersistentDataContainer { pdc -> pdc.set(BOUNTY_BOOK_KEY, PersistentDataType.INTEGER, numBooks + 1) }
        }
    }
}
