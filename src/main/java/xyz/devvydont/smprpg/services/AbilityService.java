package xyz.devvydont.smprpg.services;

import xyz.devvydont.smprpg.ability.listeners.HotShotProjectileCollideListener;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides global functionality to interact with the ability mechanic of the plugin.
 * Mostly used for instantiating important events for certain abilities to function.
 */
public class AbilityService implements IService {

    private final List<ToggleableListener> listeners = new ArrayList<>();

    /**
     * Set up the service. When this method executes, all other services will be instantiated, making SMPRPG.getService()
     * calls safe to run. Run any initialization code that wasn't fit at construction time.
     *
     * @throws RuntimeException Thrown when the service was unable to startup.
     */
    @Override
    public void setup() throws RuntimeException {

        listeners.add(new HotShotProjectileCollideListener());  // Allows the Hot Shot ability to function.
        for (var listener : listeners)
            listener.start();

    }

    /**
     * Clean up the service. Run any code that this required for graceful cleanup of this service.
     */
    @Override
    public void cleanup() {

        for (var listener : listeners)
            listener.start();

    }
}
