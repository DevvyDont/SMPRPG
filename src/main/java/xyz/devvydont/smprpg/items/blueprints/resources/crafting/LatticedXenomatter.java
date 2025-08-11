package xyz.devvydont.smprpg.items.blueprints.resources.crafting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import xyz.devvydont.smprpg.entity.fishing.SeaCreature;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.items.interfaces.ISmeltable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.List;

public class LatticedXenomatter extends CustomItemBlueprint implements IHeaderDescribable, ISellable, ISmeltable {

    public LatticedXenomatter(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<Component> getHeader(ItemStack itemStack) {
        return List.of(
                ComponentUtils.merge(ComponentUtils.create("An intense "), ComponentUtils.create("hyper-stabilized", NamedTextColor.LIGHT_PURPLE), ComponentUtils.create(" material")),
                ComponentUtils.merge(ComponentUtils.create("formed from the "), ComponentUtils.create("mystical essence", NamedTextColor.LIGHT_PURPLE), ComponentUtils.create(" of the")),
                ComponentUtils.merge(ComponentUtils.create("most powerful "), ComponentUtils.create("creatures", NamedTextColor.RED), ComponentUtils.create(" that are lurking")),
                ComponentUtils.merge(ComponentUtils.create("in the deep")),
                ComponentUtils.EMPTY,
                ComponentUtils.merge(ComponentUtils.create("Used for crafting "), ComponentUtils.create("powerful fishing gear", SeaCreature.NAME_COLOR, TextDecoration.BOLD))
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
        return 300_000 * item.getAmount();
    }

    /**
     * Get the ingredient that is used to smelt this item.
     *
     * @return The {@link RecipeChoice} that will turn into this item when cooked.
     */
    @Override
    public RecipeChoice getIngredient() {
        return new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.XENOMATTER));
    }

    /**
     * The vanilla Minecraft experience that is awarded as a result for cooking this item.
     *
     * @return The vanilla Minecraft experience.
     */
    @Override
    public float getExperience() {
        return 100;
    }

    /**
     * The cooking time in ticks in order to cook this item.
     *
     * @return The time in ticks.
     */
    @Override
    public long getCookingTime() {
        return TickTime.minutes(15);
    }

    /**
     * Gets the recipe type for this furnace.
     *
     * @return The type of smelting recipe.
     */
    @Override
    public RecipeType getRecipeType() {
        return RecipeType.CAMPFIRE;
    }
}
