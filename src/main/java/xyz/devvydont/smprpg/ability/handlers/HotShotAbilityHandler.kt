package xyz.devvydont.smprpg.ability.handlers

import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Fireball
import org.bukkit.entity.Player
import org.w3c.dom.Attr
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*

class HotShotAbilityHandler : AbilityHandler {

    override val cooldown : Long get() = COOLDOWN

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

        ctx.caster.world.playSound(ctx.caster.location, Sound.ENTITY_BLAZE_SHOOT, 1f, 1f)
        val projectile = ctx.caster.launchProjectile(
            Fireball::class.java,
            ctx.caster.location.getDirection().normalize().multiply(2)
        )
        var dmg = EntityDamageCalculatorService.getIntelligenceScaledDamage(DAMAGE.toDouble() + instance.getOrCreateAttribute(ctx.caster,
            AttributeWrapper.STRENGTH).value,
            instance.getOrCreateAttribute(ctx.caster, AttributeWrapper.INTELLIGENCE).value,
            ABILITY_SCALING + instance.getOrCreateAttribute(ctx.caster, AttributeWrapper.ARCANE_RATING).value)
        SMPRPG.getService(EntityDamageCalculatorService::class.java)
            .setBaseProjectileDamage(projectile, dmg)
        setInfernoProjectile(projectile)

        if (ctx.caster is Player && ctx.hand != null) ctx.caster.setCooldown(
            ctx.caster.equipment.getItem(ctx.hand), TickTime.seconds(
                COOLDOWN.toLong()
            ).toInt()
        )

        return true
    }

    companion object {
        val COOLDOWN: Long = TickTime.seconds(3)
        const val DAMAGE: Int = 10000
        const val EXPLOSION_RADIUS: Double = 5.0
        const val FALLOFF_GRACE = 2.0
        const val ABILITY_SCALING = 0.3

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
