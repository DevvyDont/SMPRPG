package xyz.devvydont.smprpg.items.blueprints.resources.mining;

import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IFurnaceFuel;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.extensions.ItemExtensionsKt;
import xyz.devvydont.smprpg.util.time.TickTime;

public class CharcoalFamilyBlueprint extends CustomItemBlueprint implements ISellable, IFurnaceFuel {

    public CharcoalFamilyBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.MATERIAL;
    }

    @Override
    public int getWorth(ItemStack itemStack) {
        return ItemExtensionsKt.calculateCompressedWorth(this, itemStack);
    }

    /**
     * Get the time to burn in ticks when a furnace consumes this.
     *
     * @return Amount of ticks to burn.
     */
    @Override
    public long getBurnTime() {

        // If you want a reference, coal takes 80 seconds (1600 ticks). A block of coal is 10x this.
        return switch (this.getCustomItemType()) {
            case COMPRESSED_CHARCOAL -> TickTime.seconds(800);
            case ENCHANTED_CHARCOAL -> TickTime.seconds(8000);
            default -> TickTime.seconds(1);
        };
    }
}
