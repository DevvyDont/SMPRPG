package xyz.devvydont.smprpg.ability.handlers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.ability.AbilityContext;
import xyz.devvydont.smprpg.ability.AbilityHandler;
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HotShotAbilityHandler implements AbilityHandler {

    public static final int COOLDOWN = 3;
    public static final int DAMAGE = 15_000;
    public static final double EXPLOSION_RADIUS = 5;

    // We need a reference to projectiles that we shoot so that we can handle them at different stages in its life
    // since PDCs do not work during the EntityExplodeEvent.
    private static final Map<UUID, Entity> projectiles = new HashMap<>();

    public static boolean isInfernoProjectile(Entity projectile) {
        return projectiles.containsKey(projectile.getUniqueId());
    }

    public static void setInfernoProjectile(Entity projectile) {
        projectiles.put(projectile.getUniqueId(), projectile);
        projectile.addScoreboardTag("hotshot");
    }

    public static void removeInfernoProjectile(Entity projectile) {
        projectiles.remove(projectile.getUniqueId());
    }

    /**
     * Attempts to execute the ability.
     *
     * @param ctx The context of the ability.
     * @return True if the ability succeeded and should have cost reduced, false otherwise.
     */
    @Override
    public boolean execute(AbilityContext ctx) {

        if (ctx.caster() instanceof Player player && ctx.hand() != null)
            if (player.hasCooldown(player.getEquipment().getItem(ctx.hand())))
                return false;

        var projectile = ctx.caster().launchProjectile(Fireball.class, ctx.caster().getLocation().getDirection().normalize().multiply(2));
        SMPRPG.getService(EntityDamageCalculatorService.class).setBaseProjectileDamage(projectile, DAMAGE);
        setInfernoProjectile(projectile);

        if (ctx.caster() instanceof Player player && ctx.hand() != null)
            player.setCooldown(player.getEquipment().getItem(ctx.hand()), (int)TickTime.seconds(COOLDOWN));

        return true;
    }
}
