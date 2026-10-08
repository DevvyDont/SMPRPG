package xyz.devvydont.smprpg.enchantments.definitions.vanilla.overrides

import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
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
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.fishing.SeaCreature
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class ImpalingEnchantment(key: TypedKey<Enchantment>) : VanillaEnchantment(key), Listener {
    override val displayName: Component get() = ComponentUtils.create("Impaling")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases damage dealt by "),
            ComponentUtils.create(
                "+" + getDamagePercentageMultiplier(level) + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" against "),
            ComponentUtils.create("wet enemies", SeaCreature.NAME_COLOR),
            ComponentUtils.create(" and "),
            ComponentUtils.create("sea creatures", SeaCreature.NAME_COLOR)
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(89, 128, 255)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_WEAPON
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.COMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.MAINHAND
    override val skillRequirement: Int get()                   = 17

    @EventHandler
    fun onWaterDamage(event: CustomEntityDamageByEntityEvent) {
        if (event.dealer !is LivingEntity) return
        val dealer = event.dealer

        if (dealer is Player) {
            val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(dealer)
            if (!isEnchantmentActive(dealer.equipment.itemInMainHand, leveledPlayer)) return
        }

        if (event.damaged !is LivingEntity) return
        val damaged = event.damaged

        // Check if the damaged entity is either a sea creature or wet.
        var valid = event.damaged.isInWater || event.damaged.isInRain
        if (SMPRPG.getService(EntityService::class.java).getEntityInstance(damaged).mobTypes.contains(MobType.SEA_CREATURE)) valid = true

        if (!valid) return

        val impalingLevel = EnchantmentUtil.getHoldingEnchantLevel(enchantment, EquipmentSlotGroup.MAINHAND, dealer.equipment)
        if (impalingLevel <= 0) return

        val multiplier: Double = 1 + getDamagePercentageMultiplier(impalingLevel) / 100.0
        event.multiplyDamage(multiplier)
    }

    companion object {
        fun getDamagePercentageMultiplier(level: Int): Int { return level * 30; }
    }
}
