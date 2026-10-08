package xyz.devvydont.smprpg.gui.items.search

/**
 * A parsed item search query supporting NEI/JEI-style syntax. Build one with [parse] and test items with [matches].
 *
 * Supported syntax:
 *  - Plain text matches the item name: `sword`
 *  - Whitespace separates terms that must ALL match (logical AND): `iron sword`
 *  - `|` separates groups where ANY may match (logical OR): `iron|diamond`
 *  - A leading `-` negates a term: `-bow` (exclude bows)
 *  - A prefix scopes a term to a field: `#` lore/tooltip, `@` rarity, `$` classification
 *  - Double quotes allow spaces inside a single term: `#"critical damage"`
 *
 * These can be freely combined, e.g. `@legendary sword -#cursed | #"on fire"`. A blank query matches everything.
 *
 * Internally the query is a list of OR-groups; each group is a list of terms that must all match.
 */
class ItemSearchQuery private constructor(private val orGroups: List<List<SearchTerm>>) {

    /** Returns true if this query has no constraints and therefore matches every item. */
    fun matchesEverything(): Boolean = orGroups.isEmpty()

    /**
     * Returns whether the item satisfies the query: at least one OR-group whose every term matches.
     */
    fun matches(item: SearchableItem): Boolean {
        if (matchesEverything())
            return true
        return orGroups.any { group -> group.all { term -> term.matches(item) } }
    }

    companion object {

        private const val GROUP_SEPARATOR = '|'
        private const val QUOTE = '"'

        /**
         * Parses a raw query string into an [ItemSearchQuery].
         *
         * @param raw The exact text the player typed.
         * @return The parsed query. Empty or all-empty input yields a query that matches everything.
         */
        fun parse(raw: String): ItemSearchQuery {
            val groups = mutableListOf<List<SearchTerm>>()
            for (rawGroup in tokenizeGroups(raw)) {
                val terms = rawGroup.mapNotNull { SearchTerm.parse(it) }
                if (terms.isNotEmpty())
                    groups.add(terms)
            }
            return ItemSearchQuery(groups)
        }

        /**
         * Splits the raw query into OR-groups (separated by [GROUP_SEPARATOR]) and each group into whitespace-separated
         * tokens, while treating anything inside double quotes as literal text (so quoted spaces and `|` are preserved).
         */
        private fun tokenizeGroups(raw: String): List<List<String>> {
            val groups = mutableListOf<MutableList<String>>()
            var currentGroup = mutableListOf<String>()
            val token = StringBuilder()
            var inQuotes = false

            fun endToken() {
                if (token.isNotEmpty()) {
                    currentGroup.add(token.toString())
                    token.clear()
                }
            }

            fun endGroup() {
                endToken()
                groups.add(currentGroup)
                currentGroup = mutableListOf()
            }

            for (character in raw) {
                when {
                    character == QUOTE -> {
                        inQuotes = !inQuotes
                        token.append(character)
                    }
                    inQuotes -> token.append(character)
                    character == GROUP_SEPARATOR -> endGroup()
                    character.isWhitespace() -> endToken()
                    else -> token.append(character)
                }
            }
            endGroup()
            return groups
        }
    }
}
