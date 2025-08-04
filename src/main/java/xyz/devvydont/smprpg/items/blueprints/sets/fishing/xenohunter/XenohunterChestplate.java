package xyz.devvydont.smprpg.items.blueprints.sets.fishing.xenohunter;

import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.services.ItemService;

public class XenohunterChestplate extends XenohunterSet {

    public static final int DEFENSE = 600;
    public static final int HEALTH = 500;

    public XenohunterChestplate(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.CHESTPLATE;
    }

    @Override
    public int getHealth() {
        return HEALTH;
    }

    @Override
    public int getDefense() {
        return DEFENSE;
    }
}
