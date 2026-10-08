package xyz.devvydont.smprpg.listeners.crafting

import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.FurnaceBurnEvent
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
        if (!blueprint.isCustom) return

        // If the item is custom and NOT fuel, don't allow this to happen!

        // Check if it's a craft engine item with a fuel value first.
        val ceItem = BukkitAdaptor.adapt((event.fuel))
        val def = ceItem.definition.orElse(null)
        if (ceItem.isCustomItem() && def != null) {
            if (def.settings().fuelTime() == 0) {
                event.isCancelled = true
                return
            }
        }
        else {
            if (blueprint !is IFurnaceFuel) {
                event.isCancelled = true
                return
            }

            event.burnTime = blueprint.getBurnTime().toInt()
        }
    }
}

