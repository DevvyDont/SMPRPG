package xyz.devvydont.smprpg.entity.fishing.goals

import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Blaze
import org.bukkit.entity.SmallFireball
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.fishing.Imp
import xyz.devvydont.smprpg.util.goals.GoalUtils
import java.util.*


class ImpAttackGoal(val blaze : Blaze, val customEntity : Imp) : Goal<Blaze> {

    val goalKey : GoalKey<Blaze> = GoalKey.of(Blaze::class.java, NamespacedKey(SMPRPG.plugin, "imp_attack"))
    var attackClock = 100

    override fun shouldActivate(): Boolean {
        if (GoalUtils.getClosestPlayer(blaze, 20.0, null) != null) {
            return true
        }
        else
            return false
    }

    override fun getKey(): GoalKey<Blaze> {
        return goalKey
    }

    override fun getTypes(): EnumSet<GoalType> {
        return EnumSet.of(GoalType.UNKNOWN_BEHAVIOR)
    }

    override fun shouldStayActive(): Boolean {
        return shouldActivate();
    }

    override fun start() {
        blaze.target = GoalUtils.chaseClosestPlayer(blaze, 20.0, 1.0, null)
    }

    override fun stop() {
        blaze.target = null
        blaze.pathfinder.stopPathfinding()
    }

    override fun tick() {
        var closestPlayer = GoalUtils.getClosestPlayer(blaze, 30.0, null)
        if (closestPlayer != null)
            blaze.lookAt(closestPlayer)
        if (attackClock == 40) blaze.world.playSound(blaze.location, Sound.ENTITY_RAVAGER_CELEBRATE, 1.0f, 2.0f)

        if (closestPlayer in blaze.world.getNearbyPlayers(blaze.location, 20.0) && attackClock <= 10) {
            blaze.launchProjectile(SmallFireball::class.java, closestPlayer!!.eyeLocation.toVector().subtract(blaze.eyeLocation.toVector()).normalize().multiply(2))
            blaze.world.playSound(blaze.location, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f)
            blaze.world.playSound(blaze.location, Sound.BLOCK_LAVA_POP, 1.0f, 2.0f - ((10 - attackClock) / 10.0f))
            if (attackClock == 0) attackClock = 100
        }
        attackClock--
    }

}