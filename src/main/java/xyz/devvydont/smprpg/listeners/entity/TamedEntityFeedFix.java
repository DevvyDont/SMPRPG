package xyz.devvydont.smprpg.listeners.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.services.AttributeService;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;

/**
 * Minecraft by default implements entity healing by food by adding health. Since entity health can get pretty high,
 * it would require an insane amount of food in order to trigger a full heal. This listener fixes that by changing
 * healing logic to be percentage of health instead.
 */
public class TamedEntityFeedFix extends ToggleableListener {

    /**
     * How much food heals for scaling with the entity's half heart amount. Directly multiplied against
     * their half heart amount and scaled using regeneration attribute.
     */
    public static final int FOOD_HEALING_MULTIPLIER = 2;

    @EventHandler(ignoreCancelled = true)
    public void onHealPet(EntityRegainHealthEvent event) {

        // Only listen to the "EATING" reason. This is from pets eating food.
        if (!event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.EATING))
            return;

        // Only living entities are affected by this logic.
        if (!(event.getEntity() instanceof LivingEntity living))
            return;

        // Use their regeneration stat to get a healing amount.
        var regen = AttributeService.getInstance().getAttribute(living, AttributeWrapper.REGENERATION);
        var halfHeart = SMPRPG.getService(EntityService.class).getEntityInstance(living).getHalfHeartValue();
        var healingAmount = halfHeart * FOOD_HEALING_MULTIPLIER;
        if (regen != null)
            healingAmount *= (regen.getValue() / 100);

        event.setAmount(healingAmount);
    }

}
