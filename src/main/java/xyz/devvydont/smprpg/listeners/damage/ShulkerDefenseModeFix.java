package xyz.devvydont.smprpg.listeners.damage;

import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;

/**
 * We need a way for shulkers to take reduced damage while they aren't peeking.
 */
public class ShulkerDefenseModeFix extends ToggleableListener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void __onShulkerTakeDamageWhileClosed(CustomEntityDamageByEntityEvent event) {

        if (!(event.getDamaged() instanceof Shulker shulker))
            return;

        var multiplier = Math.max(shulker.getPeek(), .1);
        event.multiplyDamage(multiplier);
    }

}
