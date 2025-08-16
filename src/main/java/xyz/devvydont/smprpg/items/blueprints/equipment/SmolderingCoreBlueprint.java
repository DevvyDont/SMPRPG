package xyz.devvydont.smprpg.items.blueprints.equipment;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.reforge.ReforgeType;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;

public class SmolderingCoreBlueprint extends ReforgeStone implements ICraftable, ISellable, ICustomTextured {

    public SmolderingCoreBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public String getTextureUrl() {
        return "eac5a3b17e8f1b98cda535abecf0667761a4f5991388af71efac2021f7bcce09";
    }

    @Override
    public ReforgeType getReforgeType() {
        return ReforgeType.OVERHEATING;
    }

    @Override
    public int getExperienceCost() {
        return 50;
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), getCustomItemType().getKey() + "-recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(getRecipeKey(), generate());
        recipe.shape(
                "ono",
                "nsn",
                "ono"
        );
        recipe.setIngredient('o', ItemService.generate(CustomItemType.ENCHANTED_BLAZE_ROD));
        recipe.setIngredient('n', ItemService.generate(CustomItemType.BOILING_INGOT));
        recipe.setIngredient('s', ItemService.generate(CustomItemType.INFERNO_REMNANT));
        recipe.setCategory(CraftingBookCategory.MISC);
        return recipe;
    }

    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(
                ItemService.generate(CustomItemType.INFERNO_REMNANT)
        );
    }

    @Override
    public int getWorth(ItemStack item) {
        return 70_000 * item.getAmount();
    }
}
