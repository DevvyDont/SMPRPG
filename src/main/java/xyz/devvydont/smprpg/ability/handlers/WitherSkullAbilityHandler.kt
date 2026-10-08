package xyz.devvydont.smprpg.ability.handlers

import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.WitherSkull
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*

class WitherSkullAbilityHandler : AbilityHandler {

    override val cooldown: Long get() = TickTime.seconds(COOLDOWN)

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

        val projectile = ctx.caster.launchProjectile(
            WitherSkull::class.java,
            ctx.caster.location.getDirection().normalize().multiply(0.025f)
        )
        SMPRPG.getService(EntityDamageCalculatorService::class.java)
            .setBaseProjectileDamage(projectile, DAMAGE.toDouble())
        setSkullProjectile(projectile)
        ctx.caster.world.playSound(ctx.caster.location, Sound.ENTITY_WITHER_SHOOT, .4f, 1f)

        if (ctx.caster is Player && ctx.hand != null) ctx.caster.setCooldown(
            ctx.caster.equipment.getItem(ctx.hand), TickTime.seconds(
                COOLDOWN
            ).toInt()
        )

        return true
    }

    companion object {
        const val COOLDOWN: Long = 15
        const val DAMAGE: Int = 10000
        const val EXPLOSION_RADIUS: Double = 4.0
        const val FALLOFF_GRACE: Double = 1.5
        val EFFECT: PotionEffect = PotionEffect(PotionEffectType.WITHER, TickTime.seconds(5).toInt(), 1, true, true);

        // We need a reference to projectiles that we shoot so that we can handle them at different stages in its life
        // since PDCs do not work during the EntityExplodeEvent.
        private val projectiles: MutableMap<UUID, Entity> = HashMap<UUID, Entity>()

        fun isSkullProjectile(projectile: Entity): Boolean {
            return projectiles.containsKey(projectile.uniqueId)
        }

        fun setSkullProjectile(projectile: Entity) {
            projectiles.put(projectile.uniqueId, projectile)
            projectile.addScoreboardTag("wither_skull")
        }

        fun removeSkullProjectile(projectile: Entity) {
            projectiles.remove(projectile.uniqueId)
        }
    }
}
