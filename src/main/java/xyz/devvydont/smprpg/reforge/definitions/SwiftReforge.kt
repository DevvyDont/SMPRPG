package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class SwiftReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            ScalarAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, getMovementSpeedBuff(rarity).toDouble()),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, getAttackSpeedBuff(rarity).toDouble())
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Greatly increases movement speed"),
            ComponentUtils.create("with a small boost in attack speed")
        )

    override fun getPowerRating(): Int {
        return 1
    }

    companion object {
        fun getMovementSpeedBuff(rarity: ItemRarity): Float {
            return rarity.ordinal * .02f + .05f
        }

        fun getAttackSpeedBuff(rarity: ItemRarity): Float {
            return rarity.ordinal * .01f + .05f
        }
    }
}
