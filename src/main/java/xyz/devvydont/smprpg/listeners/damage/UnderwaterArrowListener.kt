package xyz.devvydont.smprpg.listeners.damage

import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.interfaces.IUnderwaterBow
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.time.TickTime
import kotlin.math.abs

/**
 * Main purpose for this listener is to permit certain bows/projectiles to
 * move without impediment underwater. Neptune Shortbow will fire arrows
 * that won't be slowed down in water this way.
 */

class UnderwaterArrowListener : ToggleableListener() {

    val trackedProjectiles : MutableMap<Projectile, Vector> = mutableMapOf()
    var tracker : BukkitRunnable? = null

    init {

        /**
         * The purpose of this tracker is to take control of arrow speed manually. We approximate values from
         * NMS to get a best fit for how the arrow should behave underwater.
         */
        tracker = object : BukkitRunnable() {
            override fun run() {

                // Heartbeart any tracked projectiles

                // Clone our trackedProjectiles map to avoid concurrent modification exceptions
                val projectileIterMap = trackedProjectiles.toMutableMap()
                for (projectilePair in projectileIterMap) {
                    val projectile = projectilePair.key
                    if (projectile.isDead) {
                        trackedProjectiles.remove(projectile)
                        continue
                    }

                    if (projectile.isInWater) {
                        val velocity: Vector = projectilePair.value.multiply(0.99f)
                        if (projectile.hasGravity()) {
                            velocity.add(GRAVITY_VECTOR)
                        }

                        if (projectile.isOnGround) {
                            velocity.zero()
                            trackedProjectiles.remove(projectile)
                        }

                        projectilePair.setValue(velocity)
                        projectile.velocity = velocity
                    }
                }
            }
        }

        tracker!!.runTaskTimer(SMPRPG.plugin, TickTime.INSTANTANEOUSLY, TickTime.TICK)
    }

    @EventHandler
    fun onShootArrow(event : EntityShootBowEvent) {
        if (event.bow == null) return

        val bowBp = ItemService.blueprint(event.bow!!)
        if (bowBp is IUnderwaterBow)
            this.trackedProjectiles.put(event.projectile as Projectile, event.projectile.velocity)
    }

    companion object {
        val GRAVITY_VECTOR : Vector = Vector(0.0, -0.05, 0.0)
    }
}