package xyz.devvydont.smprpg.market.auction

/**
 * Per-player data for unclaimed auction items and coins.
 * Items are Base64-encoded ItemStack byte arrays.
 * Coins represent money waiting to be collected from successful sales or refunded bids.
 */
data class PlayerAuctionData(
    val unclaimedItems: MutableList<String> = mutableListOf(),
    var unclaimedCoins: Long = 0
) {

    fun hasUnclaimed(): Boolean {
        return unclaimedItems.isNotEmpty() || unclaimedCoins > 0
    }

    fun addCoins(amount: Long) {
        unclaimedCoins += amount
    }

    fun addItem(itemData: String) {
        unclaimedItems.add(itemData)
    }
}
