package xyz.devvydont.smprpg.items.blueprints.resources.crafting;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.items.interfaces.ISmeltable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.time.TickTime;

public class SteelIngot extends CustomItemBlueprint implements ISmeltable, ISellable {

    public SteelIngot(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.ITEM;
    }

    /**
     * Get the ingredient that is used to smelt this item.
     *
     * @return The {@link RecipeChoice} that will turn into this item when cooked.
     */
    @Override
    public RecipeChoice getIngredient() {
        return new RecipeChoice.ExactChoice(ItemService.generate(Material.IRON_INGOT));
    }

    /**
     * The vanilla Minecraft experience that is awarded as a result for cooking this item.
     *
     * @return The vanilla Minecraft experience.
     */
    @Override
    public float getExperience() {
        return 5;
    }

    /**
     * The cooking time in ticks in order to cook this item.
     *
     * @return The time in ticks.
     */
    @Override
    public long getCookingTime() {
        return TickTime.seconds(40);
    }

    /**
     * Gets the recipe type for this furnace.
     *
     * @return The type of smelting recipe.
     */
    @Override
    public RecipeType getRecipeType() {
        return RecipeType.BLASTING;
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
        return 50;
    }
}
