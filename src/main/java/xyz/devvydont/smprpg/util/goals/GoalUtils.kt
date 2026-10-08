package xyz.devvydont.smprpg.util.goals

import org.bukkit.GameMode
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.effects.tasks.ShroudedEffect
import xyz.devvydont.smprpg.services.SpecialEffectService

class GoalUtils {

    companion object {
        fun chaseClosestPlayer(mob: Mob, radius: Double, speed: Double, priorityPlayer : Player?): Player {
            val closestPlayer = getClosestPlayer(mob, radius, priorityPlayer) as LivingEntity
            mob.target = closestPlayer
            if (mob.location.distanceSquared(closestPlayer.location) < 0.25) {
                mob.pathfinder.stopPathfinding()
            } else {
                mob.pathfinder.moveTo(closestPlayer, speed)
            }
            return closestPlayer as Player
        }

        fun getClosestPlayer(mob: Mob, radius: Double, priorityPlayer: Player?): Player? {
            val nearbyPlayers: MutableCollection<Player> = mob.world.getNearbyPlayers(
                mob.location,
                radius,
                { player -> !player.isDead && (player.gameMode !== GameMode.SPECTATOR && player.gameMode !== GameMode.CREATIVE) && player.isValid })
            var closestDistance = -1.0
            var closestPlayer: Player? = null
            for (player in nearbyPlayers) {
                val activeEffect = SMPRPG.getService(SpecialEffectService::class.java).getActiveEffectTask(player)
                if (activeEffect is ShroudedEffect) continue
                val distance = player.location.distanceSquared(mob.location)
                if ((priorityPlayer != null) && player == priorityPlayer) {
                    closestDistance = player.location.distanceSquared(mob.location)
                    if (distance <= closestDistance) {
                        return player
                    }
                    continue
                }
                if (closestDistance != -1.0 && !(distance < closestDistance)) {
                    continue
                }
                closestDistance = distance
                closestPlayer = player
            }
            return closestPlayer
        }
    }
}