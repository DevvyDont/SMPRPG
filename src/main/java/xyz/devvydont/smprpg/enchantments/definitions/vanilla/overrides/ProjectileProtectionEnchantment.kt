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
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class ProjectileProtectionEnchantment(key: TypedKey<Enchantment>) : VanillaEnchantment(key), Listener {
    override val displayName: Component get() = ComponentUtils.create("Projectile Protection")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases projectile resistance by "),
            ComponentUtils.create(
                "+" + getProjectileResistancePercent(level) + "%",
                NamedTextColor.GREEN
            )
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(192, 192, 192)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_ARMOR
    override val maxLevel: Int get()                           = 10
    override val weight: Int get()                             = EnchantmentRarity.COMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.ARMOR
    override val skillRequirement: Int get()                   = 0

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentKeys.FIRE_PROTECTION,
            EnchantmentKeys.BLAST_PROTECTION,
        )

    @EventHandler(priority = EventPriority.HIGH)
    fun onFireDamage(event: EntityDamageEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.PROJECTILE) return

        if (event.entity !is LivingEntity) return
        val living = event.entity as LivingEntity

        val projRes = EnchantmentUtil.getWornEnchantLevel(enchantment, living.equipment)
        if (projRes <= 0) return

        val multiplier: Double = 1 - getProjectileResistancePercent(projRes) / 100.0
        event.setDamage(EntityDamageEvent.DamageModifier.BASE, event.damage* multiplier)
    }

    companion object {
        fun getProjectileResistancePercent(level: Int): Int {
            return (level * 2.0).toInt()
        }
    }
}
