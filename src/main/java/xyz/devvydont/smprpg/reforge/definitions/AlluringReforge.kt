package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.fishing.SeaCreature
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class AlluringReforge(type: ReforgeType) : ReforgeBase(type) {

    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf(
            AttributeEntry.additive(AttributeWrapper.STRENGTH, (10 + rarity.ordinal * 5).toDouble()),
            AttributeEntry.additive(AttributeWrapper.FISHING_RATING, (30 + rarity.ordinal * 5).toDouble()),
            AttributeEntry.additive(AttributeWrapper.FISHING_CREATURE_CHANCE, getChance(rarity))
        )
    }

    override val description: List<Component>
        /**
         * An item lore friendly list of components to display as a vague description of the item for what it does
         *
         * @return
         */
        get() = listOf(
            ComponentUtils.create("Increases your chance to"),
            ComponentUtils.merge(
                ComponentUtils.create("reel up "),
                ComponentUtils.create("Sea Creatures", SeaCreature.NAME_COLOR)
            )
        )

    /**
     * How much should we increase the power rating of an item if this container is present
     *
     * @return
     */
    override fun getPowerRating(): Int {
        return 3
    }

    companion object {
        fun getChance(rarity: ItemRarity): Double {
            return (2 + rarity.ordinal).toDouble()
        }
    }
}
