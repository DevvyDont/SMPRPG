package xyz.devvydont.smprpg.entity.slayer.shambling.goals

import com.destroystokyo.paper.ParticleBuilder
import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.Equippable
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.slayer.shambling.ShamblingAbominationExpert
import xyz.devvydont.smprpg.entity.slayer.shambling.ShamblingAbominationIntermediate
import xyz.devvydont.smprpg.entity.slayer.shambling.ShamblingAbominationParent
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured
import xyz.devvydont.smprpg.services.AttributeService
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*
import kotlin.math.roundToInt

class ShamblingAbominationImplodeGoal(val slayer : ShamblingAbominationParent,
                                      val spawnPlayer : Player?,
                                      var timeBetweenImplosions : Int = TickTime.seconds(10).toInt()) : Goal<Zombie> {

    var implosionClock = timeBetweenImplosions
    val zombie = slayer.entity as Zombie
    var chaseGoal : ShamblingAbominationChaseGoal? = null
    var enrageCheckPassed = slayer is ShamblingAbominationExpert
    var clockMod : Int = 1

    override fun shouldActivate(): Boolean {
        return true
    }

    override fun getKey(): GoalKey<Zombie> {
        return GOAL_KEY
    }

    override fun getTypes(): EnumSet<GoalType> {
        return EnumSet.of(GoalType.UNKNOWN_BEHAVIOR)
    }

    override fun shouldStayActive(): Boolean {
        return true;  // This goal always runs on a cycle.
    }

    override fun start() {
    }

    override fun stop() {
    }

    override fun tick() {
        implosionClock -= clockMod

        // T5 Shambling Abomination will double implosion speed, T4 will not.
        if (!enrageCheckPassed) {
            val enrageGoal: ShamblingAbominationEnrageGoal = Bukkit.getMobGoals().getGoal(zombie, ShamblingAbominationEnrageGoal.GOAL_KEY) as ShamblingAbominationEnrageGoal
            if (enrageGoal.activated) {
                clockMod = 2
                timeBetweenImplosions /= clockMod
                implosionClock = timeBetweenImplosions
                enrageCheckPassed = true
            }
        }

        when (implosionClock) {
            50 -> {
                // Freeze our zombie in place
                val mobGoals = Bukkit.getMobGoals()
                chaseGoal = mobGoals.getGoal(zombie, ShamblingAbominationChaseGoal.GOAL_KEY) as ShamblingAbominationChaseGoal
                chaseGoal?.stop()
                warnExplosion(1.4f, 1.0)
            }
            40 -> warnExplosion(1.0f, 2.0)
            30 -> warnExplosion(1.6f, 3.0)
            20 -> warnExplosion(1.1f, 4.0)
            10 -> warnExplosion(1.8f, 5.0)
            8 -> zombie.world.playSound(zombie.location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.75f, 2f)
            6 -> zombie.world.playSound(zombie.location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.75f, 2f)
            4 -> zombie.world.playSound(zombie.location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.75f, 2f)
            2 -> zombie.world.playSound(zombie.location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.75f, 2f)
            0 -> {
                // Reset the cycle
                implosionClock = timeBetweenImplosions

                // Damage all participants in a 10 block radius
                val damage = 500.0  // This damage value actually doesn't matter. the damage is overridden during an event handler anyway.
                for (participant in slayer.activelyInvolvedPlayers) {
                    if (participant.location.distance(zombie.location) <= 10.0) {
                        participant.damage(damage, DamageSource.builder(DamageType.EXPLOSION).withCausingEntity(zombie).withDirectEntity(zombie).build())
                        participant.playSound(participant, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                    }
                }
                zombie.world.playSound(zombie.location, Sound.ENTITY_WITHER_SPAWN, 1f, 2f)
                ParticleBuilder(Particle.EXPLOSION)
                    .location(zombie.location)
                    .count(8)
                    .offset(.5, .2, .5)
                    .spawn()

                // Allow the Shambling Abomination to move again
                chaseGoal?.start()
            }
        }

    }

    fun warnExplosion(pitch : Float, radius : Double) {
        zombie.world.playSound(zombie.location, Sound.BLOCK_END_PORTAL_SPAWN, 0.75f, pitch)
        ParticleBuilder(Particle.EFFECT)
            .location(zombie.location)
            .count(512)
            .data(Particle.Spell(Color.WHITE, 1.0f))
            .extra(0.0)
            .offset(radius, 0.0, radius)
            .spawn()
        ParticleBuilder(Particle.EFFECT)
            .location(zombie.location)
            .count(512)
            .data(Particle.Spell(Color.GRAY, 1.0f))
            .extra(0.0)
            .offset(radius, 0.0, radius)
            .spawn()
        ParticleBuilder(Particle.EFFECT)
            .location(zombie.location)
            .count(512)
            .data(Particle.Spell(Color.RED, 0.1f))
            .extra(0.0)
            .offset(radius, 0.0, radius)
            .spawn()
    }

    companion object {
        val GOAL_KEY : GoalKey<Zombie> = GoalKey.of(Zombie::class.java, NamespacedKey(SMPRPG.Companion.plugin, "shambling_abomination_implode_goal"))
    }

}