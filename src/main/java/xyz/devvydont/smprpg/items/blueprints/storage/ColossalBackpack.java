package xyz.devvydont.smprpg.items.blueprints.storage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.base.BackpackBase;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Collection;
import java.util.List;

public class ColossalBackpack extends BackpackBase implements IModelOverridden, ICraftable {

    public ColossalBackpack(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getSlots() {
        return 252;  // This is close to the max amount of item stacks the component is allowed to store. (256)
    }

    @Override
    public int getStackSize() {
        return 99;
    }

    @Override
    public Component getInterfaceTitleComponent() {
        return ComponentUtils.create("Colossal Backpack", NamedTextColor.BLACK);
    }

    @Override
    public Material getDisplayMaterial() {
        return Material.ORANGE_BUNDLE;
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), this.getCustomItemType().getKey() + "_recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(this.getRecipeKey(), generate());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        recipe.shape("mmm", "mbm", "mmm");
        recipe.setIngredient('m', ItemService.generate(CustomItemType.ENCHANTED_SHULKER_SHELL));
        recipe.setIngredient('b', ItemService.generate(CustomItemType.GIGANTIC_BACKPACK));
        return recipe;
    }

    /**
     * A collection of items that will unlock the recipe for this item. Typically will be one of the components
     * of the recipe itself, but can be set to whatever is desired
     *
     * @return
     */
    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(ItemService.generate(CustomItemType.ENCHANTED_SHULKER_SHELL));
    }
}
