package xyz.devvydont.smprpg.items.blueprints.resources.crafting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Collection;
import java.util.List;

public class Xenomatter extends CustomItemBlueprint implements IHeaderDescribable, ISellable, ICraftable {

    public Xenomatter(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<Component> getHeader(ItemStack itemStack) {
        return List.of(
                ComponentUtils.create("A warped mess of raw creature"),
                ComponentUtils.merge(ComponentUtils.create("essence from "), ComponentUtils.create("unnatural", NamedTextColor.LIGHT_PURPLE), ComponentUtils.create(" sources")),
                ComponentUtils.EMPTY,
                ComponentUtils.merge(ComponentUtils.create("Perhaps a "), ComponentUtils.create("slow roast", NamedTextColor.RED), ComponentUtils.create(" can make")),
                ComponentUtils.create("this stuff actually usable?"),
                ComponentUtils.EMPTY,
                ComponentUtils.merge(ComponentUtils.create("Used for various "), ComponentUtils.create("crafting", NamedTextColor.GOLD), ComponentUtils.create(" components"))
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
        return 250_000 * item.getAmount();
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), this.getCustomItemType().getKey() + "_recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(getRecipeKey(), generate());
        recipe.shape(
                "imc",
                "phg",
                "ems"
        );
        recipe.setIngredient('i', ItemService.generate(CustomItemType.INFERNO_REMNANT));
        recipe.setIngredient('c', ItemService.generate(CustomItemType.DRACONIC_CRYSTAL));
        recipe.setIngredient('h', ItemService.generate(CustomItemType.HEART_OF_THE_VOID));
        recipe.setIngredient('g', ItemService.generate(CustomItemType.IMPOSSIBLE_GEOMETRY));
        recipe.setIngredient('p', ItemService.generate(CustomItemType.FLAMEBROILED_PORKCHOP));
        recipe.setIngredient('e', ItemService.generate(CustomItemType.ERRATIC_SLIME));
        recipe.setIngredient('s', ItemService.generate(CustomItemType.DISSIPATING_SEA_SHELL));
        recipe.setIngredient('m', ItemService.generate(CustomItemType.LEGENDARY_FISH_ESSENCE));
        recipe.setCategory(CraftingBookCategory.MISC);
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
        return List.of(
                ItemService.generate(CustomItemType.HEART_OF_THE_VOID)
        );
    }
}
