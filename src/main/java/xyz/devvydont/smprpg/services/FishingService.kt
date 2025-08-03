package xyz.devvydont.smprpg.services

import xyz.devvydont.smprpg.fishing.listeners.FishingAnnouncementListeners
import xyz.devvydont.smprpg.fishing.listeners.FishingBehaviorListeners
import xyz.devvydont.smprpg.fishing.listeners.FishingGalleryListener
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

class FishingService : IService {
    private val listeners: MutableList<ToggleableListener> = ArrayList<ToggleableListener>()

    @Throws(RuntimeException::class)
    override fun setup() {
        // Start up listeners that cause fishing to work.
        listeners.add(FishingBehaviorListeners())
        listeners.add(FishingGalleryListener())
        listeners.add(FishingAnnouncementListeners())
        for (listener in listeners) listener.start()
    }

    override fun cleanup() {
        // Stop listeners.
        for (listener in listeners) listener.stop()
    }
}
