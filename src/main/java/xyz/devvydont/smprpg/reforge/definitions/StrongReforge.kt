package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class StrongReforge(type: ReforgeType) : ReforgeBase(type) {
    fun getDamageBoost(rarity: ItemRarity): Float {
        return .03f * (rarity.ordinal + 1)
    }

    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, getDamageBoost(rarity).toDouble()),
            AttributeEntry.additive(AttributeWrapper.CRITICAL_DAMAGE, (20 + rarity.ordinal * 5).toDouble())
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(ComponentUtils.create("Provides a small damage boost"))

    override fun getPowerRating(): Int {
        return 1
    }
}
