package xyz.devvydont.smprpg.items.blueprints.resources.crafting;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;

public class ImpossibleGeometry extends CustomItemBlueprint implements ISellable, IModelOverridden {

    public ImpossibleGeometry(ItemService itemService, CustomItemType type) {
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
     * Get the material that this item should display as, regardless of what it actually is internally.
     * This allows you to change how an item looks without affecting its behavior.
     *
     * @return The material this item should render as.
     */
    @Override
    public Material getDisplayMaterial() {
        return Material.STRUCTURE_VOID;
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
        return 20_000;
    }
}
