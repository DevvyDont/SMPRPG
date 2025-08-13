package xyz.devvydont.smprpg.gui.base

import org.bukkit.event.inventory.InventoryClickEvent

/**
 * Delegate that handles a menu button click.
 */
fun interface MenuButtonClickHandler {
    /**
     * Handles a button being clicked.
     *
     * @param event The client event that triggered the invocation.
     */
    fun handleClick(event: InventoryClickEvent)
}
