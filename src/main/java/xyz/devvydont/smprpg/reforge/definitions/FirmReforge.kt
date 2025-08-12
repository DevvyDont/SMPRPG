package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class FirmReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            AdditiveAttributeEntry(AttributeWrapper.KNOCKBACK_RESISTANCE, getKnockbackResist(rarity).toDouble()),
            AdditiveAttributeEntry(
                AttributeWrapper.EXPLOSION_KNOCKBACK_RESISTANCE,
                getKnockbackResist(rarity).toDouble()
            )
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Provides a small boost"),
            ComponentUtils.create("to knockback resistance")
        )

    override fun getPowerRating(): Int {
        return 1
    }

    companion object {
        fun getKnockbackResist(rarity: ItemRarity): Float {
            return when (rarity) {
                ItemRarity.COMMON -> .01f
                ItemRarity.UNCOMMON, ItemRarity.RARE, ItemRarity.EPIC -> .02f
                ItemRarity.LEGENDARY, ItemRarity.MYTHIC -> .03f
                ItemRarity.DIVINE -> .04f
                else -> .05f
            }
        }
    }
}
