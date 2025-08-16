package xyz.devvydont.smprpg.ability.handlers

import org.bukkit.Effect
import org.bukkit.Sound
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class InstantTransmissionAbilityHandler : AbilityHandler {
    /**
     * Attempts to execute the ability.
     *
     * @param ctx The context of the ability.
     * @return True if the ability succeeded and should have cost reduced, false otherwise.
     */
    override fun execute(ctx: AbilityContext): Boolean {
        // Teleport.

        val player = ctx.caster
        val old = player.eyeLocation
        var foundSpot = false
        var distance: Int = TELEPORT_DISTANCE

        while (!foundSpot && distance > INVALID_DISTANCE_THRESHOLD) {
            distance--

            if (old.getWorld().rayTraceBlocks(old, old.getDirection(), distance.toDouble()) == null) foundSpot = true
        }

        if (!foundSpot) {
            player.sendMessage(ComponentUtils.error("No free spot ahead of you!"))
            return false
        }

        val newLocation = old.add(old.getDirection().normalize().multiply(distance))
        player.teleport(newLocation)
        player.world.playEffect(old, Effect.ENDER_SIGNAL, 1)
        player.world.playEffect(newLocation, Effect.ENDER_SIGNAL, 0)
        player.world.playSound(newLocation, Sound.ENTITY_ENDER_EYE_DEATH, .4f, 1f)
        player.world.playSound(old, Sound.ENTITY_ENDERMAN_TELEPORT, .4f, 1f)
        player.fallDistance = 0f
        return true
    }

    companion object {
        /**
         * How far to teleport.
         */
        const val TELEPORT_DISTANCE: Int = 12

        /**
         * How close to prevent teleporting. (Touching a wall, interacting with stuff right in front of us, etc.)
         */
        const val INVALID_DISTANCE_THRESHOLD: Int = 2
    }
}
