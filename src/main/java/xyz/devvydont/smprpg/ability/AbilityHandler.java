package xyz.devvydont.smprpg.ability;

/**
 * All ability handlers should implement this interface. Represents an "action"an ability takes when executed.
 */
public interface AbilityHandler {

    /**
     * Attempts to execute the ability.
     * @param ctx The context of the ability.
     * @return True if the ability succeeded and should have cost reduced, false otherwise.
     */
    boolean execute(AbilityContext ctx);

}
