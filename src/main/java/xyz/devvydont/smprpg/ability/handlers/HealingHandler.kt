package xyz.devvydont.smprpg.ability.handlers

import com.destroystokyo.paper.ParticleBuilder
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.scheduler.BukkitRunnable
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.services.ActionBarService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*

class HealingHandler(val healingPerHalfSecond: Int, val secondsActive: Int) : AbilityHandler {
    /**
     * Attempts to execute the ability.
     *
     * @param ctx The context of the ability.
     * @return True if the ability succeeded and should have cost reduced, false otherwise.
     */
    override fun execute(ctx: AbilityContext): Boolean {
        // If the player already has healing applying to them, then don't do anything.

        if (entityIdToHealingTask.containsKey(ctx.caster.uniqueId)) return false

        // The player is allowed to heal. Make them a task.
        val task: BukkitRunnable? = object : BukkitRunnable() {
            private var halfSeconds = 0
            override fun run() {
                val player = Bukkit.getPlayer(ctx.caster.uniqueId)
                if (player == null || (halfSeconds / 2 > secondsActive)) {
                    cancel()
                    return
                }

                val secLeft: Int = secondsActive - (halfSeconds / 2)
                if (halfSeconds > 2) SMPRPG.getService(ActionBarService::class.java)
                    .addActionBarComponent(
                        player,
                        ActionBarService.ActionBarSource.MISC,
                        ComponentUtils.create("HEALING " + secLeft + "s", NamedTextColor.GREEN),
                        1
                    )
                player.heal(healingPerHalfSecond.toDouble())
                halfSeconds++
                ParticleBuilder(Particle.HEART)
                    .location(player.location.add(0.0, 1.0, 0.0))
                    .count(2)
                    .offset(.25, .1, .25)
                    .receivers(10)
                    .extra(0.0)
                    .spawn()
            }

            @Synchronized
            @Throws(IllegalStateException::class)
            override fun cancel() {
                super.cancel()
                entityIdToHealingTask.remove(ctx.caster.uniqueId)
            }
        }

        task!!.runTaskTimer(plugin, TickTime.INSTANTANEOUSLY, TickTime.HALF_SECOND)
        entityIdToHealingTask.put(ctx.caster.uniqueId, task)
        ctx.caster.world.playSound(ctx.caster.location, Sound.ENTITY_CREAKING_DEATH, 1f, 2f)
        return true
    }

    companion object {
        var SMALL_HEAL_AMOUNT: Int = 5
        var NORMAL_HEAL_AMOUNT: Int = 15
        var BIG_HEAL_AMOUNT: Int = 25
        var HEFTY_HEAL_AMOUNT: Int = 50
        var COLOSSAL_HEAL_AMOUNT: Int = 75
        var SMALL_HEAL_SECONDS: Int = 3
        var NORMAL_HEAL_SECONDS: Int = 5
        var BIG_HEAL_SECONDS: Int = 8

        private val entityIdToHealingTask = HashMap<UUID?, Runnable?>()
    }
}
