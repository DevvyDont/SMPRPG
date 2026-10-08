package xyz.devvydont.smprpg.ability.handlers

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.WindCharge
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.util.time.TickTime

class WindStormAbilityHandler : AbilityHandler {

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

        ctx.caster.world.playSound(ctx.caster.location, Sound.ENTITY_BREEZE_DEATH, 1f, 0.5f)
        val baseVec = Vector(2, 0, 2)
        for (i in 0..40) {
            val angledVec =  baseVec.clone().rotateAroundY(9.0 * i)
            val projectile = ctx.caster.launchProjectile(
                WindCharge::class.java,
                angledVec
            )
        }

        if (ctx.caster is Player && ctx.hand != null) ctx.caster.setCooldown(
            ctx.caster.equipment.getItem(ctx.hand), TickTime.seconds(
                COOLDOWN
            ).toInt()
        )

        return true
    }

    companion object {
        const val COOLDOWN: Long = 3
        const val DAMAGE: Int = 10000
    }
}
