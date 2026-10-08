package xyz.devvydont.smprpg.gui.items.search

/**
 * The different fields of an item that a single search term can target. Every field other than [NAME] is selected by a
 * leading prefix character in the query, mirroring how NEI/JEI scope their searches (e.g. '#' for tooltip text).
 */
enum class SearchField(val prefix: Char?) {

    /** The item's display name. This is the default field used when a term has no recognized prefix. */
    NAME(null),

    /** The item's tooltip, which includes its name and rendered lore. Selected with '#'. */
    TOOLTIP('#'),

    /** The item's rarity, e.g. "legendary". Selected with '@'. */
    RARITY('@'),

    /** The item's classification/category, e.g. "sword" or "weapon". Selected with '$'. */
    CLASSIFICATION('$');

    companion object {

        /**
         * Resolves the field selected by a leading prefix character.
         *
         * @param character The first character of a search term.
         * @return The matching field, or null if the character is not a recognized prefix.
         */
        fun fromPrefix(character: Char): SearchField? = entries.firstOrNull { it.prefix == character }
    }
}
