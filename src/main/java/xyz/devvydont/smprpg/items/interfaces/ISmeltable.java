package xyz.devvydont.smprpg.items.interfaces;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.base.SMPItemBlueprint;
import xyz.devvydont.smprpg.items.base.VanillaItemBlueprint;

/**
 * Represents an item that can be retrieved via smelting another item.
 */
public interface ISmeltable {

    /**
     * The type of furnace recipe this item is best cooked in.
     * If you want a recipe to act normal, use the DEFAULT type.
     * If you want a recipe to cook 2x faster in a blast furnace, use BLASTING.
     * If you want a recipe to cook 2x faster in a smoker, use SMOKING.
     */
    enum RecipeType {
        DEFAULT,
        BLASTING,
        CAMPFIRE,
        SMOKING
    }

    /**
     * Get the ingredient that is used to smelt this item.
     * @return The {@link RecipeChoice} that will turn into this item when cooked.
     */
    RecipeChoice getIngredient();

    /**
     * The vanilla Minecraft experience that is awarded as a result for cooking this item.
     * @return The vanilla Minecraft experience.
     */
    float getExperience();

    /**
     * The cooking time in ticks in order to cook this item.
     * @return The time in ticks.
     */
    long getCookingTime();

    /**
     * Gets the recipe type for this furnace.
     * @return The type of smelting recipe.
     */
    RecipeType getRecipeType();

    static CookingRecipe<?> generateRecipe(SMPItemBlueprint blueprint, ISmeltable smeltable) {

        String identifier = null;
        if (blueprint instanceof VanillaItemBlueprint vanilla)
            identifier = vanilla.getMaterial().name().toLowerCase();
        else if (blueprint instanceof CustomItemBlueprint custom)
            identifier = custom.getCustomItemType().getKey();

        if (identifier == null)
            throw new IllegalStateException("Smeltable items must either extend VanillaItemBlueprint or CustomItemBlueprint.");

        var key = new NamespacedKey(SMPRPG.getPlugin(), identifier + "_smelt_recipe");
        if (smeltable.getRecipeType().equals(RecipeType.BLASTING))
            return new BlastingRecipe(key, blueprint.generate(), smeltable.getIngredient(), smeltable.getExperience(), (int) smeltable.getCookingTime());
        if (smeltable.getRecipeType().equals(RecipeType.SMOKING))
            return new SmokingRecipe(key, blueprint.generate(), smeltable.getIngredient(), smeltable.getExperience(), (int) smeltable.getCookingTime());
        if (smeltable.getRecipeType().equals(RecipeType.DEFAULT))
            return new FurnaceRecipe(key, blueprint.generate(), smeltable.getIngredient(), smeltable.getExperience(), (int) smeltable.getCookingTime());
        if (smeltable.getRecipeType().equals(RecipeType.CAMPFIRE))
            return new CampfireRecipe(key, blueprint.generate(), smeltable.getIngredient(), smeltable.getExperience(), (int) smeltable.getCookingTime());

        throw new IllegalStateException("Could not register smoking recipe for " + smeltable.getRecipeType() + ". You need to add a relevant CookingRecipe!");
    }

}
