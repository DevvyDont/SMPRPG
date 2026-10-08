package xyz.devvydont.smprpg.items.blueprints.sets.fishing.xenohunter;

import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.services.ItemService;

public class XenohunterHelmet extends XenohunterSet {

    public XenohunterHelmet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.HELMET;
    }

    @Override
    public int getHealth() {
        return 1550;
    }

    @Override
    public int getDefense() {
        return 290;
    }
}
