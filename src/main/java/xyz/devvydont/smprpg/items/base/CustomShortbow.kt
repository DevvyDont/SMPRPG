package xyz.devvydont.smprpg.items.base

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Arrow
import org.bukkit.entity.SpectralArrow
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.unchanged.FireAspectEnchantment
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.services.ActionBarService
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.items.AbilityUtil

/*
 * A variant of a bow that can instantly shoot when used.
 */
abstract class CustomShortbow(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IHeaderDescribable, Listener {

    override val itemClassification: ItemClassification get() = ItemClassification.SHORTBOW

    override fun getHeader(itemStack: ItemStack?): MutableList<Component?> {
        return mutableListOf(
            AbilityUtil.getAbilityComponent("Shortbow (Left/Right Click)"),
            ComponentUtils.create("Instantly shoots arrows!")
        )
    }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.HAND }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    fun onPlayerInteractedWithShortbow(event: PlayerInteractEvent) {
        // Is this a shortbow?

        val item = event.item
        if (item == null) return
        if (!isItemOfType(item)) return

        // Denies the vanilla usage of the bow. We are overriding the behavior.
        event.setUseItemInHand(Event.Result.DENY)

        // Are we clicking a block?
        if (event.clickedBlock != null) return

        // Event should be cancelled no matter what if we aren't clicking a block.
        event.setCancelled(true)

        if (event.hand == null) return

        // Are we on attack cooldown?
        if (event.player.getCooldown(customItemType.DisplayMaterial) > 0) return

        // Do we meet skill requirements?
        if (!ItemService.meetsRequirements(item, event.player)) return

        // Do we have an arrow to use as a consumable?
        var consumable: ItemStack? = null
        var arrowClass: Class<out AbstractArrow>? = null
        val shouldConsume = true
        for (inventoryItem in event.getPlayer().inventory.contents) {
            if (inventoryItem == null) continue

            if (inventoryItem.type == Material.ARROW) {
                consumable = inventoryItem
                arrowClass = Arrow::class.java
                break
            }

            if (inventoryItem.type == Material.SPECTRAL_ARROW) {
                consumable = inventoryItem
                arrowClass = SpectralArrow::class.java
                break
            }
        }

        // If the player is in creative mode, then we just use some fake arrow...
        if (event.player.gameMode == GameMode.CREATIVE) if (consumable != null) consumable =
            consumable.clone()
        else {
            consumable = generate(Material.ARROW)
            arrowClass = Arrow::class.java
        }

        if (consumable == null) {
            event.player
                .playSound(event.player.location, Sound.ENTITY_VILLAGER_WORK_FLETCHER, 1.0f, 1.5f)
            SMPRPG.getService(ActionBarService::class.java)
                .addActionBarComponent(
                    event.player,
                    ActionBarService.ActionBarSource.MISC,
                    ComponentUtils.create("NO ARROWS!", NamedTextColor.RED),
                    2
                )
            return
        }

        // Now launch the arrow.
        event.setCancelled(true)
        val arrow: AbstractArrow = event.getPlayer().launchProjectile(
            arrowClass!!,
            event.player.location.direction.normalize()
                .multiply(EntityDamageCalculatorService.MAX_ARROW_DAMAGE_VELOCITY / 20)
        )
        arrow.itemStack = consumable.asOne()

        if (item.containsEnchantment(Enchantment.INFINITY)) arrow.pickupStatus = AbstractArrow.PickupStatus.CREATIVE_ONLY

        if (item.containsEnchantment(Enchantment.FIRE_ASPECT)) arrow.fireTicks = FireAspectEnchantment.getSecondsOfBurn(item.getEnchantmentLevel(Enchantment.FIRE_ASPECT)) * 20

        // We should be good to shoot an arrow. Manually call the shoot bow event so plugins can modify it.
        // This will also cause the damage listener to set the damage as intended on the arrow so we don't have to that
        val bowEvent = EntityShootBowEvent(event.getPlayer(), item, consumable, arrow, event.hand!!, 0.5f, shouldConsume)
        bowEvent.callEvent()

        // If something cancelled the event, remove the arrow and cancel the interaction
        if (bowEvent.isCancelled) {
            event.setCancelled(true)
            arrow.remove()
            return
        }

        item.damage(1, event.getPlayer())

        // Post-processing effects that we call only when the event was successful.
        val player = event.player
        player.world.playSound(player.eyeLocation, Sound.ENTITY_ARROW_SHOOT, 1f, 1.5f)
        player.resetCooldown()
        val cooldown = (20 / player.getAttribute(Attribute.ATTACK_SPEED)!!.value).toInt()
        player.setCooldown(customItemType.DisplayMaterial, cooldown)

        // If the arrow shot was a normal arrow and the bow has infinity, consumable logic is handled by
        // the infinity enchantment class.
        if (consumable.type == Material.ARROW && item.containsEnchantment(Enchantment.INFINITY)) return


        // If the event flags this event as an instance where we should consume the item, we should do it
        if (bowEvent.shouldConsumeItem()) consumable.amount = consumable.amount - 1
    }
}
