package xyz.devvydont.smprpg.items.blueprints.sets.fishing.holomoku;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.services.ItemService;

public class HolomokuBoots extends HolomokuSet {

    public HolomokuBoots(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getHealth() {
        return HolomokuChestplate.HEALTH / 2;
    }

    @Override
    public int getDefense() {
        return HolomokuChestplate.DEFENSE / 2;
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.BOOTS;
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(this.getRecipeKey(), generate());
        recipe.shape("mbm", "m m");
        recipe.setIngredient('m', ItemService.generate(HolomokuSet.UPGRADE_MATERIAL));
        recipe.setIngredient('b', ItemService.generate(CustomItemType.MINNOW_BOOTS));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }
}
