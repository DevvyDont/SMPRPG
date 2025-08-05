package xyz.devvydont.smprpg.items.blueprints.sets.fishing.nocturnum;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.interfaces.IDyeable;
import xyz.devvydont.smprpg.services.ItemService;

public class NocturnumBoots extends NocturnumSet implements IDyeable {

    public NocturnumBoots(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getHealth() {
        return NocturnumChestplate.HEALTH / 2;
    }

    @Override
    public int getDefense() {
        return NocturnumChestplate.DEFENSE / 2;
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.BOOTS;
    }

    @Override
    public TrimMaterial getTrimMaterial() {
        return TrimMaterial.AMETHYST;
    }

    @Override
    public Color getColor() {
        return Color.BLACK;
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(this.getRecipeKey(), generate());
        recipe.shape("mbm", "m m");
        recipe.setIngredient('m', ItemService.generate(NocturnumSet.UPGRADE_MATERIAL));
        recipe.setIngredient('b', ItemService.generate(CustomItemType.RUINATION_BOOTS));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }
}
