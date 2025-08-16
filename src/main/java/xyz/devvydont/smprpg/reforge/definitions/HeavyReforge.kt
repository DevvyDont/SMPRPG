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


class HeavyReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            AdditiveAttributeEntry(AttributeWrapper.KNOCKBACK_RESISTANCE, getKnockbackResist(rarity).toDouble()),
            AdditiveAttributeEntry(
                AttributeWrapper.EXPLOSION_KNOCKBACK_RESISTANCE,
                getKnockbackResist(rarity).toDouble()
            ),
            ScalarAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, -getSpeedDebuff(rarity).toDouble())
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Provides a moderate boost"),
            ComponentUtils.create("to knockback resistance"),
            ComponentUtils.create("at the cost of movement speed")
        )

    override fun getPowerRating(): Int {
        return 1
    }

    companion object {
        fun getKnockbackResist(rarity: ItemRarity): Float {
            return when (rarity) {
                ItemRarity.COMMON -> .08f
                ItemRarity.UNCOMMON, ItemRarity.RARE, ItemRarity.EPIC -> .10f
                ItemRarity.LEGENDARY, ItemRarity.MYTHIC -> .12f
                ItemRarity.DIVINE -> .15f
                else -> .11f
            }
        }

        fun getSpeedDebuff(rarity: ItemRarity): Float {
            return when (rarity) {
                ItemRarity.COMMON -> .05f
                ItemRarity.UNCOMMON, ItemRarity.RARE, ItemRarity.EPIC -> .03f
                ItemRarity.LEGENDARY, ItemRarity.MYTHIC -> .02f
                else -> .01f
            }
        }
    }
}
