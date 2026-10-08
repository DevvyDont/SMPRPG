package xyz.devvydont.smprpg.listeners.block

import io.papermc.paper.event.block.VaultChangeStateEvent
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import net.momirealms.craftengine.bukkit.api.CraftEngineItems
import net.momirealms.craftengine.bukkit.util.ItemStackUtils
import org.bukkit.block.Vault
import org.bukkit.craftbukkit.entity.CraftFallingBlock
import org.bukkit.entity.FallingBlock
import org.bukkit.event.EventHandler
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.events.block.AscendingBlockRegisterEvent
import xyz.devvydont.smprpg.listeners.damage.UnderwaterArrowListener.Companion.GRAVITY_VECTOR
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.craftengine.CraftEngineHelpers
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.time.TickTime

class AscendingBlockListener : ToggleableListener() {
    /**
     * Tracks ascending blocks, and handles gravity overrides on them.
     */

    var tracker : BukkitRunnable? = null
    val ascendingBlocks = mutableSetOf<FallingBlock>()

    init {
        /**
         * The purpose of this tracker is to take control of arrow speed manually. We approximate values from
         * NMS to get a best fit for how the arrow should behave underwater.
         */
        tracker = object : BukkitRunnable() {
            override fun run() {

                // Heartbeart any tracked blocks

                val blockIterMap = ascendingBlocks.toMutableSet()
                for (block in blockIterMap) {
                    // Kill any blocks that float too high up (they met Icarus)
                    if (block.y > 768) {
                        ascendingBlocks.remove(block)
                        block.remove()
                    }
                    val newVec =  block.velocity.clone()
                    newVec.y += GRAVITY_CONSTANT
                    if (newVec.y > TERMINAL_VELOCITY)
                        newVec.y = TERMINAL_VELOCITY
                    block.velocity = newVec

                    val locationAbove = block.location.clone()
                    locationAbove.y = locationAbove.y + 1
                    val blockAbove = block.world.getBlockAt(locationAbove)
                    if (!blockAbove.isReplaceable) {
                        // Remove the block from our tracker, its logic is concluded.
                        ascendingBlocks.remove(block)
                        block.remove()
                        // Now we need to place our "block" here.
                        if (block.world.getBlockAt(block.location).isReplaceable) {
                            CraftEngineBlocks.place(
                                block.location,
                                CraftEngineBlocks.getCustomBlockState(block.blockData)!!,
                                false
                            )
                        }
                        else {
                            val item = ItemStackUtils.getBukkitStack(CraftEngineItems.byId(CraftEngineHelpers.getBlockKey(block.blockState)!!)!!)
                            block.world.dropItemNaturally(block.location, item)
                        }
                    }
                }
            }
        }

        tracker!!.runTaskTimer(SMPRPG.plugin, TickTime.INSTANTANEOUSLY, TickTime.TICK)
    }

    @EventHandler
    @Suppress("unused")
    private fun onVaultInteract(event: AscendingBlockRegisterEvent) {
        if (event.fallingBlock in ascendingBlocks) return
        ascendingBlocks.add(event.fallingBlock)
    }

    companion object {
        /**
         * Falling blocks, for whatever reason, accelerate at 0.5x standard gravity.
         * However, since we want to counteract this force, we will double it
         * Terminal Velocity will stay at the 0.5x value though.
         */

        const val GRAVITY_CONSTANT = 0.0784  // Other entities are .08 m/s^2/tick ~(32 m/s^2), then we precompute the drag coefficient for 0.0784 m/s^2/tick
        const val TERMINAL_VELOCITY = 1.96  // Other entities are 3.92 m/tick
        const val SNAP_FACTOR = 0.1  // How close we need to be to the block before we can finalize our land
    }
}
