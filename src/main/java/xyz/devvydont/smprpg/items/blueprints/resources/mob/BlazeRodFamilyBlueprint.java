package xyz.devvydont.smprpg.items.blueprints.resources.mob;

import org.bukkit.Material;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.base.CustomCompressableBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IFurnaceFuel;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.crafting.CompressionRecipeMember;
import xyz.devvydont.smprpg.util.crafting.MaterialWrapper;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.List;

public class BlazeRodFamilyBlueprint extends CustomCompressableBlueprint implements IFurnaceFuel {

    public static final List<CompressionRecipeMember> COMPRESSION_FLOW = List.of(
            new CompressionRecipeMember(new MaterialWrapper(Material.BLAZE_ROD)),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.PREMIUM_BLAZE_ROD)),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.ENCHANTED_BLAZE_ROD))
    );

    public BlazeRodFamilyBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<CompressionRecipeMember> getCompressionFlow() {
        return COMPRESSION_FLOW;
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
