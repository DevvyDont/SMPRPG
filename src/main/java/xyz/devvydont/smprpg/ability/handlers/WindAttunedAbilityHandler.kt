package xyz.devvydont.smprpg.ability.handlers

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.WindCharge
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService

class WindAttunedAbilityHandler : AbilityHandler {
    override val cooldown: Long get() = COOLDOWN

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
            WindCharge::class.java,
            ctx.caster.location.getDirection().normalize().multiply(2)
        )
        var dmg = EntityDamageCalculatorService.getIntelligenceScaledDamage(
            DAMAGE.toDouble() + instance.getOrCreateAttribute(ctx.caster,
            AttributeWrapper.STRENGTH).value,
            instance.getOrCreateAttribute(ctx.caster, AttributeWrapper.INTELLIGENCE).value,
            ABILITY_SCALING + instance.getOrCreateAttribute(ctx.caster, AttributeWrapper.ARCANE_RATING).value)
        SMPRPG.getService(EntityDamageCalculatorService::class.java)
            .setBaseProjectileDamage(projectile, dmg)
        ctx.caster.getWorld().playSound(ctx.caster.location, Sound.ENTITY_BREEZE_DEATH, 1f, 0.5f)

        if (ctx.caster is Player && ctx.hand != null) ctx.caster.setCooldown(
            ctx.caster.equipment.getItem(ctx.hand), COOLDOWN.toInt()
        )

        return true
    }

    companion object {
        const val COOLDOWN: Long = 10
        const val DAMAGE: Int = 5
        const val ABILITY_SCALING = 0.05
    }
}
