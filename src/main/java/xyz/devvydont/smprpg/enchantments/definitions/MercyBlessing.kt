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
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.ScrollColor
import xyz.devvydont.smprpg.services.ActionBarService
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class MercyBlessing(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Blessing of Mercy", NamedTextColor.YELLOW)
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Survive a "),
            ComponentUtils.create("fatal blow", NamedTextColor.DARK_PURPLE),
            ComponentUtils.create(" once every "),
            ComponentUtils.create(COOLDOWN.toString() + "s", NamedTextColor.GREEN)
        )
    override val enchantColor: TextColor get()   = NamedTextColor.YELLOW
    override val scrollColor: Color get()        = ScrollColor.BLESSING.color
    override val scrollBindingColor: Color get() = Color.fromRGB(255, 230, 29)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_CHEST_ARMOR
    override val maxLevel: Int get()                           = 1
    override val weight: Int get()                             = EnchantmentRarity.BLESSING.weight
    override val isBlessing: Boolean get()                     = true
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.CHEST
    override val skillRequirement: Int get()                   = 40

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentService.KEEPING_BLESSING.typedKey
        )

    /**
     * Listen for when damage is dealt. We aren't *supposed* to use monitor here, but this needs to happen
     * ABSOLUTELY LAST. If it's not, then there's a chance we risk this proc'ing when it's not supposed to.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onFatalDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player

        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        if (!isEnchantmentActive(player.equipment.chestplate, leveledPlayer)) return

        val chestplate: ItemStack? = player.equipment.chestplate
        if (chestplate == null || chestplate.type == Material.AIR) return

        if (!chestplate.containsEnchantment(enchantment)) return

        if (player.getCooldown(chestplate) > 0) return

        // Invalid damage type?
        val invalid = when (event.cause) {
            DamageCause.CUSTOM, DamageCause.POISON, DamageCause.VOID, DamageCause.SUICIDE, DamageCause.KILL -> true
            else -> false
        }

        if (invalid) return

        // If we have absorption we can't die yet
        if (player.absorptionAmount > 0) return

        // Is this going to kill us?
        if (event.damage < player.health) return

        event.setDamage(EntityDamageEvent.DamageModifier.BASE, player.health - 1)
        player.setCooldown(chestplate, COOLDOWN * 20)
        player.world.spawnParticle(Particle.TOTEM_OF_UNDYING, player.eyeLocation, 25)
        player.world.playSound(player.location, Sound.ITEM_TOTEM_USE, 1f, 1f)
        player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 20 * 30, 4, true))
        SMPRPG.getService(ActionBarService::class.java)
            .addActionBarComponent(
                player,
                ActionBarService.ActionBarSource.MISC,
                ComponentUtils.create("MERCY!", NamedTextColor.YELLOW),
                3
            )
    }

    companion object {
        const val COOLDOWN: Int = 180
    }
}
