package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class CopiousReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf(
            AttributeEntry.additive(AttributeWrapper.LUCK, (rarity.ordinal + 3).toDouble())
        )
    }

    override val description: List<Component>
        get() = listOf(
            ComponentUtils.create("Moderately boosts luck")
        )

    override fun getPowerRating(): Int {
        return 2
    }
}
