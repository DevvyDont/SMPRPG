package xyz.devvydont.smprpg.items.blueprints.debug

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.ItemService

class InfinirocketBlueprint(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    Listener {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        itemStack.addUnsafeEnchantment(EnchantmentService.KEEPING_BLESSING.enchantment, 1)
    }

    override fun wantFakeEnchantGlow(): Boolean {
        return true
    }

    @EventHandler
    private fun onRocket(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_AIR) return

        if (event.item == null) return
        if (!isItemOfType(event.item!!)) return

        event.setCancelled(true)
    }

    @EventHandler
    private fun onRocket(event: PlayerElytraBoostEvent) {
        if (!isItemOfType(event.itemStack)) return

        event.setShouldConsume(false)
    }
}
