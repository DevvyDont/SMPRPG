package xyz.devvydont.smprpg.enchantments.definitions.vanilla.unchanged

import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class InfinityEnchantment(key: TypedKey<Enchantment>) : VanillaEnchantment(key), Listener {
    override val displayName: Component get() = ComponentUtils.create("Infinity")
    override val description: Component
        get() = ComponentUtils.merge(
            ComponentUtils.create("Provides a "),
            ComponentUtils.create(
                getNonconsumeChance(level).toString() + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" chance to not consume arrows")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(116, 161, 27)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_BOW
    override val maxLevel: Int get()                           = 10
    override val weight: Int get()                             = EnchantmentRarity.RARE.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.HAND
    override val skillRequirement: Int get()                   = 25

    /*
    * We need to make vanilla infinity not work and function as a normal bow so that the next even can work for both
    * instances where a bow does and doesn't have infinity.
    */
    @EventHandler(priority = EventPriority.LOWEST)
    fun onShotBowWithInfinity(event: EntityShootBowEvent) {
        // Only players can take advantage of infinity.

        if (event.entity !is Player) return
        val player = event.entity as Player

        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        if (!isEnchantmentActive(player.equipment.itemInMainHand, leveledPlayer)) return

        val infinityLevel = EnchantmentUtil.getHoldingEnchantLevel(
            enchantment,
            event.hand.group,
            event.entity.equipment
        )

        // If we don't have infinity, then we use vanilla's logic
        if (infinityLevel <= 0) return

        // The arrow was not an arrow affected by infinity. Use vanilla's logic.
        if (event.consumable == null) return

        if (event.consumable!!.type != Material.ARROW) return

        // RNG for saving arrow. If this check evaluates to true, we use vanilla's logic for saving an arrow with infinity.
        if (getNonconsumeChance(infinityLevel) / 100.0 > Math.random()) return

        if (event.bow == null) return

        if (!event.bow!!.containsEnchantment(Enchantment.INFINITY)) return

        // Not sure when this should happen, but it is true even when we have infinity.
        if (!event.shouldConsumeItem() || event.consumable == null) return

        val consumableBlueprint = SMPRPG.getService(ItemService::class.java)
            .getBlueprint(event.consumable!!)

        // We shot a bow with infinity. Take away the consumable from the inventory.
        for (item in player.inventory.contents) {
            // No item in this inventory slot? don't care

            if (item == null) return

            // Does the consumable blueprint have a match with this item?
            if (!consumableBlueprint.isItemOfType(item)) continue

            // We found an item match, decrease the amount of the item at this inventory slot by one
            item.amount = (item.amount - 1)

            // If the projectile shot was an arrow, modify some attributes on it.
            // We need to allow the arrow to be picked up again, and override the itemStack to be picked up from
            // the arrow object so that it stacks nicely back into our inventory.
            if (event.projectile is Arrow) {
                val arrow = event.projectile as Arrow
                arrow.pickupStatus = AbstractArrow.PickupStatus.ALLOWED
                val arrowItemStack: ItemStack = item.clone()
                arrowItemStack.amount = 1
                arrow.itemStack = arrowItemStack
            }
            break
        }
    }

    companion object {
        fun getNonconsumeChance(level: Int): Int { return (level + 1) * 9 }
    }
}
