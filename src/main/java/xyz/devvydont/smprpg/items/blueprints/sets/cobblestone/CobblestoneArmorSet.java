package xyz.devvydont.smprpg.items.blueprints.sets.cobblestone;

import net.kyori.adventure.key.Key;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IEquippableAssetOverride;
import xyz.devvydont.smprpg.services.ItemService;

public abstract class CobblestoneArmorSet extends CustomAttributeItem implements IEquippableAssetOverride {

    private static final Key key = Key.key("cobblestone");

    public CobblestoneArmorSet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public Key getAssetId() {
        return key;
    }
}
