package xyz.devvydont.smprpg.listeners.entity

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * Minecraft by default implements entity healing by food by adding health. Since entity health can get pretty high,
 * it would require an insane amount of food in order to trigger a full heal. This listener fixes that by changing
 * healing logic to be percentage of health instead.
 */
class TamedEntityFeedFix : ToggleableListener() {

    @EventHandler(ignoreCancelled = true)
    @Suppress("unused")
    fun onHealPet(event: EntityRegainHealthEvent) {

        // Only listen to the "EATING" reason. This is from pets eating food.
        if (event.regainReason != RegainReason.EATING)
            return

        // Only living entities are affected by this logic.
        if (event.getEntity() !is LivingEntity)
            return
        val living = event.getEntity() as LivingEntity

        // Use their regeneration stat to get a healing amount.
        val regen = instance.getAttribute(living, AttributeWrapper.REGENERATION)
        val halfHeart = SMPRPG.getService(EntityService::class.java).getEntityInstance(living).halfHeartValue
        var healingAmount: Double = halfHeart * FOOD_HEALING_MULTIPLIER
        if (regen != null)
            healingAmount *= (regen.getValue() / 100)
        event.amount = healingAmount
    }

    companion object {
        /**
         * How much food heals for scaling with the entity's half heart amount. Directly multiplied against
         * their half heart amount and scaled using regeneration attribute.
         */
        const val FOOD_HEALING_MULTIPLIER: Int = 2
    }
}
