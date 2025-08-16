package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class PlunderingReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            AttributeEntry.additive(AttributeWrapper.LUCK, (2 + rarity.ordinal * 2).toDouble()),
            AttributeEntry.additive(AttributeWrapper.FISHING_RATING, (50 + rarity.ordinal * 10).toDouble()),
            AttributeEntry.additive(AttributeWrapper.FISHING_TREASURE_CHANCE, (1 + rarity.ordinal).toDouble())
        )
    }

    override val description: List<Component>
        /**
         * An item lore friendly list of components to display as a vague description of the item for what it does
         *
         * @return
         */
        get() = listOf<Component>(
            ComponentUtils.create("Increases your chance to"),
            ComponentUtils.merge(
                ComponentUtils.create("reel up "),
                ComponentUtils.create("treasure", NamedTextColor.GOLD)
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
