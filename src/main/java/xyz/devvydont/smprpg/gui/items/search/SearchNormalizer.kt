package xyz.devvydont.smprpg.gui.items.search

import java.util.Locale

/**
 * Normalizes text so that searchable item fields and the player's query values are compared on equal footing.
 *
 * Most fields are reduced to a "compact" form (lowercased with spaces, underscores, and dashes removed) so that
 * formatting differences between what a player types and how a name is stored never prevent a match. Tooltip text
 * keeps its spacing intact so that multi-word phrase searches still work.
 */
object SearchNormalizer {

    private const val SPACE = " "
    private const val UNDERSCORE = "_"
    private const val DASH = "-"
    private const val EMPTY = ""

    /**
     * Normalizes [text] for comparison within the given [field]. The same method is used on both the stored item
     * field and the query value, guaranteeing they are normalized identically.
     */
    fun normalize(field: SearchField, text: String): String {
        val lowered = text.lowercase(Locale.getDefault())
        return when (field) {
            SearchField.TOOLTIP -> lowered.trim()
            else -> lowered.replace(SPACE, EMPTY).replace(UNDERSCORE, EMPTY).replace(DASH, EMPTY)
        }
    }
}
