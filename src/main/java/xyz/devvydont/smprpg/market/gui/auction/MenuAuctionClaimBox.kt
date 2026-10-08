package xyz.devvydont.smprpg.market.gui.auction

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.InterfaceUtil
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.market.MarketService
import xyz.devvydont.smprpg.market.auction.PlayerAuctionData
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.Base64

/**
 * Menu for claiming expired/won auction items and sold coins.
 */
class MenuAuctionClaimBox(
    player: Player,
    parentMenu: MenuBase? = null
) : MenuBase(player, ROWS, parentMenu) {

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(Component.text("Auction - Claim Box"))
        render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    private fun render() {
        setBorderFull()
        setBackButton()

        val auctionManager = SMPRPG.getService(MarketService::class.java).auctionManager
        val data = auctionManager.getPlayerData(player.uniqueId.toString())

        // Show unclaimed coins info
        if (data.unclaimedCoins > 0) {
            val coinsItem = InterfaceUtil.getNamedItemWithDescription(
                Material.GOLD_INGOT,
                ComponentUtils.create("Unclaimed Coins", NamedTextColor.GOLD),
                ComponentUtils.merge(
                    ComponentUtils.create("Amount: ", NamedTextColor.GRAY),
                    ComponentUtils.create(EconomyService.formatMoney(data.unclaimedCoins), NamedTextColor.GOLD)
                )
            )
            setSlot(COINS_DISPLAY_SLOT, coinsItem)
        }

        // Show unclaimed items in the inner grid, skipping border columns
        var slot = ITEMS_START_SLOT
        for (itemData in data.unclaimedItems) {
            if (slot >= inventorySize - 9) break
            // Skip border columns (first and last column of each row)
            while (slot < inventorySize - 9 && (slot % 9 == 0 || slot % 9 == 8)) slot++
            if (slot >= inventorySize - 9) break

            try {
                val bytes = Base64.getDecoder().decode(itemData)
                val item = ItemStack.deserializeBytes(bytes)
                val meta = item.itemMeta

                val lore = meta.lore()?.toMutableList() ?: mutableListOf()
                lore.add(ComponentUtils.EMPTY)
                lore.add(ComponentUtils.create("Unclaimed auction item", NamedTextColor.GRAY))

                meta.lore(ComponentUtils.cleanItalics(lore))
                item.itemMeta = meta

                setSlot(slot, item)
            } catch (e: Exception) {
                SMPRPG.plugin.logger.warning("Failed to display claim item: ${e.message}")
            }
            slot++
        }

        // Claim All button
        if (data.hasUnclaimed()) {
            val claimItem = InterfaceUtil.getNamedItemWithDescription(
                Material.LIME_WOOL,
                ComponentUtils.create("Claim All", NamedTextColor.GREEN),
                ComponentUtils.create("Click to claim all items and coins!", NamedTextColor.YELLOW)
            )
            setButton(CLAIM_SLOT, claimItem) { _: InventoryClickEvent ->
                val (itemsClaimed, coinsClaimed) = auctionManager.claimItems(player)
                if (itemsClaimed > 0 || coinsClaimed > 0) {
                    val message = buildString {
                        append("Claimed")
                        if (itemsClaimed > 0) append(" $itemsClaimed item(s)")
                        if (coinsClaimed > 0) {
                            if (itemsClaimed > 0) append(" and")
                            append(" ${EconomyService.formatMoney(coinsClaimed)}")
                        }
                        append("!")
                    }
                    player.sendMessage(ComponentUtils.success(message))
                    playSuccessAnimation()
                    render()
                } else {
                    playInvalidAnimation()
                }
            }
        } else {
            val emptyItem = InterfaceUtil.getNamedItemWithDescription(
                Material.RED_STAINED_GLASS_PANE,
                ComponentUtils.create("Nothing to Claim", NamedTextColor.RED),
                ComponentUtils.create("Your claim box is empty!", NamedTextColor.GRAY)
            )
            setSlot(CLAIM_SLOT, emptyItem)
        }
    }

    companion object {
        private const val ROWS = 5
        private const val COINS_DISPLAY_SLOT = 4
        private const val ITEMS_START_SLOT = 10
        private const val CLAIM_SLOT = 40
    }
}
