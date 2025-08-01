package xyz.devvydont.smprpg.reforge.definitions;

import net.kyori.adventure.text.Component;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.reforge.ReforgeBase;
import xyz.devvydont.smprpg.reforge.ReforgeType;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Collection;
import java.util.List;

public class HeartyReforge extends ReforgeBase {

    public HeartyReforge(ReforgeType type) {
        super(type);
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiersWithRarity(ItemRarity rarity) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.HEALTH, (rarity.ordinal()+1)*7),
                AttributeEntry.additive(AttributeWrapper.REGENERATION, (rarity.ordinal()+1)*10)
        );
    }

    @Override
    public List<Component> getDescription() {
        return List.of(
                ComponentUtils.create("Provides a moderate boost"),
                ComponentUtils.create("to maximum health")
        );
    }

    @Override
    public int getPowerRating() {
        return 2;
    }
}
