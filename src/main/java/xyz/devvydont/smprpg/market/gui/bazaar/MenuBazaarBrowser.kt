package xyz.devvydont.smprpg.market.gui.bazaar

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.InterfaceUtil
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.market.MarketService
import xyz.devvydont.smprpg.market.bazaar.BazaarCategoryNode
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Category selection grid for the Bazaar.
 * Shows all bazaar categories as clickable buttons with item listing tooltips.
 */
class MenuBazaarBrowser(
    player: Player,
    parentMenu: MenuBase? = null
) : MenuBase(player, ROWS, parentMenu) {

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(Component.text("Bazaar"))
        render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
        playInvalidAnimation()
    }

    private fun render() {
        setBorderFull()
        setBackButton()

        val bazaarManager = SMPRPG.getService(MarketService::class.java).bazaarManager
        val categories = bazaarManager.getRootCategories()

        // Split the categories across the two middle rows and horizontally center each row.
        // Prefer an odd-sized top row so it centers perfectly in the 9-wide grid (e.g. 8 -> 5 + 3).
        var topRowCount = categories.size - categories.size / 2
        if (topRowCount % 2 == 0 && categories.size > topRowCount) topRowCount++
        val rows = listOf(categories.take(topRowCount), categories.drop(topRowCount))

        for ((rowIndex, rowCategories) in rows.withIndex()) {
            if (rowCategories.isEmpty()) continue
            val startColumn = ((WIDTH - rowCategories.size) / 2).coerceAtLeast(0)

            for ((columnIndex, node) in rowCategories.withIndex()) {
                val slot = CATEGORY_ROWS[rowIndex] * WIDTH + startColumn + columnIndex
                if (slot >= inventorySize) break

                val loreLines = buildItemTooltip(node)
                loreLines.add(ComponentUtils.EMPTY)
                loreLines.add(ComponentUtils.create("Click to browse!", NamedTextColor.YELLOW))

                val item = InterfaceUtil.getNamedItemWithDescription(
                    node.icon,
                    ComponentUtils.create(node.name, NamedTextColor.GREEN),
                    loreLines
                )

                setButton(slot, item) { _: InventoryClickEvent ->
                    if (node.hasChildren) {
                        openSubMenu(MenuBazaarSubcategoryBrowser(player, node, this))
                    } else {
                        openSubMenu(MenuBazaarCategory(
                            player,
                            "Bazaar - ${node.name}",
                            { bazaarManager.getItemsByPath(node.path) },
                            this
                        ))
                    }
                }
            }
        }

        val sellAllItem = InterfaceUtil.getNamedItemWithDescription(
            Material.HOPPER,
            ComponentUtils.create("Sell All", NamedTextColor.GOLD),
            ComponentUtils.create("Sell all bazaar items in your inventory!", NamedTextColor.YELLOW)
        )
        setButton(SELL_ALL_SLOT, sellAllItem) { _: InventoryClickEvent ->
            openSubMenu(MenuBazaarSellAll(player, this))
        }
    }

    companion object {
        private const val ROWS = 4
        private const val WIDTH = 9
        private const val SELL_ALL_SLOT = 34
        private const val MAX_TOOLTIP_ITEMS = 15
        private val CATEGORY_ROWS = intArrayOf(1, 2)

        fun buildItemTooltip(node: BazaarCategoryNode): MutableList<Component> {
            val allItems = node.allItems
            val lines = mutableListOf<Component>()
            lines.add(ComponentUtils.create("${allItems.size} items available", NamedTextColor.GRAY))
            lines.add(ComponentUtils.EMPTY)

            val displayItems = allItems.take(MAX_TOOLTIP_ITEMS)
            for (item in displayItems) {
                lines.add(ComponentUtils.create("  ${item.displayName}", NamedTextColor.DARK_GRAY))
            }
            val remaining = allItems.size - displayItems.size
            if (remaining > 0) {
                lines.add(ComponentUtils.create("  ...and $remaining more", NamedTextColor.DARK_GRAY))
            }

            return lines
        }
    }
}
