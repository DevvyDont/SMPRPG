package xyz.devvydont.smprpg.market.gui.auction

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.market.MarketConstants
import xyz.devvydont.smprpg.market.MarketService
import xyz.devvydont.smprpg.market.auction.Auction
import xyz.devvydont.smprpg.market.auction.AuctionType
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Displays a player's own active auctions with management options.
 */
class MenuAuctionManage(
    player: Player,
    parentMenu: MenuBase? = null
) : MenuBase(player, ROWS, parentMenu) {

    private var page = 0
    private var auctions: List<Auction> = emptyList()

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(Component.text("Your Auctions"))
        refreshAuctions()
        render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    private fun refreshAuctions() {
        val auctionManager = SMPRPG.getService(MarketService::class.java).auctionManager
        auctions = auctionManager.getActiveAuctionsByPlayer(player.uniqueId.toString())
            .sortedByDescending { it.createdAt }
    }

    private fun render() {
        clear()
        setBorderEdge()

        // Header info
        val headerItem = createNoRenderNamedItem(
            Material.BLACK_STAINED_GLASS_PANE,
            ComponentUtils.merge(
                ComponentUtils.create("Active: ", NamedTextColor.GRAY),
                ComponentUtils.create("${auctions.size}/${MarketConstants.AUCTION_MAX_ACTIVE_PER_PLAYER}", NamedTextColor.GOLD)
            )
        )
        setSlot(4, headerItem)

        // Display auctions
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
            val displayItem = auction.getItemStack()
            val meta = displayItem.itemMeta

            val lore = meta.lore()?.toMutableList() ?: mutableListOf()
            lore.add(ComponentUtils.EMPTY)

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
                        ComponentUtils.create("Bids: ", NamedTextColor.GRAY),
                        ComponentUtils.create(auction.highestBidderName!!, NamedTextColor.WHITE)
                    ))
                } else {
                    lore.add(ComponentUtils.create("No bids yet", NamedTextColor.GRAY))
                }
            }

            lore.add(ComponentUtils.merge(
                ComponentUtils.create("Time Left: ", NamedTextColor.GRAY),
                ComponentUtils.create(auction.getFormattedTimeRemaining(), NamedTextColor.YELLOW)
            ))
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Click to manage!", NamedTextColor.YELLOW))

            meta.lore(ComponentUtils.cleanItalics(lore))
            displayItem.itemMeta = meta

            setButton(slot, displayItem) { _: InventoryClickEvent ->
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

        setBackButton()
    }

    companion object {
        private const val ROWS = 6
        private const val ITEMS_PER_ROW = 7
    }
}
