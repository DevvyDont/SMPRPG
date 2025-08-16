package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.fishing.SeaCreature
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class SaltyReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            AttributeEntry.additive(AttributeWrapper.FISHING_RATING, (-20 + rarity.ordinal * 5).toDouble()),
            AttributeEntry.additive(AttributeWrapper.FISHING_CREATURE_CHANCE, .4 + rarity.ordinal * .2),
            AttributeEntry.additive(AttributeWrapper.FISHING_TREASURE_CHANCE, .2 + rarity.ordinal * .1),
            AttributeEntry.additive(AttributeWrapper.FISHING_SPEED, (10 + rarity.ordinal * 2).toDouble())
        )
    }

    override val description: List<Component>
        /**
         * An item lore friendly list of components to display as a vague description of the item for what it does
         *
         * @return
         */
        get() = listOf<Component>(
            ComponentUtils.create("Slightly increases your chance to reel up"),
            ComponentUtils.merge(
                ComponentUtils.create("both "),
                ComponentUtils.create("Sea Creatures", SeaCreature.NAME_COLOR),
                ComponentUtils.create(" and "),
                ComponentUtils.create("Treasure", NamedTextColor.GOLD)
            )
        )

    /**
     * How much should we increase the power rating of an item if this container is present?
     *
     * @return
     */
    override fun getPowerRating(): Int {
        return 3
    }
}
