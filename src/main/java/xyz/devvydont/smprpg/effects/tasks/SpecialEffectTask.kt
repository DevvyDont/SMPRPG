package xyz.devvydont.smprpg.effects.tasks

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.services.SpecialEffectService
import xyz.devvydont.smprpg.services.ActionBarService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/*
 * Represents a base "effect" that players can have. These are typically referred to as "ailments" and a player can only
 * have one of these at a time. The simplest one is the "Shrouded" effect, where upon respawning players cannot be
 * targeted by hostile mobs for a short period of time to allow easy recovery for their items
 */
abstract class SpecialEffectTask(protected val service: SpecialEffectService, val player: Player, var seconds: Int) :
    BukkitRunnable() {
    protected var ticks: Int = 0

    fun getSecondsRemaining(): Int {
        return seconds
    }

    fun setSecondsRemaining(seconds: Int) {
        this.seconds = seconds + 1
    }

    abstract val expiredComponent: Component

    abstract val nameComponent: Component

    abstract val timerColor: TextColor

    /*
     * Logic to perform every tick of this ailment.
     */
    protected abstract fun tick()

    /*
     * Logic to perform when the effect expires naturally from time running out. This is not called when the effect
     * is forcefully removed.
     */
    protected abstract fun expire()

    /*
     * Logic to perform when the effect is forcefully removed instead of having time run out. This is not called when
     * the effect naturally expires from time running out.
     */
    abstract fun removed()

    private fun generateComponent(seconds: Int): Component {
        val displaySeconds = seconds - 1
        val minutes = (displaySeconds + 1) / 60

        val expired = seconds <= 0
        val time: Component
        var timestring = String.format("%d:%02d", minutes, seconds % 60)
        if (displaySeconds <= 59) timestring = String.format("%d.%d", displaySeconds, (9 - ticks % 10))
        if (expired) time = this.expiredComponent
        else time = ComponentUtils.create(timestring, this.timerColor)
        return this.nameComponent.append(ComponentUtils.create(" - ")).append(time)
    }

    @JvmOverloads
    fun sendActionBar(seconds: Int = this.seconds) {
        SMPRPG.getService(ActionBarService::class.java)
            .addActionBarComponent(this.player, ActionBarService.ActionBarSource.AILMENT, generateComponent(seconds), 2)
    }

    override fun run() {
        ticks++

        // If we were canceled from somewhere else, they handled the logic already
        if (isCancelled()) return

        // Did they log out or did we lose the reference? Cancel the task if that is the case
        if (!player.isValid) {
            removed()
            service.removeEffect(player.uniqueId)
            cancel()
            return
        }

        // If a second has gone by (PERIOD * ticks is divisible by the tick rate of the server), then take a second away
        if (PERIOD * ticks % 20 == 0) seconds--

        // Announce to them how much time they have left with this effect
        tick()
        sendActionBar()

        // If the task expired, remove this task
        if (seconds <= 0) {
            sendActionBar(-1)
            service.removeEffect(player.uniqueId)
            expire()
            cancel()
            return
        }
    }

    companion object {
        // How many ticks to wait to run this task. 2 would be every 2 ticks, or 10 times a second.
        const val PERIOD: Int = 2
    }
}
