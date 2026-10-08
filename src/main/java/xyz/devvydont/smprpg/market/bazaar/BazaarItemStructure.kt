package xyz.devvydont.smprpg.market.bazaar

/**
 * Immutable structural definition of a bazaar-tradeable item.
 * Persisted separately from runtime stock so it can be hand-edited and hot-reloaded
 * without being clobbered by the frequent stock autosave.
 *
 * The key is either a CustomItemType key (lowercase enum name) or a Material name.
 * The category is a path-based string (e.g. "Mining/Ores", "Farming").
 */
data class BazaarItemStructure(
    val key: String,
    val displayName: String,
    val category: String,
    val minPrice: Long,
    val maxPrice: Long,
    val maxStock: Int,
    val canGoOutOfStock: Boolean = true
)
