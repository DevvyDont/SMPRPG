package xyz.devvydont.smprpg.market.gui.auction

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.scheduler.BukkitTask
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.InterfaceUtil
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.market.MarketConstants
import xyz.devvydont.smprpg.market.MarketService
import xyz.devvydont.smprpg.market.auction.Auction
import xyz.devvydont.smprpg.market.auction.AuctionStatus
import xyz.devvydont.smprpg.market.auction.AuctionType
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import kotlin.math.roundToLong

/**
 * Single auction detail view with bid/buy actions.
 */
class MenuAuctionView(
    player: Player,
    private val auction: Auction,
    parentMenu: MenuBase? = null,
    private var selectedRaiseIndex: Int = MarketConstants.AUCTION_DEFAULT_RAISE_INDEX
) : MenuBase(player, ROWS, parentMenu) {

    private var refreshTask: BukkitTask? = null

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(Component.text("Auction - ${auction.itemDisplayName}"))
        render()
        startRefreshTask()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    override fun handleInventoryClosed(event: InventoryCloseEvent) {
        stopRefreshTask()
    }

    private fun startRefreshTask() {
        refreshTask = Bukkit.getScheduler().runTaskTimer(SMPRPG.plugin, Runnable {
            if (auction.status != AuctionStatus.ACTIVE) {
                val message = when (auction.status) {
                    AuctionStatus.COMPLETED -> "This auction has been sold!"
                    AuctionStatus.CANCELLED -> "This auction has been cancelled!"
                    AuctionStatus.EXPIRED_UNCLAIMED -> "This auction has expired!"
                    else -> "This auction is no longer available!"
                }
                player.sendMessage(ComponentUtils.alert(message))
                closeMenu()
                return@Runnable
            }

            if (auction.isExpired()) {
                player.sendMessage(ComponentUtils.alert("This auction has expired!"))
                closeMenu()
                return@Runnable
            }

            render()
        }, REFRESH_INTERVAL_TICKS, REFRESH_INTERVAL_TICKS)
    }

    private fun stopRefreshTask() {
        refreshTask?.cancel()
        refreshTask = null
    }

    private fun render() {
        setBorderFull()
        setBackButton()

        // Display item in center
        setSlot(ITEM_SLOT, auction.getItemStack())

        // Auction info panel
        renderInfoPanel()

        val isOwnAuction = player.uniqueId.toString() == auction.sellerUUID

        if (auction.type == AuctionType.BUY_IT_NOW && !isOwnAuction) {
            renderBuyItNowButton()
        } else if (auction.type == AuctionType.BID && !isOwnAuction) {
            renderBidButtons()
        }

        if (isOwnAuction && !auction.hasBids()) {
            renderCancelButton()
        }
    }

    private fun renderInfoPanel() {
        val infoLore = mutableListOf<Component>()
        infoLore.add(ComponentUtils.merge(
            ComponentUtils.create("Seller: ", NamedTextColor.GRAY),
            ComponentUtils.create(auction.sellerName, NamedTextColor.WHITE)
        ))
        infoLore.add(ComponentUtils.merge(
            ComponentUtils.create("Type: ", NamedTextColor.GRAY),
            ComponentUtils.create(auction.type.displayName, NamedTextColor.AQUA)
        ))

        if (auction.type == AuctionType.BUY_IT_NOW) {
            infoLore.add(ComponentUtils.merge(
                ComponentUtils.create("Price: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(auction.startingPrice), NamedTextColor.GOLD)
            ))
        } else {
            infoLore.add(ComponentUtils.merge(
                ComponentUtils.create("Starting Price: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(auction.startingPrice), NamedTextColor.GOLD)
            ))
            infoLore.add(ComponentUtils.merge(
                ComponentUtils.create("Current Bid: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(auction.currentBid), NamedTextColor.GOLD)
            ))
            if (auction.hasBids()) {
                infoLore.add(ComponentUtils.merge(
                    ComponentUtils.create("Top Bidder: ", NamedTextColor.GRAY),
                    ComponentUtils.create(auction.highestBidderName!!, NamedTextColor.WHITE)
                ))
            }
        }

        infoLore.add(ComponentUtils.EMPTY)
        infoLore.add(ComponentUtils.merge(
            ComponentUtils.create("Time Left: ", NamedTextColor.GRAY),
            ComponentUtils.create(auction.getFormattedTimeRemaining(), NamedTextColor.YELLOW)
        ))

        val infoItem = InterfaceUtil.getNamedItemWithDescription(
            Material.OAK_SIGN,
            ComponentUtils.create("Auction Info", NamedTextColor.GREEN),
            infoLore
        )
        setSlot(INFO_SLOT, infoItem)
    }

    private fun renderBuyItNowButton() {
        val buyItem = InterfaceUtil.getNamedItemWithDescription(
            Material.EMERALD,
            ComponentUtils.create("Buy It Now", NamedTextColor.GREEN),
            ComponentUtils.merge(
                ComponentUtils.create("Price: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(auction.startingPrice), NamedTextColor.GOLD)
            ),
            ComponentUtils.EMPTY,
            ComponentUtils.create("Click to purchase!", NamedTextColor.YELLOW)
        )

        setButton(BUY_SLOT, buyItem) { _: InventoryClickEvent ->
            val auctionManager = SMPRPG.getService(MarketService::class.java).auctionManager
            val error = auctionManager.buyItNow(player, auction)
            if (error != null) {
                player.sendMessage(ComponentUtils.error(error))
                playInvalidAnimation()
            } else {
                playSuccessAnimation()
                closeMenu()
            }
        }
    }

    private fun renderBidButtons() {
        val isTopBidder = auction.hasBids() && player.uniqueId.toString() == auction.highestBidderUUID

        if (isTopBidder) {
            val item = InterfaceUtil.getNamedItemWithDescription(
                Material.GOLD_BLOCK,
                ComponentUtils.create("Highest Bidder", NamedTextColor.GREEN),
                ComponentUtils.merge(
                    ComponentUtils.create("Your Bid: ", NamedTextColor.GRAY),
                    ComponentUtils.create(EconomyService.formatMoney(auction.currentBid), NamedTextColor.GOLD)
                ),
                ComponentUtils.EMPTY,
                ComponentUtils.create("You are already the highest bidder!", NamedTextColor.GREEN)
            )
            setSlot(BUY_SLOT, item)
            return
        }

        if (auction.hasBids()) {
            renderRaiseToggle()
            renderPlaceBidButton(calculateBidAmount())
        } else {
            // First bid — no raise toggle, just a bid button at starting price
            renderPlaceBidButton(auction.startingPrice)
        }
    }

    /**
     * Calculates the bid amount based on the current bid and selected raise percentage.
     */
    private fun calculateBidAmount(): Long {
        val currentBid = auction.currentBid
        val raisePercent = MarketConstants.AUCTION_BID_RAISE_OPTIONS[selectedRaiseIndex].first
        val raiseAmount = (currentBid * raisePercent).roundToLong().coerceAtLeast(1)
        return currentBid + raiseAmount
    }

    private fun renderRaiseToggle() {
        val options = MarketConstants.AUCTION_BID_RAISE_OPTIONS
        val currentBid = auction.currentBid

        val lore = mutableListOf<Component>()
        for ((index, option) in options.withIndex()) {
            val (percent, label) = option
            val raiseCoins = (currentBid * percent).roundToLong().coerceAtLeast(1)
            val coinText = EconomyService.formatMoney(raiseCoins)

            if (index == selectedRaiseIndex) {
                lore.add(ComponentUtils.create("> $label ($coinText)", NamedTextColor.GREEN))
            } else {
                lore.add(ComponentUtils.create("  $label ($coinText)", NamedTextColor.GRAY))
            }
        }

        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Left-click: next", NamedTextColor.YELLOW))
        lore.add(ComponentUtils.create("Right-click: previous", NamedTextColor.YELLOW))

        val currentLabel = options[selectedRaiseIndex].second
        val toggleItem = InterfaceUtil.getNamedItemWithDescription(
            Material.COMPARATOR,
            ComponentUtils.create("Raise Amount: $currentLabel", NamedTextColor.AQUA),
            lore
        )

        setButton(RAISE_TOGGLE_SLOT, toggleItem) { event: InventoryClickEvent ->
            val size = options.size
            selectedRaiseIndex = if (event.click == ClickType.RIGHT) {
                (selectedRaiseIndex - 1 + size) % size
            } else {
                (selectedRaiseIndex + 1) % size
            }
            render()
        }
    }

    private fun renderPlaceBidButton(bidAmount: Long) {
        val balance = SMPRPG.getService(EconomyService::class.java).getMoney(player)

        val lore = mutableListOf<Component>()
        if (auction.hasBids()) {
            lore.add(ComponentUtils.merge(
                ComponentUtils.create("Current Bid: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(auction.currentBid), NamedTextColor.GOLD)
            ))
            val raiseCoins = bidAmount - auction.currentBid
            lore.add(ComponentUtils.merge(
                ComponentUtils.create("Raise: ", NamedTextColor.GRAY),
                ComponentUtils.create("+${EconomyService.formatMoney(raiseCoins)}", NamedTextColor.GREEN)
            ))
        }
        lore.add(ComponentUtils.merge(
            ComponentUtils.create("Your Balance: ", NamedTextColor.GRAY),
            ComponentUtils.create(EconomyService.formatMoney(balance), NamedTextColor.GOLD)
        ))
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Click to place bid!", NamedTextColor.YELLOW))

        val bidItem = InterfaceUtil.getNamedItemWithDescription(
            Material.GOLD_INGOT,
            ComponentUtils.create("Place Bid: ${EconomyService.formatMoney(bidAmount)}", NamedTextColor.GOLD),
            lore
        )

        // Center the bid button when there's no raise toggle (first bid)
        val slotToUse = if (auction.hasBids()) BID_SLOT else BUY_SLOT

        setButton(slotToUse, bidItem) { _: InventoryClickEvent ->
            val auctionManager = SMPRPG.getService(MarketService::class.java).auctionManager
            val error = auctionManager.placeBid(player, auction, bidAmount)
            if (error != null) {
                player.sendMessage(ComponentUtils.error(error))
                playInvalidAnimation()
            } else {
                playSuccessAnimation()
                render()
            }
        }
    }

    private fun renderCancelButton() {
        val cancelItem = InterfaceUtil.getNamedItemWithDescription(
            Material.BARRIER,
            ComponentUtils.create("Cancel Auction", NamedTextColor.RED),
            ComponentUtils.create("Click to cancel this auction!", NamedTextColor.YELLOW)
        )

        setButton(CANCEL_SLOT, cancelItem) { _: InventoryClickEvent ->
            val auctionManager = SMPRPG.getService(MarketService::class.java).auctionManager
            val error = auctionManager.cancelAuction(player, auction)
            if (error != null) {
                player.sendMessage(ComponentUtils.error(error))
                playInvalidAnimation()
            } else {
                playSuccessAnimation()
                closeMenu()
            }
        }
    }

    companion object {
        private const val ROWS = 5
        private const val ITEM_SLOT = 13
        private const val INFO_SLOT = 15
        private const val BUY_SLOT = 22
        private const val BID_SLOT = 20
        private const val RAISE_TOGGLE_SLOT = 24
        private const val CANCEL_SLOT = 41
        private const val REFRESH_INTERVAL_TICKS = 20L // 1 second
    }
}
