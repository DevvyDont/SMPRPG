package xyz.devvydont.smprpg.market.gui.auction

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
import xyz.devvydont.smprpg.market.auction.Auction
import xyz.devvydont.smprpg.market.auction.AuctionCategory
import xyz.devvydont.smprpg.market.auction.AuctionSortMode
import xyz.devvydont.smprpg.market.auction.AuctionType
import xyz.devvydont.smprpg.market.auction.AuctionTypeFilter
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Paginated auction browsing menu with category filters and search.
 */
class MenuAuctionBrowser(
    player: Player,
    parentMenu: MenuBase? = null,
    private val query: String? = null,
    private val categoryFilter: AuctionCategory? = null,
    private var sortMode: AuctionSortMode = AuctionSortMode.NEWEST,
    private var typeFilter: AuctionTypeFilter = AuctionTypeFilter.ALL
) : MenuBase(player, ROWS, parentMenu) {

    private var page = 0
    private var auctions: List<Auction> = emptyList()

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        val title = when {
            query != null -> "Auction House - Search: $query"
            categoryFilter != null -> "Auction House - ${categoryFilter.displayName}"
            else -> "Auction House"
        }
        event.titleOverride(Component.text(title))
        refreshAuctions()
        render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    private fun refreshAuctions() {
        val auctionManager = SMPRPG.getService(MarketService::class.java).auctionManager
        auctions = when {
            query != null -> auctionManager.searchAuctions(query)
            categoryFilter != null -> auctionManager.getAuctionsByCategory(categoryFilter)
            else -> auctionManager.getActiveAuctions()
        }
            .filter { typeFilter.matches(it) }
            .let { sortMode.sort(it) }
    }

    private fun render() {
        clear()
        setBorderEdge()

        // Category filter buttons in top row
        val categories = AuctionCategory.entries
        for ((index, category) in categories.withIndex()) {
            val slot = index + 1
            if (slot >= 8) break

            val isSelected = categoryFilter == category
            val material = if (isSelected) Material.LIME_STAINED_GLASS_PANE else category.icon

            val categoryItem = InterfaceUtil.getNamedItemWithDescription(
                material,
                ComponentUtils.create(category.displayName, if (isSelected) NamedTextColor.GREEN else NamedTextColor.GRAY),
                if (isSelected)
                    ComponentUtils.create("Currently viewing", NamedTextColor.GREEN)
                else
                    ComponentUtils.create("Click to filter!", NamedTextColor.YELLOW)
            )

            setButton(slot, categoryItem) { _: InventoryClickEvent ->
                if (isSelected) {
                    openSubMenu(MenuAuctionBrowser(player, parentMenu, sortMode = sortMode, typeFilter = typeFilter))
                } else {
                    openSubMenu(MenuAuctionBrowser(player, parentMenu, categoryFilter = category, sortMode = sortMode, typeFilter = typeFilter))
                }
            }
        }

        // Auctions in middle area
        val totalItems = auctions.size
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

        for (slot in 0 until inventorySize) {
            if (itemIndexOffset >= totalItems) break
            if (getItem(slot) != null) continue

            val auction = auctions[itemIndexOffset]
            val auctionItem = createAuctionDisplayItem(auction)

            setButton(slot, auctionItem) { _: InventoryClickEvent ->
                openSubMenu(MenuAuctionView(player, auction, this))
            }
            itemIndexOffset++
        }

        // Pagination
        val displayPage = page + 1
        val displayPageMax = lastPage + 1

        setButton(
            (ROWS - 1) * 9,
            createNoRenderNamedItem(
                Material.BLACK_STAINED_GLASS_PANE,
                ComponentUtils.create("Previous Page ($displayPage/$displayPageMax)", NamedTextColor.GOLD)
            )
        ) { _: InventoryClickEvent ->
            page--
            render()
            sounds.playPagePrevious()
        }

        setButton(
            (ROWS - 1) * 9 + 8,
            createNoRenderNamedItem(
                Material.BLACK_STAINED_GLASS_PANE,
                ComponentUtils.create("Next Page ($displayPage/$displayPageMax)", NamedTextColor.GOLD)
            )
        ) { _: InventoryClickEvent ->
            page++
            render()
            sounds.playPageNext()
        }

        // Type filter button
        setButton(TYPE_FILTER_SLOT, buildTypeFilterItem()) { event: InventoryClickEvent ->
            typeFilter = if (event.isRightClick) typeFilter.previous() else typeFilter.next()
            page = 0
            refreshAuctions()
            render()
            sounds.playPageNext()
        }

        // Sort mode button
        setButton(SORT_SLOT, buildSortModeItem()) { event: InventoryClickEvent ->
            sortMode = if (event.isRightClick) sortMode.previous() else sortMode.next()
            page = 0
            refreshAuctions()
            render()
            sounds.playPageNext()
        }

        setBackButton()
    }

    private fun buildTypeFilterItem(): org.bukkit.inventory.ItemStack {
        val lore = mutableListOf<Component>()
        lore.add(ComponentUtils.EMPTY)
        for (filter in AuctionTypeFilter.entries) {
            val color = if (filter == typeFilter) NamedTextColor.GREEN else NamedTextColor.GRAY
            lore.add(ComponentUtils.create(filter.displayName, color))
        }
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Left-click to cycle forward", NamedTextColor.YELLOW))
        lore.add(ComponentUtils.create("Right-click to cycle backward", NamedTextColor.YELLOW))

        return InterfaceUtil.getNamedItemWithDescription(
            Material.HOPPER,
            ComponentUtils.create("Type: ${typeFilter.displayName}", NamedTextColor.GOLD),
            lore
        )
    }

    private fun buildSortModeItem(): org.bukkit.inventory.ItemStack {
        val lore = mutableListOf<Component>()
        lore.add(ComponentUtils.EMPTY)
        for (mode in AuctionSortMode.entries) {
            val color = if (mode == sortMode) NamedTextColor.GREEN else NamedTextColor.GRAY
            lore.add(ComponentUtils.create(mode.displayName, color))
        }
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Left-click to cycle forward", NamedTextColor.YELLOW))
        lore.add(ComponentUtils.create("Right-click to cycle backward", NamedTextColor.YELLOW))

        return InterfaceUtil.getNamedItemWithDescription(
            Material.COMPARATOR,
            ComponentUtils.create("Sort: ${sortMode.displayName}", NamedTextColor.GOLD),
            lore
        )
    }

    private fun createAuctionDisplayItem(auction: Auction): org.bukkit.inventory.ItemStack {
        val itemStack = auction.getItemStack()
        val meta = itemStack.itemMeta

        val lore = meta.lore()?.toMutableList() ?: mutableListOf()
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.merge(
            ComponentUtils.create("Seller: ", NamedTextColor.GRAY),
            ComponentUtils.create(auction.sellerName, NamedTextColor.WHITE)
        ))

        if (auction.type == AuctionType.BUY_IT_NOW) {
            lore.add(ComponentUtils.merge(
                ComponentUtils.create("Price: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(auction.startingPrice), NamedTextColor.GOLD)
            ))
            lore.add(ComponentUtils.create("[BIN]", NamedTextColor.GOLD))
        } else {
            lore.add(ComponentUtils.merge(
                ComponentUtils.create("Current Bid: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(auction.currentBid), NamedTextColor.GOLD)
            ))
            if (auction.hasBids()) {
                lore.add(ComponentUtils.merge(
                    ComponentUtils.create("Top Bidder: ", NamedTextColor.GRAY),
                    ComponentUtils.create(auction.highestBidderName!!, NamedTextColor.WHITE)
                ))
            }
        }

        lore.add(ComponentUtils.merge(
            ComponentUtils.create("Time Left: ", NamedTextColor.GRAY),
            ComponentUtils.create(auction.getFormattedTimeRemaining(), NamedTextColor.YELLOW)
        ))
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Click to view!", NamedTextColor.YELLOW))

        meta.lore(ComponentUtils.cleanItalics(lore))
        itemStack.itemMeta = meta
        return itemStack
    }

    companion object {
        private const val ROWS = 6
        private const val ITEMS_PER_ROW = 7
        private const val TYPE_FILTER_SLOT = 46
        private const val SORT_SLOT = 52
    }
}
