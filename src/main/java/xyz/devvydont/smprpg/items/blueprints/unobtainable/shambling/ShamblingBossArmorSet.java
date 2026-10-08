package xyz.devvydont.smprpg.items.blueprints.unobtainable.shambling;

import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IEquippableAssetOverride;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;

public abstract class ShamblingBossArmorSet extends CustomAttributeItem implements IEquippableAssetOverride {

    private static final Key ASSET_KEY = Key.key("shambling_boss");


    public ShamblingBossArmorSet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public Key getAssetId() {
        return ASSET_KEY;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(AttributeEntry.additive(AttributeWrapper.ARMOR, 0.0));
    }

    @Override
    public int getPowerRating() {
        return 0;
    }
}
