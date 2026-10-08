package xyz.devvydont.smprpg.market.storage

import xyz.devvydont.smprpg.market.bazaar.BazaarItemStructure

/**
 * Serializable container for the bazaar's structural definitions (categories, display, price
 * bounds, stock capacity). Persisted as human-editable JSON; never overwritten by the stock
 * autosave, only when defaults are generated.
 */
data class BazaarStructureFile(
    val items: MutableMap<String, BazaarItemStructure> = mutableMapOf()
)
