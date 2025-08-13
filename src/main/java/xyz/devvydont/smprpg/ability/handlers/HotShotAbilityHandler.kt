package xyz.devvydont.smprpg.ability.handlers

import org.bukkit.entity.Entity
import org.bukkit.entity.Fireball
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*

class HotShotAbilityHandler : AbilityHandler {
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
            Fireball::class.java,
            ctx.caster.location.getDirection().normalize().multiply(2)
        )
        SMPRPG.getService(EntityDamageCalculatorService::class.java)
            .setBaseProjectileDamage(projectile, DAMAGE.toDouble())
        setInfernoProjectile(projectile)

        if (ctx.caster is Player && ctx.hand != null) ctx.caster.setCooldown(
            ctx.caster.equipment.getItem(ctx.hand), TickTime.seconds(
                COOLDOWN.toLong()
            ).toInt()
        )

        return true
    }

    companion object {
        const val COOLDOWN: Int = 3
        const val DAMAGE: Int = 15000
        const val EXPLOSION_RADIUS: Double = 5.0

        // We need a reference to projectiles that we shoot so that we can handle them at different stages in its life
        // since PDCs do not work during the EntityExplodeEvent.
        private val projectiles: MutableMap<UUID, Entity> = HashMap<UUID, Entity>()

        fun isInfernoProjectile(projectile: Entity): Boolean {
            return projectiles.containsKey(projectile.uniqueId)
        }

        fun setInfernoProjectile(projectile: Entity) {
            projectiles.put(projectile.uniqueId, projectile)
            projectile.addScoreboardTag("hotshot")
        }

        fun removeInfernoProjectile(projectile: Entity) {
            projectiles.remove(projectile.uniqueId)
        }
    }
}
