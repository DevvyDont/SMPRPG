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
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class DensityEnchantment(key: TypedKey<Enchantment>) : VanillaEnchantment(key), Listener {
    override val displayName: Component get() = ComponentUtils.create("Density")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases damage dealt by "),
            ComponentUtils.create("+" + getDamagePerBlock(level), NamedTextColor.GREEN),
            ComponentUtils.create(" per block fallen")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(117, 117, 117)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_MACE
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.RARE.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.MAINHAND
    override val skillRequirement: Int get()                   = 30

    @EventHandler(priority = EventPriority.NORMAL)
    fun onFallingMaceDamage(event: CustomEntityDamageByEntityEvent) {
        if (event.dealer !is Player) return
        val dealer = event.dealer

        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(dealer)
        if (!isEnchantmentActive(dealer.equipment.itemInMainHand, leveledPlayer)) return

        if (dealer.fallDistance <= 3.0) return

        val density = EnchantmentUtil.getHoldingEnchantLevel(enchantment, EquipmentSlotGroup.MAINHAND, dealer.equipment)
        if (density <= 0) return

        val damage: Int = getDamagePerBlock(density)
        event.addDamage(damage.toDouble())
    }

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        /**
         * A set of enchantments that this enchantment conflicts with.
         * If there are none, this enchantment has no conflicts
         *
         * @return
         */
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentKeys.BREACH
        )

    companion object {
        fun getDamagePerBlock(level: Int): Int {
            return when (level) {
                0 -> 0
                1 -> 1
                2 -> 3
                3 -> 6
                5 -> 10
                else -> getDamagePerBlock(5) + (level - 5) * 5
            }
        }
    }
}
