package xyz.devvydont.smprpg.ability

import org.bukkit.event.block.Action

/**
 * Simply represents a method of activating an ability.
 */
enum class AbilityActivationMethod {
    RIGHT_CLICK,
    LEFT_CLICK,
    ;

    /**
     * Checks if this activation method passes for a given action.
     * @param action The action to check. Typically retrieved from [org.bukkit.event.player.PlayerInteractEvent.getAction]
     * @return True if the interaction passes the activation method.
     */
    fun passes(action: Action): Boolean {
        return when (this) {
            RIGHT_CLICK -> action.isRightClick
            LEFT_CLICK -> action.isLeftClick
        }
    }

    val displayName: String
        get() = this.name.replace("_", " ")
}
