package xyz.devvydont.smprpg.commands.enchantments;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.commands.CommandSimplePlayer;
import xyz.devvydont.smprpg.enchantments.CustomEnchantment;
import xyz.devvydont.smprpg.gui.enchantments.EnchantmentMenu;
import xyz.devvydont.smprpg.gui.enchantments.EnchantmentSubMenu;
import xyz.devvydont.smprpg.services.EnchantmentService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandEnchantments extends CommandSimplePlayer {

    // The index of the optional argument that lets a player jump straight to an enchantment's sub menu.
    private static final int ENCHANTMENT_ARGUMENT_INDEX = 0;

    public CommandEnchantments(String name) {
        super(name);
    }

    @Override
    protected void playerInvoked(@NotNull Player player, @NotNull CommandSourceStack ctx, @NotNull String @NotNull [] args) {

        // No enchantment was specified, open the full enchantment listing as usual.
        if (args.length == 0) {
            new EnchantmentMenu(player).openMenu();
            return;
        }

        // An enchantment was specified, attempt to jump straight to its detailed sub menu.
        String query = args[ENCHANTMENT_ARGUMENT_INDEX];
        CustomEnchantment enchantment = findEnchantment(query);
        if (enchantment == null) {
            player.sendMessage(ComponentUtils.error("There is no enchantment with the id '" + query + "'!"));
            return;
        }

        // Use the full listing as the parent so the back button returns to the overview.
        new EnchantmentSubMenu(player, new EnchantmentMenu(player), enchantment).openMenu();
    }

    /**
     * Finds a registered custom enchantment whose id matches the given query (case-insensitive).
     *
     * @param query The enchantment id to look for.
     * @return The matching enchantment, or null if none matched.
     */
    private @Nullable CustomEnchantment findEnchantment(String query) {
        for (CustomEnchantment enchantment : SMPRPG.getService(EnchantmentService.class).getCustomEnchantments())
            if (enchantment.getId().equalsIgnoreCase(query))
                return enchantment;
        return null;
    }

    /**
     * Collects the ids of every registered custom enchantment, sorted alphabetically for friendlier completions.
     *
     * @return A collection of enchantment ids.
     */
    private Collection<String> getEnchantmentIds() {
        List<String> ids = new ArrayList<>();
        for (CustomEnchantment enchantment : SMPRPG.getService(EnchantmentService.class).getCustomEnchantments())
            ids.add(enchantment.getId());
        ids.sort(String.CASE_INSENSITIVE_ORDER);
        return ids;
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {

        // Show every enchantment immediately, even before the player has started typing. The base implementation
        // would otherwise fall back to online player names when no argument has been entered yet.
        int argumentIndex = args.length == 0 ? ENCHANTMENT_ARGUMENT_INDEX : args.length - 1;
        String input = args.length == 0 ? "" : args[args.length - 1];
        return determineSuggestions(argumentIndex, input);
    }

    @Override
    protected Collection<String> determineSuggestions(int argumentIndex, String input) {

        // Only the first argument represents an enchantment to open.
        if (argumentIndex != ENCHANTMENT_ARGUMENT_INDEX)
            return List.of();

        return generateArgumentCollection(input, getEnchantmentIds());
    }

    @Override
    public Collection<String> getAliases() {
        return List.of(
                "enchants",
                "listenchants",
                "listenchantments"
        );
    }
}
