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

public class AncientReforge extends ReforgeBase {

    public AncientReforge(ReforgeType type) {
        super(type);
    }

    public static int getDefenseBonus(ItemRarity rarity) {
        return rarity.ordinal() + 1;
    }

    public static float getMovementSpeedBonus(ItemRarity rarity) {
        if (rarity.ordinal() >= ItemRarity.LEGENDARY.ordinal())
            return .02f;
        return .01f;
    }

    public static float getStrengthBonus(ItemRarity rarity) {
        if (rarity.ordinal() >= ItemRarity.EPIC.ordinal())
            return .05f;
        return .03f;
    }

    public static float getLuckBonus(ItemRarity rarity) {
        if (rarity.ordinal() >= ItemRarity.EPIC.ordinal())
            return 2;
        return 1;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiersWithRarity(ItemRarity rarity) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.DEFENSE, getDefenseBonus(rarity)),
                new AdditiveAttributeEntry(AttributeWrapper.HEALTH, getDefenseBonus(rarity)),
                new AdditiveAttributeEntry(AttributeWrapper.ARMOR, rarity.ordinal() >= ItemRarity.EPIC.ordinal() ? 2 : 1),
                new ScalarAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, getMovementSpeedBonus(rarity)),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, getMovementSpeedBonus(rarity)),
                new ScalarAttributeEntry(AttributeWrapper.STRENGTH, getStrengthBonus(rarity)),
                AttributeEntry.additive(AttributeWrapper.LUCK, getLuckBonus(rarity))
        );
    }

    @Override
    public List<Component> getDescription() {
        return List.of(
                ComponentUtils.create("Provides a moderate"),
                ComponentUtils.create("boost for all stats")

        );
    }

    @Override
    public int getPowerRating() {
        return 4;
    }

}
