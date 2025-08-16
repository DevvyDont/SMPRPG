package xyz.devvydont.smprpg.services

import xyz.devvydont.smprpg.block.BlockLootOverrideListener
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

class BlockLootService : IService {

    private val listeners: MutableList<ToggleableListener> = ArrayList()

    /**
     * Set up the service. When this method executes, all other services will be instantiated, making SMPRPG.getService()
     * calls safe to run. Run any initialization code that wasn't fit at construction time.
     *
     * @throws RuntimeException Thrown when the service was unable to startup.
     */
    @Throws(RuntimeException::class)
    override fun setup() {
        listeners.add(BlockLootOverrideListener()) // Hooks into block break events to override loot.

        for (listener in listeners)
            listener.start()
    }

    /**
     * Clean up the service. Run any code that this required for graceful cleanup of this service.
     */
    override fun cleanup() {
        for (listener in listeners)
            listener.stop()
    }
}
