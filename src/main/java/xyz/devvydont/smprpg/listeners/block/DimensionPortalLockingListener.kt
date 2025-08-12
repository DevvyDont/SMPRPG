package xyz.devvydont.smprpg.listeners.block

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.PortalType
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPortalEnterEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.entity.player.ProfileDifficulty
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import java.time.Duration
import java.time.Instant
import java.util.*

/**
 * Intercepts dimension changing events and determines if we should allow it or not.
 * If you want dimensions to have requirements such as average skill thresholds, or time, then instantiate this listener.
 */
class DimensionPortalLockingListener : ToggleableListener() {
    private val messageCooldown: MutableMap<UUID?, Long> = HashMap<UUID?, Long>()

    /**
     * A container that defines what this dimension is locked behind.
     * @param timelock The time at which this dimension is accessible.
     * @param level The level needed to enter this dimension.
     */
    @JvmRecord
    data class DimensionLock(val timelock: Instant, val level: Int)

    override fun start() {
        super.start()

        // Reload from config to initialize.
        try {
            reload()
        } catch (e: Exception) {
            plugin.logger.severe("Failed to initialize dimension locks: " + e.message)
            e.printStackTrace()
        }
    }

    /**
     * Reloads the timestamps and level requirements for the end and the nether from the config.
     */
    fun reload() {
        val cfg = plugin.getConfig()

        // Read the sections from the config we care about.
        val netherLvl = cfg.getInt("world_skill_unlocks.nether")
        val endLvl = cfg.getInt("world_skill_unlocks.end")
        val netherTime = cfg.getString("world_time_unlocks.nether")
        val endTime = cfg.getString("world_time_unlocks.end")

        // Check if anything went wrong.
        checkNotNull(netherTime) { "Failed to parse time unlock for the nether!" }
        checkNotNull(endTime) { "Failed to parse time unlock for the end!" }

        // Update.
        NETHER_LOCK = DimensionLock(Instant.parse(netherTime), netherLvl)
        END_LOCK = DimensionLock(Instant.parse(endTime), endLvl)
        plugin.logger.info("Reloaded dimension locks from config")
    }

    /**
     * Intercept the event when someone is attempting to teleport to a new dimension.
     * If they don't meet the requirements, stop them and let them know.
     * @param event The [EntityPortalEnterEvent] event that provides us with relevant context.
     */
    @EventHandler
    @Suppress("unused")
    private fun onAttemptDimensionTeleport(event: EntityPortalEnterEvent) {

        // For admin purposes, if this is someone in creative mode then allow it to happen.
        if (event.entity is Player) {
            val player = event.entity as Player
            if (player.gameMode == GameMode.CREATIVE)
                return
        }

        // Retrieve the lock. If there isn't one, we don't care.
        val lock: DimensionLock? = fromPortal(event.portalType)
        if (lock == null)
            return

        // First, check the time. We don't want anything or anyone to get through if the dimension is locked by time.
        val now = Instant.now()
        if (now.isBefore(lock.timelock)) {
            event.isCancelled = true
            sendTimeDiffMessage(event.getEntity(), Duration.between(now, lock.timelock))
            return
        }

        // The dimension is unlocked timewise! Only players are relevant past this point.
        if (event.entity !is Player)
            return
        val player = event.entity as Player

        // If this player is playing on easy, we don't check their skill and allow them to go.
        val wrapper = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        if (wrapper.difficulty == ProfileDifficulty.EASY)
            return

        // Is this player high enough skill?
        if (wrapper.getAverageSkillLevel() < lock.level) {
            event.isCancelled = true
            sendSkillTooLowMessage(wrapper, lock.level)
            return
        }

        // They are allowed to go! We don't need to do anything.
        plugin.logger.finest(
            String.format(
                "%s has passed all checks for dimension travel using a %s portal.",
                player.name,
                event.portalType
            )
        )
    }

    /**
     * Given a time duration, format a clean string representation that is friendly for a player to view.
     * @param duration The time duration.
     * @return A formatted string.
     */
    private fun formatTimeDifference(duration: Duration): String {
        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60

        val sb = StringBuilder()
        if (days > 0) sb.append(days).append(" Days ")

        if (hours > 0 || days > 0) sb.append(hours).append(" Hours ")

        if (minutes > 0 || days > 0 || hours > 0) sb.append(minutes).append("m")

        sb.append(seconds).append("s")

        return sb.toString().trim { it <= ' ' }
    }

    /**
     * Sends a message to the player that they are too low of a level to go there.
     * @param player The player that is too low.
     * @param requirement The requirement they need to go there.
     */
    private fun sendSkillTooLowMessage(player: LeveledPlayer, requirement: Int) {
        // Don't do anything if we are on cooldown

        val now = System.currentTimeMillis()
        val cooldown = messageCooldown.getOrDefault(player.player.uniqueId, 0L)
        if (cooldown > now) return

        messageCooldown.put(player.player.uniqueId, now + MESSAGE_COOLDOWN)
        player.player.sendMessage(
            ComponentUtils.error(
                ComponentUtils.merge(
                    ComponentUtils.create("You must have an average skill level of ", NamedTextColor.RED),
                    ComponentUtils.create("" + requirement, NamedTextColor.DARK_RED),
                    ComponentUtils.create(
                        " to enter this portal. You average skill level is currently ",
                        NamedTextColor.RED
                    ),
                    ComponentUtils.create("" + player.getAverageSkillLevel().toInt(), NamedTextColor.DARK_RED),
                    ComponentUtils.create("!", NamedTextColor.RED)
                )
            )
        )
    }

    /**
     * Sends a message to the player that this dimension is locked, and when it unlocks.
     * @param entity The entity that wants to receive the message.
     * @param lockedFor The time until the dimension unlocks.
     */
    private fun sendTimeDiffMessage(entity: Entity?, lockedFor: Duration) {
        // Dont send messages to non-players

        if (entity !is Player)
            return

        // Don't do anything if we are on cooldown
        val now = System.currentTimeMillis()
        val cooldown = messageCooldown.getOrDefault(entity.uniqueId, 0L)
        if (cooldown > now)
            return

        messageCooldown.put(entity.uniqueId, now + MESSAGE_COOLDOWN)
        val timeDiff = ComponentUtils.create(formatTimeDifference(lockedFor), NamedTextColor.DARK_RED)
        entity.sendMessage(ComponentUtils.error("This dimension is locked for another ").append(timeDiff))
    }


    companion object {
        // When standing in a portal, the attempt transport event fires every tick. We need a cooldown for the message.
        const val MESSAGE_COOLDOWN: Int = 1000

        // The requirements to enter the nether.
        var NETHER_LOCK: DimensionLock = DimensionLock(Instant.now(), 0)

        // The requirements to enter the end.
        var END_LOCK: DimensionLock = DimensionLock(Instant.now(), 0)

        /**
         * Retrieve the requirement from the portal type.
         * @param portal The portal that is being used.
         * @return The lock that is associated with it. Returns null if it is always unlocked.
         */
        fun fromPortal(portal: PortalType): DimensionLock? {
            return when (portal) {
                PortalType.NETHER -> NETHER_LOCK
                PortalType.ENDER -> END_LOCK
                else -> null
            }
        }
    }
}
