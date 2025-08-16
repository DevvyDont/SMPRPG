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


class HeftyReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            AdditiveAttributeEntry(AttributeWrapper.KNOCKBACK_RESISTANCE, getKnockbackResist(rarity).toDouble()),
            AdditiveAttributeEntry(
                AttributeWrapper.EXPLOSION_KNOCKBACK_RESISTANCE,
                getKnockbackResist(rarity).toDouble()
            ),
            ScalarAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, -getSpeedDebuff(rarity).toDouble()),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, (-getSpeedDebuff(rarity) * 2).toDouble()),
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, ((rarity.ordinal + 1) / 500.0))
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Provides a generous boost"),
            ComponentUtils.create("to knockback resistance and strength"),
            ComponentUtils.create("at the cost of general speed")
        )

    override fun getPowerRating(): Int {
        return 2
    }

    companion object {
        fun getKnockbackResist(rarity: ItemRarity): Float {
            return when (rarity) {
                ItemRarity.COMMON -> .14f
                ItemRarity.UNCOMMON, ItemRarity.RARE, ItemRarity.EPIC -> .16f
                ItemRarity.LEGENDARY, ItemRarity.MYTHIC -> .19f
                ItemRarity.DIVINE -> .24f
                else -> .25f
            }
        }

        fun getSpeedDebuff(rarity: ItemRarity): Float {
            return when (rarity) {
                ItemRarity.COMMON -> .03f
                ItemRarity.UNCOMMON, ItemRarity.RARE, ItemRarity.EPIC -> .02f
                ItemRarity.LEGENDARY, ItemRarity.MYTHIC -> .02f
                else -> .01f
            }
        }
    }
}
