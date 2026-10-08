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
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class BreachEnchantment(key: TypedKey<Enchantment>) : VanillaEnchantment(key), Listener {
    override val displayName: Component get() = ComponentUtils.create("Breach")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Attacks pierce through "),
            ComponentUtils.create(
                getDefensePiercing(level).toString(),
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" defense"),
            ComponentUtils.create(" **NOT IMPLEMENTED", NamedTextColor.DARK_RED)
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(171, 178, 179)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_MACE
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.RARE.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.MAINHAND
    override val skillRequirement: Int get()                   = 50

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        /**
         * A set of enchantments that this enchantment conflicts with.
         * If there are none, this enchantment has no conflicts
         *
         * @return
         */
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentKeys.DENSITY
        )

    @EventHandler
    fun onDealMaceDamage(event: CustomEntityDamageByEntityEvent?) {
        // todo figure out how to decrease defense on attacks
    }

    companion object {
        fun getDefensePiercing(level: Int): Int {
            return when (level) {
                0 -> 0
                1 -> 100
                2 -> 250
                3 -> 600
                4 -> 1000
                5 -> 2000
                else -> 1000 * level + 2000
            }
        }
    }
}
