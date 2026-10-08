package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class CalamityEnchantment(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Calamity")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases damage by "),
            ComponentUtils.create(
                "+" + getPercentDamageIncreaseForLowPlayer(level) + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" when you are below "),
            ComponentUtils.create(
                getPercentDamageIncreaseForLowPlayer(level).toString() + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" of your maximum health")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(163, 0, 0)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_CHEST_ARMOR
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.UNCOMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.CHEST
    override val skillRequirement: Int get()                   = 46

    @EventHandler
    fun onDealDamageWithCalamity(event: CustomEntityDamageByEntityEvent) {
        if (event.dealer !is LivingEntity) return

        val dealer = event.dealer

        if (dealer is Player) {
            val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(dealer)
            if (!isEnchantmentActive(dealer.equipment.chestplate, leveledPlayer)) return
        }


        if (dealer.equipment == null) return

        // Are they over the threshold?
        val hp: Double = dealer.health
        val maxHP: Double = dealer.getAttribute(Attribute.MAX_HEALTH)!!.value
        if (hp / maxHP * 100 > HEALTH_THRESHOLD) return

        // Retrieve the higher first strike level of the two hands to determine which one to use
        if (dealer.equipment!!.chestplate == null) return

        val calamityLevels: Int = dealer.equipment!!.chestplate.getEnchantmentLevel(enchantment)
        if (calamityLevels <= 0) return

        val multiplier: Double = 1.0 + getPercentDamageIncreaseForLowPlayer(calamityLevels) / 100.0
        event.multiplyDamage(multiplier)
    }

    companion object {
        fun getPercentDamageIncreaseForLowPlayer(level: Int): Int { return level * 10 }
        var HEALTH_THRESHOLD: Int = 50
    }
}
