package xyz.devvydont.smprpg.reforge.definitions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.fishing.SeaCreature;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.reforge.ReforgeBase;
import xyz.devvydont.smprpg.reforge.ReforgeType;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Collection;
import java.util.List;

public class SaltyReforge extends ReforgeBase {

    public SaltyReforge(ReforgeType type) {
        super(type);
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiersWithRarity(ItemRarity rarity) {
        return List.of(
                AttributeEntry.additive(AttributeWrapper.FISHING_RATING, -20 + rarity.ordinal() * 5),
                AttributeEntry.additive(AttributeWrapper.FISHING_CREATURE_CHANCE, .4 + rarity.ordinal() * .2),
                AttributeEntry.additive(AttributeWrapper.FISHING_TREASURE_CHANCE, .4 + rarity.ordinal() * .2)
        );
    }

    /**
     * An item lore friendly list of components to display as a vague description of the item for what it does
     *
     * @return
     */
    @Override
    public List<Component> getDescription() {
        return List.of(
                ComponentUtils.create("Slightly increases your chance to reel up"),
                ComponentUtils.merge(
                        ComponentUtils.create("both "),
                        ComponentUtils.create("Sea Creatures", SeaCreature.NAME_COLOR),
                        ComponentUtils.create(" and "),
                        ComponentUtils.create("Treasure", NamedTextColor.GOLD)
                )
        );
    }

    /**
     * How much should we increase the power rating of an item if this container is present?
     *
     * @return
     */
    @Override
    public int getPowerRating() {
        return 3;
    }
}
