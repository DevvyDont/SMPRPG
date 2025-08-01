package xyz.devvydont.smprpg.util.formatting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ComponentUtils {
    // Colors
    public static final TextColor TEXT_DEFAULT = NamedTextColor.WHITE;

    // Presets
    public static final TextComponent SPACE = create(" ");
    public static final TextComponent EMPTY = Component.empty();
    public static final TextComponent SYMBOL_BRACKET_LEFT = create("[");
    public static final TextComponent SYMBOL_BRACKET_RIGHT = create("]");
    public static final TextComponent SYMBOL_EXCLAMATION = create("!");

    /**
     * Creates a text component with the default styling.
     *
     * @param message     The message to turn into a component.
     * @param decorations Additional decorations to apply to the component.
     * @return The styled text component.
     */
    public static TextComponent create(String message, TextDecoration... decorations) {
        return create(message, TEXT_DEFAULT, decorations);
    }

    /**
     * Creates a text component that's styled with the specified color and decorations.
     *
     * @param message     The message to turn into a component.
     * @param color       The color to apply to the component.
     * @param decorations Additional decorations to apply to the component.
     * @return The styled text component.
     */
    public static TextComponent create(String message, TextColor color, TextDecoration... decorations) {
        return Component.text(message, Style.style(color, decorations));
    }

    /**
     * Creates a text component that's styled with the specified color and decorations.
     * This variant of the method auto converts integers to strings for ease of use.
     *
     * @param message     The message to turn into a component.
     * @param color       The color to apply to the component.
     * @param decorations Additional decorations to apply to the component.
     * @return The styled text component.
     */
    public static TextComponent create(int message, TextColor color, TextDecoration... decorations) {
        return create(String.valueOf(message), color, decorations);
    }

    /**
     * Creates a text component that is a gradient from one color to another using the MiniMessage library.
     *
     * @param message The message to apply a gradient to.
     * @param startColor The starting color (left).
     * @param endColor The ending color (right).
     * @return A component with gradient applied from left to right.
     */
    public static Component gradient(String message, TextColor startColor, TextColor endColor) {
        return MiniMessage.miniMessage().deserialize(String.format("<gradient:%s:%s>%s</gradient>", startColor.asHexString(), endColor.asHexString(), message));
    }

    /**
     * Creates a silly text component that encrypts random characters in the string.
     * @param message The message to encrypt.
     * @param intensity The percentage of characters that should randomly be encrypted.
     * @return A new component with random encrypted characters.
     */
    public static Component encrypt(String message, double intensity) {

        var newComponent = create("");

        for (var _char : message.toCharArray()) {
            var charComponent = Math.random() < intensity ?
                    Component.text(_char).decorate(TextDecoration.OBFUSCATED) :
                    Component.text(_char);
            newComponent = newComponent.append(charComponent);
        }
        return newComponent;
    }

    // -----------
    //   Helpers
    // -----------

    /**
     * Merges a collection of component into a single component.
     *
     * @param components The components to merge together.
     * @return The result of the merge.
     */
    public static Component merge(Component... components) {
        var outputComponent = EMPTY;
        for (var component : components) {
            outputComponent = outputComponent.append(component);
        }
        return outputComponent;
    }

    /**
     * Removes the italics from a collection of components.
     *
     * @param components The components to clean.
     * @return The cleaned components.
     */
    public static List<Component> cleanItalics(Collection<Component> components) {
        var cleanComponents = new ArrayList<Component>(components.size());
        for (var component : components) {
            cleanComponents.add(component.decoration(TextDecoration.ITALIC, false));
        }
        return cleanComponents;
    }

    // -----------
    //   Presets
    // -----------

    /**
     * Creates a text component that's styled like an alert message.
     *
     * @param message A message alerting the user about something.
     * @return The styled text component.
     */
    public static TextComponent alert(String message) {
        return alert(create(message));
    }

    /**
     * Creates a text component that's styled like an alert message.
     *
     * @param message     A message alerting the user about something.
     * @param prefixColor The color of the alert prefix.
     * @return The styled text component.
     */
    public static TextComponent alert(String message, TextColor prefixColor) {
        return alert(create(message), prefixColor);
    }

    /**
     * Creates a text component that's styled like an alert message.
     *
     * @param message     A message alerting the user about something.
     * @param prefixColor The color of the alert prefix.
     * @param textColor   The color of the alert text.
     * @return The styled text component.
     */
    public static TextComponent alert(String message, TextColor prefixColor, TextColor textColor) {
        return alert(create(message, textColor), prefixColor);
    }

    /**
     * Creates a text component that's styled like an alert message.
     *
     * @param message A message alerting the user about something.
     * @return The styled text component.
     */
    public static TextComponent alert(Component message) {
        return alert(message, TEXT_DEFAULT);
    }

    /**
     * Creates a text component that's styled like an alert message.
     *
     * @param message     A message alerting the user about something.
     * @param prefixColor The color of the alert prefix.
     * @return The styled text component.
     */
    public static TextComponent alert(Component message, TextColor prefixColor) {
        return alert(SYMBOL_EXCLAMATION.color(prefixColor), message);
    }

    /**
     * Creates a text component that's styled like an alert message.
     *
     * @param prefix  The contents of the alert prefix.
     * @param message A message alerting the user about something.
     * @return The styled text component.
     */
    public static TextComponent alert(Component prefix, Component message) {
        return EMPTY
                .append(SYMBOL_BRACKET_LEFT)
                .append(prefix)
                .append(SYMBOL_BRACKET_RIGHT)
                .append(SPACE)
                .append(message);
    }

    /**
     * Creates a text component that's styled like a success message.
     *
     * @param text A message explaining the success.
     * @return The styled text component.
     */
    public static TextComponent success(String text) {
        return alert(text, NamedTextColor.DARK_GREEN, NamedTextColor.GREEN);
    }

    /**
     * Creates a text component that's styled like a success message.
     *
     * @param message A message explaining the success.
     * @return The styled text component.
     */
    public static TextComponent success(Component message) {
        return alert(message, NamedTextColor.DARK_GREEN);
    }

    /**
     * Creates a text component that's styled like an error message.
     *
     * @param text A message explaining the failure.
     * @return The styled text component.
     */
    public static TextComponent error(String text) {
        return alert(text, NamedTextColor.DARK_RED, NamedTextColor.RED);
    }

    /**
     * Creates a text component that's styled like an error message.
     *
     * @param message A message explaining the failure.
     * @return The styled text component.
     */
    public static TextComponent error(Component message) {
        return alert(message, NamedTextColor.DARK_RED);
    }

    /**
     * Creates a text component that's styled like an upgrade message.
     *
     * @param oldValue      The value before the upgrade.
     * @param newValue      The value after the upgrade.
     * @param newValueColor The colour of the upgraded value.
     * @return The styled text component.
     */
    public static TextComponent upgrade(String oldValue, String newValue, TextColor newValueColor) {
        return upgrade(create(oldValue, NamedTextColor.DARK_GRAY), create(newValue, newValueColor));
    }

    /**
     * Creates a text component that's styled like an upgrade message.
     *
     * @param oldValue The value before the upgrade.
     * @param newValue The value after the upgrade.
     * @return The styled text component.
     */
    public static TextComponent upgrade(Component oldValue, Component newValue) {
        return EMPTY
                .append(oldValue)
                .append(SPACE)
                .append(ComponentUtils.create(Symbols.RIGHT_ARROW, NamedTextColor.DARK_GRAY))
                .append(SPACE)
                .append(newValue);
    }

    /**
     * Creates a text component that displays a power level.
     *
     * @param level The level to display.
     * @return The styled text component.
     */
    public static TextComponent powerLevel(int level) {
        return ComponentUtils.create(Symbols.POWER + level, NamedTextColor.YELLOW);
    }

    /**
     * Creates a text component that displays a power level as a prefix.
     *
     * @param level The level to display.
     * @return The styled text component.
     */
    public static TextComponent powerLevelPrefix(int level) {
        return EMPTY.append(SYMBOL_BRACKET_LEFT).append(powerLevel(level)).append(SYMBOL_BRACKET_RIGHT);
    }

    /**
     * Inserts components at the beginning of an already defined list of components. Useful for adding components
     * at the beginning of item lore.
     *
     * @param original The original list of components.
     * @param toInsert All the components that are desired to be inserted before the original components.
     * @return
     */
    public static List<Component> insertComponents(List<Component> original, Component...toInsert) {
        List<Component> components = new ArrayList<>();
        components.addAll(Arrays.asList(toInsert));
        components.addAll(original);
        return components;
    }
}
