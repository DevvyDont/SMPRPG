package xyz.devvydont.smprpg.gui.economy

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuButtonClickHandler
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.blueprints.economy.CustomItemCoin
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.services.EconomyService.Companion.formatMoney
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import kotlin.math.min

class MenuWithdraw(owner: Player) : MenuBase(owner, 3) {
    private var totalWithdrawn = 0
    private val coins: Array<CustomItemCoin> = arrayOf(
        SMPRPG.getService(ItemService::class.java).getBlueprint(CustomItemType.COPPER_COIN) as CustomItemCoin,
        SMPRPG.getService(ItemService::class.java).getBlueprint(CustomItemType.SILVER_COIN) as CustomItemCoin,
        SMPRPG.getService(ItemService::class.java).getBlueprint(CustomItemType.GOLD_COIN) as CustomItemCoin,
        SMPRPG.getService(ItemService::class.java).getBlueprint(CustomItemType.PLATINUM_COIN) as CustomItemCoin,
        SMPRPG.getService(ItemService::class.java).getBlueprint(CustomItemType.ENCHANTED_COIN) as CustomItemCoin,
    )
    private val economyService: EconomyService = SMPRPG.getService(EconomyService::class.java)

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        // Prepare the inventory
        event.titleOverride(ComponentUtils.create("Withdraw Coins", NamedTextColor.BLACK))

        // Render the UI
        this.renderMenu()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        // This UI uses buttons, so there's no code here.
        // But we still need to cancel the event to prevent stealing the borders.
        event.isCancelled = true
        this.playInvalidAnimation()
    }

    override fun handleInventoryClosed(event: InventoryCloseEvent) {
        if (this.totalWithdrawn == 0) return

        this.player.sendMessage(
            ComponentUtils.success(
                ComponentUtils.merge(
                    ComponentUtils.create("You've withdrawn ", NamedTextColor.GREEN),
                    ComponentUtils.create(formatMoney(this.totalWithdrawn), NamedTextColor.GOLD),
                    ComponentUtils.create("! Your balance is now ", NamedTextColor.GREEN),
                    ComponentUtils.create(this.economyService.formatMoney(this.player), NamedTextColor.GOLD)
                )
            )
        )
    }

    /**
     * Clears and re-renders the menu UI.
     */
    private fun renderMenu() {
        // Reset the UI
        this.clear()
        this.setBorderFull()

        // Create the balance item
        this.setSlot(
            4, createNamedItem(
                Material.PAPER, ComponentUtils.merge(
                    ComponentUtils.create("Your current balance is ", NamedTextColor.GREEN),
                    ComponentUtils.create(this.economyService.formatMoney(this.player), NamedTextColor.GOLD)
                )
            )
        )

        // Starts at row 2 column 2 (slot 10)
        var currentSlot = 10
        val playerBalance = this.economyService.getMoney(this.player)
        for (coin in this.coins) {
            currentSlot++

            // They cannot afford the coin, add filler
            val coinStack = coin.generate()
            if (playerBalance < coin.getWorth(coinStack)) {
                val clayText = String.format("You are %s short!", formatMoney(playerBalance - coin.getWorth(coinStack)))
                val clayName = ComponentUtils.create(clayText, NamedTextColor.RED)
                this.setButton(
                    currentSlot,
                    createNamedItem(Material.CLAY_BALL, clayName)
                ) { e: InventoryClickEvent -> this.playInvalidAnimation() }
                continue
            }

            // They can afford the coin, create a button.
            this.setButton(currentSlot, coin.generate()) { e: InventoryClickEvent ->
                when (e.click) {
                    ClickType.LEFT -> this.performWithdrawal(coin, 1)
                    ClickType.RIGHT -> this.performWithdrawal(coin, 50)
                    ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT -> this.performWithdrawal(coin, 99)
                    else -> {}
                }
            }
        }
    }

    /**
     * Deducts money from the player and adds coins to their inventory.
     */
    private fun performWithdrawal(coin: CustomItemCoin, desiredStackSize: Int) {
        // First lets calculate how much of this coin the player can withdraw.
        val coinStack = coin.generate()
        val currentBalance = this.economyService.getMoney(this.player)
        val maxCoinsPlayerCanAfford = currentBalance / coin.getWorth(coinStack)
        val amountOfCoinsToGive = min(desiredStackSize.toLong(), maxCoinsPlayerCanAfford)
        val totalCost = amountOfCoinsToGive * coin.getWorth(coinStack)

        // Ensure the player has enough money.
        if (amountOfCoinsToGive == 0L || currentBalance < totalCost) {
            this.playInvalidAnimation()
            this.player.sendMessage(ComponentUtils.error("You cannot afford to withdrawal this coin."))
            return
        }

        // Try to take the money out of their account.
        val moneyTakenSuccessfully = this.economyService.takeMoney(this.player, totalCost.toDouble())
        if (!moneyTakenSuccessfully) {
            this.playInvalidAnimation()
            this.player.sendMessage(ComponentUtils.error("Something went wrong. Transaction was canceled."))
            return
        }

        // Spin up the money printer and make some coins.
        val mintedCoins = coin.generate()
        mintedCoins.amount = amountOfCoinsToGive.toInt()

        // Hand out the money to the player.
        val overflowItems = this.player.inventory.addItem(mintedCoins)
        for (entry in overflowItems.entries) this.player.world
            .dropItemNaturally(this.player.eyeLocation, entry.value!!)

        // Tell them it was successful
        this.player.sendMessage(
            ComponentUtils.success(
                ComponentUtils.merge(
                    ComponentUtils.create("You withdrew ", NamedTextColor.GREEN),
                    ComponentUtils.create(formatMoney(totalCost), NamedTextColor.GOLD),
                    ComponentUtils.create(" from your account! ", NamedTextColor.GREEN),
                    ComponentUtils.create("Your balance is now ", NamedTextColor.GREEN),
                    ComponentUtils.create(this.economyService.formatMoney(this.player), NamedTextColor.GOLD)
                )
            )
        )

        // Add to the running total
        this.totalWithdrawn += totalCost.toInt()
        this.renderMenu()
        this.playSuccessAnimation()
    }
}
