package xyz.devvydont.smprpg.gui.anvil.operations

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.gui.anvil.AnvilOperation
import xyz.devvydont.smprpg.gui.anvil.AnvilResult
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.blueprints.augment.HotPotatoBook
import xyz.devvydont.smprpg.services.ItemService

/**
 * Adds a spell slot to a tome by consuming a single Necronomicon Excerpt, up to the tome's excerpt cap.
 */
object HotPotatoBookOperation : AnvilOperation {

    private const val BOOKS_CONSUMED = 1

    override fun tryApply(player: LeveledPlayer, primary: ItemStack, secondary: ItemStack): AnvilResult? {
        // The secondary item must be a Hot potato book, and the primary item must be a weapon/armor piece.
        if (ItemService.blueprint(secondary) !is HotPotatoBook) return null
        val blueprint = ItemService.blueprint(primary)
        when (blueprint.itemClassification) {
            ItemClassification.WEAPON,
            ItemClassification.SWORD,
            ItemClassification.SPEAR,
            ItemClassification.STAFF,
            ItemClassification.TRIDENT,
            ItemClassification.MACE,
            ItemClassification.KNIFE,
            ItemClassification.BOW,
            ItemClassification.SHORTBOW,
            ItemClassification.CROSSBOW,
            ItemClassification.AXE,
            ItemClassification.HATCHET,
            ItemClassification.HELMET,
            ItemClassification.CHESTPLATE,
            ItemClassification.LEGGINGS,
            ItemClassification.BOOTS,
            ItemClassification.ROD -> {
                // The tome must not already be maxed out on books.
                val books = primary.persistentDataContainer.getOrDefault(
                    HotPotatoBook.HOT_POTATO_BOOK_KEY, PersistentDataType.INTEGER, 0
                )
                if (books >= HotPotatoBook.MAX_HOT_POTATO_BOOKS) return null

                val result = primary.clone()
                HotPotatoBook.addHotPotatoBookToItem(result)
                blueprint.updateItemData(result)
                return AnvilResult(result, BOOKS_CONSUMED)
            }
            else -> return null
        }
    }
}
