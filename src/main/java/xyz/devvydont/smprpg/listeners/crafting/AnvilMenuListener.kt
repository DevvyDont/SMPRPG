package xyz.devvydont.smprpg.listeners.crafting

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.anvil.MenuAnvil
import xyz.devvydont.smprpg.hooks.WorldGuardHook
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * Intercepts every attempt to open a vanilla anvil (both the vanilla block and our custom netherite anvil,
 * which opens a vanilla anvil window via CraftEngine) and replaces it with our fully custom [MenuAnvil].
 * This keeps anvil behavior entirely under our control and avoids the bugs of the vanilla anvil GUI.
 */
class AnvilMenuListener : ToggleableListener() {

    @EventHandler
    @Suppress("unused")
    private fun onAnvilOpened(event: InventoryOpenEvent) {
        if (event.inventory.type != InventoryType.ANVIL) return

        val player = event.player as? Player ?: return
        event.isCancelled = true

        // Remember which anvil block was used so the menu can mimic vanilla anvil wear.
        val anvilLocation = resolveAnvilLocation(event.inventory, player)

        // Respect WorldGuard region protection. Because our menu (and our anvil wear) entirely replaces the
        // vanilla anvil, we must enforce the USE flag ourselves; otherwise players could use and damage anvils
        // inside protected zones. Virtual anvils with no backing block are unaffected.
        if (anvilLocation != null && !isUseAllowed(anvilLocation, player)) {
            player.sendMessage(ComponentUtils.error("You don't have permission to use this anvil here."))
            return
        }

        // Opening an inventory from within an InventoryOpenEvent is unsafe, so defer it a tick.
        Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable { MenuAnvil(player, anvilLocation).openMenu() }, 1L)
    }

    /**
     * Resolves the world location of the anvil block backing this inventory. Falls back to the block the
     * player is looking at, since anvil inventories do not always expose their location. Returns null if no
     * anvil block can be determined (e.g. a virtual anvil), in which case no block wear occurs.
     */
    private fun resolveAnvilLocation(inventory: Inventory, player: Player): Location? {
        inventory.location?.let { return it }
        return player.getTargetBlockExact(ANVIL_REACH)?.location
    }

    /**
     * Whether the player may use the anvil at the given location. Defers to WorldGuard's USE flag when the
     * plugin is present, and allows the interaction otherwise (WorldGuard is an optional dependency).
     *
     * Custom blocks (such as the netherite anvil) are always permitted: they never degrade or break, so there
     * is nothing for region protection to guard against.
     */
    private fun isUseAllowed(location: Location, player: Player): Boolean {
        if (CraftEngineBlocks.isCustomBlock(location.block)) return true
        if (Bukkit.getServer().pluginManager.getPlugin(WORLDGUARD_PLUGIN) == null) return true
        return WorldGuardHook.isLocationUsable(location, player)
    }

    companion object {
        private const val ANVIL_REACH = 6
        private const val WORLDGUARD_PLUGIN = "WorldGuard"
    }
}
