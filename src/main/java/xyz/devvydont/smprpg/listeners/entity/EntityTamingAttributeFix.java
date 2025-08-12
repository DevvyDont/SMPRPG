package xyz.devvydont.smprpg.listeners.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTameEvent;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;
import xyz.devvydont.smprpg.util.time.TickTime;

/**
 * When entities are tamed, their attributes seem to break. We need to figure out a way to prevent this.
 * I think a good idea for now, is to make the entity scale to the owner's level.
 */
public class EntityTamingAttributeFix extends ToggleableListener {

    @EventHandler
    private void __onTame(EntityTameEvent e) {
        var owner = e.getOwner();
        if (!(owner instanceof Player player))
            return;

        var entityWrapper = SMPRPG.getService(EntityService.class).getEntityInstance(e.getEntity());

        // Do it on the next tick so vanilla doesn't override our behavior.
        Bukkit.getScheduler().runTaskLater(SMPRPG.getPlugin(), () -> entityWrapper.copyLevel(player), TickTime.INSTANTANEOUSLY);
    }

}
