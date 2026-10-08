package xyz.devvydont.smprpg.items.blueprints.equipment

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.items.AbilityUtil
import xyz.devvydont.smprpg.util.items.AbilityUtil.getCooldownComponent
import kotlin.math.max
import kotlin.math.min

class GrapplingHook(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IHeaderDescribable, Listener, IBreakableEquipment {

    override val itemClassification: ItemClassification get() = ItemClassification.EQUIPMENT

    override fun getHeader(itemStack: ItemStack): MutableList<Component> {
        return mutableListOf(
            AbilityUtil.getAbilityComponent("Grapple"),
            ComponentUtils.create("Use to propel yourself"),
            ComponentUtils.create("when reeling in!"),
            getCooldownComponent(COOLDOWN.toString() + "s")
        )
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.HAND
    }

    override fun getAttributeModifiers(item: ItemStack): MutableCollection<AttributeEntry> {
        return mutableListOf(
            MultiplicativeAttributeEntry(AttributeWrapper.FALL_DAMAGE_MULTIPLIER, -.9),
            AdditiveAttributeEntry(AttributeWrapper.SAFE_FALL, 15.0)
        )
    }

    override fun getPowerRating(): Int {
        return 1
    }

    override fun getMaxDurability(): Int {
        return 1024
    }

    enum class GrapplingState {
        CAST,
        REEL
    }

    private fun use(event: PlayerFishEvent, state: GrapplingState) {
        // Handle the case we are sending the bobber out

        if (state == GrapplingState.CAST) {
            return
        }

        // Handle the case where we are reeling
        val dir = event.hook.location.toVector().subtract(event.player.location.toVector())

        // Dampen the velocity
        dir.multiply(VELOCITY_DAMPENING_FACTOR)

        // Make sure we didn't hit a limit
        dir.setX(min(max(-MAX_HORIZONTAL_VELOCITY.toDouble(), dir.getX()), MAX_HORIZONTAL_VELOCITY.toDouble()))
        dir.setY(min(max(MIN_VERTICAL_VELOCITY.toDouble(), dir.getY()), MAX_VERTICAL_VELOCITY.toDouble()))
        dir.setZ(min(max(-MAX_HORIZONTAL_VELOCITY.toDouble(), dir.getZ()), MAX_HORIZONTAL_VELOCITY.toDouble()))

        event.getPlayer().velocity = dir
        event.getPlayer().fallDistance = -30f
        if (event.hand != null) {
            val item = event.player.inventory.getItem(event.hand!!)
            item.damage(1, event.player)
        }
        event.getPlayer().setCooldown(Material.FISHING_ROD, COOLDOWN * 20)
    }

    @EventHandler
    fun onUseGrapplingHook(event: PlayerFishEvent) {
        if (event.hand == null) return

        val fishingRod = event.getPlayer().inventory.getItem(event.hand!!)
        if (fishingRod.type == Material.AIR) return

        if (!isItemOfType(fishingRod)) return

        // If the player is casting the hook like normal, allow the event to happen
        if (event.state == PlayerFishEvent.State.FISHING) {
            use(event, GrapplingState.CAST)
            return
        }

        // If the player is reeling in, allow the event to happen
        if (event.state == PlayerFishEvent.State.REEL_IN || event.state == PlayerFishEvent.State.IN_GROUND || event.state == PlayerFishEvent.State.CAUGHT_ENTITY) {
            use(event, GrapplingState.REEL)
            return
        }

        // Every other state is not allowed
        event.isCancelled = true
    }

    companion object {
        const val COOLDOWN: Int = 2

        const val VELOCITY_DAMPENING_FACTOR: Float = .3f

        const val MAX_HORIZONTAL_VELOCITY: Float = 4f

        const val MIN_VERTICAL_VELOCITY: Float = 1f
        const val MAX_VERTICAL_VELOCITY: Float = 2f
    }
}
