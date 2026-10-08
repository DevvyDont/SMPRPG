package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class SyphonEnchantment(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Syphon")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Heal "),
            ComponentUtils.create(
                "+" + getLifestealPercent(level) + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" of max health when killing an enemy")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(79, 27, 19)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_WEAPON
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.UNCOMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.MAINHAND
    override val skillRequirement: Int get()                   = 40

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerLeechedEntity(event: EntityDeathEvent) {
        // If nobody killed this entity don't do anything

        if (event.entity.killer == null) return

        val killer = event.entity.killer

        if (killer is Player) {
            val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(killer)
            if (!isEnchantmentActive(killer.equipment.itemInMainHand, leveledPlayer)) return
        }

        val maxHP = killer!!.getAttribute(Attribute.MAX_HEALTH)
        if (maxHP == null) return

        // Is this player holding the enchantment?
        val leechLevels = EnchantmentUtil.getHoldingEnchantLevel(this, EquipmentSlotGroup.HAND, killer.equipment)
        if (leechLevels <= 0) return

        // Heal for a percentage of their max HP
        killer.heal(
            getLifestealPercent(leechLevels) / 100.0 * maxHP.value,
            EntityRegainHealthEvent.RegainReason.CUSTOM
        )
        killer.world.playSound(event.entity, Sound.ENTITY_BAT_DEATH, .07f, .75f)
    }

    companion object {
        fun getLifestealPercent(level: Int): Int {
            return when (level) {
                1 -> 2
                2 -> 5
                3 -> 10
                4 -> 16
                5 -> 25
                else -> level * 10 + getLifestealPercent(5)
            }
        }
    }
}
