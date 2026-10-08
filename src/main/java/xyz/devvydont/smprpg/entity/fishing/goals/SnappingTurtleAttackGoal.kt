package xyz.devvydont.smprpg.entity.fishing.goals

import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import org.bukkit.NamespacedKey
import org.bukkit.entity.Pig
import org.bukkit.entity.Turtle
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.util.goals.GoalUtils
import java.util.*


class SnappingTurtleAttackGoal(val turtle : Turtle) : Goal<Turtle> {

    val goalKey : GoalKey<Turtle> = GoalKey.of(Turtle::class.java, NamespacedKey(SMPRPG.plugin, "snapping_turtle_attack"))
    var attackClock = 0

    override fun shouldActivate(): Boolean {
        if (GoalUtils.getClosestPlayer(turtle, 20.0, null) != null) {
            return true
        }
        else
            return false
    }

    override fun getKey(): GoalKey<Turtle> {
        return goalKey
    }

    override fun getTypes(): EnumSet<GoalType> {
        return EnumSet.of(GoalType.TARGET, GoalType.MOVE)
    }

    override fun shouldStayActive(): Boolean {
        return shouldActivate();
    }

    override fun start() {
        turtle.target = GoalUtils.chaseClosestPlayer(turtle, 20.0, 1.0, null)
    }

    override fun stop() {
        turtle.target = null
        turtle.pathfinder.stopPathfinding()
    }

    override fun tick() {
        var closestPlayer = GoalUtils.chaseClosestPlayer(turtle, 20.0, 5.0, null)
        turtle.lookAt(closestPlayer)
        if (closestPlayer in turtle.world.getNearbyPlayers(turtle.location, 0.75) && attackClock <= 0) {
            turtle.attack(closestPlayer)
            attackClock = 10
        }
        attackClock--
    }

}