package xyz.devvydont.smprpg.items.blueprints.sets.inferno;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.items.interfaces.ISmeltable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.time.TickTime;

public class ScorchingString extends CustomItemBlueprint implements ISmeltable, ISellable {

    public ScorchingString(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.MATERIAL;
    }

    @Override
    public int getWorth(ItemStack itemStack) {
        return 10_000 * itemStack.getAmount();
    }

    /**
     * Get the ingredient that is used to smelt this item.
     *
     * @return The {@link RecipeChoice} that will turn into this item when cooked.
     */
    @Override
    public RecipeChoice getIngredient() {
        return new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.ENCHANTED_STRING));
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
        return TickTime.minutes(5);
    }

    /**
     * Gets the recipe type for this furnace.
     *
     * @return The type of smelting recipe.
     */
    @Override
    public RecipeType getRecipeType() {
        return RecipeType.DEFAULT;
    }
}
