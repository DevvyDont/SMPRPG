package xyz.devvydont.smprpg.gui.items.search

import org.bukkit.inventory.ItemStack

/**
 * A display-ready item paired with its pre-computed, normalized searchable text fields.
 *
 * Building these once (when the item directory cache is warmed) keeps querying cheap: matching a term is just a
 * substring check against an already-normalized string, with no per-query item generation or lore rendering.
 */
class SearchableItem(
    val displayItem: ItemStack,
    private val fields: Map<SearchField, String>
) {

    /**
     * Returns the normalized searchable text for the given field.
     *
     * @param field The field to look up.
     * @return The normalized text, or an empty string if this item has nothing indexed for that field.
     */
    fun textFor(field: SearchField): String = fields[field] ?: ""
}
