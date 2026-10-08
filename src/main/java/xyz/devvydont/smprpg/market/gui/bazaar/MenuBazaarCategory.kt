package xyz.devvydont.smprpg.market.gui.bazaar

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.market.MarketService
import xyz.devvydont.smprpg.market.bazaar.BazaarDemandDisplay
import xyz.devvydont.smprpg.market.bazaar.BazaarItem
import xyz.devvydont.smprpg.market.bazaar.BazaarPricingEngine
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Paginated list of items within a bazaar category.
 */
class MenuBazaarCategory(
    player: Player,
    private val displayTitle: String,
    private val itemProvider: () -> List<BazaarItem>,
    parentMenu: MenuBase? = null
) : MenuBase(player, ROWS, parentMenu) {

    private var page = 0
    private var bazaarItems: List<BazaarItem> = emptyList()

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(Component.text(displayTitle))
        refreshItems()
        render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    private fun refreshItems() {
        bazaarItems = itemProvider()
    }

    private fun render() {
        clear()
        setBorderEdge()

        val totalItems = bazaarItems.size
        val area = (ROWS - 2) * ITEMS_PER_ROW
        val lastPage = if (totalItems > 0) (totalItems - 1) / area else 0

        var itemIndexOffset = page * area
        if (itemIndexOffset >= totalItems) {
            itemIndexOffset = 0
            page = 0
        }
        if (itemIndexOffset < 0) {
            page = lastPage
            itemIndexOffset = area * page
        }

        val bazaarManager = SMPRPG.getService(MarketService::class.java).bazaarManager

        for (slot in 0 until inventorySize) {
            if (itemIndexOffset >= totalItems) break
            if (getItem(slot) != null) continue

            val bazaarItem = bazaarItems[itemIndexOffset]
            val buyPrice = BazaarPricingEngine.calculateBuyPrice(bazaarItem)
            val sellPrice = BazaarPricingEngine.calculateSellPrice(bazaarItem)

            val itemType = CustomItemType.entries.find { it.getKey() == bazaarItem.key }
            val compressionFlow = bazaarManager.findCompressionFlow(bazaarItem.key)

            val loreLines = mutableListOf(
                ComponentUtils.merge(
                    ComponentUtils.create("Buy Price: ", NamedTextColor.GRAY),
                    ComponentUtils.create(EconomyService.formatMoney(buyPrice), NamedTextColor.GOLD)
                ),
                ComponentUtils.merge(
                    ComponentUtils.create("Sell Price: ", NamedTextColor.GRAY),
                    ComponentUtils.create(EconomyService.formatMoney(sellPrice), NamedTextColor.GOLD)
                ),
                ComponentUtils.EMPTY,
            )
            loreLines.addAll(BazaarDemandDisplay.buildDemandLore(player, bazaarItem))
            loreLines.add(ComponentUtils.EMPTY)

            if (compressionFlow != null) {
                loreLines.add(ComponentUtils.create("Click to view tiers!", NamedTextColor.YELLOW))
            } else {
                loreLines.add(ComponentUtils.create("Click to buy/sell!", NamedTextColor.YELLOW))
            }

            val item = createBazaarDisplayItem(itemType, bazaarItem.displayName, loreLines, bazaarKey = bazaarItem.key)

            setButton(slot, item) { _: InventoryClickEvent ->
                if (compressionFlow != null) {
                    openSubMenu(MenuBazaarCompressionView(player, bazaarItem, compressionFlow, this))
                } else {
                    openSubMenu(MenuBazaarItemView(player, bazaarItem, this))
                }
            }
            itemIndexOffset++
        }

        // Pagination buttons — visible arrows, shown only when that direction has a page so the
        // arrow itself signals that more items exist. Unused corners keep the border pane.
        val displayPage = page + 1
        val displayPageMax = lastPage + 1

        if (page > 0) {
            setButton(
                (ROWS - 1) * 9,
                createNamedItem(
                    Material.ARROW,
                    ComponentUtils.create("<- Previous Page ($displayPage/$displayPageMax)", NamedTextColor.GOLD)
                )
            ) { _: InventoryClickEvent ->
                page--
                render()
                sounds.playPagePrevious()
            }
        }

        if (page < lastPage) {
            setButton(
                (ROWS - 1) * 9 + 8,
                createNamedItem(
                    Material.ARROW,
                    ComponentUtils.create("Next Page ($displayPage/$displayPageMax) ->", NamedTextColor.GOLD)
                )
            ) { _: InventoryClickEvent ->
                page++
                render()
                sounds.playPageNext()
            }
        }

        setBackButton()
    }

    companion object {
        private const val ROWS = 6
        private const val ITEMS_PER_ROW = 7

        /**
         * Creates an ItemStack for display in bazaar menus, preserving custom model data
         * so resource pack overrides render correctly. Falls back to Material lookup by
         * bazaar key, or PAPER if neither resolves.
         */
        fun createBazaarDisplayItem(
            itemType: CustomItemType?,
            displayName: String,
            lore: List<Component>,
            bazaarKey: String? = null
        ): ItemStack {
            val item = if (itemType != null) {
                SMPRPG.getService(ItemService::class.java).getCustomItem(itemType)
            } else {
                val material = bazaarKey?.let {
                    try { Material.valueOf(it.uppercase()) } catch (_: IllegalArgumentException) { null }
                }
                ItemStack(material ?: Material.PAPER)
            }
            val meta = item.itemMeta
            meta.displayName(ComponentUtils.create(displayName, NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false))
            meta.lore(ComponentUtils.cleanItalics(lore))
            item.itemMeta = meta
            return item
        }
    }
}
