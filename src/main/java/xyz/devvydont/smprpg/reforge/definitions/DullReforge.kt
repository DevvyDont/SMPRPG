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

class DullReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf(
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, -.15),
            AdditiveAttributeEntry(AttributeWrapper.SWEEPING, getSweepBoost(rarity).toDouble())
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Decreases base damage but increases"),
            ComponentUtils.create("effectiveness of sweeping attacks")
        )

    override fun getPowerRating(): Int {
        return 1
    }

    companion object {
        fun getSweepBoost(rarity: ItemRarity): Float {
            return .02f * rarity.ordinal + .05f
        }
    }
}
