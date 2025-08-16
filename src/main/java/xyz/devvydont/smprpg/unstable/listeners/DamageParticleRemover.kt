package xyz.devvydont.smprpg.unstable.listeners

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.Particle
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin

const val MAX_DAMAGE_PARTICLES: Int = 10

/**
 * Uses ProtocolLib to intercept particle events and limit the amount of damage particles that are allowed to send at once.
 * This is necessary as dealing damage in the thousands will spawn thousands of particles.
 */
class DamageParticleRemover {

    init {
        registerPacketListeners()
    }

    private fun registerPacketListeners() {
        if (plugin.server.pluginManager.getPlugin("ProtocolLib") == null) {
            plugin.logger.severe("ProtocolLib is not installed. High damage particles will clutter clients' screens.")
            return
        }

        // Create a packet listener where whenever we attempt to spawn damage particles over a certain amount,
        // cap the amount that can spawn so we don't obstruct player's views
        ProtocolLibrary.getProtocolManager().addPacketListener(object :
            PacketAdapter(plugin, ListenerPriority.HIGH, PacketType.Play.Server.WORLD_PARTICLES) {
            override fun onPacketSending(event: PacketEvent) {

                val packet = event.packet

                // Only listen to world particle packets
                if (event.packetType !== PacketType.Play.Server.WORLD_PARTICLES)
                    return

                // Only listen for damage particle packets
                if (packet.newParticles.read(0).particle != Particle.DAMAGE_INDICATOR)
                    return

                // Cap off the amount of particles in this packet
                if (packet.integers.read(0) > MAX_DAMAGE_PARTICLES)
                    packet.integers.write(0, MAX_DAMAGE_PARTICLES)
            }
        })
    }
}
