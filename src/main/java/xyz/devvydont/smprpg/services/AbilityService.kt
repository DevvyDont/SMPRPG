package xyz.devvydont.smprpg.services

import xyz.devvydont.smprpg.ability.handlers.passive.AbyssalAnnihilationListener
import xyz.devvydont.smprpg.ability.handlers.passive.AnglerListener
import xyz.devvydont.smprpg.ability.listeners.HotShotProjectileCollideListener
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * Provides global functionality to interact with the ability mechanic of the plugin.
 * Mostly used for instantiating important events for certain abilities to function.
 */
class AbilityService : IService {
    private val listeners: MutableList<ToggleableListener> = ArrayList()

    /**
     * Set up the service. When this method executes, all other services will be instantiated, making SMPRPG.getService()
     * calls safe to run. Run any initialization code that wasn't fit at construction time.
     *
     * @throws RuntimeException Thrown when the service was unable to startup.
     */
    @Throws(RuntimeException::class)
    override fun setup() {
        listeners.add(HotShotProjectileCollideListener())
        listeners.add(AnglerListener());
        listeners.add(AbyssalAnnihilationListener());
        for (listener in listeners)
            listener.start()
    }

    /**
     * Clean up the service. Run any code that this required for graceful cleanup of this service.
     */
    override fun cleanup() {
        for (listener in listeners) listener.start()
    }
}
