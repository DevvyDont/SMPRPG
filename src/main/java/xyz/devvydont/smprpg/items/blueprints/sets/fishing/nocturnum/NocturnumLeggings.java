package xyz.devvydont.smprpg.items.blueprints.sets.fishing.nocturnum;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.services.ItemService;

public class NocturnumLeggings extends NocturnumSet {

    public NocturnumLeggings(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getHealth() {
        return NocturnumChestplate.HEALTH - 40;
    }

    @Override
    public int getDefense() {
        return NocturnumChestplate.DEFENSE - 40;
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.LEGGINGS;
    }

    @Override
    public TrimMaterial getTrimMaterial() {
        return TrimMaterial.REDSTONE;
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(this.getRecipeKey(), generate());
        recipe.shape("mmm", "mlm", "m m");
        recipe.setIngredient('m', ItemService.generate(NocturnumSet.UPGRADE_MATERIAL));
        recipe.setIngredient('l', ItemService.generate(CustomItemType.RUINATION_LEGGINGS));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }
}
