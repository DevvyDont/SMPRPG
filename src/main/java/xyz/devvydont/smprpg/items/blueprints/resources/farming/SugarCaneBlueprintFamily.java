package xyz.devvydont.smprpg.items.blueprints.resources.farming;

import org.bukkit.Material;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.base.CustomCompressableBlueprint;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.crafting.CompressionRecipeMember;
import xyz.devvydont.smprpg.util.crafting.MaterialWrapper;

import java.util.List;

public class SugarCaneBlueprintFamily extends CustomCompressableBlueprint {

    public static final List<CompressionRecipeMember> COMPRESSION_FLOW = List.of(
            new CompressionRecipeMember(new MaterialWrapper(Material.SUGAR_CANE), 9),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.PREMIUM_SUGAR), 9),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.PREMIUM_SUGAR_CANE), 9),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.ENCHANTED_SUGAR), 9),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.ENCHANTED_SUGAR_CANE), 9),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.SUGAR_SINGULARITY), 9)
    );

    public SugarCaneBlueprintFamily(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<CompressionRecipeMember> getCompressionFlow() {
        return COMPRESSION_FLOW;
    }

}
