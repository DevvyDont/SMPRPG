package xyz.devvydont.smprpg.reforge.definitions;

import net.kyori.adventure.text.Component;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry;
import xyz.devvydont.smprpg.reforge.ReforgeBase;
import xyz.devvydont.smprpg.reforge.ReforgeType;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Collection;
import java.util.List;

public class StrongReforge extends ReforgeBase {

    public StrongReforge(ReforgeType type) {
        super(type);
    }

    public float getDamageBoost(ItemRarity rarity) {
        return .03f * (rarity.ordinal()+1);
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiersWithRarity(ItemRarity rarity) {
        return List.of(
                new ScalarAttributeEntry(AttributeWrapper.STRENGTH, getDamageBoost(rarity)),
                AttributeEntry.additive(AttributeWrapper.CRITICAL_DAMAGE, 20 + rarity.ordinal() * 5)
        );
    }

    @Override
    public List<Component> getDescription() {
        return List.of(ComponentUtils.create("Provides a small damage boost"));
    }

    @Override
    public int getPowerRating() {
        return 1;
    }
}
