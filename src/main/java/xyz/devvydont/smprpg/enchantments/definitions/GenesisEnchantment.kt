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

class GenesisEnchantment(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Genesis")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases damage dealt by "),
            ComponentUtils.create(
                "+" + getPercentageIncrease(level) + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" against "),
            ComponentUtils.create("ender mobs.", NamedTextColor.RED)
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(238, 212, 255)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_WEAPON
    override val maxLevel: Int get()                           = 10
    override val weight: Int get()                             = EnchantmentRarity.COMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.MAINHAND
    override val skillRequirement: Int get()                   = 0

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentKeys.SMITE,
            EnchantmentKeys.BANE_OF_ARTHROPODS,
            EnchantmentService.BLESSED.typedKey,
            EnchantmentService.DAMNED.typedKey,
            EnchantmentService.VIGILANTE.typedKey,
            EnchantmentService.MUFFLE.typedKey
        )

    override val magicExperience: Int
        get() = level * 200 * (1 + (level * 3 / maxLevel))

    @EventHandler(priority = EventPriority.HIGH)
    fun onDamageEnder(event: CustomEntityDamageByEntityEvent) {
        // Skip non ender

        if (!isEnder(SMPRPG.getService(EntityService::class.java).getEntityInstance(event.damaged))) return

        // Skip entity if they aren't alive
        if (event.dealer !is LivingEntity) return
        val dealer = event.dealer
        if (dealer is Player) {
            val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(dealer)
            if (!isEnchantmentActive(dealer.equipment.itemInMainHand, leveledPlayer)) return
        }

        val level = EnchantmentUtil.getHoldingEnchantLevel(enchantment, EquipmentSlotGroup.HAND, dealer.equipment)
        if (level <= 0) return

        val multiplier: Double = 1.0 + (getPercentageIncrease(level) / 100.0)
        event.multiplyDamage(multiplier)
    }

    companion object {
        fun getPercentageIncrease(level: Int): Int {
            return level * 30
        }

        fun isEnder(entity: LeveledEntity<*>): Boolean {
            return entity.mobTypes.contains(MobType.ENDER)
        }
    }
}
