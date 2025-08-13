package xyz.devvydont.smprpg.events.damage

import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * A simple event that is broadcasted when damage is dealt to an entity's Absorption heart pool.
 * This event represents the "post" state of the damage being dealt, so the damage event has already happened.
 * This is mostly meant for monitoring.
 */
class AbsorptionDamageDealtEvent(
    /**
     * Get the entity that took damage in this event.
     * @return A [LivingEntity] instance.
     */
    val victim: LivingEntity,
    /**
     * Get the damage that the entity withstood from the event.
     * @return A number representing damage.
     */
    val damage: Double,
    /**
     * Check if the entity has completely exhausted their absorption as a result of this damage.
     * The term "cracked" essentially means they ran out of "shields" or absorption in Minecraft terms.
     * @return True if the entity no longer has absorption, false if they still have some left over.
     */
    val isCracked: Boolean
) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}
