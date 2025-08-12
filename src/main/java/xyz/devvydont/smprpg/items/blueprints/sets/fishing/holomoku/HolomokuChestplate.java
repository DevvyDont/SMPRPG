package xyz.devvydont.smprpg.items.blueprints.sets.fishing.holomoku;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.services.ItemService;

public class HolomokuChestplate extends HolomokuSet {

    public static int DEFENSE = 90;
    public static int HEALTH = 10;

    public HolomokuChestplate(ItemService itemService, CustomItemType type) {
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
        recipe.setIngredient('m', ItemService.generate(HolomokuSet.UPGRADE_MATERIAL));
        recipe.setIngredient('c', ItemService.generate(CustomItemType.MINNOW_CHESTPLATE));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }
}
