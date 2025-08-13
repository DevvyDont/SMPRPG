package xyz.devvydont.smprpg.events.crafting

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe

/**
 * An extension off of the [org.bukkit.event.inventory.PrepareItemCraftEvent] event.
 * This event only fires when the above event is fired and a potential candidate for an item being transmuted
 * into another one is possible. The logic had to be hacked in since the Recipe API sucks, but if you need to listen
 * to the event and modify it before it occurs you can use this event.
 */
class PrepareItemCraftTransmuteEvent(
    /**
     * Get the recipe that is causing this transmute event.
     * @return The recipe that is responsible for the transmute event.
     */
    val recipe: Recipe,
    /**
     * Get the inventory that is relative to this event.
     * @return A crafting inventory.
     */
    val inventory: CraftingInventory,
    /**
     * Get the view that is relative to this event.
     * @return The inventory view.
     */
    val view: InventoryView,
    /**
     * Get the ingredient that is being transmuted. This item *should* have its data copied to the new item.
     * @return The item that's being transmuted.
     */
    val transmuteIngredient: ItemStack,
    result: ItemStack
) : Event(), Cancellable {
    private var cancelled = false
    /**
     * The result of the recipe. If it's null, that means the item will not force update itself in the original event.
     * @return The item to force set as a result in the crafting grid.
     */
    /**
     * Set the result to force as a result of the craft. Can be set to null to cancel the craft.
     * @param recipeResult The new item to make the recipe craft.
     */
    var recipeResult: ItemStack? = result

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    /**
     * Checks if this event is cancelled. If it is, no loot will generate.
     * @return True if cancelled, False otherwise.
     */
    override fun isCancelled(): Boolean {
        return this.cancelled
    }

    /**
     * Cancels the event.
     * @param cancel `true` if you wish to cancel this event
     */
    override fun setCancelled(cancel: Boolean) {
        this.cancelled = cancel
    }

    companion object {
        val handlerList: HandlerList = HandlerList()
    }
}
