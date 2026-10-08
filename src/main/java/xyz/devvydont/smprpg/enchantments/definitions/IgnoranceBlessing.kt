package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.set.RegistryKeySet
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.ability.AbilityCost
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.ScrollColor
import xyz.devvydont.smprpg.events.abilities.AbilityCastEvent
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class IgnoranceBlessing(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Blessing of Ignorance", NamedTextColor.YELLOW)
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Reduces the cost of "),
            ComponentUtils.create("mana based attacks and abilities", NamedTextColor.AQUA),
            ComponentUtils.create(" by " + getManaCostReduction(level) + "%")
        )
    override val enchantColor: TextColor get() = NamedTextColor.YELLOW
    override val scrollColor: Color get()        = ScrollColor.BLESSING.color
    override val scrollBindingColor: Color get() = Color.fromRGB(0, 0, 255)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_WEAPON
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.BLESSING.weight
    override val isBlessing: Boolean get()                     = true
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.ANY
    override val skillRequirement: Int get()                   = 25

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentService.KEEPING_BLESSING.typedKey,
            EnchantmentService.MERCY_BLESSING.typedKey,
            EnchantmentService.VOIDSTRIDING_BLESSING.typedKey,
            EnchantmentService.REPLENISHING.typedKey,
            EnchantmentService.TELEKINESIS_BLESSING.typedKey
        )

    @EventHandler
    private fun onAbilityCast(event: AbilityCastEvent) {
        val dealer = event.player
        if (!isEnchantmentActive(dealer.player.equipment.itemInMainHand, dealer)) return

        if (event.abilityCost.resource == AbilityCost.Resource.MANA) {
            val ench = event.item.getEnchantmentLevel(this.enchantment)
            if (ench > 0) event.abilityCost = event.abilityCost.reduce(getManaCostReduction(ench) / 100.0)
        }
    }

    companion object {
        fun getManaCostReduction(level: Int): Int { return level * 10 }
    }
}
