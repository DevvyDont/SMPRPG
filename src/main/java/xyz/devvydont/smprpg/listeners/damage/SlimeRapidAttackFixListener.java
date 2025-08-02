package xyz.devvydont.smprpg.listeners.damage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;

/**
 * For whatever reason, slimes and magma cubes have insane attack speeds since their attack logic
 * fires simply for just touching their hitbox. This is a vanilla issue.
 * This listener simply just adds cooldowns to slime attacks.
 */
public class SlimeRapidAttackFixListener extends ToggleableListener {

    private static final String COOLDOWN_KEY = "attack_cooldown";
    private static final long SLIME_ATTACK_COOLDOWN_MS = 1000;
    private static final long NO_COOLDOWN = 0;

    private long getCooldown(Entity entity) {
        for (var meta : entity.getMetadata(COOLDOWN_KEY))
            if (meta.getOwningPlugin() == SMPRPG.getPlugin())
                return meta.asLong();
        return NO_COOLDOWN;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSlimeDealtDamage(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Slime slime))
            return;

        // Check if the slime is on cooldown. If not, set one and allow the event to occur.
        // If they are on cooldown, cancel the event.
        var cooldown = getCooldown(slime);
        if (cooldown > System.currentTimeMillis()) {
            event.setCancelled(true);
            return;
        }

        slime.setMetadata(COOLDOWN_KEY, new FixedMetadataValue(SMPRPG.getPlugin(), System.currentTimeMillis() + SLIME_ATTACK_COOLDOWN_MS));
    }

}
