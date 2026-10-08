package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.set.RegistryKeySet
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.entity.interfaces.IDamageTrackable
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import kotlin.math.max

class FirstStrikeEnchantment(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("First Strike")
    override val description: Component
        get() = ComponentUtils.merge(
            ComponentUtils.create("Increases damage by "),
            ComponentUtils.create(
                "+" + getFirstHitDamage(level) + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" when hitting an enemy for the first time")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(255, 153, 0)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_WEAPON
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.COMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.HAND
    override val skillRequirement: Int get()                   = 2

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentService.DOUBLE_TAP.typedKey
        )

    @EventHandler(ignoreCancelled = true)
    fun onDealDamage(event: CustomEntityDamageByEntityEvent) {
        if (event.dealer !is LivingEntity) return

        val dealer = event.dealer
        if (dealer is Player) {
            val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(dealer)
            if (!isEnchantmentActive(dealer.equipment.itemInMainHand, leveledPlayer)) return
        }

        if (dealer.equipment == null) return

        // Is this the first hit?
        if (SMPRPG.getService(EntityService::class.java).getEntityInstance(event.damaged) !is IDamageTrackable) return
        val trackable = SMPRPG.getService(EntityService::class.java).getEntityInstance(event.damaged) as IDamageTrackable

        val numHits: Int = trackable.damageTracker.getNumberOfHitsDealtByEntity(event.dealer)
        if (numHits > 0) return

        // Retrieve the higher first strike level of the two hands to determine which one to use
        val firstStrikeLevels: Int
        val mainHandFSLevels: Int = dealer.equipment!!.itemInMainHand.getEnchantmentLevel(enchantment)
        val offHandFSLevels: Int = dealer.equipment!!.itemInOffHand.getEnchantmentLevel(enchantment)
        firstStrikeLevels = max(mainHandFSLevels, offHandFSLevels)

        if (firstStrikeLevels <= 0) return

        val multiplier: Double = 1.0 + getFirstHitDamage(firstStrikeLevels) / 100.0
        event.multiplyDamage(multiplier)
    }

    companion object {
        fun getFirstHitDamage(level: Int): Int {
            return when (level) {
                0 -> 0
                1 -> 25
                2 -> 40
                3 -> 55
                4 -> 75
                5 -> 100
                else -> getFirstHitDamage(5) + 20 * level
            }
        }
    }
}
