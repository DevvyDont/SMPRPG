package xyz.devvydont.smprpg.ability.handlers

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.craftbukkit.entity.CraftEvokerFangs
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.EvokerFangs
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.slayer.shambling.goals.ShamblingAbominationSyphonGoal.Companion.SYPHON_PARTICLE
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.util.particles.ParticleUtil
import xyz.devvydont.smprpg.util.time.TickTime
import xyz.devvydont.smprpg.util.world.LocationUtil
import java.util.function.Predicate
import kotlin.math.cos
import kotlin.math.sin

class FangStrikeAbilityHandler : AbilityHandler {

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

        val yaw = ctx.caster.yaw
        val normalizedDir = Vector(
            -sin(Math.toRadians(yaw.toDouble())),
            0.0,
            cos(Math.toRadians(yaw.toDouble()))
        ).normalize()
        var delay = 0
        var fangSpawnLoc = ctx.caster.location.clone()
        fangSpawnLoc = LocationUtil.snapLocationToFloor(fangSpawnLoc)
        fangSpawnLoc = fangSpawnLoc.add(normalizedDir)
        for (i in 0..<NUM_FANGS) {
            fangSpawnLoc = fangSpawnLoc.add(normalizedDir)
            spawnFang(fangSpawnLoc, ctx.caster, delay)
            delay += 1
        }

        return true
    }

    fun spawnFang(location : Location, caster: LivingEntity, delay: Int) : EvokerFangs {
        val fang: EvokerFangs = caster.world.spawnEntity(
            location,
            EntityType.EVOKER_FANGS,
            CreatureSpawnEvent.SpawnReason.CUSTOM
        ) as EvokerFangs
        fang.owner = caster
        fang.attackDelay = delay
        return fang
    }

    companion object {
        const val DAMAGE = 10000
        const val ABILITY_SCALING = 0.2
        const val NUM_FANGS = 5
        val COOLDOWN = TickTime.seconds(3)
    }
}
