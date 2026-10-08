package xyz.devvydont.smprpg.enchantments.definitions.vanilla.unchanged

import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.UnchangedEnchantment
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class ThornsEnchantment(key: TypedKey<Enchantment>) : UnchangedEnchantment(key), Listener {
    override val displayName: Component get() = ComponentUtils.create("Thorns")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Provides a "),
            ComponentUtils.create(
                getReflectChance(level).toString() + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" chance to reflect "),
            ComponentUtils.create(getReflectDamage(level).toString(), NamedTextColor.RED),
            ComponentUtils.create(" damage when hurt")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(0, 102, 0)

    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_ARMOR
    override val weight: Int get() = EnchantmentRarity.COMMON.weight
    override val skillRequirement: Int get() = 26

    @EventHandler(priority = EventPriority.LOW)
    fun onThornsDamage(event: EntityDamageByEntityEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.THORNS) return

        if (event.damager !is LivingEntity) return

        // If this is player on player, cancel it for the sake of PVE annoyance
        if (event.getEntity() is Player && event.damageSource.causingEntity is Player) {
            event.isCancelled = true
            return
        }
        val living = event.damager as LivingEntity

        val thornsLevel = EnchantmentUtil.getWornEnchantLevel(enchantment, living.equipment)

        // This probably means it was a mob like the guardian
        if (thornsLevel <= 0) {
            event.setDamage(EntityDamageEvent.DamageModifier.BASE, 20.0)
        } else {
            event.setDamage(EntityDamageEvent.DamageModifier.BASE, getReflectDamage(thornsLevel).toDouble())
        }
    }

    companion object {
        fun getReflectChance(level: Int): Int {
            return level * 15
        }

        fun getReflectDamage(level: Int): Int {
            return level * level * 20
        }
    }
}
