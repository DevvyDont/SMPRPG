package xyz.devvydont.smprpg.ability.handlers

import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.Vibration
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.ability.listeners.PlayerFreezeService
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.services.AttributeService
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.util.time.TickTime

class SonicSmashAbilityHandler : AbilityHandler {

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

        ctx.caster.world.playSound(ctx.caster.location, Sound.ENTITY_WARDEN_SONIC_CHARGE, .4f, 0.8f)
        ctx.caster.world.playSound(ctx.caster.location, Sound.ENTITY_WARDEN_ROAR, .8f, 0.6f)
        SMPRPG.getService(PlayerFreezeService::class.java).addMovementWatch(ctx.caster.uniqueId)  // Freeze player in place, but let them look around

        // Charges flying out
        ParticleBuilder(Particle.SCULK_CHARGE)
            .location(ctx.caster.location)
            .count(128)
            .data(0.0f)
            .extra(0.0)
            .offset(.75, .0, .75)
            .spawn()

        // Roar effects
        ParticleBuilder(Particle.SHRIEK)
            .location(ctx.caster.eyeLocation)
            .count(8)
            .data(0)
            .offset(.5, .0, .5)
            .spawn()

        if (ctx.caster is Player && ctx.hand != null) ctx.caster.setCooldown(
            ctx.caster.equipment.getItem(ctx.hand), TickTime.seconds(
                COOLDOWN.toLong()
            ).toInt()
        )

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            blastAway(ctx)
        }, TickTime.seconds(2))

        return true
    }

    private fun blastAway(ctx: AbilityContext) {
        ParticleBuilder(Particle.EXPLOSION)
            .location(ctx.caster.location)
            .count(3)
            .offset(.5, .2, .5)
            .spawn()

        ctx.caster.world.playSound(ctx.caster.location, Sound.ENTITY_GENERIC_EXPLODE, .8f, 0.8f)
        SMPRPG.getService(PlayerFreezeService::class.java).removeMovementWatch(ctx.caster.uniqueId)  // Unfreeze our caster
        ctx.caster.velocity = Vector(0.0, 1.2, 0.0)

        var dmg = EntityDamageCalculatorService.getIntelligenceScaledDamage(
            ShardStrikeAbilityHandler.Companion.DAMAGE.toDouble() + instance.getOrCreateAttribute(ctx.caster,
                AttributeWrapper.STRENGTH).value,
            instance.getOrCreateAttribute(ctx.caster, AttributeWrapper.INTELLIGENCE).value,
            ShardStrikeAbilityHandler.Companion.ABILITY_SCALING + instance.getOrCreateAttribute(ctx.caster, AttributeWrapper.ARCANE_RATING).value)
        for (living in ctx.caster.location.getNearbyLivingEntities(EXPLOSION_RADIUS)) {
            if (living is Player) continue  // Don't damage players

            living.killer = ctx.caster as Player

            living.damage(
                dmg,
                DamageSource.builder(DamageType.MAGIC)
                    .withDirectEntity(ctx.caster)
                    .withCausingEntity(ctx.caster)
                    .withDamageLocation(ctx.caster.location).build()
            )

            ctx.caster.world.playSound(ctx.caster.location, Sound.ENTITY_WARDEN_SONIC_BOOM, 1f, 1f)
            ParticleBuilder(Particle.VIBRATION)
                .location(ctx.caster.location)
                .count(1)
                .data(Vibration(Vibration.Destination.EntityDestination(living), 40))
                .receivers(32, true)
                .spawn()
        }
    }

    companion object {
        const val DAMAGE: Int = 10000
        const val EXPLOSION_RADIUS: Double = 10.0
        const val COOLDOWN: Long = 10
        const val ABILITY_SCALING: Double = 0.4
    }
}
