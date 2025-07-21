package xyz.devvydont.smprpg.items.blueprints.resources.farming;

import org.bukkit.Material;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.base.CustomCompressableBlueprint;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.crafting.CompressionRecipeMember;
import xyz.devvydont.smprpg.util.crafting.MaterialWrapper;

import java.util.List;

public class MelonBlueprintFamily extends CustomCompressableBlueprint {

    public static final List<CompressionRecipeMember> COMPRESSION_FLOW = List.of(
            new CompressionRecipeMember(new MaterialWrapper(Material.MELON_SLICE), 9),
            new CompressionRecipeMember(new MaterialWrapper(Material.MELON), 9),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.PREMIUM_MELON_SLICE), 9),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.PREMIUM_MELON), 9),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.ENCHANTED_MELON_SLICE), 9),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.ENCHANTED_MELON), 9),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.MELON_SLICE_SINGULARITY), 9),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.MELON_SINGULARITY), 9)
    );

    public MelonBlueprintFamily(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<CompressionRecipeMember> getCompressionFlow() {
        return COMPRESSION_FLOW;
    }

}
