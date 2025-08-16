package xyz.devvydont.smprpg.gui.base

import org.bukkit.Sound
import org.bukkit.entity.Player

/**
 * A menu sound effect.
 */
class MenuSound internal constructor(
    private val player: Player,
    private val sound: Sound,
    private val volume: Float,
    private val pitch: Float
) {
    /**
     * Plays the sound effect on the players' client.
     */
    fun play() {
        this.player.playSound(this.player.location, this.sound, this.volume, this.pitch)
    }
}
