package xyz.devvydont.smprpg.entity.fishing.goals

import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import org.bukkit.NamespacedKey
import org.bukkit.entity.Pig
import org.bukkit.entity.PolarBear
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.util.goals.GoalUtils
import java.util.*


class SeaBearAttackGoal(val polarBear : PolarBear) : Goal<PolarBear> {

    val goalKey : GoalKey<PolarBear> = GoalKey.of(PolarBear::class.java, NamespacedKey(SMPRPG.plugin, "sea_bear_attack"))
    var attackClock = 0
    var standClock = 0

    override fun shouldActivate(): Boolean {
        if (GoalUtils.getClosestPlayer(polarBear, 20.0, null) != null) {
            return true
        }
        else
            return false
    }

    override fun getKey(): GoalKey<PolarBear> {
        return goalKey
    }

    override fun getTypes(): EnumSet<GoalType> {
        return EnumSet.of(GoalType.TARGET, GoalType.MOVE)
    }

    override fun shouldStayActive(): Boolean {
        return shouldActivate();
    }

    override fun start() {
        polarBear.target = GoalUtils.chaseClosestPlayer(polarBear, 20.0, 1.0, null)
    }

    override fun stop() {
        polarBear.target = null
        polarBear.pathfinder.stopPathfinding()
    }

    override fun tick() {
        var closestPlayer = GoalUtils.chaseClosestPlayer(polarBear, 20.0, 1.0, null)
        polarBear.lookAt(closestPlayer)
        if (closestPlayer in polarBear.world.getNearbyPlayers(polarBear.location, 0.75) && attackClock <= 0) {
            polarBear.attack(closestPlayer)
            polarBear.isStanding = true
            val upVel = closestPlayer.velocity.clone()
            upVel.y += 0.25
            closestPlayer.velocity = upVel
            attackClock = 10
            standClock = 10
        }

        if (standClock <= 0)
            polarBear.isStanding = false
        standClock--
        attackClock--
    }

}