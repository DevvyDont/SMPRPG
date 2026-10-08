package xyz.devvydont.smprpg.market.storage

import xyz.devvydont.smprpg.market.auction.Auction
import xyz.devvydont.smprpg.market.auction.PlayerAuctionData

/**
 * Serializable container for all auction house state.
 * Persisted as JSON via Gson.
 */
data class AuctionDataFile(
    val auctions: MutableList<Auction> = mutableListOf(),
    val playerData: MutableMap<String, PlayerAuctionData> = mutableMapOf()
)
