package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class GleaningReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf(
            AdditiveAttributeEntry(AttributeWrapper.FARMING_FORTUNE, getYieldBonus(rarity).toDouble()),
            ScalarAttributeEntry(AttributeWrapper.MINING_REACH, getReachBonus(rarity).toDouble()),
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Provides a generous boost to"),
            ComponentUtils.create("Yield and non-combat reach")
        )

    override val powerRating: Int get() = 3

    companion object {
        fun getYieldBonus(rarity: ItemRarity): Float {
            return 5 + rarity.ordinal * 15.0f
        }

        fun getReachBonus(rarity: ItemRarity): Float {
            return 0.03f + .03f * rarity.ordinal
        }
    }
}
