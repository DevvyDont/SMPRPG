package xyz.devvydont.smprpg.util.formatting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.World;

/**
 * Shared display helpers for rendering a {@link World.Environment} as a colored, human-readable component.
 */
public final class EnvironmentDisplay {

    private EnvironmentDisplay() {
    }

    /**
     * @param environment The dimension to describe.
     * @return A colored component naming the dimension (e.g. "the Overworld", "The End").
     */
    public static Component name(World.Environment environment) {
        return switch (environment) {
            case THE_END -> ComponentUtils.create("The End", NamedTextColor.LIGHT_PURPLE);
            case NETHER -> ComponentUtils.create("the Nether", NamedTextColor.RED);
            case NORMAL -> ComponentUtils.create("the Overworld", NamedTextColor.DARK_GREEN);
            case CUSTOM -> ComponentUtils.create("an unknown world", NamedTextColor.LIGHT_PURPLE);
        };
    }
}
