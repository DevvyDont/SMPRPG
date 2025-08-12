package xyz.devvydont.smprpg.items.blueprints.sets.fishing.ruination;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.blueprints.sets.fishing.nocturnum.RuinationSet;
import xyz.devvydont.smprpg.services.ItemService;

public class RuinationBoots extends RuinationSet {

    public RuinationBoots(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getHealth() {
        return RuinationChestplate.HEALTH / 2;
    }

    @Override
    public int getDefense() {
        return RuinationChestplate.DEFENSE / 2;
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
        recipe.setIngredient('m', ItemService.generate(RuinationSet.UPGRADE_MATERIAL));
        recipe.setIngredient('b', ItemService.generate(CustomItemType.HOLOMOKU_BOOTS));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }
}
