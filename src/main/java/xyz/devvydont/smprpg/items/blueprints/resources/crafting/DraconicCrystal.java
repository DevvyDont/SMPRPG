package xyz.devvydont.smprpg.items.blueprints.resources.crafting;

import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;

public class DraconicCrystal extends CustomItemBlueprint implements ICustomTextured, ISellable {

    public DraconicCrystal(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public String getTextureUrl() {
        return "c145dd7e0a35db50c9a4bdc465ca1235356fc5dd470737d2a74ea99cc1525bdb";
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.MATERIAL;
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
}
