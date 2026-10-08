package xyz.devvydont.smprpg.services

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.ShadowColor
import net.kyori.adventure.text.format.TextColor
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
import xyz.devvydont.smprpg.entity.player.settings.HealthDisplayMode
import xyz.devvydont.smprpg.entity.player.settings.PlayerSettings
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
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

    private fun getImageComponent(player: Player): Component {
        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        val mana = ceil(leveledPlayer.mana)
        val max = ceil(leveledPlayer.getMaxMana())

        // Mana Bar
        var percentage = (mana / max)
        val icons = mutableListOf<Component>()
        var numIcons = 0
        var retComp = ComponentUtils.create(Symbols.OFFSET_128 + Symbols.OFFSET_32 + Symbols.OFFSET_8 + Symbols.OFFSET_5)

        while (numIcons < 10) {
            if (percentage > 0.1) {
                icons.add(ComponentUtils.create(Symbols.MANA_FULL).shadowColor(ShadowColor.shadowColor(0)))
                percentage -= 0.1
            }
            else if (percentage > 0.05) {
                icons.add(ComponentUtils.create(Symbols.MANA_HALF).shadowColor(ShadowColor.shadowColor(0)))
                percentage -= 0.05
            }
            else {
                icons.add(ComponentUtils.create(Symbols.MANA_EMPTY).shadowColor(ShadowColor.shadowColor(0)))
            }
            icons.add(ComponentUtils.create(Symbols.OFFSET_NEG_2))
            numIcons++
        }
        icons.reverse()
        for (comp in icons) {
            retComp = retComp.append(comp)
        }

        // Reset our offset (-90 for mana bitmaps) (-173 for bar offset)
        retComp = retComp.append(ComponentUtils.create(Symbols.OFFSET_NEG_64 + Symbols.OFFSET_NEG_32 + Symbols.OFFSET_6 +  // Mana Bitmaps
                                                       Symbols.OFFSET_128 + Symbols.OFFSET_32 + Symbols.OFFSET_8 + Symbols.OFFSET_5))  // Bar Offset

        return retComp
    }

    /**
     * A helper method to retrieve the health component for the player. Returns [ComponentUtils.EMPTY] when the
     * player has chosen to hide their health. Effective (EHP) modes use a distinct heart + shield symbol so they
     * can be told apart from raw HP at a glance, and their (potentially large) numbers get thousands separators.
     */
    private fun getHealthComponent(player: Player): Component {
        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        val mode = leveledPlayer.settings.healthDisplayMode

        if (mode == HealthDisplayMode.HIDDEN)
            return ComponentUtils.EMPTY

        val totalHp = leveledPlayer.getTotalHp()
        val maxHp = leveledPlayer.getMaxHp()

        // The color always reflects the true health percentage, regardless of which numbers we show.
        val color = EntityGlobals.getChatColorFromHealth(totalHp, maxHp)
        val defense = leveledPlayer.defense.toDouble()

        return when (mode) {
            HealthDisplayMode.NORMAL ->
                healthReadout(ceil(totalHp).toLong(), ceil(maxHp).toLong(), color, heartSymbol())

            HealthDisplayMode.HP_ONLY ->
                healthReadout(ceil(totalHp).toLong(), null, color, heartSymbol())

            HealthDisplayMode.EFFECTIVE ->
                healthReadout(
                    ceil(EntityDamageCalculatorService.calculateEffectiveHealth(totalHp, defense)).toLong(),
                    ceil(EntityDamageCalculatorService.calculateEffectiveHealth(maxHp, defense)).toLong(),
                    color, effectiveHeartSymbol()
                )

            HealthDisplayMode.EHP_ONLY ->
                healthReadout(
                    ceil(EntityDamageCalculatorService.calculateEffectiveHealth(totalHp, defense)).toLong(),
                    null, color, effectiveHeartSymbol()
                )

            // Handled above, but the when must be exhaustive.
            HealthDisplayMode.HIDDEN -> ComponentUtils.EMPTY
        }
    }

    /**
     * Builds a health readout of the form "current/max <symbol>", or "current <symbol>" when [max] is null.
     */
    private fun healthReadout(current: Long, max: Long?, color: TextColor, symbol: Component): Component {
        var component = ComponentUtils.create(MinecraftStringUtils.formatNumber(current), color)
        if (max != null) {
            component = component.append(ComponentUtils.create("/"))
                .append(ComponentUtils.create(MinecraftStringUtils.formatNumber(max), NamedTextColor.GREEN))
        }
        return component.append(symbol)
    }

    /**
     * The symbol used for raw health: a red heart.
     */
    private fun heartSymbol(): Component =
        ComponentUtils.create(Symbols.HEART, NamedTextColor.RED)

    /**
     * The symbol used for effective health: a red heart paired with a gray shield, signifying health combined
     * with defense, and distinguishing it from raw HP.
     */
    private fun effectiveHeartSymbol(): Component =
        ComponentUtils.merge(
            ComponentUtils.create(Symbols.HEART, NamedTextColor.RED),
            ComponentUtils.create(Symbols.SHIELD, NamedTextColor.GRAY)
        )

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

        val settings = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player).settings

        // Only the source components the player allows are shown; this drives both rendering and the defense check.
        val visibleComponents = getPlayerComponents(player).values.filter { isSourceVisible(it.source, settings) }

        // Collect the enabled stat readouts. Any of them may be omitted (e.g. hidden health), so we join only
        // what is present to avoid dangling separators.
        val stats = mutableListOf<Component>()

        val health = getHealthComponent(player)
        if (health != ComponentUtils.EMPTY) stats.add(health)

        // Show defense if enabled, but omit it if we are already displaying more than 1 extra component to save space.
        if (settings.isDefenseInActionBar && visibleComponents.size <= 1) stats.add(getDefenseComponent(player))

        if (settings.isManaInActionBar) stats.add(getManaComponent(player))

        var message: Component = ComponentUtils.EMPTY
        var hasContent = false
        for (stat in stats) {
            message = if (!hasContent) stat else message.append(ComponentUtils.create("  ")).append(stat)
            hasContent = true
        }

        // Append any temporary/source components, divided by a bar. If nothing precedes them, the first leads.
        for (component in visibleComponents) {
            message = if (!hasContent) component.display
            else message.append(ComponentUtils.create(" | ")).append(component.display)
            hasContent = true
        }

        return message
    }

    /**
     * Whether a source component should be shown, according to the player's settings. Sources without a
     * dedicated setting are always shown.
     */
    private fun isSourceVisible(source: ActionBarSource, settings: PlayerSettings): Boolean {
        return when (source) {
            ActionBarSource.SKILL -> settings.isSkillExperienceInActionBar
            // The structure notice decides its own visibility where it is added, since that needs to know
            // whether the player is underleveled. If it was added at all, it should be shown.
            ActionBarSource.STRUCTURE, ActionBarSource.AILMENT, ActionBarSource.MISC -> true
        }
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
