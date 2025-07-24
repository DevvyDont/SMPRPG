package xyz.devvydont.smprpg.reforge.definitions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry;
import xyz.devvydont.smprpg.reforge.ReforgeBase;
import xyz.devvydont.smprpg.reforge.ReforgeType;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Collection;
import java.util.List;

public class OverheatingReforge extends ReforgeBase {

    public OverheatingReforge(ReforgeType type) {
        super(type);
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiersWithRarity(ItemRarity rarity) {
        return List.of(
                AttributeEntry.scalar(AttributeWrapper.STRENGTH, SpicyReforge.getDamageBonus(rarity)/2),
                AttributeEntry.additive(AttributeWrapper.CRITICAL_DAMAGE, Math.round(SpicyReforge.getCriticalBonus(rarity)/4.0)),
                AttributeEntry.additive(AttributeWrapper.CRITICAL_CHANCE, 5+rarity.ordinal()),
                AttributeEntry.scalar(AttributeWrapper.ATTACK_SPEED, SwiftReforge.getAttackSpeedBuff(rarity)*2),
                AttributeEntry.scalar(AttributeWrapper.MOVEMENT_SPEED, AgileReforge.getMovementSpeedBuff(rarity)*2)
        );
    }

    @Override
    public List<Component> getDescription() {
        return List.of(
                ComponentUtils.create("Provides a").append(ComponentUtils.create(" SIGNIFICANT", NamedTextColor.GOLD)).append(ComponentUtils.create(" boost")),
                ComponentUtils.create("in movement speed and attack speed"),
                ComponentUtils.create("with a moderate strength buff")
        );
    }

    @Override
    public int getPowerRating() {
        return 5;
    }
}
