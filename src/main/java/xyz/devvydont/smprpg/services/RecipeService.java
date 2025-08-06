package xyz.devvydont.smprpg.services;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
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
