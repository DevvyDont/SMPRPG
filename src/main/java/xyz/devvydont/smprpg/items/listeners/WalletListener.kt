package xyz.devvydont.smprpg.items.listeners

import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.economy.MenuWithdrawWallet
import xyz.devvydont.smprpg.items.blueprints.economy.CustomItemCoin
import xyz.devvydont.smprpg.items.blueprints.equipment.WalletBlueprint
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

class WalletListener: ToggleableListener() {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPickupCoin(event: PlayerAttemptPickupItemEvent) {
        // Ignore the event if its cancelled
        if (event.isCancelled) return

        val itemStack = event.item.itemStack.clone()
        val itemService = SMPRPG.getService(ItemService::class.java)
        val bp = itemService.getBlueprint(itemStack)

        // If this coin is owned by someone and we are trying to pick it up don't play the noise
        if (event.item.owner != null && event.item.owner != event.player.uniqueId) return

        // Ignore this item pickup event if the type of the item is not a coin
        if (bp !is CustomItemCoin) return

        // Attempt to fill wallets first
        val wallets = mutableListOf<ItemStack>()
        val contents = event.player.inventory.contents
        for (invItem in contents) {
            if (invItem != null) {
                if (itemService.getBlueprint(invItem) is WalletBlueprint) wallets.add(invItem)
            }
        }
        val coinsPerItem = CustomItemCoin.getCoinValue(bp.customItemType)
        val coinsInStack = coinsPerItem * itemStack.amount
        for (wallet in wallets) {
            val walletBp = itemService.getBlueprint(wallet) as WalletBlueprint
            val walletCoins = walletBp.maxCoins - wallet.getData(DataComponentTypes.DAMAGE)!!
            if (walletCoins + coinsInStack <= walletBp.maxCoins) {
                // Capacity was not met, we can consume the entire stack and break.
                wallet.setData(DataComponentTypes.DAMAGE, wallet.getData(DataComponentTypes.DAMAGE)!! - coinsInStack)
                itemStack.amount = 0
                event.item.itemStack = itemStack
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
                walletBp.updateItemData(wallet)
                break
            }
        }

        // Ignore this event if the item stack count isn't changing
        if (event.remaining == itemStack.amount) return

        // Play cute noise :3
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
    }

    @EventHandler
    fun onPlayerUseWallet(event: PlayerInteractEvent) {
        if (event.item != null && (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)) {
            val bp = SMPRPG.getService(ItemService::class.java).getBlueprint(event.item!!)
            if (bp is WalletBlueprint) {
                MenuWithdrawWallet(event.player, event.item!!, bp).openMenu()
            }
        }
    }
}