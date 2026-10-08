package xyz.devvydont.smprpg.ability.handlers

import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import net.momirealms.craftengine.bukkit.world.BukkitExistingBlock
import net.momirealms.craftengine.core.util.Key
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.particles.ParticleUtil
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.function.Predicate

class SpreadShotAbilityHandler : AbilityHandler {

    override val cooldown: Long get() = COOLDOWN

    /**
     * Attempts to execute the ability.
     *
     * @param ctx The context of the ability.
     * @return True if the ability succeeded and should have cost reduced, false otherwise.
     */
    override fun execute(ctx: AbilityContext): Boolean {

        val player = ctx.caster as Player
        val playerVec = player.location.direction
        val vectors = arrayOf(
            playerVec.clone().rotateAroundY(-SPREAD).rotateAroundX(-SPREAD), // Top Left
            playerVec.clone().rotateAroundX(-SPREAD),                      // Top Middle
            playerVec.clone().rotateAroundY(SPREAD).rotateAroundX(-SPREAD),  // Top Right
            playerVec.clone().rotateAroundY(-SPREAD),                      // Middle Left
            playerVec.clone(),                                           // Middle
            playerVec.clone().rotateAroundY(SPREAD),                       // Middle Right
            playerVec.clone().rotateAroundY(-SPREAD).rotateAroundX(SPREAD),  // Bottom Left
            playerVec.clone().rotateAroundX(SPREAD),                       // Bottom Middle
            playerVec.clone().rotateAroundY(SPREAD).rotateAroundX(SPREAD),   // Bottom Right
        )
        val world = player.world
        val playerMidpoint = player.location.toVector().midpoint(player.eyeLocation.toVector()).toLocation(world)
        val filter = Predicate<Entity> { entity -> entity is LivingEntity && entity != player }

        for (vec in vectors) {
            val result = world.rayTraceEntities(playerMidpoint,
                vec,
                RANGE,
                COLLISION_RADIUS,
                filter)
            if (result != null) {
                val entity = result.hitEntity
                if (entity is LivingEntity) {
                    val dmg = EntityDamageCalculatorService.getIntelligenceScaledDamage(
                        DAMAGE.toDouble() + instance.getOrCreateAttribute(ctx.caster,
                            AttributeWrapper.STRENGTH).value,
                        instance.getOrCreateAttribute(ctx.caster, AttributeWrapper.INTELLIGENCE).value,
                        ABILITY_SCALING + instance.getOrCreateAttribute(ctx.caster, AttributeWrapper.ARCANE_RATING).value)
                    // All beams fire in the same tick, so clear i-frames before each hit. Otherwise the first beam's
                    // invulnerability window causes EntityDamageCalculatorService to cancel every subsequent beam,
                    // collapsing the spread into a single registered hit.
                    entity.noDamageTicks = 0
                    entity.damage(dmg, DamageSource.builder(DamageType.MAGIC).withDirectEntity(player).withCausingEntity(player).build())
                }
            }
            val endpoint = playerMidpoint.toVector().add(vec.clone().multiply(RANGE))
            ParticleUtil.spawnParticlesBetweenTwoPoints(Particle.ENCHANTED_HIT, player.world, playerMidpoint.toVector(), endpoint, 25)
            ParticleUtil.spawnParticlesBetweenTwoPoints(Particle.CRIT, player.world, playerMidpoint.toVector(), endpoint, 25)
        }
        player.velocity = player.velocity.clone().add(player.location.direction.multiply(-1.0))
        world.playSound(player.location, Sound.ENTITY_ALLAY_HURT, 1f, 0.5f)
        world.playSound(player.location, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 2f)
        return true
    }

    companion object {
        val COOLDOWN: Long = TickTime.seconds(2)
        val RANGE: Double = 10.0
        val COLLISION_RADIUS: Double = 0.25
        val DAMAGE = 1000
        val SPREAD = Math.toRadians(10.0)
        const val ABILITY_SCALING = 0.1
        const val RADIUS: Int = 2
    }
}
