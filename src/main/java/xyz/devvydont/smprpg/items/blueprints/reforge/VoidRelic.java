package xyz.devvydont.smprpg.items.blueprints.reforge;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.blueprints.equipment.ReforgeStone;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.reforge.ReforgeType;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;

public class VoidRelic extends ReforgeStone implements ISellable, ICraftable, ICustomTextured {

    public VoidRelic(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public ReforgeType getReforgeType() {
        return ReforgeType.CRYSTALLIZED;
    }

    @Override
    public int getExperienceCost() {
        return 100;
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), this.getCustomItemType().getKey() + "_recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(getRecipeKey(), generate());
        recipe.shape(
                "cdc",
                "djd",
                "cdc"
        );
        recipe.setIngredient('c', ItemService.generate(CustomItemType.DRACONIC_CRYSTAL));
        recipe.setIngredient('d', ItemService.generate(CustomItemType.ENCHANTED_ENDER_PEARL));
        recipe.setIngredient('j', ItemService.generate(CustomItemType.SUMMONING_CRYSTAL));
        recipe.setCategory(CraftingBookCategory.MISC);
        return recipe;
    }

    /**
     * A collection of items that will unlock the recipe for this item. Typically, will be one of the components
     * of the recipe itself, but can be set to whatever is desired
     * @return
     */
    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(ItemService.generate(Material.ENDER_PEARL));
    }

    /**
     * Retrieve the URL to use for the custom head texture of this item.
     * The link that is set here should follow the following format:
     * Let's say you have the following link to a skin;
     * <a href="https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a">...</a>
     * You should only use the very last component of the URL, as the backend will fill in the rest.
     * Meaning we would end up using: "18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a"
     *
     * @return The URL to the skin.
     */
    @Override
    public String getTextureUrl() {
        return "df7467c5f738c641246c09f8ce791e339a86e81de62049b41f492888172fa726";
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
        return 100_000;
    }
}
