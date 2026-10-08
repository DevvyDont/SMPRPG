package xyz.devvydont.smprpg.ability.listeners

import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Particle
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.ability.handlers.FireballAbilityHandler
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.time.TickTime

/**
 * When fireballs from Hot Shot collide with something, we need to override the damage.
 */
class FireballCollideListener : ToggleableListener() {
    /*
     * When a fireball collides, deal falloff damage, then ignite the enemy.
     */
    @EventHandler
    @Suppress("unused")
    private fun onFireballHit(event: ProjectileHitEvent) {
        // If this isn't an inferno projectile we don't care
        if (!FireballAbilityHandler.Companion.isFireballProjectile(event.getEntity())) return

        var source: Player? = null
        if (event.entity.shooter is Player)
            source = event.entity.shooter as Player

        ParticleBuilder(Particle.FLAME)
            .location(event.entity.location)
            .count(24)
            .receivers(32, true)
            .extra(0.05)
            .spawn()

        for (living in event.entity.location.getNearbyLivingEntities(FireballAbilityHandler.Companion.ENGULF_RADIUS)) {
            // Players are immune to this.

            if (living is Player) continue

            val damage: Double = SMPRPG.getService(EntityDamageCalculatorService::class.java).getBaseProjectileDamage(event.entity)

            if (source != null) {
                living.killer = source
                living.damage(
                    damage,
                    DamageSource.builder(DamageType.MAGIC).withCausingEntity(source).withDirectEntity(source).build()
                )
            }
            else living.damage(damage, DamageSource.builder(DamageType.MAGIC).build())

            living.fireTicks = TickTime.seconds(8).toInt()
        }
        FireballAbilityHandler.Companion.removeFireballProjectile(event.getEntity())
    }

    /**
     * More of a hack if anything. Disables the default damage that the hot shot explosion does. Our damage we calculate
     * is 100% manual and done through magic.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    fun onFireballProjectileDamage(event: EntityDamageEvent) {
        if (event.damageSource.directEntity == null)
            return

        if (event.damageSource.directEntity!!.scoreboardTags.contains("fireball"))
            event.isCancelled = true
    }
}
