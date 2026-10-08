package xyz.devvydont.smprpg.enchantments.definitions.vanilla.overrides

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.set.RegistryKeySet
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class FireProtectionEnchantment(key: TypedKey<Enchantment>) : VanillaEnchantment(key), Listener {
    override val displayName: Component get() = ComponentUtils.create("Fire Protection")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases fire resistance by "),
            ComponentUtils.create(
                "+" + getFireResistancePercent(level) + "%",
                NamedTextColor.GREEN
            )
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(255, 89, 23)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_ARMOR
    override val maxLevel: Int get()                           = 10
    override val weight: Int get()                             =  EnchantmentRarity.COMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.ARMOR
    override val skillRequirement: Int get()                   = 0

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentKeys.BLAST_PROTECTION,
            EnchantmentKeys.PROJECTILE_PROTECTION
        )

    @EventHandler(priority = EventPriority.HIGH)
    fun onFireDamage(event: EntityDamageEvent) {
        if (!isFireCause(event.cause)) return

        if (event.entity !is LivingEntity) return
        val living = event.entity as LivingEntity

        val fireRes = EnchantmentUtil.getWornEnchantLevel(enchantment, living.equipment)
        if (fireRes <= 0) return

        val multiplier: Double = 1 - getFireResistancePercent(fireRes) / 100.0
        event.setDamage(EntityDamageEvent.DamageModifier.BASE, event.damage * multiplier)
    }

    companion object {
        fun getFireResistancePercent(level: Int): Int { return (level * 2.5).toInt() }
        fun isFireCause(cause: DamageCause): Boolean {
            return when (cause) {
                DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.CAMPFIRE, DamageCause.LAVA, DamageCause.LIGHTNING, DamageCause.HOT_FLOOR, DamageCause.MELTING, DamageCause.DRAGON_BREATH -> true
                else -> false
            }
        }
    }
}
