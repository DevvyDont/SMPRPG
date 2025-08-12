package xyz.devvydont.smprpg.items.blueprints.resources.mining;

import org.bukkit.Material;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.base.CustomCompressableBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IFurnaceFuel;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.crafting.CompressionRecipeMember;
import xyz.devvydont.smprpg.util.crafting.MaterialWrapper;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.List;

public class CoalFamilyBlueprint extends CustomCompressableBlueprint implements IFurnaceFuel {

    public static final List<CompressionRecipeMember> COMPRESSION_FLOW = List.of(
            new CompressionRecipeMember(new MaterialWrapper(Material.COAL)),
            new CompressionRecipeMember(new MaterialWrapper(Material.COAL_BLOCK)),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.ENCHANTED_COAL)),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.ENCHANTED_COAL_BLOCK)),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.COAL_SINGULARITY))
    );

    public CoalFamilyBlueprint(ItemService itemService, CustomItemType type) {
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

        // If you want a reference, coal takes 80 seconds (1600 ticks). A block of coal is 10x this.
        return switch (this.getCustomItemType()) {
            case ENCHANTED_COAL -> TickTime.seconds(8_000);
            case ENCHANTED_COAL_BLOCK -> TickTime.seconds(80_000);
            case COAL_SINGULARITY -> TickTime.seconds(800_000);
            default -> TickTime.seconds(1);
        };
    }
}
