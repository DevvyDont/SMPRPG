package xyz.devvydont.smprpg.listeners.damage

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.events.damage.AbsorptionDamageDealtEvent
import xyz.devvydont.smprpg.listeners.damage.popup.DamagePopup
import xyz.devvydont.smprpg.listeners.damage.popup.PopupStyleResolver
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * Listens to every health-related event and delegates the "damage popup" you see floating in the
 * world to the popup subsystem. This class only gathers context and hands it off — the styling
 * lives in [PopupStyleResolver] and the spawning/animation/merging lives in [DamagePopup].
 */
class DamagePopupListener : ToggleableListener() {

    /**
     * Entity-vs-entity damage. This is the only path that knows about criticals and their tier, and
     * it is what makes magic/fire/poison/ability crits render distinctly instead of flat red.
     * Self-inflicted damage (fall, drowning, etc.) is handled by [onEntityTakeGenericDamage].
     * @param event The [CustomEntityDamageByEntityEvent] that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onEntityTakeDamage(event: CustomEntityDamageByEntityEvent) {

        // If the underlying damage event was cancelled, we shouldn't care.
        if (event.originalEvent.isCancelled)
            return

        // Only worry about living entities.
        if (event.damaged !is LivingEntity)
            return
        val living = event.damaged

        if (living.maximumNoDamageTicks > 0 && living.noDamageTicks * 2 > living.maximumNoDamageTicks)
            return

        val style = PopupStyleResolver.resolveDamage(
            event.originalEvent.damageSource.damageType,
            event.vanillaCause,
            event.isCritical,
            event.criticalTier
        )
        DamagePopup.spawn(living, event.finalDamage, style)
    }

    /**
     * Damage that did not involve another entity (drowning, fall damage, cactus, poison, fire, etc).
     * The resolver buckets these by cause so poison ticks, fire, and fall damage each read
     * distinctly instead of all being generic gray.
     * @param event The [EntityDamageEvent] that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onEntityTakeGenericDamage(event: EntityDamageEvent) {

        // Only worry about living entities.
        if (event.entity !is LivingEntity)
            return
        val living = event.entity as LivingEntity

        // Entity-caused damage is handled by onEntityTakeDamage.
        if (event.damageSource.causingEntity != null)
            return

        val style = PopupStyleResolver.resolveDamage(event.damageSource.damageType, event.cause, false, 0)
        DamagePopup.spawn(living, event.finalDamage, style)
    }

    /**
     * Absorption damage. Our plugin handles absorption by zeroing the event damage and subtracting
     * from the absorption pool separately, so it surfaces as its own event.
     * @param event The [AbsorptionDamageDealtEvent] event that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onEntityTakeAbsorptionDamage(event: AbsorptionDamageDealtEvent) {
        DamagePopup.spawn(event.victim, event.damage, PopupStyleResolver.absorptionLoss())
    }

    /**
     * Healing. The style is chosen from the regain reason so natural regen and ability lifesteal
     * read differently.
     * @param event The [EntityRegainHealthEvent] that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onRegenerate(event: EntityRegainHealthEvent) {

        // We only care about living entities.
        if (event.entity !is LivingEntity)
            return
        val living = event.entity as LivingEntity

        DamagePopup.spawn(living, event.amount, PopupStyleResolver.resolveHeal(event.regainReason))
    }

    /**
     * Gaining absorption hearts, which our plugin treats as a "temporary armor" mechanic that scales
     * with health.
     * @param event The [EntityPotionEffectEvent] event that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onPotionEffectUpdate(event: EntityPotionEffectEvent) {

        // We are only concerned with the absorption potion effect.
        val newEffect = event.newEffect
        if (newEffect == null || newEffect.type != PotionEffectType.ABSORPTION)
            return

        // Only worry about living entities. We need information about their max health.
        if (event.entity !is LivingEntity)
            return
        val living = event.entity as LivingEntity

        // Retrieve the wrapper so we can easily extract health information.
        val leveled = SMPRPG.getService(EntityService::class.java).getEntityInstance(living)

        // The idea is that their absorption hearts are equal in health to their normal hearts.
        val amount = (newEffect.amplifier + 1.0) * leveled.halfHeartValue * ABSORPTION_HEARTS_PER_LEVEL

        DamagePopup.spawn(living, amount, PopupStyleResolver.absorptionGain())
    }

    companion object {
        // How many half-hearts of absorption health each level of the absorption effect grants.
        private const val ABSORPTION_HEARTS_PER_LEVEL = 4
    }
}
