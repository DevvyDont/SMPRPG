package xyz.devvydont.smprpg.services

import org.bukkit.event.Listener
import xyz.devvydont.smprpg.listeners.crafting.CraftingTransmuteUpgradeFix
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * Handles all recipe logic on the server. This includes crafting, smelting, etc.
 */
class RecipeService : IService, Listener {
    private val listeners: MutableList<ToggleableListener> = ArrayList()

    @Throws(RuntimeException::class)
    override fun setup() {
        // Start listeners.

        listeners.add(CraftingTransmuteUpgradeFix())
        for (listener in listeners) listener.start()
    }

    override fun cleanup() {
        for (listener in listeners) listener.stop()
    }
}
