package xyz.devvydont.smprpg.items.blueprints.sets.slimy;

import org.bukkit.Color;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.interfaces.IDyeable;
import xyz.devvydont.smprpg.items.interfaces.ITrimmable;
import xyz.devvydont.smprpg.services.ItemService;

public class SlimyChestplate extends SlimyArmorSet implements IDyeable, ITrimmable {

    public SlimyChestplate(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getDefense() {
        return 95;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.CHESTPLATE;
    }

    @Override
    public TrimMaterial getTrimMaterial() {
        return TrimMaterial.DIAMOND;
    }

    @Override
    public TrimPattern getTrimPattern() {
        return TrimPattern.WARD;
    }

    @Override
    public Color getColor() {
        return Color.fromRGB(0x80c71f);
    }
}
