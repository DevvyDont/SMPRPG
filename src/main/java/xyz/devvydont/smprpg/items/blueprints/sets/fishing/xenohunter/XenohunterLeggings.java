package xyz.devvydont.smprpg.items.blueprints.sets.fishing.xenohunter;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.services.ItemService;

public class XenohunterLeggings extends XenohunterSet {

    public XenohunterLeggings(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.LEGGINGS;
    }

    @Override
    public int getHealth() {
        return XenohunterChestplate.HEALTH - 50;
    }

    @Override
    public int getDefense() {
        return XenohunterChestplate.DEFENSE - 50;
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(this.getRecipeKey(), generate());
        recipe.shape(
                "btb",
                "tlt",
                "b b");
        recipe.setIngredient('t', ItemService.generate(XenohunterSet.UPGRADE_MATERIAL));
        recipe.setIngredient('b', ItemService.generate(XenohunterSet.UPGRADE_BINDING));
        recipe.setIngredient('l', ItemService.generate(CustomItemType.NOCTURNUM_LEGGINGS));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }
}
