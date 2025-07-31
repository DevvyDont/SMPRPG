package xyz.devvydont.smprpg.ability.handlers;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import xyz.devvydont.smprpg.ability.AbilityContext;
import xyz.devvydont.smprpg.ability.AbilityHandler;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Set;

public class InstantTransmissionAbilityHandler implements AbilityHandler {

    /**
     * How far to teleport.
     */
    public static final int TELEPORT_DISTANCE = 12;

    /**
     * How close to prevent teleporting. (Touching a wall, interacting with stuff right in front of us, etc.)
     */
    public static final int INVALID_DISTANCE_THRESHOLD = 2;

    /**
     * Attempts to execute the ability.
     *
     * @param ctx The context of the ability.
     * @return True if the ability succeeded and should have cost reduced, false otherwise.
     */
    @Override
    public boolean execute(AbilityContext ctx) {

        // Teleport.
        var player = ctx.caster();
        Location old = player.getEyeLocation();
        boolean foundSpot = false;
        int distance = TELEPORT_DISTANCE;

        while (!foundSpot && distance > INVALID_DISTANCE_THRESHOLD){

            distance--;

            if (old.getWorld().rayTraceBlocks(old, old.getDirection(), distance) == null)
                foundSpot = true;
        }

        if (!foundSpot){
            player.sendMessage(ComponentUtils.error("No free spot ahead of you!"));
            return false;
        }

        Location newLocation = old.add(old.getDirection().normalize().multiply(distance));
        player.teleport(newLocation);
        player.getWorld().playEffect(old, Effect.ENDER_SIGNAL, 1);
        player.getWorld().playEffect(newLocation, Effect.ENDER_SIGNAL, 0);
        player.getWorld().playSound(newLocation, Sound.ENTITY_ENDER_EYE_DEATH, .4f, 1);
        player.getWorld().playSound(old, Sound.ENTITY_ENDERMAN_TELEPORT, .4f, 1);
        return true;
    }
}
