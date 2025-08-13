package xyz.devvydont.smprpg.ability.handlers.passive;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.ability.Passive;
import xyz.devvydont.smprpg.entity.fishing.SeaCreature;
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent;
import xyz.devvydont.smprpg.items.interfaces.IPassiveProvider;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;

/**
 * Provides the functionality for ABYSSAL_ANNIHILATION to work as intended.
 */
public class AbyssalAnnihilationListener extends ToggleableListener {

    public static final int MULTIPLIER = 10;

    /**
     * When a sea creature takes damage from an item with abyssal annihilation.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTakeDamage(CustomEntityDamageByEntityEvent event) {

        if (!(event.dealer instanceof LivingEntity dealer))
            return;

        if (!(event.damaged instanceof LivingEntity damaged))
            return;

        if (!(SMPRPG.getService(EntityService.class).getEntityInstance(damaged) instanceof SeaCreature<?>))
            return;

        // Edge case. Is this a trident that has the item? This is valid.
        if (event.projectile instanceof Trident trident) {
            if (ItemService.blueprint(trident.getItemStack()) instanceof IPassiveProvider passiveProvider) {
                if (passiveProvider.getPassives().contains(Passive.ABYSSAL_ANNIHILATION)) {
                    event.multiplyDamage(MULTIPLIER);
                    return;
                }
            }
        }

        var equipment = dealer.getEquipment();
        if (equipment == null)
            return;

        var mainItem = equipment.getItemInMainHand();
        if (mainItem.getType() == Material.AIR)
            return;

        if (ItemService.blueprint(mainItem) instanceof IPassiveProvider passiveProvider)
            if (passiveProvider.getPassives().contains(Passive.ABYSSAL_ANNIHILATION))
                event.multiplyDamage(MULTIPLIER);
    }

}
