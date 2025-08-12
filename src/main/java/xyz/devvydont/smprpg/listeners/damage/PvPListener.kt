package xyz.devvydont.smprpg.listeners.damage

import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/*
 * Listener in charge of managing PVP events. Since this is mostly a PVE plugin, we should restrict PVP in certain
 * scenarios.
 */
class PvPListener : ToggleableListener() {
    /**
     * Checks the structures in an entity's chunk and checks if any of them are overlapping with the entity.
     *
     * @param entity An entity to check for.
     * @return true if the entity is currently in a structure, false if they are not.
     */
    private fun entityIsInStructure(entity: Entity): Boolean {
        for (structure in entity.chunk.structures)
            if (structure.boundingBox.overlaps(entity.boundingBox))
                return true
        return false
    }

    /*
     * Prevent players from dealing damage to each other if they are both inside of a structure.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    fun onPlayerPVPWithinStructure(event: CustomEntityDamageByEntityEvent) {
        // Are both of these entities players and contained in a structure? Cancel the event if so
        if (event.dealer is Player && event.damaged is Player) {
            if (entityIsInStructure(event.dealer) && entityIsInStructure(event.damaged)) event.isCancelled =
                true
        }
    }

    /**
     * Make all PVP damage 20% as effective. Not sure how I feel about this fix, but we will see how it goes
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("unused")
    fun onPlayerPVP(event: CustomEntityDamageByEntityEvent) {
        if (event.dealer is Player && event.damaged is Player)
            event.multiplyDamage(.2)
    }
}
