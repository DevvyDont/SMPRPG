package xyz.devvydont.smprpg.items.blueprints.sets.mystic;

import org.bukkit.Material;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemArmor;
import xyz.devvydont.smprpg.services.ItemService;

public class LuxeHelmet extends LuxeArmorSet {

    public LuxeHelmet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getDefense() {
        return ItemArmor.getDefenseFromMaterial(Material.IRON_CHESTPLATE);
    }

    @Override
    public int getHealth() {
        return 5;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.HELMET;
    }
}
