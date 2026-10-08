package xyz.devvydont.smprpg.block.entity.renderers

import com.destroystokyo.paper.ParticleBuilder
import kr.toxicity.model.api.animation.AnimationModifier
import kr.toxicity.model.api.tracker.DummyTracker
import kr.toxicity.model.api.tracker.ModelScaler
import net.momirealms.craftengine.core.block.ImmutableBlockState
import net.momirealms.craftengine.core.block.entity.BlockEntity
import net.momirealms.craftengine.core.block.entity.BlockEntityController
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker
import net.momirealms.craftengine.core.world.BlockPos
import net.momirealms.craftengine.core.world.CEWorld
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.util.time.TickTime

class ReforgeTableBlockEntityController(blockEntity: BlockEntity): BlockEntityController(blockEntity) {

    enum class ReforgeTableAnimationState {
        IDLE,
        INTO_HAMMER,
        HAMMER,
        INTO_IDLE
    }

    var tracker: DummyTracker? = null
    var currState: ReforgeTableAnimationState = ReforgeTableAnimationState.IDLE
    var clock = 0
    var tableLocation: Location? = null
    var nearbyPlayers: Collection<Player> = listOf()

    init {

        val checkNearbyTask = object: BukkitRunnable() {
            override fun run() {
                if (tableLocation == null) {
                    if (blockEntity.world == null)
                        this.cancel()  // Failsafe, in case a crash ever happens and the entity isnt cleaned up properly.
                    val bukkitWorld = blockEntity.world.world.platformWorld() as World
                    tableLocation = Location(
                        bukkitWorld,
                        blockEntity.pos.x.toDouble(),
                        blockEntity.pos.y.toDouble(),
                        blockEntity.pos.z.toDouble()
                    )
                }
                if (!blockEntity.isValid) cancel()
                nearbyPlayers = tableLocation!!.getNearbyPlayers(2.5)
            }
        }
        checkNearbyTask.runTaskTimer(SMPRPG.plugin, TickTime.TICK * 4, TickTime.TICK)
    }

    fun enterIdle() {
        clock = 0
        currState = ReforgeTableAnimationState.INTO_IDLE
        val tracker = cacheTracker()
        tracker.animate("idle_into", AnimationModifier.DEFAULT_WITH_PLAY_ONCE) {
            tracker.location().task {
                tracker.animate("idle")
                clock = 0
                currState = ReforgeTableAnimationState.IDLE
            }
        }
    }

    fun enterHammer() {
        clock = 0
        currState = ReforgeTableAnimationState.INTO_HAMMER
        val tracker = cacheTracker()
        tracker.animate("hammer_into", AnimationModifier.DEFAULT_WITH_PLAY_ONCE) {
            tracker.location().task {
                tracker.animate("hammer")
                clock = 0
                currState = ReforgeTableAnimationState.HAMMER
            }
        }
    }

    fun cacheTracker() : DummyTracker {
        // Making a HUGE assumption here that the BetterModel element is first
        // If it isn't, this will fail spectacularly.
        if (tracker != null) return tracker!!

        val betterModelElement = blockEntity.world.getChunkAtIfLoaded(blockEntity.pos)?.getConstantBlockEntityRenderer(blockEntity.pos)?.elements?.first()
        val trackerField = betterModelElement!!::class.java.getDeclaredField("dummyTracker")
        trackerField.isAccessible = true
        tracker = trackerField.get(betterModelElement) as DummyTracker
        tracker!!.scaler(ModelScaler.value(1.015f))
        return tracker!!
    }

    override fun <C : BlockEntityController?> createAsyncBlockEntityTicker(level: CEWorld, state: ImmutableBlockState): BlockEntityTicker<C> {
        return createTickerHelper<C, ReforgeTableBlockEntityController>(BlockEntityTicker { ceWorld: CEWorld, blockPos: BlockPos, state: ImmutableBlockState, table: ReforgeTableBlockEntityController ->
            tick(
                ceWorld,
                blockPos,
                state,
                table
            )
        })
    }

    companion object {

        fun tick(ceWorld: CEWorld, blockPos: BlockPos, state: ImmutableBlockState, table: ReforgeTableBlockEntityController) {
            if (table.nearbyPlayers.isNotEmpty() && table.currState == ReforgeTableAnimationState.IDLE) table.enterHammer()
            else if (table.nearbyPlayers.isEmpty() && table.currState == ReforgeTableAnimationState.HAMMER) table.enterIdle()

            if (table.currState == ReforgeTableAnimationState.HAMMER && table.clock == 15) {
                Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable {
                    table.tableLocation!!.world.playSound(table.tableLocation!!, Sound.BLOCK_ANVIL_PLACE, 0.5f, 2f)
                    val particleLoc = table.tableLocation!!.clone()
                    particleLoc.x += 0.5
                    particleLoc.y += 0.85
                    particleLoc.z += 0.5
                    ParticleBuilder(Particle.CRIT)
                        .location(particleLoc)
                        .count(10)
                        .extra(0.0)
                        .offset(0.2, 0.0, 0.2)
                        .spawn()
                }, TickTime.INSTANTANEOUSLY)
                table.clock = -25
            }
            table.clock++
        }
    }
}