package xyz.devvydont.smprpg.ability

import org.bukkit.entity.Player
import org.bukkit.event.block.Action

/**
 * Simply represents a method of activating an ability.
 */
enum class AbilityActivationMethod(val displayName : String) {
    RIGHT_CLICK("RIGHT CLICK"),
    LEFT_CLICK("LEFT CLICK"),
    SNEAK_RIGHT_CLICK("SNEAK + RIGHT CLICK"),
    SNEAK_LEFT_CLICK("SNEAK + LEFT CLICK"),
    EXCLUSIVE_RIGHT_CLICK("RIGHT CLICK (NOT SNEAKING)"),
    EXCLUSIVE_LEFT_CLICK("LEFT CLICK (NOT SNEAKING)")
    ;

    /**
     * Checks if this activation method passes for a given action.
     * @param action The action to check. Typically retrieved from [org.bukkit.event.player.PlayerInteractEvent.getAction]
     * @return True if the interaction passes the activation method.
     */
    fun passes(action: Action, player : Player): Boolean {
        return when (this) {
            RIGHT_CLICK -> action.isRightClick
            LEFT_CLICK -> action.isLeftClick
            SNEAK_RIGHT_CLICK -> action.isRightClick && player.isSneaking
            SNEAK_LEFT_CLICK -> action.isLeftClick && player.isSneaking
            EXCLUSIVE_RIGHT_CLICK -> action.isRightClick && !player.isSneaking
            EXCLUSIVE_LEFT_CLICK -> action.isLeftClick && !player.isSneaking
        }
    }
}
