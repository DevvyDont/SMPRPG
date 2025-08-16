package xyz.devvydont.smprpg.services

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.entity.EntityGlobals
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*
import kotlin.math.ceil

// The desired update frequency of the action bar.
private const val UPDATE_FREQUENCY: Long = TickTime.TICK * 5

class ActionBarService : IService, Listener {
    
    @JvmRecord
    data class ActionBarComponent(val source: ActionBarSource, val display: Component, val expiry: Long)

    /**
     * Works as a key so we can display multiple action bar things at the same time.
     */
    enum class ActionBarSource {
        SKILL,
        STRUCTURE,
        AILMENT,
        MISC
    }

    /**
     * The task that sends action bars to every player on the server.
     */
    private var sendAllActionBarTask: BukkitRunnable? = null

    /**
     * Maps player ID's to the extra components that they should be shown on their action bar.
     */
    private val components: MutableMap<UUID, MutableMap<ActionBarSource, ActionBarComponent>> =
        HashMap<UUID, MutableMap<ActionBarSource, ActionBarComponent>>()

    /**
     * Gets the current components that are to be shown to a player.
     */
    private fun getPlayerComponents(player: Player): MutableMap<ActionBarSource, ActionBarComponent> {
        var playersComponents = this.components[player.uniqueId]
        if (playersComponents == null) {
            playersComponents = HashMap<ActionBarSource, ActionBarComponent>()
            this.components.put(player.uniqueId, playersComponents)
        }
        return playersComponents
    }

    /**
     * Adds a component to display to the player's action bar. Overwrites previous components of similar source.
     * @param player The player to show a component to.
     * @param source The source of the component. Only one component can be shown per source.
     * @param display The component to show on the bar.
     * @param seconds The seconds to display it for.
     */
    fun addActionBarComponent(player: Player, source: ActionBarSource, display: Component, seconds: Int) {
        val expiry = System.currentTimeMillis() + seconds * 1000L
        val component = ActionBarComponent(source, display, expiry)
        addActionBarComponent(player, component)
    }

    /**
     * Adds a component to display to the player's action bar. Overwrites previous components of similar source.
     * @param player The player show a component to.
     * @param component The full component.
     */
    fun addActionBarComponent(player: Player, component: ActionBarComponent) {
        val componentMap = getPlayerComponents(player)
        componentMap.put(component.source, component)
        display(player)
    }

    /**
     * A helper method to retrieve the health component for the player.
     */
    private fun getHealthComponent(player: Player): Component {
        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        val hp = ceil(leveledPlayer.getTotalHp()).toInt()
        val maxHP = ceil(leveledPlayer.getMaxHp()).toInt()
        val color = EntityGlobals.getChatColorFromHealth(hp.toDouble(), maxHP.toDouble())
        return ComponentUtils.create(hp.toString() + "", color)
            .append(ComponentUtils.create("/"))
            .append(ComponentUtils.create(maxHP.toString() + "", NamedTextColor.GREEN))
            .append(ComponentUtils.create(Symbols.HEART, NamedTextColor.RED))
    }

    /**
     * A helper method to retrieve the defense component for the player.
     */
    private fun getDefenseComponent(player: Player): Component {
        val def = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player).defense
        return ComponentUtils.create(def.toString() + "", NamedTextColor.DARK_GREEN)
            .append(ComponentUtils.create(Symbols.SHIELD, NamedTextColor.GRAY))
    }

    /**
     * A helper method to retrieve the mana component for the player.
     */
    private fun getManaComponent(player: Player): Component {
        val playerWrapper = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        val mana = ceil(playerWrapper.mana).toInt()
        val max = ceil(playerWrapper.getMaxMana()).toInt()

        return ComponentUtils.merge(
            ComponentUtils.create("" + mana, NamedTextColor.AQUA),
            ComponentUtils.create("/"),
            ComponentUtils.create(max.toString() + "", NamedTextColor.AQUA),
            ComponentUtils.create(Symbols.MANA, NamedTextColor.AQUA)
        )
    }

    /**
     * A helper method to retrieve the level component for the player.
     */
    private fun getPowerComponent(player: Player): Component {
        val p = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        return ComponentUtils.powerLevelPrefix(p.getLevel())
    }

    /**
     * Get the full component to display about some player.
     * @param player The player to query a full component for.
     * @return A fully formatted action bar component at a moment in time.
     */
    private fun getPlayersComponent(player: Player): Component {
        // Do not attempt to display leveled info about the player if the entity service is not tracking them.
        // Since this task is async, we don't want to force entity setup mechanisms.

        if (!SMPRPG.getService(EntityService::class.java)
                .isTracking(player)
        ) return ComponentUtils.EMPTY

        // The component Will always have their health
        var message = getPowerComponent(player).append(ComponentUtils.create(" ")).append(getHealthComponent(player))

        // Check for components
        val playersComponents = getPlayerComponents(player)

        // If we are displaying more than 1 extra component, omit defense
        if (playersComponents.size <= 1) message =
            message.append(ComponentUtils.create("  ")).append(getDefenseComponent(player))

        message = message.append(ComponentUtils.create("  ")).append(getManaComponent(player))

        for (entry in playersComponents.entries) message =
            message.append(ComponentUtils.create(" | ")).append(entry.value.display)

        return message
    }

    /**
     * Given a player, display something to their action bar.
     */
    private fun display(player: Player) {
        // If the player is in creative mode, there is no point on showing information to them.

        if (player.gameMode == GameMode.CREATIVE) return

        // If the player is in spectator mode, and they are spectating someone, use theirs.
        var playerToShow: Player? = player
        if (player.gameMode == GameMode.SPECTATOR && player.spectatorTarget is Player) playerToShow =
            player.spectatorTarget as Player?

        // If they are spectating and not spectating a player, don't show them a bar.
        if (player.gameMode == GameMode.SPECTATOR && playerToShow == player) return

        // Get the component for the player. If it's empty, don't even bother sending it.
        val message = getPlayersComponent(playerToShow!!)
        if (message == ComponentUtils.EMPTY) return

        player.sendActionBar(message)
    }

    /**
     * Checks all components in the component map. If a component has expired, it removes it
     * @param player The player to check for stale components.
     */
    private fun checkForExpiredComponents(player: Player) {
        val playersComponents = getPlayerComponents(player)
        for (source in ActionBarSource.entries) {
            val component = playersComponents[source]
            if (component == null)
                continue

            if (component.expiry < System.currentTimeMillis())
                playersComponents.remove(source)
        }
    }

    @Throws(RuntimeException::class)
    override fun setup() {

        sendAllActionBarTask?.cancel()
        sendAllActionBarTask = object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    checkForExpiredComponents(player)
                    display(player)
                }
            }
        }
        sendAllActionBarTask!!.runTaskTimerAsynchronously(plugin, TickTime.INSTANTANEOUSLY, UPDATE_FREQUENCY)
    }

    override fun cleanup() {
        sendAllActionBarTask?.cancel()
        sendAllActionBarTask = null
    }

    /**
     * When a player quits, we no longer need to keep track of their components.
     */
    @EventHandler
    @Suppress("unused")
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        components.remove(event.getPlayer().uniqueId)
    }
}
