package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class StingingReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            AttributeEntry.multiplicative(AttributeWrapper.STRENGTH, getDamageBonus(rarity).toDouble()),
            AttributeEntry.additive(AttributeWrapper.CRITICAL_DAMAGE, (50 + rarity.ordinal * 10).toDouble()),
            AttributeEntry.additive(AttributeWrapper.CRITICAL_CHANCE, (20 + rarity.ordinal * 5).toDouble())
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Provides a moderate damage boost")
        )

    override fun getPowerRating(): Int {
        return 2
    }

    companion object {
        fun getDamageBonus(rarity: ItemRarity): Float {
            return .02f * rarity.ordinal + .04f
        }
    }
}
