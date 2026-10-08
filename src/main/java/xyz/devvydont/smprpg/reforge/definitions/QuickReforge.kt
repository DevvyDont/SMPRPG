package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class QuickReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, rarity.ordinal * 75.0),
            AdditiveAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, rarity.ordinal * 0.03)
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Slightly increases harvest speed"),
            ComponentUtils.create("when breaking blocks meant for this tool")
        )

    override val powerRating: Int get() = 1
}
