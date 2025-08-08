package xyz.devvydont.smprpg.items.blueprints.sets.fishing.ruination;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.blueprints.sets.fishing.nocturnum.RuinationSet;
import xyz.devvydont.smprpg.services.ItemService;

public class RuinationChestplate extends RuinationSet {

    public static int DEFENSE = 200;
    public static int HEALTH = 100;

    public RuinationChestplate(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getHealth() {
        return HEALTH;
    }

    @Override
    public int getDefense() {
        return DEFENSE;
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.CHESTPLATE;
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(this.getRecipeKey(), generate());
        recipe.shape("mcm", "mmm", "mmm");
        recipe.setIngredient('m', ItemService.generate(RuinationSet.UPGRADE_MATERIAL));
        recipe.setIngredient('c', ItemService.generate(CustomItemType.HOLOMOKU_CHESTPLATE));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }
}
