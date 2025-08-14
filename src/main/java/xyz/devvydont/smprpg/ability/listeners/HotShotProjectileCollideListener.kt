package xyz.devvydont.smprpg.ability.listeners

import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import xyz.devvydont.smprpg.ability.handlers.HotShotAbilityHandler
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * When fireballs from Hot Shot collide with something, we need to override the damage.
 */
class HotShotProjectileCollideListener : ToggleableListener() {
    /*
     * When the fireball explodes, we need to ensure that it does not damage terrain and also damage nearby entities
     * at a penalty (for missing)
     */
    @EventHandler
    @Suppress("unused")
    private fun onFireballExplode(event: EntityExplodeEvent) {
        // If this isn't an inferno projectile we don't care
        if (!HotShotAbilityHandler.Companion.isInfernoProjectile(event.getEntity())) return

        var source: Player? = null
        if (event.entity is Projectile && (event.entity as Projectile).shooter is Player)
            source = (event.entity as Projectile).shooter as Player

        // Cancel the actual explosion part and create a safe one that doesn't break things.
        event.isCancelled = true
        event.blockList().clear()
        for (living in event.location.getNearbyLivingEntities(HotShotAbilityHandler.Companion.EXPLOSION_RADIUS)) {
            // Players are immune to this.

            if (living is Player) continue

            var falloff: Double =
                HotShotAbilityHandler.Companion.EXPLOSION_RADIUS - event.location.distance(living.location)
            falloff /= HotShotAbilityHandler.Companion.EXPLOSION_RADIUS
            val damage: Double = HotShotAbilityHandler.Companion.DAMAGE * falloff

            if (falloff < 0) continue

            if (source != null) living.killer = source

            living.damage(
                damage,
                DamageSource.builder(DamageType.MAGIC).build()
            )
        }
        ParticleBuilder(Particle.EXPLOSION)
            .location(event.location)
            .count(3)
            .offset(.5, .2, .5)
            .spawn()
        event.getEntity().world.playSound(event.getEntity().location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.25f)
        HotShotAbilityHandler.Companion.removeInfernoProjectile(event.getEntity())
        event.isCancelled = true
    }

    /**
     * More of a hack if anything. Disables the default damage that the hot shot explosion does. Our damage we calculate
     * is 100% manual and done through magic.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    fun onHotShotProjectileDamage(event: EntityDamageEvent) {
        if (event.damageSource.directEntity == null)
            return

        if (event.damageSource.directEntity!!.scoreboardTags.contains("hotshot"))
            event.isCancelled = true
    }
}
