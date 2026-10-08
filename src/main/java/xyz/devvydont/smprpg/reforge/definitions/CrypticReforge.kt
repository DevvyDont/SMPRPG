package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class CrypticReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf(
            AttributeEntry.additive(
                AttributeWrapper.INTELLIGENCE,
                50.0 * rarity.ordinal
            ),
            AttributeEntry.additive(
                AttributeWrapper.ARCANE_RATING,
                2.0 * rarity.ordinal
            ),
            AttributeEntry.scalar(
                AttributeWrapper.ATTACK_SPEED,
                SwiftReforge.Companion.getAttackSpeedBuff(rarity).toDouble()
            )
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Provides a").append(ComponentUtils.create(" SIGNIFICANT", NamedTextColor.GOLD))
                .append(ComponentUtils.create(" boost")),
            ComponentUtils.create("in intelligence and arcane rating"),
            ComponentUtils.create("with a small attack speed buff")
        )

    override val powerRating: Int get() = 4
}
