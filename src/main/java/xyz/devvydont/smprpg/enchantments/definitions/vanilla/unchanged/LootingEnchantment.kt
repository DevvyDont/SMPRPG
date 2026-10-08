package xyz.devvydont.smprpg.enchantments.definitions.vanilla.unchanged

import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment
import xyz.devvydont.smprpg.events.CustomItemQuantityRollDropEvent
import xyz.devvydont.smprpg.items.blueprints.economy.CustomItemCoin
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import kotlin.math.roundToInt

class LootingEnchantment(key: TypedKey<Enchantment>) : VanillaEnchantment(key), Listener {
    override val displayName: Component get() = ComponentUtils.create("Looting")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Provides a drop bonus of "),
            ComponentUtils.create(
                "+~" + getLootingPercentEstimation(level) + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" from mobs")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(12, 153, 17)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_WEAPON
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.UNCOMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.MAINHAND
    override val skillRequirement: Int get()                   = 0

    @EventHandler
    fun onItemQuantityRoll(event: CustomItemQuantityRollDropEvent) {
        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.player)
        if (!isEnchantmentActive(event.player.equipment.itemInMainHand, leveledPlayer)) return

        val looting = EnchantmentUtil.getEnchantLevel(Enchantment.LOOTING, event.tool)
        if (looting < 1) return

        // Don't proc looting on coins...
        if (blueprint(event.getDrop()) is CustomItemCoin) return

        val extraDrops = (Math.random() * looting + 1).roundToInt()
        event.amount = event.amount + extraDrops
    }

    companion object {
        /**
         * By no means an actual value, simply just an estimation
         *
         * @param level
         * @return
         */
        fun getLootingPercentEstimation(level: Int): Int { return level * 100 }
    }
}
