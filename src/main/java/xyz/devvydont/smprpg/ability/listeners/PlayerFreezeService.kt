package xyz.devvydont.smprpg.ability.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.services.IService
import java.util.UUID

class PlayerFreezeService : IService, Listener {

    private var orientationFrozen: MutableSet<UUID> = HashSet()
    private var movementFrozen: MutableSet<UUID> = HashSet()

    fun addOrientationWatch(uuid: UUID) {
        orientationFrozen.add(uuid)
    }

    fun removeOrientationWatch(uuid: UUID) {
        orientationFrozen.remove(uuid)
    }

    fun addMovementWatch(uuid: UUID) {
        movementFrozen.add(uuid)
    }

    fun removeMovementWatch(uuid: UUID) {
        movementFrozen.remove(uuid)
    }

    @EventHandler
    private fun onPlayerMove(event: PlayerMoveEvent) {
        if (event.player.uniqueId in orientationFrozen && event.hasChangedOrientation()) {
            event.to.pitch = event.from.pitch
            event.to.yaw = event.from.yaw
        }

        if (event.player.uniqueId in movementFrozen && event.hasChangedPosition()) {
            event.to.x = event.from.x
            event.to.y = event.from.y
            event.to.z = event.from.z
        }
    }

    override fun setup() {
        val plugin = plugin
        plugin.logger.info("Setting up Player Freeze service")
    }

    override fun cleanup() {
        plugin.logger.info("Cleaning up PlayerFreezeService")
    }
}