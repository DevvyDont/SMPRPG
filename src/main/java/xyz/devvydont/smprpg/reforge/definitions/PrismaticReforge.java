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

public class PrismaticReforge extends ReforgeBase {

    public PrismaticReforge(ReforgeType type) {
        super(type);
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiersWithRarity(ItemRarity rarity) {
        return List.of(
                AttributeEntry.additive(AttributeWrapper.FISHING_RATING, 20 + rarity.ordinal() * 10),
                AttributeEntry.additive(AttributeWrapper.FISHING_CREATURE_CHANCE, 1 + rarity.ordinal() * .5),
                AttributeEntry.additive(AttributeWrapper.FISHING_TREASURE_CHANCE, .5 + rarity.ordinal() * .25),
                AttributeEntry.additive(AttributeWrapper.FISHING_SPEED, 25 + rarity.ordinal() * 5)
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
                ComponentUtils.create("Dramatically increases your chance to reel up"),
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
