package xyz.devvydont.smprpg.ability;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Simply represents a method of activating an ability.
 */
public enum AbilityActivationMethod {

    RIGHT_CLICK,
    LEFT_CLICK,

    ;

    /**
     * Checks if this activation method passes for a given action.
     * @param action The action to check. Typically retrieved from {@link PlayerInteractEvent#getAction()}
     * @return True if the interaction passes the activation method.
     */
    public boolean passes(Action action) {
        return switch (this) {
            case RIGHT_CLICK -> action.isRightClick();
            case LEFT_CLICK -> action.isLeftClick();
        };
    }

    public String getDisplayName() {
        return this.name().replace("_", " ");
    }

}
