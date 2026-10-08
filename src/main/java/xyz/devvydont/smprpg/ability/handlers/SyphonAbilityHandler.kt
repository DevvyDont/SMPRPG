package xyz.devvydont.smprpg.ability.handlers

import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.slayer.shambling.goals.ShamblingAbominationSyphonGoal.Companion.SYPHON_PARTICLE
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.util.particles.ParticleUtil
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.function.Predicate

class SyphonAbilityHandler : AbilityHandler {

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

        val result = ctx.caster.eyeLocation.world.rayTraceEntities(
            ctx.caster.eyeLocation,
            ctx.caster.eyeLocation.direction,
            Bukkit.getViewDistance() * DISTANCE_MULT,
            PLAYER_FILTER)

        val source = ctx.caster as Player
        val dmg = EntityDamageCalculatorService.getIntelligenceScaledDamage(
            DAMAGE.toDouble() + instance.getOrCreateAttribute(ctx.caster,
                AttributeWrapper.STRENGTH).value,
            instance.getOrCreateAttribute(ctx.caster, AttributeWrapper.INTELLIGENCE).value,
            ABILITY_SCALING + instance.getOrCreateAttribute(ctx.caster, AttributeWrapper.ARCANE_RATING).value)
        if (result != null) {
            val target = result.hitEntity as LivingEntity

            target.killer = source
            target.damage(
                dmg,
                DamageSource.builder(DamageType.MAGIC).withCausingEntity(source).withDirectEntity(source).build()
            )

            source.heal(dmg / 10.0)
            source.world.playSound(source.location, Sound.ENTITY_GENERIC_DRINK, 0.75f, 0.5f)

            ParticleUtil.spawnParticlesBetweenTwoPoints(SYPHON_PARTICLE,
                target.world,
                target.location.add(target.eyeLocation).multiply(0.5).toVector(),
                source.location.add(source.eyeLocation).multiply(0.5).toVector(),
                10)
        }
        else {
            source.killer = source
            source.damage(
                dmg / 5.0,
                DamageSource.builder(DamageType.MAGIC).withCausingEntity(source).withDirectEntity(source).build()
            )
            ParticleUtil.spawnParticlesBetweenTwoPoints(SYPHON_PARTICLE,
                source.world,
                source.location.add(source.eyeLocation).multiply(0.5).toVector(),
                source.location.add(source.eyeLocation).multiply(0.5).toVector(),
                10)
            source.world.playSound(source.location, Sound.ENTITY_BABY_HORSE_DEATH, 1f, 1f)
        }
        return true
    }

    companion object {
        const val DAMAGE = 250
        const val ABILITY_SCALING = 0.1
        const val DISTANCE_MULT = 4.0
        val COOLDOWN = TickTime.seconds(10)
        val PLAYER_FILTER = Predicate<Entity> { entity -> entity is LivingEntity && entity.type != EntityType.PLAYER }

        val SYPHON_PARTICLE : Particle = Particle.HEART.builder()
            .extra(0.0)
            .count(3)
            .offset(0.05, 0.05, 0.05)
            .particle()
        val FAILED_SYPHON_PARTICLE : Particle = Particle.ANGRY_VILLAGER.builder()
            .extra(0.0)
            .count(3)
            .offset(0.05, 0.05, 0.05)
            .particle()
    }
}
