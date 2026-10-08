package xyz.devvydont.smprpg.gui.economy

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.items.blueprints.equipment.WalletBlueprint
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.services.EconomyService.Companion.formatMoney
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class MenuDeposit(owner: Player) : MenuBase(owner, 5) {
    private val itemService: ItemService = SMPRPG.getService(ItemService::class.java)
    private val economyService: EconomyService = SMPRPG.getService(EconomyService::class.java)

    private fun isSellable(item: ItemStack): Boolean {
        val blueprint = ItemService.blueprint(item)
        return blueprint is ISellable || blueprint is WalletBlueprint
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        // Prepare the inventory
        event.titleOverride(ComponentUtils.create("Sell Items", NamedTextColor.BLACK))
        this.setMaxStackSize(100)

        // Render the UI
        this.clear()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        // If an inventory was clicked and the slot clicked was empty, allow it
        if (event.clickedInventory != null && event.clickedInventory!!.getItem(event.slot) == null) {
            event.isCancelled = false
            return
        }

        // No item involved, don't allow it
        val item = event.currentItem
        if (item == null) {
            event.isCancelled = true
            return
        }

        // If the item clicked is not sellable, we can't do anything with it.
        val itemBlueprint = this.itemService.getBlueprint(event.getCurrentItem()!!)
        event.isCancelled = !isSellable(item)

        // If the item is sellable, but not worth anything, don't let them sell it.
        // This is usually the case with some CraftEngine items, as they use a universal parent blueprint.
        if (itemBlueprint is ISellable) {
            if ((itemBlueprint as ISellable).getWorth(event.currentItem) == 0)
                event.isCancelled = true
        }

        // If the item clicked is enchanted or reforged, we should prevent the item from being shift clicked.
        if (event.isShiftClick && (!event.getCurrentItem()!!.enchantments.isEmpty() || itemBlueprint.isReforged(
                event.getCurrentItem()
            ))
        ) {
            val error = ComponentUtils.merge(
                ComponentUtils.create("WHOA THERE!", NamedTextColor.RED, TextDecoration.BOLD),
                ComponentUtils.create(" Do you really want to sell this? Manually drag the item in the menu if so.")
            )
            player.sendMessage(ComponentUtils.alert(error, NamedTextColor.RED))
            player.playSound(player.location, Sound.ENTITY_VILLAGER_DEATH, 1f, 1.5f)
            event.isCancelled = true
        }
    }

    override fun handleInventoryClosed(event: InventoryCloseEvent) {
        // Work out how much was deposited.
        var quantitySold = 0
        var amountToCredit = 0
        for (depositedItem in this.items) {
            if (depositedItem == null) {
                continue
            }

            val itemBlueprint = this.itemService.getBlueprint(depositedItem)

            if (itemBlueprint is WalletBlueprint) {
                amountToCredit += itemBlueprint.getBalance(depositedItem)
                quantitySold++
                itemBlueprint.setBalance(depositedItem, 0)
                itemBlueprint.updateItemData(depositedItem)
                giveItemToPlayer(depositedItem)
                continue
            }

            if (itemBlueprint is ISellable) {
                quantitySold += depositedItem.amount
                amountToCredit += itemBlueprint.getWorth(depositedItem)
                depositedItem.amount = 0
            }
        }

        // Ignore if nothing was deposited.
        if (amountToCredit <= 0) {
            return
        }

        this.economyService.addMoney(this.player, amountToCredit.toDouble())
        this.player.sendMessage(
            ComponentUtils.success(
                ComponentUtils.merge(
                    ComponentUtils.create("You sold ", NamedTextColor.GREEN),
                    ComponentUtils.create(quantitySold.toString(), NamedTextColor.AQUA),
                    ComponentUtils.create(" items totaling ", NamedTextColor.GREEN),
                    ComponentUtils.create(formatMoney(amountToCredit), NamedTextColor.GOLD),
                    ComponentUtils.create("! Your balance is now ", NamedTextColor.GREEN),
                    ComponentUtils.create(this.economyService.formatMoney(this.player), NamedTextColor.GOLD)
                )
            )
        )
        this.sounds.playActionConfirm()
    }
}
