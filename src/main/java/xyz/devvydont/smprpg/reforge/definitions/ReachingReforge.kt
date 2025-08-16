package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class ReachingReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            ScalarAttributeEntry(AttributeWrapper.MINING_REACH, .1),
            ScalarAttributeEntry(AttributeWrapper.COMBAT_REACH, .1)
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Slightly increases reach distance")
        )

    override fun getPowerRating(): Int {
        return 1
    }
}
