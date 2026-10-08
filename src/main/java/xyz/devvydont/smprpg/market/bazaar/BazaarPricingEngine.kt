package xyz.devvydont.smprpg.market.bazaar

import xyz.devvydont.smprpg.market.MarketConstants
import kotlin.math.roundToLong

/**
 * Isolated pricing formula for the Bazaar.
 * Uses linear interpolation between maxPrice (at 0 stock) and minPrice (at max stock).
 * Inspired by Wizard101's bazaar system.
 */
object BazaarPricingEngine {

    /**
     * Calculate the buy price for a bazaar item based on current stock level.
     * Lower stock = higher price, higher stock = lower price.
     */
    fun calculateBuyPrice(item: BazaarItem): Long {
        val stockRatio = (item.currentStock.toDouble() / item.maxStock).coerceIn(0.0, 1.0)
        val price = item.maxPrice - (item.maxPrice - item.minPrice) * stockRatio
        return price.roundToLong().coerceAtLeast(item.minPrice)
    }

    /**
     * Calculate the sell price for a bazaar item.
     * Always lower than buy price by the spread factor to prevent zero-risk arbitrage.
     */
    fun calculateSellPrice(item: BazaarItem): Long {
        val buyPrice = calculateBuyPrice(item)
        return (buyPrice * MarketConstants.BAZAAR_SELL_SPREAD).roundToLong().coerceAtLeast(1)
    }

    /**
     * Calculate the total cost for buying a given quantity.
     * Price adjusts per unit as stock depletes during the transaction.
     */
    fun calculateBulkBuyCost(item: BazaarItem, quantity: Int): Long {
        var total = 0L
        var simulatedStock = item.currentStock
        for (i in 0 until quantity) {
            if (item.canGoOutOfStock && simulatedStock <= 0) break
            val ratio = (simulatedStock.toDouble() / item.maxStock).coerceIn(0.0, 1.0)
            val unitPrice = item.maxPrice - (item.maxPrice - item.minPrice) * ratio
            total += unitPrice.roundToLong().coerceAtLeast(item.minPrice)
            simulatedStock--
        }
        return total
    }

    /**
     * Calculate the total payout for selling a given quantity.
     * Price adjusts per unit as stock increases during the transaction.
     */
    fun calculateBulkSellPayout(item: BazaarItem, quantity: Int): Long {
        var total = 0L
        var simulatedStock = item.currentStock
        for (i in 0 until quantity) {
            val ratio = (simulatedStock.toDouble() / item.maxStock).coerceIn(0.0, 1.0)
            val buyPrice = item.maxPrice - (item.maxPrice - item.minPrice) * ratio
            val sellPrice = (buyPrice * MarketConstants.BAZAAR_SELL_SPREAD).roundToLong().coerceAtLeast(1)
            total += sellPrice
            simulatedStock++
        }
        return total
    }
}
