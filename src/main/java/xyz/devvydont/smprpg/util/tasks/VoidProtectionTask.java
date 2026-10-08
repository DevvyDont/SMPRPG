package xyz.devvydont.smprpg.util.tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.services.DropsService;
import xyz.devvydont.smprpg.util.persistence.KeyStore;

/**
 * Implements simple logic to prevent certain items from falling in the void and perishing.
 */
public class VoidProtectionTask extends BukkitRunnable {

    /**
     * Checks an item. If it is a drop that is worth protecting, then make sure that it will never fall into
     * the void. This can be done by making the item float at Y = 1 in the end and giving it zero gravity.
     * @param item The {@link Item} to protect.
     */
    public static void checkAndFloatAboveVoid(Item item) {

        if (!item.isInvulnerable())
            return;

        // If this item spawned in the void in the end, lets make them float at y=1
        if (item.getLocation().getY() < (item.getWorld().getMinHeight() - 5)) {
            // Turn off gravity, teleport it to y=1, give it no y velocity
            item.setGravity(false);
            item.teleport(item.getLocation().set(item.getLocation().getX(), item.getWorld().getMinHeight(), item.getLocation().getZ()));
            item.setVelocity(item.getVelocity().setY(0));
        }
    }

    public static boolean hasVoidProtection(World world) {
        return world.getEnvironment() == World.Environment.THE_END
                || world.getKey().equals(KeyStore.DIM_AETHER);
    }

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds())
            if (hasVoidProtection(world))
                for (Item item : world.getEntitiesByClass(Item.class))
                    if (item.hasGravity())
                        checkAndFloatAboveVoid(item);
    }
}
