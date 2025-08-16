package xyz.devvydont.smprpg.listeners.entity

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * Makes health regeneration work correctly. In normal minecraft, HP regen is usually always 1 hp. We need regeneration
 * to scale based on how much health someone has. As a bonus, we also utilize a new "regeneration" attribute to
 * determine its effectiveness.
 */
class HealthRegenerationListener : ToggleableListener() {
    fun isNaturalRegeneration(regainReason: RegainReason): Boolean {
        return when (regainReason) {
            RegainReason.REGEN, RegainReason.SATIATED, RegainReason.MAGIC_REGEN, RegainReason.ENDER_CRYSTAL -> true
            else -> false
        }
    }

    @EventHandler
    @Suppress("unused")
    private fun onNaturalRegeneration(event: EntityRegainHealthEvent) {

        if (!isNaturalRegeneration(event.regainReason))
            return

        if (event.getEntity() !is LivingEntity)
            return

        val entity = SMPRPG.getService(EntityService::class.java).getEntityInstance(event.getEntity())
        event.amount = entity.getRegenerationAmount(event.regainReason)
    }
}
