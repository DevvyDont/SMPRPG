package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class SpicyReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            MultiplicativeAttributeEntry(AttributeWrapper.STRENGTH, getDamageBonus(rarity).toDouble()),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, .05),
            AttributeEntry.additive(AttributeWrapper.CRITICAL_DAMAGE, getCriticalBonus(rarity).toDouble())
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Provides a generous boost to"),
            ComponentUtils.create("damage output stats")
        )

    override fun getPowerRating(): Int {
        return 3
    }

    companion object {
        fun getDamageBonus(rarity: ItemRarity): Float {
            return .05f * rarity.ordinal + .10f
        }

        fun getCriticalBonus(rarity: ItemRarity): Int {
            return 10 + rarity.ordinal * 10
        }
    }
}
