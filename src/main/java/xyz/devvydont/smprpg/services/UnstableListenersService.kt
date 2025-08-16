package xyz.devvydont.smprpg.services

import xyz.devvydont.smprpg.unstable.listeners.DamageParticleRemover

/**
 * If ProtocolLib is required for a listener, then it should be handled here.
 * The job of this service is to instantiate ProtocolLib packet handlers, hence the name "Unstable".
 */
class UnstableListenersService : IService {
    @Throws(RuntimeException::class)
    override fun setup() {
        DamageParticleRemover()
    }

    override fun cleanup() {
    }
}
