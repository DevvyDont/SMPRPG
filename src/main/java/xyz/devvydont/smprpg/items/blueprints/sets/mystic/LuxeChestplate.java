package xyz.devvydont.smprpg.items.blueprints.sets.mystic;

import org.bukkit.Color;
import org.bukkit.Material;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemArmor;
import xyz.devvydont.smprpg.items.interfaces.IDyeable;
import xyz.devvydont.smprpg.services.ItemService;

public class LuxeChestplate extends LuxeArmorSet implements IDyeable {

    public LuxeChestplate(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getDefense() {
        return ItemArmor.getDefenseFromMaterial(Material.IRON_HELMET);
    }

    @Override
    public int getHealth() {
        return 10;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.CHESTPLATE;
    }

    @Override
    public Color getColor() {
        return Color.fromRGB(0x3c44aa);
    }
}
