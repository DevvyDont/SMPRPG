package xyz.devvydont.smprpg.listeners.entity

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent
import org.bukkit.Input
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInputEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import java.util.UUID

/**
 * Listens for player input, and registers it to a map of inputs for use
 * on other classes.
 */
class PlayerInputListener: ToggleableListener() {
    val playerInputMap: MutableMap<UUID, Input> = mutableMapOf()

    @EventHandler
    private fun onPlayerInput(event: PlayerInputEvent) {
        playerInputMap.put(event.player.uniqueId, event.input)
    }

    @EventHandler
    private fun onPlayerLogout(event: PlayerConnectionCloseEvent) {
        playerInputMap.remove(event.playerUniqueId)
    }

    companion object {
        fun getPlayerInput(player: Player): Input? {
            val listener = SMPRPG.Companion.getListener(PlayerInputListener::class.java)
            return listener?.playerInputMap!![player.uniqueId]
        }

        fun getPlayerInput(uuid: UUID): Input? {
            val listener = SMPRPG.Companion.getListener(PlayerInputListener::class.java)
            return listener?.playerInputMap!![uuid]
        }
    }
}