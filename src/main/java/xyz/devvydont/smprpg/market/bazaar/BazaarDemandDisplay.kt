package xyz.devvydont.smprpg.market.bazaar

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

private const val DEBUG_PERMISSION = "smprpg.market.debug"

/**
 * Demand tiers based on stock percentage.
 * Lower stock = higher demand = higher prices for players.
 */
enum class DemandLevel(val label: String, val color: NamedTextColor) {
    VERY_HIGH("Very High", NamedTextColor.DARK_RED),
    HIGH("High", NamedTextColor.RED),
    MODERATE("Moderate", NamedTextColor.YELLOW),
    LOW("Low", NamedTextColor.GREEN),
    VERY_LOW("Very Low", NamedTextColor.DARK_GREEN);

    companion object {
        fun fromStockPercent(stockPercent: Int): DemandLevel = when {
            stockPercent <= 5 -> VERY_HIGH
            stockPercent <= 20 -> HIGH
            stockPercent <= 50 -> MODERATE
            stockPercent <= 80 -> LOW
            else -> VERY_LOW
        }
    }
}

object BazaarDemandDisplay {

    private fun canSeeDebugInfo(player: Player): Boolean {
        return player.isOp || player.hasPermission(DEBUG_PERMISSION)
    }

    /**
     * Build demand lore lines for a bazaar item.
     * Normal players see a vague "Demand: High" label.
     * Operators/debug-permitted players also see exact stock numbers.
     */
    fun buildDemandLore(player: Player, bazaarItem: BazaarItem): List<Component> {
        val stockPercent = (bazaarItem.currentStock.toDouble() / bazaarItem.maxStock * 100).toInt()
        val demand = DemandLevel.fromStockPercent(stockPercent)

        val lines = mutableListOf(
            ComponentUtils.merge(
                ComponentUtils.create("Demand: ", NamedTextColor.GRAY),
                ComponentUtils.create(demand.label, demand.color)
            )
        )

        if (canSeeDebugInfo(player)) {
            lines.add(ComponentUtils.merge(
                ComponentUtils.create("Stock: ", NamedTextColor.DARK_RED),
                ComponentUtils.create("${bazaarItem.currentStock}/${bazaarItem.maxStock} ($stockPercent%)", NamedTextColor.DARK_RED)
            ))
        }

        return lines
    }

    /**
     * Build demand lore for a compressed tier, showing effective stock.
     * Normal players see the demand label.
     * Operators/debug-permitted players also see effective and raw stock numbers.
     */
    fun buildCompressedDemandLore(player: Player, bazaarItem: BazaarItem, compressionAmount: Int): List<Component> {
        val stockPercent = (bazaarItem.currentStock.toDouble() / bazaarItem.maxStock * 100).toInt()
        val demand = DemandLevel.fromStockPercent(stockPercent)
        val effectiveStock = bazaarItem.currentStock / compressionAmount

        val lines = mutableListOf(
            ComponentUtils.merge(
                ComponentUtils.create("Demand: ", NamedTextColor.GRAY),
                ComponentUtils.create(demand.label, demand.color)
            )
        )

        if (canSeeDebugInfo(player)) {
            lines.add(ComponentUtils.merge(
                ComponentUtils.create("Effective: ", NamedTextColor.DARK_RED),
                ComponentUtils.create("$effectiveStock", NamedTextColor.DARK_RED)
            ))
            lines.add(ComponentUtils.merge(
                ComponentUtils.create("Base Stock: ", NamedTextColor.DARK_RED),
                ComponentUtils.create("${bazaarItem.currentStock}/${bazaarItem.maxStock} ($stockPercent%)", NamedTextColor.DARK_RED)
            ))
        }

        return lines
    }
}
