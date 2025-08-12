package xyz.devvydont.smprpg.listeners.crafting;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.devvydont.smprpg.items.blueprints.fishing.FishBlueprint;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;

/**
 * Restricts cooking fish in normal campfires. They should use soul campfires instead.
 */
public class NormalFishCampfireBlacklist extends ToggleableListener {

    public static String[] ERRORS = new String [] {
            "This flame rejects your fishy friend.",
            "This flame lacks the whisper of souls.",
            "Only a flame touched by the beyond can unbind essence.",
            "The embers know nothing of the deep.",
            "The essence remains bound - The flame is wrong.",
            "This fire burns, but it does not understand.",
            "This flame does not resonate with the tune of its essence."
    };

    @EventHandler(priority = EventPriority.LOWEST)
    private void __onInteractWithNormalCampfireWithFish(PlayerInteractEvent event) {

        var block = event.getClickedBlock();
        if (block == null)
            return;

        if (!block.getType().equals(Material.CAMPFIRE))
            return;

        if (event.getItem() == null || event.getItem().getType().equals(Material.AIR))
            return;

        var blueprint = ItemService.blueprint(event.getItem());
        if (!(blueprint instanceof FishBlueprint))
            return;

        event.setCancelled(true);
        var phrase = ERRORS[(int) (Math.random() * ERRORS.length)];
        event.getPlayer().sendMessage(ComponentUtils.error(phrase));
        event.getPlayer().getWorld().playSound(block.getLocation(), Sound.BLOCK_SOUL_SAND_BREAK, 1, 1.5f);
        new ParticleBuilder(Particle.SOUL_FIRE_FLAME)
                .location(block.getLocation().toCenterLocation())
                .count(10)
                .offset(.2, .1, .2)
                .spawn();
    }

}
