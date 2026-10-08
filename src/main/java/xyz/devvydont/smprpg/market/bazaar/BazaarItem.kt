package xyz.devvydont.smprpg.market.bazaar

/**
 * Runtime view of a bazaar item: an immutable [BazaarItemStructure] paired with its mutable
 * stock level. Structure is loaded from the editable structure file; [currentStock] is the only
 * value that mutates at runtime and is persisted to the separate stock file.
 *
 * Structural fields are exposed as delegating getters so existing call sites read them directly
 * off the item (e.g. [minPrice], [category]) without reaching into [structure].
 */
class BazaarItem(val structure: BazaarItemStructure, var currentStock: Int) {

    val key get() = structure.key
    val displayName get() = structure.displayName
    val category get() = structure.category
    val minPrice get() = structure.minPrice
    val maxPrice get() = structure.maxPrice
    val maxStock get() = structure.maxStock
    val canGoOutOfStock get() = structure.canGoOutOfStock
}
