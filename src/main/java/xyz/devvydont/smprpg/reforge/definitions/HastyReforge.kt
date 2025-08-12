package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class HastyReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, rarity.ordinal * .03 + .07),
            AdditiveAttributeEntry(AttributeWrapper.MINING_EFFICIENCY, rarity.ordinal * .04 + .1)
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Moderately increases mining"),
            ComponentUtils.create("speed and efficiency")
        )

    override fun getPowerRating(): Int {
        return 3
    }
}
