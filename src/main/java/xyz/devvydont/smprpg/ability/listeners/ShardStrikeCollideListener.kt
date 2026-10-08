package xyz.devvydont.smprpg.ability.listeners

import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.BlockType
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.ability.handlers.HotShotAbilityHandler
import xyz.devvydont.smprpg.ability.handlers.ShardStrikeAbilityHandler
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.time.TickTime

/**
 * When fireballs from Hot Shot collide with something, we need to override the damage.
 */
class ShardStrikeCollideListener : ToggleableListener() {
    /*
     * When a shard collides, deal falloff damage, then deal a bleed tick 1 second later.
     */
    @EventHandler
    @Suppress("unused")
    private fun onShardHit(event: ProjectileHitEvent) {
        // If this isn't an inferno projectile we don't care
        if (!ShardStrikeAbilityHandler.Companion.isShardProjectile(event.getEntity())) return

        var source: Player? = null
        if (event.entity is Projectile && (event.entity as Projectile).shooter is Player)
            source = (event.entity as Projectile).shooter as Player

        ParticleBuilder(Particle.BLOCK_CRUMBLE)
            .location(event.entity.location)
            .count(24)
            .data(BlockType.AMETHYST_BLOCK.createBlockData())
            .receivers(32, true)
            .spawn()

        for (living in event.entity.location.getNearbyLivingEntities(ShardStrikeAbilityHandler.Companion.SHATTER_RADIUS)) {
            // Players are immune to this.

            if (living is Player) continue

            val damage: Double = SMPRPG.getService(EntityDamageCalculatorService::class.java).getBaseProjectileDamage(event.entity as Projectile)

            if (source != null) {
                living.killer = source
                living.damage(
                    damage,
                    DamageSource.builder(DamageType.MAGIC)
                        .withDirectEntity(source)
                        .withCausingEntity(source)
                        .build()
                )
            }
            else {
                living.damage(
                    damage,
                    DamageSource.builder(DamageType.MAGIC).build()
                )
            }

            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                living.damage(
                    damage * 0.25,
                    DamageSource.builder(DamageType.MAGIC).build()
                )
                ParticleBuilder(Particle.BLOCK_CRUMBLE)
                    .location(living.location)
                    .count(8)
                    .data(BlockType.REDSTONE_BLOCK.createBlockData())
                    .receivers(32, true)
                    .spawn()
            }, TickTime.seconds(1))
        }
        event.getEntity().world.playSound(event.getEntity().location, Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1f, 1.25f)
        ShardStrikeAbilityHandler.Companion.removeShardProjectile(event.getEntity())
    }

    /**
     * More of a hack if anything. Disables the default damage that the hot shot explosion does. Our damage we calculate
     * is 100% manual and done through magic.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    fun onShardStrikeProjectileDamage(event: EntityDamageEvent) {
        if (event.damageSource.directEntity == null)
            return

        if (event.damageSource.directEntity!!.scoreboardTags.contains("shard_strike"))
            event.isCancelled = true
    }
}
