package xyz.devvydont.smprpg.items.blueprints.sets.fishing.xenohunter;

import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.services.ItemService;

public class XenohunterLeggings extends XenohunterSet {

    public XenohunterLeggings(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.LEGGINGS;
    }

    @Override
    public int getHealth() {
        return XenohunterChestplate.HEALTH - 50;
    }

    @Override
    public int getDefense() {
        return XenohunterChestplate.DEFENSE - 50;
    }
}
