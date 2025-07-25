package xyz.devvydont.smprpg.reforge.definitions;

import net.kyori.adventure.text.Component;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry;
import xyz.devvydont.smprpg.reforge.ReforgeBase;
import xyz.devvydont.smprpg.reforge.ReforgeType;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Collection;
import java.util.List;

public class SavageReforge extends ReforgeBase {

    public SavageReforge(ReforgeType type) {
        super(type);
    }

    public float getDamageBoost(ItemRarity rarity) {
        return .04f * (rarity.ordinal()+1) + .16f;
    }

    public int getCritical(ItemRarity rarity) {
        return 10 + rarity.ordinal() * 15;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiersWithRarity(ItemRarity rarity) {
        return List.of(
                new ScalarAttributeEntry(AttributeWrapper.STRENGTH, getDamageBoost(rarity)),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, .05),
                new AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, getCritical(rarity)),
                new AdditiveAttributeEntry(AttributeWrapper.HEALTH, -50 - 10*rarity.ordinal()),
                new AdditiveAttributeEntry(AttributeWrapper.DEFENSE, -50 - 10*rarity.ordinal())
        );
    }

    @Override
    public List<Component> getDescription() {
        return List.of(
                ComponentUtils.create("Boosts damage significantly at"),
                ComponentUtils.create("the expense of survivability")
        );
    }

    @Override
    public int getPowerRating() {
        return 1;
    }
}
