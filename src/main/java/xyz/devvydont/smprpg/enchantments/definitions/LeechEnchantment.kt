package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class LeechEnchantment(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Leech")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Heal "),
            ComponentUtils.create(
                "+" + getLifestealPercent(level) + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" of max health when hurting an enemy")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(74, 0, 14)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_WEAPON
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.UNCOMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.MAINHAND
    override val skillRequirement: Int get()                   = 15

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerLeechedEntity(event: CustomEntityDamageByEntityEvent) {
        if (event.dealer !is Player) return

        val dealer = event.dealer
        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(dealer)
        if (!isEnchantmentActive(dealer.equipment.itemInMainHand, leveledPlayer)) return

        val maxHP: AttributeInstance? = dealer.getAttribute(Attribute.MAX_HEALTH)
        if (maxHP == null) return

        // Is this player holding the enchantment?
        val leechLevels = EnchantmentUtil.getHoldingEnchantLevel(this, EquipmentSlotGroup.HAND, dealer.equipment)
        if (leechLevels <= 0) return

        // Heal for a percentage of their max HP
        dealer.heal(
            getLifestealPercent(leechLevels) / 100.0 * maxHP.value,
            EntityRegainHealthEvent.RegainReason.CUSTOM
        )
        dealer.world.playSound(event.damaged, Sound.BLOCK_LAVA_POP, .3f, 1.75f)
    }

    companion object {
        fun getLifestealPercent(level: Int): Double {
            return when (level) {
                1 -> 0.2
                2 -> 0.35
                3 -> 0.65
                4 -> 0.95
                5 -> 1.25
                6 -> 1.60
                7 -> 2.0
                else -> level * .25 + getLifestealPercent(5)
            }
        }
    }
}
