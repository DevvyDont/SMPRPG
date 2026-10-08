package xyz.devvydont.smprpg.blockbreaking

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDamageEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.UUID

class PacketManager : Listener {
    val plugin: SMPRPG = SMPRPG.plugin

    val manager: ProtocolManager = ProtocolLibrary.getProtocolManager()

    val damage: BlockDamage

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
        damage = BlockDamage()
        receivedArmAnimation()
        checkArmAnimation()
    }

    private fun receivedArmAnimation() {
        manager.addPacketListener(object :
            PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.ARM_ANIMATION) {
            override fun onPacketReceiving(event: PacketEvent) {
                armSwinging.put(event.player.uniqueId, System.currentTimeMillis())
            }
        })
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun blockStartBreaking(event: BlockDamageEvent) {
        event.isCancelled = true // Cancel any vanilla behavior
        BlockDamage.cancelTaskWithBlockReset(event.player)
        damage.configureBreakingPacket(event.player, event.block)
    }

    /**
     * Checks that an arm swing packet was delivered in the last tick (0.15 seconds)
     */
    private fun checkArmAnimation() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            // Clone our keyset so that we can avoid concurrent modification.
            val keySet: MutableSet<UUID> = armSwinging.keys.toMutableSet()
            val currentTime = System.currentTimeMillis()
            for (uuid in keySet) {
                if (armSwinging.get(uuid)!! + 150 < currentTime) {
                    armSwinging.remove(uuid)
                }
            }
        }, TickTime.INSTANTANEOUSLY, TickTime.TICK)
    }

    companion object {
        @JvmField
		var armSwinging: HashMap<UUID, Long> = HashMap()
        val blockPropertiesRegistry = BlockPropertiesRegistry()  // Hacky, but this initializes the class for static references.
    }
}
