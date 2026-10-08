package xyz.devvydont.smprpg.services

import org.bukkit.Bukkit
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.fishing.listeners.FishingAnnouncementListeners
import xyz.devvydont.smprpg.fishing.listeners.FishingBehaviorListeners
import xyz.devvydont.smprpg.fishing.listeners.FishingGalleryListener
import xyz.devvydont.smprpg.fishing.tasks.FishingWeatherBonusTask
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.time.TickTime

class FishingService : IService {
    private val listeners: MutableList<ToggleableListener> = ArrayList<ToggleableListener>()
    private var weatherBonusTask: FishingWeatherBonusTask? = null

    @Throws(RuntimeException::class)
    override fun setup() {
        // Start up listeners that cause fishing to work.
        listeners.add(FishingBehaviorListeners())
        listeners.add(FishingGalleryListener())
        listeners.add(FishingAnnouncementListeners())
        for (listener in listeners) listener.start()

        // Keep environmental (rain/thunderstorm) fishing speed bonuses synced with the weather players are standing in.
        weatherBonusTask = FishingWeatherBonusTask().also {
            it.runTaskTimer(plugin, TickTime.INSTANTANEOUSLY, TickTime.seconds(5))
        }
    }

    override fun cleanup() {
        // Stop listeners.
        for (listener in listeners) listener.stop()

        // Stop the weather bonus task and strip any lingering weather bonuses from online players.
        weatherBonusTask?.cancel()
        weatherBonusTask = null
        for (player in Bukkit.getOnlinePlayers())
            FishingWeatherBonusTask.removeBonuses(player)
    }
}
