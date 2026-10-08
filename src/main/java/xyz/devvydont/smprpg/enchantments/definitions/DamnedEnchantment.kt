package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.EnchantmentKeys
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
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.base.LeveledEntity
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class DamnedEnchantment(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Damned")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases damage dealt by "),
            ComponentUtils.create(
                "+" + getPercentageIncrease(level) + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" against "),
            ComponentUtils.create(MobType.HOLY.symbol, MobType.HOLY.symbolColor),
            ComponentUtils.create(" Holy", NamedTextColor.RED),
            ComponentUtils.create(" mobs.")
        )

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_WEAPON
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.COMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.HAND
    override val skillRequirement: Int get()                   = 19
    override val scrollBindingColor: Color get() = Color.fromRGB(255, 215, 0)

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        /**
         * A set of enchantments that this enchantment conflicts with.
         * If there are none, this enchantment has no conflicts
         *
         * @return
         */
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentKeys.SMITE,
            EnchantmentKeys.BANE_OF_ARTHROPODS,
            EnchantmentService.BLESSED.typedKey,
            EnchantmentService.GENESIS.typedKey,
            EnchantmentService.VIGILANTE.typedKey,
            EnchantmentService.MUFFLE.typedKey
        )

    @EventHandler(priority = EventPriority.HIGH)
    fun onDamageNetherMob(event: CustomEntityDamageByEntityEvent) {
        // Skip non undead

        if (!isNether(
                SMPRPG.getService(EntityService::class.java).getEntityInstance(event.damaged)
            )
        ) return

        // Skip entity if they aren't alive
        if (event.dealer !is LivingEntity) return
        val dealer = event.dealer

        if (event.dealer is Player) {
            val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(dealer)
            if (!isEnchantmentActive(dealer.equipment.itemInMainHand, leveledPlayer)) return
        }

        val level = EnchantmentUtil.getHoldingEnchantLevel(enchantment, EquipmentSlotGroup.HAND, dealer.equipment)
        if (level <= 0) return

        val multiplier: Double = 1.0 + (getPercentageIncrease(level) / 100.0)
        event.multiplyDamage(multiplier)
    }

    companion object {
        fun isNether(entity: LeveledEntity<*>): Boolean {
            return entity.mobTypes.contains(MobType.NETHER)
        }

        fun getPercentageIncrease(level: Int): Int {
            return level * 30
        }
    }
}
