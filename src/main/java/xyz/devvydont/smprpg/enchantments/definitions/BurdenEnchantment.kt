package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class BurdenEnchantment(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Burden")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Recover "),
            ComponentUtils.create(
                "+" + getManastealPercent(level) + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" of max mana when hurting an enemy")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(92, 117, 148)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_WEAPON
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.UNCOMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.MAINHAND
    override val skillRequirement: Int get()                   = 15

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerBurdenedEntity(event: CustomEntityDamageByEntityEvent) {
        if (event.dealer !is Player) return

        val dealer = event.dealer
        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(dealer)
        if (!isEnchantmentActive(dealer.equipment.itemInMainHand, leveledPlayer)) return


        val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.dealer)
        val maxMana = player.getMaxMana()

        // Is this player holding the enchantment?
        val leechLevels = EnchantmentUtil.getHoldingEnchantLevel(this, EquipmentSlotGroup.HAND, player.player.equipment)
        if (leechLevels <= 0) return

        // Heal for a percentage of their max HP
        player.gainMana((getManastealPercent(leechLevels) / 100.0 * maxMana).toInt())
        player.player.world.playSound(event.damaged, Sound.ENTITY_EVOKER_PREPARE_SUMMON, .3f, 2.0f)
    }

    companion object {
        fun getManastealPercent(level: Int): Double { return 0.5 * level }
    }
}
