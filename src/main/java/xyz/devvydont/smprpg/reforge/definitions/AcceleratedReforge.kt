package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class AcceleratedReforge(type: ReforgeType) : ReforgeBase(type) {
    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Provides a").append(ComponentUtils.create(" SIGNIFICANT", NamedTextColor.GOLD)),
            ComponentUtils.create("boost in ")
                .append(ComponentUtils.create("movement/attack speed", NamedTextColor.WHITE))
        )

    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf(
            ScalarAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, getMovementSpeedBuff(rarity).toDouble()),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, (rarity.ordinal + 1) * 5 / 100.0)
        )
    }

    override fun getPowerRating(): Int {
        return 4
    }

    companion object {
        fun getMovementSpeedBuff(rarity: ItemRarity): Float {
            return when (rarity) {
                ItemRarity.COMMON -> .10f
                ItemRarity.UNCOMMON -> .15f
                ItemRarity.RARE -> .20f
                ItemRarity.EPIC -> .30f
                ItemRarity.LEGENDARY -> .40f
                ItemRarity.MYTHIC -> .50f
                ItemRarity.DIVINE -> .65f
                ItemRarity.TRANSCENDENT -> .8f
                ItemRarity.SPECIAL -> .95f
            }
        }
    }
}
