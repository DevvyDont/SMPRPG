package xyz.devvydont.smprpg.items.blueprints.sets.fishing.nocturnum;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.services.ItemService;

public class NocturnumChestplate extends NocturnumSet {

    public static int DEFENSE = 300;
    public static int HEALTH = 270;

    public NocturnumChestplate(ItemService itemService, CustomItemType type) {
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
    public TrimMaterial getTrimMaterial() {
        return TrimMaterial.NETHERITE;
    }

    @Override
    public TrimPattern getTrimPattern() {
        return TrimPattern.SILENCE;
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(this.getRecipeKey(), generate());
        recipe.shape("mcm", "mmm", "mmm");
        recipe.setIngredient('m', ItemService.generate(NocturnumSet.UPGRADE_MATERIAL));
        recipe.setIngredient('c', ItemService.generate(CustomItemType.RUINATION_CHESTPLATE));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }
}
