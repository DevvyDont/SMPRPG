package xyz.devvydont.smprpg.ability.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import xyz.devvydont.smprpg.ability.handlers.DamageAuraAbilityHandler
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.world.LocationUtil

/**
 * When fireballs from Hot Shot collide with something, we need to override the damage.
 */
class DamageAuraCollideListener : ToggleableListener() {
    /*
     * When a aura projectile collides, spawn a damage aura, then cleanup
     */
    @EventHandler
    @Suppress("unused")
    private fun onAuraProjectileHit(event: ProjectileHitEvent) {
        // If this isn't an inferno projectile we don't care
        if (!DamageAuraAbilityHandler.Companion.isAuraProjectile(event.getEntity())) return

        var player: Player? = null
        if (event.entity.shooter is Player)
            player = event.entity.shooter as Player

        if (player != null) {
            val loc = LocationUtil.snapLocationToFloor(event.entity.location)
            loc.yaw = 0f
            loc.pitch = 0f
            DamageAuraAbilityHandler.DamageAuraTask(loc, player)
            DamageAuraAbilityHandler.Companion.removeAuraProjectile(event.getEntity())
        }
    }
}
