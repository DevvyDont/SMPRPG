package xyz.devvydont.smprpg.entity.base.listeners.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.entity.fishing.SeaCreature;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;

/**
 * Don't let sea creatures burn, since a third of them spawn from lava.
 */
public class SeaCreatureBurnPrevention extends ToggleableListener {

    @EventHandler(priority = EventPriority.LOWEST)
    private void __onSeaCreatureTakeBurnDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof LivingEntity living))
            return;

        var wrapper = SMPRPG.getService(EntityService.class).getEntityInstance(living);
        if (!(wrapper instanceof SeaCreature<?>))
            return;

        if (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) ||
        event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) ||
        event.getCause().equals(EntityDamageEvent.DamageCause.LAVA))
            event.setCancelled(true);
    }

}
