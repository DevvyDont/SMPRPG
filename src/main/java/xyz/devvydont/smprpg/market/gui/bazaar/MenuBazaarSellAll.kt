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
import xyz.devvydont.smprpg.market.bazaar.BazaarSellEntry
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Confirmation menu for selling all bazaar-tradeable items in the player's inventory at once.
 */
class MenuBazaarSellAll(
    player: Player,
    parentMenu: MenuBase
) : MenuBase(player, ROWS, parentMenu) {

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(Component.text("Bazaar - Sell All"))
        render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
        playInvalidAnimation()
    }

    private fun render() {
        clear()
        setBorderFull()
        setBackButton(BACK_SLOT)

        val bazaarManager = SMPRPG.getService(MarketService::class.java).bazaarManager
        val entries = bazaarManager.calculateSellAll(player)

        if (entries.isEmpty()) {
            renderNothingToSell()
            return
        }

        renderConfirmButton(entries)
    }

    private fun renderNothingToSell() {
        val barrier = InterfaceUtil.getNamedItemWithDescription(
            Material.BARRIER,
            ComponentUtils.create("Nothing to Sell", NamedTextColor.RED),
            ComponentUtils.create("You don't have any bazaar items!", NamedTextColor.GRAY)
        )
        setSlot(CONFIRM_SLOT, barrier)
    }

    private fun renderConfirmButton(entries: List<BazaarSellEntry>) {
        val totalPayout = entries.sumOf { it.payout }
        val loreLines = mutableListOf<Component>(
            ComponentUtils.merge(
                ComponentUtils.create("Total Payout: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(totalPayout), NamedTextColor.GOLD)
            ),
            ComponentUtils.EMPTY
        )

        val displayEntries = if (entries.size > MAX_DISPLAY_ENTRIES) {
            entries.take(MAX_DISPLAY_ENTRIES)
        } else {
            entries
        }

        for (entry in displayEntries) {
            loreLines.add(
                ComponentUtils.merge(
                    ComponentUtils.create("${entry.quantity}x ", NamedTextColor.WHITE),
                    ComponentUtils.create(entry.displayName, NamedTextColor.AQUA),
                    ComponentUtils.create(" — ", NamedTextColor.DARK_GRAY),
                    ComponentUtils.create(EconomyService.formatMoney(entry.payout), NamedTextColor.GOLD)
                )
            )
        }

        if (entries.size > MAX_DISPLAY_ENTRIES) {
            val remaining = entries.size - MAX_DISPLAY_ENTRIES
            loreLines.add(
                ComponentUtils.create("...and $remaining more items", NamedTextColor.DARK_GRAY)
            )
        }

        loreLines.add(ComponentUtils.EMPTY)
        loreLines.add(ComponentUtils.create("Click to confirm!", NamedTextColor.YELLOW))

        val confirmItem = InterfaceUtil.getNamedItemWithDescription(
            Material.GREEN_WOOL,
            ComponentUtils.create("Confirm Sell All", NamedTextColor.GREEN),
            loreLines
        )

        setButton(CONFIRM_SLOT, confirmItem) { _: InventoryClickEvent ->
            val bazaarManager = SMPRPG.getService(MarketService::class.java).bazaarManager
            val freshEntries = bazaarManager.calculateSellAll(player)
            if (freshEntries.isEmpty()) {
                playInvalidAnimation()
                render()
                return@setButton
            }
            val error = bazaarManager.executeSellAll(player, freshEntries)
            if (error != null) {
                player.sendMessage(ComponentUtils.error(error))
                playInvalidAnimation()
            } else {
                playSuccessAnimation()
                render()
            }
        }
    }

    companion object {
        private const val ROWS = 3
        private const val CONFIRM_SLOT = 13
        private const val BACK_SLOT = 22
        private const val MAX_DISPLAY_ENTRIES = 15
    }
}
