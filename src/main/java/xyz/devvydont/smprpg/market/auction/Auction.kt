package xyz.devvydont.smprpg.market.auction

import org.bukkit.inventory.ItemStack
import java.util.Base64
import java.util.UUID

/**
 * Represents a single auction listing in the auction house.
 * Item data is stored as a Base64-encoded byte array from ItemStack.serializeAsBytes().
 */
data class Auction(
    val id: String = UUID.randomUUID().toString(),
    val sellerUUID: String,
    val sellerName: String,
    val itemData: String,
    val itemDisplayName: String,
    val itemKey: String,
    val category: AuctionCategory,
    val type: AuctionType,
    val startingPrice: Long,
    var currentBid: Long,
    var highestBidderUUID: String? = null,
    var highestBidderName: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long,
    var status: AuctionStatus = AuctionStatus.ACTIVE
) {

    fun getItemStack(): ItemStack {
        val bytes = Base64.getDecoder().decode(itemData)
        return ItemStack.deserializeBytes(bytes)
    }

    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= expiresAt
    }

    fun hasBids(): Boolean {
        return highestBidderUUID != null
    }

    fun getRemainingTimeMs(): Long {
        return (expiresAt - System.currentTimeMillis()).coerceAtLeast(0)
    }

    fun getFormattedTimeRemaining(): String {
        val remaining = getRemainingTimeMs()
        if (remaining <= 0) return "Expired"

        val hours = remaining / (1000 * 60 * 60)
        val minutes = (remaining % (1000 * 60 * 60)) / (1000 * 60)

        return when {
            hours >= 24 -> "${hours / 24}d ${hours % 24}h"
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "Ending Soon!"
        }
    }

    companion object {
        fun serializeItem(item: ItemStack): String {
            return Base64.getEncoder().encodeToString(item.serializeAsBytes())
        }
    }
}
