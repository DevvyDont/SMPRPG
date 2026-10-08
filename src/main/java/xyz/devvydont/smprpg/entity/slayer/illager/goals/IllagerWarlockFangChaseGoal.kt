package xyz.devvydont.smprpg.entity.slayer.illager.goals

import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.entity.Evoker
import org.bukkit.entity.EvokerFangs
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.slayer.illager.IllagerWarlockBrutal
import xyz.devvydont.smprpg.entity.slayer.illager.IllagerWarlockExpert
import xyz.devvydont.smprpg.entity.slayer.illager.IllagerWarlockParent
import xyz.devvydont.smprpg.entity.slayer.shambling.ShamblingAbominationParent
import xyz.devvydont.smprpg.util.goals.GoalUtils
import java.util.EnumSet

class IllagerWarlockFangChaseGoal(val slayer : IllagerWarlockParent, val spawnPlayer : Player?, val startCycleSpeed : Int) :
    Goal<Evoker> {

    var cycleSpeed = startCycleSpeed
    var attackClock = cycleSpeed
    val evoker = slayer.entity as Evoker
    val relativeVectors = arrayOf(Vector(1, 0, 0), Vector(-1, 0, 0), Vector(0, 0, 1), Vector(0, 0, -1),
                                  Vector(1, 0, 1), Vector(-1, 0, -1), Vector(-1, 0, 1), Vector(1, 0, -1))

    override fun shouldActivate(): Boolean {
        return slayer.revived
    }

    override fun getKey(): GoalKey<Evoker> {
        return GOAL_KEY
    }

    override fun getTypes(): EnumSet<GoalType> {
        return EnumSet.of(GoalType.TARGET)
    }

    override fun shouldStayActive(): Boolean {
        return shouldActivate();
    }

    override fun tick() {
        attackClock--
        if (attackClock == 0) {
            attackClock = cycleSpeed  // Reset our clock
            for (player in slayer.activelyInvolvedPlayers) {
                if (player.isOnGround) {
                    spawnFang(player.location)
                    if (slayer is IllagerWarlockExpert) {
                        for (i in 0..3) {
                            spawnFang(player.location.add(relativeVectors[i]))
                        }
                    }
                    else if (slayer is IllagerWarlockBrutal) {
                        for (i in 0..7) {
                            spawnFang(player.location.add(relativeVectors[i]))
                        }
                    }
                }
            }
        }
    }

    fun spawnFang(location : Location) : EvokerFangs {
        val fang: EvokerFangs = evoker.world.spawnEntity(
            location,
            EntityType.EVOKER_FANGS,
            CreatureSpawnEvent.SpawnReason.CUSTOM
        ) as EvokerFangs
        fang.owner = evoker
        return fang
    }

    companion object {
        val GOAL_KEY : GoalKey<Evoker> = GoalKey.of(
            Evoker::class.java,
            NamespacedKey(SMPRPG.Companion.plugin, "illager_warlock_fang_chase_goal")
        )
    }

}