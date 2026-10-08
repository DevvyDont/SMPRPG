package xyz.devvydont.smprpg.entity.slayer.shambling.goals

import com.destroystokyo.paper.ParticleBuilder
import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.Equippable
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
import xyz.devvydont.smprpg.entity.slayer.shambling.ShamblingAbominationIntermediate
import xyz.devvydont.smprpg.entity.slayer.shambling.ShamblingAbominationParent
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured
import xyz.devvydont.smprpg.services.AttributeService
import xyz.devvydont.smprpg.util.particles.ParticleUtil
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*

class ShamblingAbominationSyphonGoal(val slayer : ShamblingAbominationParent, val spawnPlayer : Player?, val syphonPercentage : Double) : Goal<Zombie> {

    val zombie = slayer.entity as Zombie
    val healRate = TickTime.seconds(3)
    var syphonClock = healRate

    override fun shouldActivate(): Boolean {
        return true
    }

    override fun getKey(): GoalKey<Zombie> {
        return GOAL_KEY
    }

    override fun getTypes(): EnumSet<GoalType> {
        return EnumSet.of(GoalType.LOOK)  // Dirty hack, paper apparently has a limit on how many unknown behavior goals can run at once.
    }

    override fun shouldStayActive(): Boolean {
        return true
    }

    override fun start() {
    }

    override fun stop() {
    }

    override fun tick() {
        syphonClock--
        if (syphonClock <= 0) {
            syphonClock = healRate
            for (participant in slayer.activelyInvolvedPlayers) {
                val maxHp = participant.getAttribute(Attribute.MAX_HEALTH)
                val damage = maxHp!!.value * syphonPercentage
                participant.damage(damage)
                zombie.heal(damage * 1000.0)
                participant.playSound(participant.location, Sound.ENTITY_GENERIC_DRINK, 0.75f, 0.5f)
                ParticleUtil.spawnParticlesBetweenTwoPoints(SYPHON_PARTICLE,
                    participant.world,
                    participant.location.add(participant.eyeLocation).multiply(0.5).toVector(),
                    zombie.location.add(zombie.eyeLocation).multiply(0.5).toVector(),
                    10)
            }
        }
    }

    companion object {
        val GOAL_KEY : GoalKey<Zombie> = GoalKey.of(Zombie::class.java, NamespacedKey(SMPRPG.Companion.plugin, "shambling_abomination_syphon_goal"))
        val SYPHON_PARTICLE : Particle = Particle.HEART.builder()
            .extra(0.0)
            .count(3)
            .offset(0.05, 0.05, 0.05)
            .particle()
    }

}