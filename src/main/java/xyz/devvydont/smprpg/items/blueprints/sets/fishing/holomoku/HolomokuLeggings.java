package xyz.devvydont.smprpg.items.blueprints.sets.fishing.holomoku;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.services.ItemService;

public class HolomokuLeggings extends HolomokuSet {

    public HolomokuLeggings(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getHealth() {
        return HolomokuChestplate.HEALTH;
    }

    @Override
    public int getDefense() {
        return HolomokuChestplate.DEFENSE - 15;
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
        recipe.shape("mmm", "mlm", "m m");
        recipe.setIngredient('m', ItemService.generate(HolomokuSet.UPGRADE_MATERIAL));
        recipe.setIngredient('l', ItemService.generate(CustomItemType.MINNOW_LEGGINGS));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }
}
