package xyz.devvydont.smprpg.ability.handlers;

import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.ability.AbilityContext;
import xyz.devvydont.smprpg.ability.AbilityHandler;
import xyz.devvydont.smprpg.services.ActionBarService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.HashMap;
import java.util.UUID;

public class HealingHandler implements AbilityHandler {

    public static int SMALL_HEAL_AMOUNT = 5;
    public static int NORMAL_HEAL_AMOUNT = 15;
    public static int BIG_HEAL_AMOUNT = 25;
    public static int HEFTY_HEAL_AMOUNT = 50;
    public static int COLOSSAL_HEAL_AMOUNT = 75;
    public static int SMALL_HEAL_SECONDS = 3;
    public static int NORMAL_HEAL_SECONDS = 5;
    public static int BIG_HEAL_SECONDS = 8;

    private static final HashMap<UUID, Runnable> entityIdToHealingTask = new HashMap<>();

    private final int healingPerHalfSecond;
    private final int secondsActive;

    public HealingHandler(int healingPerHalfSecond, int secondsActive) {
        this.healingPerHalfSecond = healingPerHalfSecond;
        this.secondsActive = secondsActive;
    }

    public int getHealingPerHalfSecond() {
        return healingPerHalfSecond;
    }

    public int getSecondsActive() {
        return secondsActive;
    }

    /**
     * Attempts to execute the ability.
     *
     * @param ctx The context of the ability.
     * @return True if the ability succeeded and should have cost reduced, false otherwise.
     */
    @Override
    public boolean execute(AbilityContext ctx) {

        // If the player already has healing applying to them, then don't do anything.
        if (entityIdToHealingTask.containsKey(ctx.caster().getUniqueId()))
            return false;

        // The player is allowed to heal. Make them a task.
        var task = new BukkitRunnable() {
            private int halfSeconds = 0;
            @Override
            public void run() {
                var player = Bukkit.getPlayer(ctx.caster().getUniqueId());
                if (player == null || (halfSeconds / 2 > getSecondsActive())) {
                    cancel();
                    return;
                }

                var secLeft = getSecondsActive() - (halfSeconds/2);
                if (halfSeconds > 2)
                    SMPRPG.getService(ActionBarService.class).addActionBarComponent(player, ActionBarService.ActionBarSource.MISC, ComponentUtils.create("HEALING " + secLeft + "s", NamedTextColor.GREEN), 1);
                player.heal(getHealingPerHalfSecond());
                halfSeconds++;
                new ParticleBuilder(Particle.HEART)
                        .location(player.getLocation().add(0, 1, 0))
                        .count(2)
                        .offset(.25, .1, .25)
                        .receivers(10)
                        .extra(0)
                        .spawn();
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                super.cancel();
                entityIdToHealingTask.remove(ctx.caster().getUniqueId());
            }
        };

        task.runTaskTimer(SMPRPG.getInstance(), TickTime.INSTANTANEOUSLY, TickTime.HALF_SECOND);
        entityIdToHealingTask.put(ctx.caster().getUniqueId(), task);
        ctx.caster().getWorld().playSound(ctx.caster().getLocation(), Sound.ENTITY_CREAKING_DEATH, 1f, 2f);
        return true;
    }
}
