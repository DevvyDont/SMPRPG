package xyz.devvydont.smprpg.market.auction

/**
 * Defines the available type filters for browsing auctions.
 */
enum class AuctionTypeFilter(val displayName: String) {
    ALL("All Auctions"),
    BID_ONLY("Auctions Only"),
    BIN_ONLY("BIN Only");

    /**
     * Returns true if the given auction passes this type filter.
     */
    fun matches(auction: Auction): Boolean {
        return when (this) {
            ALL -> true
            BID_ONLY -> auction.type == AuctionType.BID
            BIN_ONLY -> auction.type == AuctionType.BUY_IT_NOW
        }
    }

    /**
     * Cycles forward to the next filter, wrapping around at the end.
     */
    fun next(): AuctionTypeFilter {
        val nextOrdinal = (ordinal + 1) % entries.size
        return entries[nextOrdinal]
    }

    /**
     * Cycles backward to the previous filter, wrapping around at the start.
     */
    fun previous(): AuctionTypeFilter {
        val prevOrdinal = (ordinal - 1 + entries.size) % entries.size
        return entries[prevOrdinal]
    }
}
