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


class PolishedReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, getDefenseBonus(rarity).toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.HEALTH, getDefenseBonus(rarity).toDouble()),
            AdditiveAttributeEntry(
                AttributeWrapper.ARMOR,
                (if (rarity.ordinal >= ItemRarity.EPIC.ordinal) 3 else 2).toDouble()
            ),
            ScalarAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, getMovementSpeedBonus(rarity).toDouble()),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, getMovementSpeedBonus(rarity).toDouble()),
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, getStrengthBonus(rarity).toDouble()),
            AttributeEntry.additive(AttributeWrapper.LUCK, getLuckBonus(rarity).toDouble())
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Provides a small boost"),
            ComponentUtils.create("for all stats")

        )

    override fun getPowerRating(): Int {
        return 2
    }

    companion object {
        fun getDefenseBonus(rarity: ItemRarity): Int {
            return (rarity.ordinal + 1) * 2 / 2
        }

        fun getMovementSpeedBonus(rarity: ItemRarity?): Float {
            return .01f
        }

        fun getStrengthBonus(rarity: ItemRarity): Float {
            if (rarity.ordinal >= ItemRarity.EPIC.ordinal) return .02f
            return .01f
        }


        fun getLuckBonus(rarity: ItemRarity?): Float {
            return 1f
        }
    }
}
