package xyz.devvydont.smprpg.ability.handlers

import com.destroystokyo.paper.ParticleBuilder
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.bukkit.api.CraftEngineItems
import net.momirealms.craftengine.bukkit.entity.projectile.ProjectileItems
import net.momirealms.craftengine.core.item.ItemBuildContext
import net.momirealms.craftengine.core.plugin.CraftEngine
import net.momirealms.craftengine.core.util.Key
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.Snowball
import org.bukkit.projectiles.ProjectileSource
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.listeners.damage.UnderwaterArrowListener.Companion.GRAVITY_VECTOR
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*

class FireballAbilityHandler : AbilityHandler {

    override val cooldown: Long get() = COOLDOWN
    val tracker : BukkitRunnable

    init {
        tracker = object : BukkitRunnable() {
            override fun run() {
                // Heartbeart any tracked projectiles

                // Create a clone to avoid concurrent modification
                val projectileClone = projectiles.toMutableMap()
                for (pair in projectileClone) {
                    val projectile = pair.value
                    if (projectile.isUnderWater && projectile.isValid) {
                        removeFireballProjectile(projectile)
                        ParticleBuilder(Particle.BUBBLE)
                            .location(projectile.location)
                            .count(24)
                            .receivers(32, true)
                            .extra(0.25)
                            .spawn()
                        projectile.world.playSound(projectile.location, Sound.BLOCK_LAVA_POP, 1f, 2f)
                        projectile.remove()
                    }
                }
            }
        }

        tracker.runTaskTimer(SMPRPG.plugin, TickTime.INSTANTANEOUSLY, 5)
    }

    /**
     * Attempts to execute the ability.
     *
     * @param ctx The context of the ability.
     * @return True if the ability succeeded and should have cost reduced, false otherwise.
     */
    override fun execute(ctx: AbilityContext): Boolean {
        if (ctx.caster is Player && ctx.hand != null) if (ctx.caster.hasCooldown(
                ctx.caster.equipment.getItem(ctx.hand)
            )
        ) return false

        val projectileItem = CraftEngineItems.byId(Key.of("smprpg:fireball_projectile"))!!.buildItem(ItemBuildContext.empty())
        val projectile = ProjectileItems.createProjectileByItem(ctx.caster.eyeLocation, projectileItem, ctx.caster, false)


        val dmg = EntityDamageCalculatorService.getIntelligenceScaledDamage(
            DAMAGE.toDouble() + instance.getOrCreateAttribute(ctx.caster,
            AttributeWrapper.STRENGTH).value,
            instance.getOrCreateAttribute(ctx.caster, AttributeWrapper.INTELLIGENCE).value,
            ABILITY_SCALING + instance.getOrCreateAttribute(ctx.caster, AttributeWrapper.ARCANE_RATING).value)
        projectile.setGravity(false)
        projectile.location.direction = ctx.caster.eyeLocation.getDirection()
        projectile.velocity = ctx.caster.eyeLocation.getDirection().normalize().multiply(2)
        SMPRPG.getService(EntityDamageCalculatorService::class.java).setBaseProjectileDamage(projectile, dmg)
        setFireballProjectile(projectile)

        Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable {
            // Force kill the projectile KILL_TIME ticks later, to prevent lingering projectiles.
            if (projectile.isValid) {
                removeFireballProjectile(projectile)
                projectile.remove()
            }
        }, KILL_TIME)

        if (ctx.caster is Player && ctx.hand != null) ctx.caster.setCooldown(
            ctx.caster.equipment.getItem(ctx.hand), cooldown.toInt()
        )

        return true
    }

    companion object {
        const val COOLDOWN: Long = 5
        const val DAMAGE: Int = 200
        const val ENGULF_RADIUS: Double = 2.0
        const val ABILITY_SCALING = 0.1
        val KILL_TIME: Long = TickTime.seconds(60)

        // We need a reference to projectiles that we shoot so that we can handle them at different stages in its life
        // since PDCs do not work during the EntityExplodeEvent.
        private val projectiles: MutableMap<UUID, Projectile> = HashMap<UUID, Projectile>()

        fun isFireballProjectile(projectile: Projectile): Boolean {
            return projectiles.containsKey(projectile.uniqueId)
        }

        fun setFireballProjectile(projectile: Projectile) {
            projectiles.put(projectile.uniqueId, projectile)
            projectile.addScoreboardTag("fireball")
        }

        fun removeFireballProjectile(projectile: Projectile) {
            projectiles.remove(projectile.uniqueId)
        }
    }
}
