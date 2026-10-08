package xyz.devvydont.smprpg.market.storage

/**
 * Serializable container for the bazaar's runtime data: current stock per item key.
 * This is the only bazaar state that mutates during play and is written by the autosave.
 */
data class BazaarDataFile(
    val stock: MutableMap<String, Int> = mutableMapOf()
)
