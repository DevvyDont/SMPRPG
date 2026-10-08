package xyz.devvydont.smprpg.market.gui.auction

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.gui.InterfaceUtil
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.market.MarketConstants
import xyz.devvydont.smprpg.market.MarketService
import xyz.devvydont.smprpg.market.auction.AuctionType
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import kotlin.math.roundToLong

/**
 * Auction creation menu. Player places an item in the submission slot,
 * then configures price, duration, and auction type via a dialog form.
 */
class MenuAuctionCreate(
    player: Player,
    parentMenu: MenuBase? = null,
    private var selectedType: AuctionType = AuctionType.BUY_IT_NOW,
    private var selectedDurationIndex: Int = MarketConstants.AUCTION_DEFAULT_DURATION_INDEX,
    private var selectedPrice: Long = MarketConstants.AUCTION_MIN_PRICE,
    private var savedItem: ItemStack? = null,
    private var priceValid: Boolean = true
) : MenuBase(player, ROWS, parentMenu) {

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(Component.text("Create Auction"))
        render()
        savedItem?.let {
            inventory.setItem(ITEM_SLOT, it)
            savedItem = null
            updateConfirmButton()
        }
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true

        if (event.clickedInventory == null) return

        Bukkit.getScheduler().runTaskLater(plugin, Runnable { updateConfirmButton() }, 0L)

        if (event.clickedInventory!!.type == InventoryType.PLAYER) {
            event.isCancelled = false
            return
        }

        if (event.clickedInventory == inventory && event.slot == ITEM_SLOT) {
            event.isCancelled = false
        }
    }

    override fun handleInventoryClosed(event: InventoryCloseEvent) {
        super.handleInventoryClosed(event)
        giveItemToPlayer(ITEM_SLOT, true)
    }

    private fun updateConfirmButton() {
        val item = inventory.getItem(ITEM_SLOT)
        val hasItem = item != null && item.type != Material.AIR

        val (durationMs, durationName) = MarketConstants.AUCTION_DURATIONS[selectedDurationIndex]
        val listingFee = (selectedPrice * MarketConstants.AUCTION_LISTING_FEE_PERCENT).roundToLong().coerceAtLeast(1)

        val confirmItem = if (!priceValid) {
            InterfaceUtil.getNamedItemWithDescription(
                Material.RED_WOOL,
                ComponentUtils.create("Confirm Listing", NamedTextColor.RED),
                ComponentUtils.create("Fix your price via Edit Listing Settings!", NamedTextColor.GRAY)
            )
        } else if (hasItem) {
            InterfaceUtil.getNamedItemWithDescription(
                Material.LIME_WOOL,
                ComponentUtils.create("Confirm Listing", NamedTextColor.GREEN),
                ComponentUtils.merge(
                    ComponentUtils.create("Price: ", NamedTextColor.GRAY),
                    ComponentUtils.create(EconomyService.formatMoney(selectedPrice), NamedTextColor.GOLD)
                ),
                ComponentUtils.merge(
                    ComponentUtils.create("Duration: ", NamedTextColor.GRAY),
                    ComponentUtils.create(durationName, NamedTextColor.AQUA)
                ),
                ComponentUtils.merge(
                    ComponentUtils.create("Type: ", NamedTextColor.GRAY),
                    ComponentUtils.create(selectedType.displayName, NamedTextColor.AQUA)
                ),
                ComponentUtils.merge(
                    ComponentUtils.create("Fee: ", NamedTextColor.GRAY),
                    ComponentUtils.create(EconomyService.formatMoney(listingFee), NamedTextColor.GOLD)
                ),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Click to list!", NamedTextColor.YELLOW)
            )
        } else {
            InterfaceUtil.getNamedItemWithDescription(
                Material.RED_WOOL,
                ComponentUtils.create("Confirm Listing", NamedTextColor.RED),
                ComponentUtils.create("Place an item in the slot above first!", NamedTextColor.GRAY)
            )
        }

        setButton(CONFIRM_SLOT, confirmItem) { _: InventoryClickEvent ->
            if (!priceValid) {
                player.sendMessage(ComponentUtils.error("Set a valid price before listing!"))
                playInvalidAnimation()
                return@setButton
            }

            val clickedItem = inventory.getItem(ITEM_SLOT)
            if (clickedItem == null || clickedItem.type == Material.AIR) {
                player.sendMessage(ComponentUtils.error("Place an item in the slot first!"))
                playInvalidAnimation()
                return@setButton
            }

            val auctionManager = SMPRPG.getService(MarketService::class.java).auctionManager
            val itemClone = clickedItem.clone()

            inventory.setItem(ITEM_SLOT, null)

            val error = auctionManager.createAuction(player, itemClone, selectedType, selectedPrice, durationMs)
            if (error != null) {
                inventory.setItem(ITEM_SLOT, itemClone)
                player.sendMessage(ComponentUtils.error(error))
                playInvalidAnimation()
            } else {
                playSuccessAnimation()
                closeMenu()
            }
        }
    }

    private fun openSettingsDialog() {
        val savedItemCopy = inventory.getItem(ITEM_SLOT)?.clone()
        inventory.setItem(ITEM_SLOT, null)
        openSubMenu(
            DialogAuctionSettings(
                player, parentMenu, selectedType, selectedDurationIndex,
                selectedPrice, savedItemCopy
            )
        )
    }

    private fun render() {
        val inputItem = inventory.getItem(ITEM_SLOT)?.clone()

        setBorderFull()
        setBackButton()

        if (inputItem != null && inputItem.type != Material.AIR) {
            inventory.setItem(ITEM_SLOT, inputItem)
        } else {
            clearSlot(ITEM_SLOT)
        }

        // Auction type display
        val typeItem = InterfaceUtil.getNamedItemWithDescription(
            if (selectedType == AuctionType.BUY_IT_NOW) Material.EMERALD else Material.GOLD_INGOT,
            ComponentUtils.create("Type: ${selectedType.displayName}", NamedTextColor.AQUA),
            if (selectedType == AuctionType.BUY_IT_NOW)
                ComponentUtils.create("Players can buy instantly at the set price")
            else
                ComponentUtils.create("Players bid, highest bid wins when time expires")
        )
        setSlot(TYPE_SLOT, typeItem)

        // Duration display
        val (_, durationName) = MarketConstants.AUCTION_DURATIONS[selectedDurationIndex]
        val durationItem = InterfaceUtil.getNamedItemWithDescription(
            Material.CLOCK,
            ComponentUtils.create("Duration: $durationName", NamedTextColor.AQUA),
            ComponentUtils.create("How long the listing will be active")
        )
        setSlot(DURATION_SLOT, durationItem)

        // Price display
        val priceItem = if (priceValid) {
            InterfaceUtil.getNamedItemWithDescription(
                Material.SUNFLOWER,
                ComponentUtils.create("Price: ${EconomyService.formatMoney(selectedPrice)}", NamedTextColor.GOLD),
                ComponentUtils.create("The starting price for this listing")
            )
        } else {
            InterfaceUtil.getNamedItemWithDescription(
                Material.BARRIER,
                ComponentUtils.create("Invalid Price!", NamedTextColor.RED),
                ComponentUtils.create("Use Edit Listing Settings to set a valid price", NamedTextColor.GRAY)
            )
        }
        setSlot(PRICE_SLOT, priceItem)

        // Listing fee display
        val listingFee = (selectedPrice * MarketConstants.AUCTION_LISTING_FEE_PERCENT).roundToLong().coerceAtLeast(1)
        val feeItem = InterfaceUtil.getNamedItemWithDescription(
            Material.PAPER,
            ComponentUtils.create("Listing Fee", NamedTextColor.RED),
            ComponentUtils.merge(
                ComponentUtils.create("Fee: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(listingFee), NamedTextColor.GOLD)
            ),
            ComponentUtils.create("${(MarketConstants.AUCTION_LISTING_FEE_PERCENT * 100).toInt()}% of starting price", NamedTextColor.GRAY)
        )
        setSlot(FEE_SLOT, feeItem)

        // Edit listing settings buttons
        val editItem = InterfaceUtil.getNamedItemWithDescription(
            Material.WRITABLE_BOOK,
            ComponentUtils.create("Edit Listing Settings", NamedTextColor.YELLOW),
            ComponentUtils.create("Click to change price, duration, and type!", NamedTextColor.GRAY)
        )
        setButton(EDIT_SLOT, editItem) { _: InventoryClickEvent ->
            sounds.playActionConfirm()
            openSettingsDialog()
        }
        setButton(EDIT_SLOT_2, editItem.clone()) { _: InventoryClickEvent ->
            sounds.playActionConfirm()
            openSettingsDialog()
        }

        // Confirm button
        updateConfirmButton()
    }

    companion object {
        private const val ROWS = 5
        private const val ITEM_SLOT = 13
        private const val TYPE_SLOT = 20
        private const val FEE_SLOT = 22
        private const val DURATION_SLOT = 24
        private const val PRICE_SLOT = 31
        private const val EDIT_SLOT = 29
        private const val EDIT_SLOT_2 = 33
        private const val CONFIRM_SLOT = 40
    }
}
