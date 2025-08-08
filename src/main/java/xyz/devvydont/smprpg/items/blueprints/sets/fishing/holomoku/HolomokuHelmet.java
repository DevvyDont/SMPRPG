package xyz.devvydont.smprpg.items.blueprints.sets.fishing.holomoku;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.services.ItemService;

public class HolomokuHelmet extends HolomokuSet {

    public HolomokuHelmet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getHealth() {
        return HolomokuChestplate.HEALTH / 2;
    }

    @Override
    public int getDefense() {
        return HolomokuChestplate.DEFENSE / 2 + 10;
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.HELMET;
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(this.getRecipeKey(), generate());
        recipe.shape("mmm", "mhm");
        recipe.setIngredient('m', ItemService.generate(HolomokuSet.UPGRADE_MATERIAL));
        recipe.setIngredient('h', ItemService.generate(CustomItemType.MINNOW_HELMET));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }
}
