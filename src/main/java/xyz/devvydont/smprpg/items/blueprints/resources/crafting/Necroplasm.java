package xyz.devvydont.smprpg.items.blueprints.resources.crafting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.entity.fishing.SeaCreature;
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

public class Necroplasm extends CustomItemBlueprint implements IHeaderDescribable, ISellable, ICraftable {

    public Necroplasm(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<Component> getHeader(ItemStack itemStack) {
        return List.of(
                ComponentUtils.create("A disgusting concoction"),
                ComponentUtils.create("formed by the remains"),
                ComponentUtils.merge(ComponentUtils.create("of various "), ComponentUtils.create("Sea Creatures", SeaCreature.NAME_COLOR)),
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

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), this.getCustomItemType().getKey() + "_recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapelessRecipe(this.getRecipeKey(), generate());
        recipe.addIngredient(2, ItemService.generate(CustomItemType.MIDNIGHT_HIDE));
        recipe.addIngredient(2, ItemService.generate(CustomItemType.DEEP_SEA_BARNACLE));
        recipe.addIngredient(ItemService.generate(CustomItemType.EPIC_FISH_ESSENCE));
        recipe.addIngredient(2, ItemService.generate(CustomItemType.SPOOKY_TENDRIL));
        recipe.addIngredient(2, ItemService.generate(CustomItemType.BRIMSTONE_RESIN));
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
                ItemService.generate(CustomItemType.MIDNIGHT_HIDE),
                ItemService.generate(CustomItemType.DEEP_SEA_BARNACLE),
                ItemService.generate(CustomItemType.SPOOKY_TENDRIL),
                ItemService.generate(CustomItemType.BRIMSTONE_RESIN)
        );
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
