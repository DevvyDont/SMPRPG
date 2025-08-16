package xyz.devvydont.smprpg.listeners.damage

import org.bukkit.entity.Shulker
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import kotlin.math.max

/**
 * We need a way for shulkers to take reduced damage while they aren't peeking.
 */
class ShulkerDefenseModeFix : ToggleableListener() {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onShulkerTakeDamageWhileClosed(event: CustomEntityDamageByEntityEvent) {
        if (event.damaged !is Shulker)
            return
        val shulker = event.damaged as Shulker
        val multiplier = max(shulker.peek.toDouble(), .1)
        event.multiplyDamage(multiplier)
    }
}
