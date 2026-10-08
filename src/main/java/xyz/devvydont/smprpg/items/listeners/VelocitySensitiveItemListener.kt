package xyz.devvydont.smprpg.items.listeners

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.inventory.EntityEquipment
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.items.base.SMPItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IMace
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import kotlin.math.abs
import kotlin.math.pow

class VelocitySensitiveItemListener : ToggleableListener() {

    /**
     * Maces have the special property of dealing increased damage based on how fast the person is falling.
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    private fun onMaceDamage(event: CustomEntityDamageByEntityEvent) {
        // Can the damaging entity hold equipment?

        if (event.dealer !is LivingEntity)
            return

        val equipment: EntityEquipment = event.dealer.equipment
            ?: return

        // Are they holding a mace?
        val item = equipment.itemInMainHand
        val blueprint: SMPItemBlueprint = ItemService.blueprint(item)
        if (blueprint !is IMace)
            return

        // Falling?
        if (event.dealer.isOnGround)
            return

        if (event.dealer.velocity.y >= 0)
            return

        // Multiply damage by their downwards velocity. Terminal velocity feels like -4ish, so let's add 1 and square it
        // and use that for the velocity efficiency multiplier
        val downwardsVelocity: Double = abs(event.dealer.velocity.y)
        val velocityMultiplier = (downwardsVelocity + 1).pow(2)
        val oldDmg = event.finalDamage.toInt()  // For debugging
        event.multiplyDamage(velocityMultiplier * blueprint.velocityMultiplier)
        val newDmg = event.finalDamage.toInt()  // For debugging
//        Bukkit.broadcast(Component.text("$oldDmg -> $newDmg"))
    }

}