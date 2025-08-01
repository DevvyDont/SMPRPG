package xyz.devvydont.smprpg.listeners;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.events.damage.AbsorptionDamageDealtEvent;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;

/**
 * Implements the "absorption" damage mechanic. This allows damage to scale correctly with absorption health, as
 * it uses completely separate logic to normal health/health scaling.
 */
public class AbsorptionDamageFix extends ToggleableListener {

    public void crackEntityArmor(LivingEntity entity) {
        entity.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, entity.getEyeLocation(), 20);
        entity.getWorld().playSound(entity.getEyeLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 1.5f);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof LivingEntity living))
            return;

        var entity = SMPRPG.getService(EntityService.class).getEntityInstance(event.getEntity());

        // If they don't have absorption don't do anything
        if (entity.getAbsorptionHealth() <= 0)
            return;

        // Subtract absorption.
        entity.addAbsorptionHealth(-event.getDamage());

        // Check if they ran out.
        var cracked = entity.getAbsorptionHealth() <= 0;
        if (cracked)
            crackEntityArmor(living);

        // Announce. Do this right before we set the damage so the event will make sense to whoever decides to interact with it.
        var absorbDmgEvent = new AbsorptionDamageDealtEvent(living, event.getDamage(), cracked);
        absorbDmgEvent.callEvent();

        // Make the original damage event do no damage.
        event.setDamage(EntityDamageEvent.DamageModifier.BASE, 0.0001);
    }

}
