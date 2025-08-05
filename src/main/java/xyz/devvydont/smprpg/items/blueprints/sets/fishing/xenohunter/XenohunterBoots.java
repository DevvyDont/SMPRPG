package xyz.devvydont.smprpg.items.blueprints.sets.fishing.xenohunter;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.services.ItemService;

public class XenohunterBoots extends XenohunterSet {

    public XenohunterBoots(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.BOOTS;
    }

    @Override
    public int getHealth() {
        return XenohunterChestplate.HEALTH / 2;
    }

    @Override
    public int getDefense() {
        return XenohunterChestplate.DEFENSE / 2;
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(this.getRecipeKey(), generate());
        recipe.shape("mbm", "m m");
        recipe.setIngredient('m', ItemService.generate(XenohunterSet.UPGRADE_COMPONENT));
        recipe.setIngredient('b', ItemService.generate(CustomItemType.NOCTURNUM_BOOTS));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }
}
