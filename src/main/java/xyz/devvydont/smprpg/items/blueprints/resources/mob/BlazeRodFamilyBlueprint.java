package xyz.devvydont.smprpg.items.blueprints.resources.mob;

import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IFurnaceFuel;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.extensions.ItemExtensionsKt;
import xyz.devvydont.smprpg.util.time.TickTime;

public class BlazeRodFamilyBlueprint extends CustomItemBlueprint implements ISellable, IFurnaceFuel {

    public BlazeRodFamilyBlueprint(ItemService itemService, CustomItemType type) {
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

        // If you want a reference, blaze rods take 120 seconds (2400 ticks).
        return switch (this.getCustomItemType()) {
            case PREMIUM_BLAZE_ROD -> TickTime.seconds(1_200);
            case ENCHANTED_BLAZE_ROD -> TickTime.seconds(12_000);
            default -> TickTime.seconds(1);
        };
    }
}
