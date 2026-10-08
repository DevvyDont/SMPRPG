package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import kotlin.math.max

class SnipeEnchantment(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Snipe")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases damage by "),
            ComponentUtils.create(
                "+" + getDamageIncreasePercentPerBlock(level) + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" for every block the arrow travels")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(56, 56, 56)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_BOW
    override val maxLevel: Int get()                           = 3
    override val weight: Int get()                             = EnchantmentRarity.RARE.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.HAND
    override val skillRequirement: Int get()                   = 36

    @EventHandler
    fun onArrowHit(event: CustomEntityDamageByEntityEvent) {
        if (event.vanillaCause != EntityDamageEvent.DamageCause.PROJECTILE) return

        if (event.projectile !is AbstractArrow) return
        val projectile = event.projectile

        if (event.dealer !is LivingEntity) return

        val dealer = event.dealer
        if (dealer.equipment == null) return

        if (dealer is Player) {
            val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(dealer)
            if (!isEnchantmentActive(dealer.equipment.itemInMainHand, leveledPlayer)) return
        }

        // Retrieve the higher snipe level of the two hands to determine which one to use
        val snipeLevels: Int
        val mainHandSnipeLevels: Int = dealer.equipment!!.itemInMainHand.getEnchantmentLevel(enchantment)
        val offHandSnipeLevels: Int = dealer.equipment!!.itemInOffHand.getEnchantmentLevel(enchantment)
        snipeLevels = max(mainHandSnipeLevels, offHandSnipeLevels)
        if (snipeLevels <= 0) return

        val multiplier: Double = 1.0 + getDamageIncreasePercentPerBlock(snipeLevels) / 100.0 * dealer.location.distance(projectile.location)
        event.multiplyDamage(multiplier)
    }

    companion object {
        fun getDamageIncreasePercentPerBlock(level: Int): Int {
            return when (level) {
                0 -> 0
                1 -> 1
                2 -> 3
                3 -> 5
                else -> getDamageIncreasePercentPerBlock(3) + 2 * level
            }
        }
    }
}
