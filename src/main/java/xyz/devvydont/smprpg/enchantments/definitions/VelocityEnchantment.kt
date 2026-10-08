package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class VelocityEnchantment(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Velocity")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases arrow speed by "),
            ComponentUtils.create(
                "+" + getSpeedIncrease(level) + "%",
                NamedTextColor.GREEN
            )
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(145, 133, 76)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_BOW
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.UNCOMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.HAND
    override val skillRequirement: Int get()                   = 42

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onShootBow(event: EntityShootBowEvent) {
        if (event.bow == null) return

        val dealer = event.entity
        if (dealer is Player) {
            val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(dealer)
            if (!isEnchantmentActive(event.bow!!, leveledPlayer)) return
        }

        val velocity = event.bow!!.getEnchantmentLevel(enchantment)
        if (velocity <= 0) return

        val speedMult: Double = getSpeedIncrease(velocity) / 100.0 * velocity + 1.0
        event.projectile.velocity = event.projectile.velocity.multiply(speedMult)
    }

    companion object {
        fun getSpeedIncrease(level: Int): Int {
            return level * 4
        }
    }
}
