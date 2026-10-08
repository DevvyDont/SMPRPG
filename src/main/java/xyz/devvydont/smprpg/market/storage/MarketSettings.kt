package xyz.devvydont.smprpg.market.storage

/**
 * Persisted administrative settings for the market services. Acts as an emergency switch so
 * admins can disable the bazaar and/or auction house for regular players without a restart.
 * Players with the bypass permission are unaffected.
 */
data class MarketSettings(
    var bazaarEnabled: Boolean = true,
    var auctionEnabled: Boolean = true
)
