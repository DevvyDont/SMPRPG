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


class ProspectingReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf(
            AdditiveAttributeEntry(AttributeWrapper.MINING_FORTUNE, getFortuneBonus(rarity).toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, getMiningSpeedBonus(rarity).toDouble()),
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Provides a generous boost to"),
            ComponentUtils.create("Fortune and Harvest Speed")
        )

    override val powerRating: Int get() = 3

    companion object {
        fun getFortuneBonus(rarity: ItemRarity): Float {
            return 5 + rarity.ordinal * 15.0f
        }

        fun getMiningSpeedBonus(rarity: ItemRarity): Float {
            return 25 + rarity.ordinal * 50.0f
        }
    }
}
