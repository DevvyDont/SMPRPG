package xyz.devvydont.smprpg.listeners.entity

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityTameEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.time.TickTime

/**
 * When entities are tamed, their attributes seem to break. We need to figure out a way to prevent this.
 * I think a good idea for now, is to make the entity scale to the owner's level.
 */
class EntityTamingAttributeFix : ToggleableListener() {

    @EventHandler
    @Suppress("unused")
    private fun onTame(e: EntityTameEvent) {
        val owner = e.owner
        if (owner !is Player) return

        val entityWrapper =
            SMPRPG.getService(EntityService::class.java).getEntityInstance(e.getEntity())

        // Do it on the next tick so vanilla doesn't override our behavior.
        Bukkit.getScheduler()
            .runTaskLater(plugin, Runnable { entityWrapper.copyLevel(owner) }, TickTime.INSTANTANEOUSLY)
    }
}
