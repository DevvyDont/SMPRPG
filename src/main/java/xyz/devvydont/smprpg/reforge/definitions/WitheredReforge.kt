package xyz.devvydont.smprpg.reforge.definitions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class WitheredReforge(type: ReforgeType) : ReforgeBase(type), Listener {
    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Provides a").append(ComponentUtils.create(" SIGNIFICANT", NamedTextColor.GOLD))
                .append(ComponentUtils.create(" boost")),
            ComponentUtils.create("in attack damage and attack speed"),
            ComponentUtils.EMPTY,
            ComponentUtils.create("Withered Bonus", NamedTextColor.BLUE),
            ComponentUtils.create("Deal ").append(ComponentUtils.create("+50%", NamedTextColor.GREEN))
                .append(ComponentUtils.create(" damage to enemies who")),
            ComponentUtils.create("have the ").append(
                ComponentUtils.create("withered", NamedTextColor.DARK_RED)
                    .append(ComponentUtils.create(" potion effect"))
            )
        )

    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, getDamageBuff(rarity).toDouble()),
            AttributeEntry.additive(AttributeWrapper.CRITICAL_DAMAGE, (2 + rarity.ordinal * 2).toDouble()),
            AttributeEntry.additive(AttributeWrapper.CRITICAL_CHANCE, (2 + rarity.ordinal * 2).toDouble()),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, .10)
        )
    }

    override fun getPowerRating(): Int {
        return 5
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    @Suppress("unused")
    private fun onDealDamageToWitheredEnemies(event: CustomEntityDamageByEntityEvent) {
        // Is the damaged entity withered?

        if (event.damaged !is LivingEntity)
            return

        val damaged = event.damaged as LivingEntity
        if (damaged.getPotionEffect(PotionEffectType.WITHER) == null)
            return

        if (event.dealer !is LivingEntity)
            return
        val dealer = event.dealer as LivingEntity

        if (dealer.equipment == null)
            return

        val hasWitheredReforge = hasReforge(dealer.equipment!!.itemInMainHand) ||
                hasReforge(dealer.equipment!!.itemInOffHand)

        if (!hasWitheredReforge)
            return

        // We have the withered reforge and the one getting attacked is withered. 2x the damage
        event.multiplyDamage(1.5)
    }

    companion object {
        fun getDamageBuff(rarity: ItemRarity): Float {
            return when (rarity) {
                ItemRarity.COMMON -> .4f
                ItemRarity.UNCOMMON -> .45f
                ItemRarity.RARE -> .5f
                ItemRarity.EPIC -> .55f
                ItemRarity.LEGENDARY -> .65f
                ItemRarity.MYTHIC -> .7f
                ItemRarity.DIVINE -> .75f
                ItemRarity.TRANSCENDENT -> .75f
                ItemRarity.SPECIAL -> .75f
            }
        }
    }
}
