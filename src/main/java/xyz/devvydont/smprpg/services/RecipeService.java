package xyz.devvydont.smprpg.services;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.base.VanillaItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ISmeltable;
import xyz.devvydont.smprpg.listeners.crafting.CraftingTransmuteUpgradeFix;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all recipe logic on the server. This includes crafting, smelting, etc.
 */
public class RecipeService implements IService, Listener {

    /**
     * Get a list of recipes for a specific item.
     * This operation will filter out vanilla recipes that think they can craft custom items, due to a
     * Material recipe match.
     * @param item
     * @return
     */
    public static List<Recipe> getRecipesFor(ItemStack item) {

        var allRecipes = Bukkit.getRecipesFor(item);

        // Filter out recipes that have the minecraft namespace that think they can craft custom items.
        // Filter out items that do not match. This function has this lovely behavior of giving us ALL recipes that give us the same underlying vanilla material.
        // Another level of filtering. Filter out custom recipes that craft vanilla items. The only time this should
        // ever really be possible is with compression recipes, and they are annoying to display anyway...
        for (var recipe : allRecipes.stream().toList()) {

            // Filter out items that simply do not match. An iron ingot cannot be crafted by a recipe that is a boiling ingot.
            if (!recipe.getResult().isSimilar(item)) {
                allRecipes.remove(recipe);
                continue;
            }

            if (!(recipe instanceof Keyed keyed))
                continue;

            // Filter out a recipe if it is vanilla, but thinks it can craft a custom item.
            var recipeIsVanilla = keyed.getKey().getNamespace().equals(NamespacedKey.MINECRAFT_NAMESPACE);
            var resultBlueprint = ItemService.blueprint(recipe.getResult());
            if (recipeIsVanilla && resultBlueprint.isCustom()) {
                allRecipes.remove(recipe);
                continue;
            }

            // Filter out a recipe if it is one of our recipes, but a vanilla item is generated. This could potentially
            // filter out recipes we want to consider valid, but there are more "lying" recipes if we allow them.
            if (!recipeIsVanilla && resultBlueprint.isVanilla()) {
                allRecipes.remove(recipe);
            }
        }


        return allRecipes;
    }

    private final List<ToggleableListener> listeners = new ArrayList<>();

    @Override
    public void setup() throws RuntimeException {

        registerFurnaceRecipes();

        // Start listeners.
        listeners.add(new CraftingTransmuteUpgradeFix());
        for (var listener : listeners)
            listener.start();
    }

    @Override
    public void cleanup() {
        for (var listener : listeners)
            listener.stop();
    }

    /**
     * Registers furnace recipes. These are blueprints that implement {@link ISmeltable}.
     */
    private void registerFurnaceRecipes() {
        // Loop through every item in the item service. If it's smeltable, add a smelt recipe for it.
        for (var item : SMPRPG.getService(ItemService.class).getCustomBlueprints()) {
            if (!(item instanceof ISmeltable smeltable))
                continue;

            Bukkit.addRecipe(ISmeltable.generateRecipe(item, smeltable));
        }
    }
}
