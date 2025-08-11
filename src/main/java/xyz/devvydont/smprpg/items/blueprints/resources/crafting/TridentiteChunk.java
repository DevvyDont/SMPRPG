package xyz.devvydont.smprpg.items.blueprints.resources.crafting;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;

public class TridentiteChunk extends CustomItemBlueprint implements ISellable, ICraftable {

    public TridentiteChunk(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getInstance(), this.getCustomItemType().getKey() + "_recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var shaped = new ShapedRecipe(getRecipeKey(), generate());
        shaped.shape(
                "hsh",
                "pcj",
                "hsh"
        );
        shaped.setIngredient('c', ItemService.generate(CustomItemType.ENCHANTED_PRISMARINE_CRYSTAL));
        shaped.setIngredient('p', ItemService.generate(CustomItemType.PLUTO_FRAGMENT));
        shaped.setIngredient('j', ItemService.generate(CustomItemType.JUPITER_CRYSTAL));
        shaped.setIngredient('s', ItemService.generate(CustomItemType.ENCHANTED_PRISMARINE_SHARD));
        shaped.setIngredient('h', ItemService.generate(CustomItemType.HEXED_CLOTH));
        shaped.setCategory(CraftingBookCategory.MISC);
        return shaped;
    }

    /**
     * A collection of items that will unlock the recipe for this item. Typically will be one of the components
     * of the recipe itself, but can be set to whatever is desired
     *
     * @return
     */
    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(
                ItemService.generate(CustomItemType.HEXED_CLOTH)
        );
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.ITEM;
    }

    /**
     * Given this item stack, how much should it be able to sell for?
     * Keep in mind that the size of the stack needs to considered as well!
     *
     * @param item The item that can be sold.
     * @return The worth of the item.
     */
    @Override
    public int getWorth(ItemStack item) {
        return 30_000 * item.getAmount();
    }
}
