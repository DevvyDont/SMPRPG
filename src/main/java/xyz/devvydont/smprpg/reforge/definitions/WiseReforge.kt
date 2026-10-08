package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class WiseReforge(type: ReforgeType) : ReforgeBase(type) {
    fun getIntelligenceBoost(rarity: ItemRarity): Double {
        return (20 + rarity.ordinal * 25).toDouble()
    }

    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf(
            AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, getIntelligenceBoost(rarity)),
            AdditiveAttributeEntry(AttributeWrapper.HEALTH, (10 * rarity.ordinal).toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, (5 * rarity.ordinal).toDouble())
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Boosts intelligence and"),
            ComponentUtils.create("survivability moderately")
        )

    override val powerRating: Int get() = 1
}
