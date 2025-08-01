package xyz.devvydont.smprpg.ability.listeners;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import xyz.devvydont.smprpg.ability.handlers.HotShotAbilityHandler;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;

import static xyz.devvydont.smprpg.ability.handlers.HotShotAbilityHandler.DAMAGE;
import static xyz.devvydont.smprpg.ability.handlers.HotShotAbilityHandler.EXPLOSION_RADIUS;

/**
 * When fireballs from Hot Shot collide with something, we need to override the damage.
 */
public class HotShotProjectileCollideListener extends ToggleableListener {

    /*
     * When the fireball explodes, we need to ensure that it does not damage terrain and also damage nearby entities
     * at a penalty (for missing)
     */
    @EventHandler
    private void __onFireballExplode(EntityExplodeEvent event) {

        // If this isn't an inferno projectile we don't care
        if (!HotShotAbilityHandler.isInfernoProjectile(event.getEntity()))
            return;

        Player source = null;
        if (event.getEntity() instanceof Projectile projectile && projectile.getShooter() instanceof Player player)
            source = player;

        // Cancel the actual explosion part and create a safe one that doesn't break things.
        event.setCancelled(true);
        event.blockList().clear();
        for (LivingEntity living : event.getLocation().getNearbyLivingEntities(EXPLOSION_RADIUS)) {

            // Players are immune to this.
            if (living instanceof Player)
                continue;

            double falloff = EXPLOSION_RADIUS - event.getLocation().distance(living.getLocation());
            falloff /= EXPLOSION_RADIUS;
            double damage = DAMAGE * falloff;

            if (falloff < 0)
                continue;

            if (source != null)
                living.setKiller(source);

            living.damage(
                    damage,
                    DamageSource.builder(DamageType.MAGIC).build()
            );
        }
        new ParticleBuilder(Particle.EXPLOSION)
                .location(event.getLocation())
                .count(3)
                .offset(.5, .2, .5)
                .spawn();
        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1.25f);
        HotShotAbilityHandler.removeInfernoProjectile(event.getEntity());
        event.setCancelled(true);
    }

    /**
     * More of a hack if anything. Disables the default damage that the hot shot explosion does. Our damage we calculate
     * is 100% manual and done through magic.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void __onHotShotProjectileDamage(EntityDamageEvent event) {
        if (event.getDamageSource().getDirectEntity() == null)
            return;

        if (event.getDamageSource().getDirectEntity().getScoreboardTags().contains("hotshot"))
            event.setCancelled(true);
    }

}
