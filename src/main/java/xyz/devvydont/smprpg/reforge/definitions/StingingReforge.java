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

public class StingingReforge extends ReforgeBase {


    public StingingReforge(ReforgeType type) {
        super(type);
    }

    public static float getDamageBonus(ItemRarity rarity) {
        return .02f * rarity.ordinal() + .04f;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiersWithRarity(ItemRarity rarity) {
        return List.of(
                AttributeEntry.multiplicative(AttributeWrapper.STRENGTH, getDamageBonus(rarity)),
                AttributeEntry.additive(AttributeWrapper.CRITICAL_DAMAGE, 50 + rarity.ordinal() * 10),
                AttributeEntry.additive(AttributeWrapper.CRITICAL_CHANCE, 20 + rarity.ordinal() * 5)
        );
    }

    @Override
    public List<Component> getDescription() {
        return List.of(
                ComponentUtils.create("Provides a moderate damage boost")
        );
    }

    @Override
    public int getPowerRating() {
        return 2;
    }
}
