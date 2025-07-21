package xyz.devvydont.smprpg.ability.handlers;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.ability.AbilityContext;
import xyz.devvydont.smprpg.ability.AbilityHandler;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.HashMap;
import java.util.UUID;

public class HealingHandler implements AbilityHandler {

    public static int SMALL_HEAL_AMOUNT = 5;
    public static int NORMAL_HEAL_AMOUNT = 10;
    public static int BIG_HEAL_AMOUNT = 20;
    public static int HEFTY_HEAL_AMOUNT = 30;
    public static int COLOSSAL_HEAL_AMOUNT = 50;
    public static int SMALL_HEAL_SECONDS = 3;
    public static int NORMAL_HEAL_SECONDS = 5;
    public static int BIG_HEAL_SECONDS = 10;

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
                if (player == null || halfSeconds*2 > getSecondsActive()) {
                    cancel();
                    return;
                }

                player.heal(getHealingPerHalfSecond());
                halfSeconds++;
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                super.cancel();
                entityIdToHealingTask.remove(ctx.caster().getUniqueId());
            }
        };

        task.runTaskTimer(SMPRPG.getInstance(), TickTime.INSTANTANEOUSLY, TickTime.HALF_SECOND);
        entityIdToHealingTask.put(ctx.caster().getUniqueId(), task);
        return true;
    }
}
