package xyz.devvydont.smprpg.entity.fishing.goals

import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import kr.toxicity.model.api.animation.AnimationModifier
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Axolotl
import org.bukkit.entity.Pig
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.fishing.Shark
import xyz.devvydont.smprpg.util.goals.GoalUtils
import java.util.*


class SharkAttackGoal(val axolotl : Axolotl, val customEntity : Shark) : Goal<Axolotl> {

    val goalKey : GoalKey<Axolotl> = GoalKey.of(Axolotl::class.java, NamespacedKey(SMPRPG.plugin, "shark_attack"))
    var attackClock = 0

    override fun shouldActivate(): Boolean {
        if (GoalUtils.getClosestPlayer(axolotl, 20.0, null) != null) {
            return true
        }
        else
            return false
    }

    override fun getKey(): GoalKey<Axolotl> {
        return goalKey
    }

    override fun getTypes(): EnumSet<GoalType> {
        return EnumSet.of(GoalType.TARGET, GoalType.MOVE)
    }

    override fun shouldStayActive(): Boolean {
        return shouldActivate();
    }

    override fun start() {
        axolotl.target = GoalUtils.chaseClosestPlayer(axolotl, 20.0, 1.0, null)
    }

    override fun stop() {
        axolotl.target = null
        axolotl.pathfinder.stopPathfinding()
    }

    override fun tick() {
        var closestPlayer = GoalUtils.chaseClosestPlayer(axolotl, 30.0, 1.0, null)
        axolotl.lookAt(closestPlayer)
        if (closestPlayer in axolotl.world.getNearbyPlayers(axolotl.location, 1.25) && attackClock <= 0) {
            axolotl.attack(closestPlayer)
            axolotl.world.playSound(axolotl.location, Sound.ENTITY_PHANTOM_BITE, 1.0f, 0.5f)
            axolotl.world.playSound(axolotl.location, Sound.ENTITY_ZOMBIE_DESTROY_EGG, 1.0f, 0.5f)
            customEntity.entityTracker!!.animate("attack", AnimationModifier.DEFAULT_WITH_PLAY_ONCE)
            attackClock = 10
        }
        attackClock--
    }

}