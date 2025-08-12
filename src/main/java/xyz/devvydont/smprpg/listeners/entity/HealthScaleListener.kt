package xyz.devvydont.smprpg.listeners.entity

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import org.bukkit.event.EventHandler
import org.bukkit.scheduler.BukkitRunnable
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.events.skills.SkillLevelUpEvent
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.time.TickTime

/**
 * Health scale is managed manually by us. Every time a player's health scale needs to be recalculated due to their
 * max HP potentially changing, update their health scale.
 */
class HealthScaleListener : ToggleableListener() {

    @EventHandler
    @Suppress("unused")
    private fun onArmorChange(event: PlayerArmorChangeEvent) {
        val plugin = plugin
        val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.getPlayer())
        object : BukkitRunnable() {
            override fun run() {
                event.getPlayer().healthScale = player.getHealthScale().toDouble()
            }
        }.runTaskLater(plugin, TickTime.INSTANTANEOUSLY)
    }

    @EventHandler
    @Suppress("unused")
    private fun onSkillLevelUp(event: SkillLevelUpEvent) {
        val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.player)
        event.player.healthScale = player.getHealthScale().toDouble()
    }
}
