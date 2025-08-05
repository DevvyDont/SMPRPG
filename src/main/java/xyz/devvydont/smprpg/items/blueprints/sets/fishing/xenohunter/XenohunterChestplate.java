package xyz.devvydont.smprpg.items.blueprints.sets.fishing.xenohunter;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.services.ItemService;

public class XenohunterChestplate extends XenohunterSet {

    public static final int DEFENSE = 600;
    public static final int HEALTH = 500;

    public XenohunterChestplate(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.CHESTPLATE;
    }

    @Override
    public int getHealth() {
        return HEALTH;
    }

    @Override
    public int getDefense() {
        return DEFENSE;
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(this.getRecipeKey(), generate());
        recipe.shape("mcm", "mmm", "mmm");
        recipe.setIngredient('m', ItemService.generate(XenohunterSet.UPGRADE_COMPONENT));
        recipe.setIngredient('c', ItemService.generate(CustomItemType.NOCTURNUM_CHESTPLATE));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }
}
