package xyz.devvydont.smprpg.gui.items.search

/**
 * A single parsed search term, e.g. `sword`, `#"critical damage"`, `@legendary`, or `-bow`.
 *
 * A term targets one [SearchField], holds a normalized [value] to look for, and may be [negated] (the item matches
 * only when the value is absent).
 */
class SearchTerm private constructor(
    private val field: SearchField,
    private val value: String,
    private val negated: Boolean
) {

    /**
     * Returns whether the given item satisfies this term. For a normal term that means the item's field contains the
     * value; for a negated term it means the field does NOT contain the value.
     */
    fun matches(item: SearchableItem): Boolean {
        val contains = item.textFor(field).contains(value)
        return contains != negated
    }

    companion object {

        private const val NEGATION_PREFIX = '-'
        private const val QUOTE = "\""

        /**
         * Parses a single raw token into a [SearchTerm].
         *
         * @param token A whitespace-delimited token from the query, e.g. `-#"on fire"`.
         * @return The parsed term, or null if the token carries no searchable value (e.g. a lone `-` or `""`).
         */
        fun parse(token: String): SearchTerm? {
            var working = token

            val negated = working.startsWith(NEGATION_PREFIX)
            if (negated)
                working = working.substring(1)

            var field = SearchField.NAME
            if (working.isNotEmpty()) {
                val resolved = SearchField.fromPrefix(working[0])
                if (resolved != null) {
                    field = resolved
                    working = working.substring(1)
                }
            }

            val unquoted = working.replace(QUOTE, "")
            val normalized = SearchNormalizer.normalize(field, unquoted)
            if (normalized.isEmpty())
                return null

            return SearchTerm(field, normalized, negated)
        }
    }
}
