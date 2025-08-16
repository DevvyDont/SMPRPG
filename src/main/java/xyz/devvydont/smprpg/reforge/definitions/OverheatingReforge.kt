package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class OverheatingReforge(type: ReforgeType) : ReforgeBase(type) {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            AttributeEntry.scalar(
                AttributeWrapper.STRENGTH,
                (SpicyReforge.Companion.getDamageBonus(rarity) / 2).toDouble()
            ),
            AttributeEntry.additive(
                AttributeWrapper.CRITICAL_DAMAGE,
                Math.round(SpicyReforge.Companion.getCriticalBonus(rarity) / 4.0).toDouble()
            ),
            AttributeEntry.additive(AttributeWrapper.CRITICAL_CHANCE, (5 + rarity.ordinal).toDouble()),
            AttributeEntry.scalar(
                AttributeWrapper.ATTACK_SPEED,
                SwiftReforge.Companion.getAttackSpeedBuff(rarity) * 1.5
            ),
            AttributeEntry.scalar(
                AttributeWrapper.MOVEMENT_SPEED,
                (AgileReforge.Companion.getMovementSpeedBuff(rarity) * 2).toDouble()
            )
        )
    }

    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Provides a").append(ComponentUtils.create(" SIGNIFICANT", NamedTextColor.GOLD))
                .append(ComponentUtils.create(" boost")),
            ComponentUtils.create("in movement speed and attack speed"),
            ComponentUtils.create("with a moderate strength buff")
        )

    override fun getPowerRating(): Int {
        return 5
    }
}
