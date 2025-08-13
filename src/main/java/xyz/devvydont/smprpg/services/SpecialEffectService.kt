package xyz.devvydont.smprpg.services

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.effects.listeners.ShroudedEffectListener
import xyz.devvydont.smprpg.effects.tasks.SpecialEffectTask
import java.util.ArrayList
import java.util.HashMap
import java.util.UUID

/*
 * Used across the plugin to give/remove/interact with special effects to apply on players.
 */
class SpecialEffectService : IService {

    private val currentTasks: MutableMap<UUID, SpecialEffectTask> = HashMap<UUID, SpecialEffectTask>()
    private val listeners: MutableList<Listener> = ArrayList<Listener>()

    private fun registerListeners() {
        listeners.add(ShroudedEffectListener(this))

        val plugin = SMPRPG.Companion.plugin
        for (listener in listeners) plugin.server.pluginManager.registerEvents(listener, plugin)
    }

    @Throws(RuntimeException::class)
    override fun setup() {
        registerListeners()
    }

    override fun cleanup() {
        for (listener in listeners)
            HandlerList.unregisterAll(listener)

        listeners.clear()
    }

    /**
     * Query if a player has a special effect or not.
     *
     * @param player The player to check for an effect.
     * @return true if the player has a special effect, false if they don't.
     */
    fun hasEffect(player: Player): Boolean {
        val task = currentTasks[player.uniqueId]
        if (task == null)
            return false

        return task.getSecondsRemaining() > 0
    }

    /**
     * Used to give a player a special effect. This will remove the current effect if they already have one.
     * This method will handle all the processing of starting the tick timer and registering events.
     *
     * @param player The player to give an effect to
     * @param effect The effect to give them, simply just needs to be a new instance of an effect.
     */
    fun giveEffect(player: Player, effect: SpecialEffectTask) {
        // Remove if it is active

        removeEffect(player)

        // Create a task and run it every second and store it
        val plugin = SMPRPG.Companion.plugin
        if (effect is Listener) plugin.server.pluginManager.registerEvents(effect, plugin)
        effect.runTaskTimer(plugin, 0, SpecialEffectTask.Companion.PERIOD.toLong())
        currentTasks.put(player.uniqueId, effect)
    }

    /**
     * Removes an effect from the player if and only if the given class matches the effect that is currently applied
     * to the player. Useful for if you want to remove an effect from a player only if it is a certain type.
     *
     * @param player The player to remove an effect from.
     * @param effectClass The type of effect to remove
     */
    fun removeEffect(player: Player, effectClass: Class<out SpecialEffectTask>) {
        val effect: SpecialEffectTask? = currentTasks[player.uniqueId]
        if (effect == null)
            return
        if (effectClass == effect.javaClass)
            removeEffect(player)
    }

    /*
     * Removes any special effects from a player regardless of what it is.
     */
    fun removeEffect(player: Player) {
        removeEffect(player.uniqueId)
    }

    /*
     * Removes any special effects from a player regardless of what it is.
     */
    fun removeEffect(uuid: UUID?) {
        val task = currentTasks[uuid]

        // They don't have an effect, don't do anything
        if (task == null)
            return

        // Cancel the task, send an expired action bar, and remove the reference to the task
        task.cancel()
        task.removed()
        currentTasks.remove(uuid)
        task.sendActionBar(-1)
        if (task is Listener) HandlerList.unregisterAll(task)
    }
}