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
import kotlin.math.max

class ProsecuteEnchantment(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Prosecute")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases damage by "),
            ComponentUtils.create(
                "+" + getPercentDamageIncreaseForHighEnemy(level) + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" for enemies over "),
            ComponentUtils.create(
                "$HEALTH_THRESHOLD%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" of their maximum health")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(157, 189, 89)

    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_WEAPON
    override val maxLevel: Int get() = 5
    override val weight: Int get() = EnchantmentRarity.UNCOMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.HAND
    override val skillRequirement: Int get() = 10

    @EventHandler
    fun onDealDamageWithExecute(event: CustomEntityDamageByEntityEvent) {
        if (event.dealer !is LivingEntity) return

        val dealer = event.dealer
        if(dealer.equipment == null) return

        if (dealer is Player) {
            val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(dealer)
            if (!isEnchantmentActive(dealer.equipment.itemInMainHand, leveledPlayer)) return
        }

        if (event.damaged !is LivingEntity) return

        val damaged = event.damaged
        if (damaged.getAttribute(Attribute.MAX_HEALTH) == null) return

        // Are they over the threshold?
        val hp: Double = damaged.health
        val maxHP: Double = damaged.getAttribute(Attribute.MAX_HEALTH)!!.value
        if (hp / maxHP * 100 < HEALTH_THRESHOLD) return

        // Retrieve the higher first strike level of the two hands to determine which one to use
        val firstStrikeLevels: Int
        val mainHandFSLevels: Int = dealer.equipment!!.itemInMainHand.getEnchantmentLevel(enchantment)
        val offHandFSLevels: Int = dealer.equipment!!.itemInOffHand.getEnchantmentLevel(enchantment)
        firstStrikeLevels = max(mainHandFSLevels, offHandFSLevels)

        if (firstStrikeLevels <= 0) return

        val multiplier: Double = 1.0 + getPercentDamageIncreaseForHighEnemy(firstStrikeLevels) / 100.0
        event.multiplyDamage(multiplier)
    }

    companion object {
        fun getPercentDamageIncreaseForHighEnemy(level: Int): Int { return level * 15 }
        var HEALTH_THRESHOLD: Int = 50
    }
}
