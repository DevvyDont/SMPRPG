package xyz.devvydont.smprpg.market.auction

/**
 * Defines the available sort modes for browsing auctions.
 */
enum class AuctionSortMode(val displayName: String) {
    NEWEST("Newest First"),
    ENDING_SOON("Ending Soon"),
    PRICE_LOW("Price: Low to High"),
    PRICE_HIGH("Price: High to Low"),
    ALPHABETICAL("Name: A-Z"),
    ALPHABETICAL_REV("Name: Z-A");

    /**
     * Returns a sorted copy of the given auction list according to this sort mode.
     */
    fun sort(auctions: List<Auction>): List<Auction> {
        return when (this) {
            NEWEST -> auctions.sortedByDescending { it.createdAt }
            ENDING_SOON -> auctions.sortedBy { it.expiresAt }
            PRICE_LOW -> auctions.sortedBy { it.currentBid }
            PRICE_HIGH -> auctions.sortedByDescending { it.currentBid }
            ALPHABETICAL -> auctions.sortedBy { it.itemDisplayName.lowercase() }
            ALPHABETICAL_REV -> auctions.sortedByDescending { it.itemDisplayName.lowercase() }
        }
    }

    /**
     * Cycles forward to the next sort mode, wrapping around at the end.
     */
    fun next(): AuctionSortMode {
        val nextOrdinal = (ordinal + 1) % entries.size
        return entries[nextOrdinal]
    }

    /**
     * Cycles backward to the previous sort mode, wrapping around at the start.
     */
    fun previous(): AuctionSortMode {
        val prevOrdinal = (ordinal - 1 + entries.size) % entries.size
        return entries[prevOrdinal]
    }
}
