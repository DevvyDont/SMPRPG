package xyz.devvydont.smprpg.listeners.damage

import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.events.damage.AbsorptionDamageDealtEvent
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * Implements the "absorption" damage mechanic. This allows damage to scale correctly with absorption health, as
 * it uses completely separate logic to normal health/health scaling.
 */
class AbsorptionDamageFix : ToggleableListener() {
    fun crackEntityArmor(entity: LivingEntity) {
        entity.world.spawnParticle(Particle.TOTEM_OF_UNDYING, entity.eyeLocation, 20)
        entity.world.playSound(entity.eyeLocation, Sound.BLOCK_GLASS_BREAK, 1f, 1.5f)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onEntityDamage(event: EntityDamageEvent) {

        if (event.getEntity() !is LivingEntity)
            return
        val living = event.getEntity() as LivingEntity

        val entity = SMPRPG.getService(EntityService::class.java).getEntityInstance(event.getEntity())

        // If they don't have absorption don't do anything
        if (entity.getAbsorptionHealth() <= 0)
            return

        // Subtract absorption.
        entity.addAbsorptionHealth(-event.damage)

        // Check if they ran out.
        val cracked = entity.getAbsorptionHealth() <= 0
        if (cracked)
            crackEntityArmor(living)

        // Announce. Do this right before we set the damage so the event will make sense to whoever decides to interact with it.
        val absorbDmgEvent = AbsorptionDamageDealtEvent(living, event.damage, cracked)
        absorbDmgEvent.callEvent()

        // Make the original damage event do no damage.
        event.setDamage(DamageModifier.BASE, 0.0001)
    }
}
