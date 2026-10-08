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
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil
import xyz.devvydont.smprpg.enchantments.base.AttributeEnchantment
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeModifierType
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import kotlin.math.max

class BlastProtectionEnchantment(key: TypedKey<Enchantment>) : VanillaEnchantment(key), AttributeEnchantment,
    Listener {
    override val displayName: Component get() = ComponentUtils.create("Blast Protection")
    override val description: Component
        get() = ComponentUtils.merge(
            ComponentUtils.create("Increases explosion resistance by "),
            ComponentUtils.create(
                "+" + getExplosiveProtectionPercent(level) + "%",
                NamedTextColor.GREEN
            )
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(60, 135, 145)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_ARMOR
    override val maxLevel: Int get()                           = 10
    override val weight: Int get()                             = EnchantmentRarity.COMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.ARMOR
    override val skillRequirement: Int get()                   = 0

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        /**
         * A set of enchantments that this enchantment conflicts with.
         * If there are none, this enchantment has no conflicts
         *
         * @return
         */
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentKeys.FIRE_PROTECTION,
            EnchantmentKeys.PROJECTILE_PROTECTION
        )

    override val powerRating : Int get() = level / 5
    override val attributeModifierType : AttributeModifierType get() = AttributeModifierType.ENCHANTMENT
    override fun getHeldAttributes() : MutableCollection<AttributeEntry?>? {
        return mutableListOf(
            ScalarAttributeEntry(
                AttributeWrapper.EXPLOSION_KNOCKBACK_RESISTANCE,
                getExplosiveProtectionPercent(level) / 100.0
            )
        )
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onExplosiveDamageTaken(event: EntityDamageEvent) {
        // Ignore non explosions

        if (event.cause != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION && event.cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) return

        // Ignore if the entity can't wear equipment
        if (event.entity !is LivingEntity) return
        val entity = event.entity as LivingEntity

        val blast = EnchantmentUtil.getWornEnchantLevel(enchantment, entity.equipment)
        if (blast <= 0) return

        val multiplier = max(0.0, 1.0 - (getExplosiveProtectionPercent(blast) / 100.0))
        event.setDamage(EntityDamageEvent.DamageModifier.BASE, event.damage * multiplier)
    }

    companion object {
        fun getExplosiveProtectionPercent(level: Int): Int {
            return (level * 2.5).toInt()
        }
    }
}
