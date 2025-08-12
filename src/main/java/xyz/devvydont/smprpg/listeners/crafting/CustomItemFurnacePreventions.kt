package xyz.devvydont.smprpg.listeners.crafting

import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.FurnaceBurnEvent
import org.bukkit.event.inventory.FurnaceStartSmeltEvent
import xyz.devvydont.smprpg.items.interfaces.IFurnaceFuel
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * Implements various listeners that prevent annoying interactions with custom items and furnaces.
 * One of the most important things this listener does is prevent custom items to be used as fuel in a furnace,
 * like preventing healing wands from being burned.
 */
class CustomItemFurnacePreventions : ToggleableListener() {
    /**
     * Never under any circumstances allow a custom item to burn unless it is explicitly meant for it.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("unused")
    private fun onAttemptBurnCustomItem(event: FurnaceBurnEvent) {
        // Vanilla items will function normally.

        val blueprint = blueprint(event.fuel)
        if (!blueprint.isCustom()) return

        // If the item is custom and NOT fuel, don't allow this to happen!
        if (blueprint !is IFurnaceFuel) {
            event.isCancelled = true
            return
        }

        event.burnTime = blueprint.getBurnTime().toInt()
    }

    /*
     * Never allow custom items to cook vanilla recipes. This shouldn't really ever happen.
     * This could be improved by instead listening to a click event, but this works too.
     */
    @EventHandler
    @Suppress("unused")
    private fun onSmeltCustomItem(event: FurnaceStartSmeltEvent) {
        if (event.recipe.key.namespace != NamespacedKey.MINECRAFT)
            return

        if (!blueprint(event.getSource()).isCustom())
            return

        event.totalCookTime = 999999
    }
}

