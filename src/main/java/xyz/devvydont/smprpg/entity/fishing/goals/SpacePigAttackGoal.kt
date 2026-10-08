package xyz.devvydont.smprpg.entity.fishing.goals

import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import org.bukkit.NamespacedKey
import org.bukkit.entity.Pig
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.util.goals.GoalUtils
import java.util.*


class SpacePigAttackGoal(val pig : Pig) : Goal<Pig> {

    val goalKey : GoalKey<Pig> = GoalKey.of(Pig::class.java, NamespacedKey(SMPRPG.plugin, "space_pig_attack"))
    var attackClock = 0

    override fun shouldActivate(): Boolean {
        if (GoalUtils.getClosestPlayer(pig, 20.0, null) != null) {
            return true
        }
        else
            return false
    }

    override fun getKey(): GoalKey<Pig> {
        return goalKey
    }

    override fun getTypes(): EnumSet<GoalType> {
        return EnumSet.of(GoalType.TARGET, GoalType.MOVE)
    }

    override fun shouldStayActive(): Boolean {
        return shouldActivate();
    }

    override fun start() {
        pig.target = GoalUtils.chaseClosestPlayer(pig, 20.0, 1.0, null)
    }

    override fun stop() {
        pig.target = null
        pig.pathfinder.stopPathfinding()
    }

    override fun tick() {
        var closestPlayer = GoalUtils.chaseClosestPlayer(pig, 20.0, 1.0, null)
        pig.lookAt(closestPlayer)
        if (closestPlayer in pig.world.getNearbyPlayers(pig.location, 0.75) && attackClock <= 0) {
            pig.attack(closestPlayer)
            attackClock = 10
        }
        attackClock--
    }

}