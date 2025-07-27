package xyz.devvydont.smprpg.items.blueprints.fishing;

import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;

/**
 * A simple extension class off of normal fish. Tweaks certain properties to make the worth and food effects better.
 */
public class CookedFishBlueprint extends FishBlueprint {
    public CookedFishBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }
}
