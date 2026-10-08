package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.EnchantmentKeys
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
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.ScrollColor
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class KeepingBlessing(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Blessing of Keeping", NamedTextColor.YELLOW)
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("This item is "),
            ComponentUtils.create("soulbound", NamedTextColor.DARK_PURPLE),
            ComponentUtils.create(" and will not drop from "),
            ComponentUtils.create("death", NamedTextColor.RED)
        )
    override val enchantColor: TextColor get()   = NamedTextColor.YELLOW
    override val scrollColor: Color get()        = ScrollColor.BLESSING.color
    override val scrollBindingColor: Color get() = Color.fromRGB(255, 149, 0)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_VANISHING
    override val maxLevel: Int get()                           = 1
    override val weight: Int get()                             = EnchantmentRarity.BLESSING.weight
    override val isBlessing: Boolean get()                     = true
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.ANY
    override val skillRequirement: Int get()                   = 10

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentKeys.VANISHING_CURSE,
            EnchantmentService.TELEKINESIS_BLESSING.typedKey,
            EnchantmentService.MERCY_BLESSING.typedKey,
            EnchantmentService.IGNORANCE_BLESSING.typedKey
        )

    override fun isEnchantmentActive(itemStack: ItemStack, player: LeveledPlayer): Boolean { return true }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onDeath(event: PlayerDeathEvent) {
        // Loop through every item in the drops. If it is enchanted with our blessing, remove it from the drops
        // and set it as an item to keep.

        for (drop in event.drops.stream().toList()) {
            // Does this item have blessing?

            if (drop.getEnchantmentLevel(enchantment) <= 0) continue

            // Remove from the drops and set as a keep item
            event.drops.remove(drop)
            event.itemsToKeep.add(drop)
        }
    }
}
