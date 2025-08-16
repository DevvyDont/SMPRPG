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


class SavageReforge(type: ReforgeType) : ReforgeBase(type) {
    fun getDamageBoost(rarity: ItemRarity): Float {
        return .04f * (rarity.ordinal + 1) + .16f
    }

    fun getCritical(rarity: ItemRarity): Int {
        return 10 + rarity.ordinal * 15
    }

    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, getDamageBoost(rarity).toDouble()),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, .05),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, getCritical(rarity).toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.HEALTH, (-50 - 10 * rarity.ordinal).toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, (-50 - 10 * rarity.ordinal).toDouble())
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Boosts damage significantly at"),
            ComponentUtils.create("the expense of survivability")
        )

    override fun getPowerRating(): Int {
        return 1
    }
}
