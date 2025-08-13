package xyz.devvydont.smprpg.events

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

class CustomItemQuantityRollDropEvent(
    /**
     * The player involved with this drop roll
     *
     * @return
     */
    val player: Player,
    /**
     * The tool used to trigger this rolling event
     *
     * @return
     */
    @JvmField val tool: ItemStack?, val min: Int, val max: Int, private val initialAmount: Int, drop: ItemStack
) : Event(), Cancellable {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    /**
     * Returns the item currently held in the offhand
     *
     * @return
     */
    val offhand: ItemStack?
    private val drop: ItemStack
    @JvmField
    var amount: Int
    private var isCancelled = false

    init {
        this.offhand = player.getInventory().getItemInOffHand()
        this.amount = initialAmount
        this.drop = drop
    }

    /**
     * The drop to be potentially dropped
     *
     * @return
     */
    fun getDrop(): ItemStack {
        return drop.clone()
    }

    /**
     * The initial unchanged drop amount
     *
     * @return
     */
    fun getInitialAmount(): Double {
        return initialAmount.toDouble()
    }

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        isCancelled = cancelled
    }

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}
