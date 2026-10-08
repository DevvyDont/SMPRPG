package xyz.devvydont.smprpg.events

import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

class CustomEnchantItemEvent(val player : Player, val table : Block, val item : ItemStack, val enchant : Enchantment, val level : Int) : Event(), Cancellable {
    private var cancelled : Boolean = false

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    override fun isCancelled(): Boolean {
        return this.cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        this.cancelled = cancel
    }

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}
