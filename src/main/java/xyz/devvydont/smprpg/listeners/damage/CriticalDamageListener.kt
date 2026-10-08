package xyz.devvydont.smprpg.listeners.damage

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityShootBowEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.items.interfaces.ICantCrit
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.time.TickTime
import kotlin.math.floor

/**
 * When this listener is initialized, the plugin will listen for "critical" damage events, and set the event to be
 * critical as a result. Whether this listener is enabled basically decides if critical hits are enabled or not.
 * This listener is in charge of the following mechanics:
 * - Apply the critical flag when manual melee crits occur by jumping and attacking.
 * - Apply the critical flag when manual bow/arrow crits occur by shooting fully charged shots.
 * - Apply the critical flag when auto crit chance procs for all melee hits. (Crit Chance % attribute).
 * - Apply the critical flag when auto crit chance procs for all bow shots (Crit Chance % attribute).
 * - Apply critical damage when critical flag is present by viewing an entity's Crit Rating attribute.
 * - Make the visuals known that the event was critical via particles and sounds.
 */
class CriticalDamageListener : ToggleableListener() {
    /**
     * Implements the logic that causes bow shots to shoot critical arrows.
     * All this event does is set the critical flag on the arrow to true if a fully charged shot occurred and that's it.
     * @param event The [EntityShootBowEvent] that contains the necessary context for us.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    private fun onCriticalArrowConditionMeet(event: EntityShootBowEvent) {

        // Skip non-arrows
        if (event.getEntity() !is AbstractArrow)
            return
        val arrow = event.entity as AbstractArrow

        // All arrow shot at full force are critical.
        if (event.force >= CRITICAL_ARROW_FORCE_THRESHOLD)
            arrow.isCritical = true
    }

    /**
     * Implements the logic that causes bow shots to automatically shoot critical arrows.
     * All this event does is set the critical flag on the arrow to true if we pass a stat check and that's it.
     * @param event The [EntityShootBowEvent] that contains the necessary context for us.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    private fun onAutoCriticalArrowConditionMeet(event: EntityShootBowEvent) {
        // Skip non-arrows

        if (event.projectile !is AbstractArrow)
            return
        val arrow = event.projectile as AbstractArrow

        // Do a crit chance check.
        val crit = instance.getAttribute(event.getEntity(), AttributeWrapper.CRITICAL_CHANCE)
        if (crit == null) return

        // Roll for auto crit.
        val chance = crit.getValue() / 100.0
        val success = Math.random() < chance

        // All arrow shot at full force are critical.
        if (success)
            arrow.isCritical = true
    }

    /**
     * Implements the logic that causes melee critical hits to occur.
     * All this event does is set the critical flag on the damage event to true, and that's it.
     * @param event The [CustomEntityDamageByEntityEvent] that contains the necessary context for us.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    private fun onMeleeCriticalDamageConditionMeet(event: CustomEntityDamageByEntityEvent) {

        // Criticals can only occur for melee hits.
        if (event.vanillaCause != DamageCause.ENTITY_ATTACK)
            return

        if (event.dealer is LivingEntity) {
            val equipment = event.dealer.equipment;
            if (equipment != null && ItemService.blueprint(equipment.itemInMainHand) is ICantCrit) {
                return;
            }
        }

        // If the entity is not airborne, this can't be a crit.
        if (event.dealer.isOnGround)
            return

        // If the entity doesn't have negative Y velocity, this can't be a crit.
        if (event.dealer.velocity.getY() >= 0)
            return

        // If this isn't a fully charged attack, it can't be a crit. Players use the charge captured
        // at swing time, since the live cooldown is reset before this event fires.
        if (event.dealer is Player && EntityDamageCalculatorService.getMeleeAttackCharge(event.dealer as Player) < EntityDamageCalculatorService.COOLDOWN_FORGIVENESS_THRESHOLD)
            return

        // We have met all the conditions for a crit.
        event.isCritical = true
    }

    /**
     * Implements the logic that causes auto melee critical hits to occur.
     * All this event does is set the critical flag on the damage event to true, and that's it.
     * @param event The [CustomEntityDamageByEntityEvent] that contains the necessary context for us.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    private fun onMeleeAutoCriticalDamageConditionMeet(event: CustomEntityDamageByEntityEvent) {

        // Criticals can only occur for melee hits.
        if (event.vanillaCause != DamageCause.ENTITY_ATTACK)
            return

        // If this isn't a fully charged attack, it can't be a crit. Players use the charge captured
        // at swing time, since the live cooldown is reset before this event fires.
        if (event.dealer is Player && EntityDamageCalculatorService.getMeleeAttackCharge(event.dealer as Player) < EntityDamageCalculatorService.COOLDOWN_FORGIVENESS_THRESHOLD)
            return

        // No point on continuing unless the dealer can have attributes.
        if (event.dealer !is LivingEntity)
            return
        val living = event.dealer as LivingEntity

        // If the dealer does not have a critable item, ignore any calculations.
        val equipment = living.equipment;
        if (equipment != null) {
            if (ItemService.blueprint(equipment.itemInMainHand) is ICantCrit)
                return;
        }

        // Check the entity for their auto crit chance.
        var chance = 0.0
        val crit = instance.getAttribute(living, AttributeWrapper.CRITICAL_CHANCE)
        if (crit != null)
            chance = crit.getValue() / 100 // Keep in mind, unformatted percentage.

        // Roll!
        val success = Math.random() < chance
        if (success)
            event.isCritical = true
    }

    /**
     * Implements the logic that causes bow shots to be critical.
     * All this event does is set the critical flag on the arrow damage event to true, and that's it.
     * @param event The [CustomEntityDamageByEntityEvent] that contains the necessary context for us.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    private fun onArrowCriticalDamageConditionMeet(event: CustomEntityDamageByEntityEvent) {

        // This one is actually pretty simple, as the critical flag is actually stored on the arrow.
        if (event.projectile !is AbstractArrow)
            return

        val arrow = event.projectile as AbstractArrow
        if (arrow.isCritical)
            event.isCritical = true
    }

    /**
     * Implements the damage increase that is added when a critical happens. This is one of the last steps in damage
     * calculation, as critical damage just multiplies the end resulting damage.
     * @param event The [CustomEntityDamageByEntityEvent] that contains the necessary context for us.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("unused")
    private fun onCriticalDamage(event: CustomEntityDamageByEntityEvent) {

        if (!event.isCritical)
            return

        // Extract the critical damage multiplier from the dealer. Their critical damage attribute is the multiplier.
        var multiplier: Double = 1.0 + DEFAULT_CRITICAL_RATING

        // Only living entities can be attribute checked.
        if (event.dealer is LivingEntity) {
            val living = event.dealer
            val critChance = instance.getAttribute(living, AttributeWrapper.CRITICAL_CHANCE)
            val crit = instance.getAttribute(living, AttributeWrapper.CRITICAL_DAMAGE)

            var bonusRolls = 1.0
            if (critChance != null) {
                bonusRolls = floor(critChance.value / 100)
                if (Math.random() < ((critChance.value / 100.0) - bonusRolls))
                    bonusRolls += 1.0
            }

            // Communicate the crit "tier" to the popup system so it can escalate the visuals.
            event.criticalTier = bonusRolls.toInt()

            // Only update if they have the attribute, and remember it is an unformatted percentage **boost**.
            if (crit != null)
                multiplier = 1.0 + ((crit.getValue() / 100.0) * bonusRolls)

            // Add a transient attack cooldown modifier if the entity is airborne when attacking, and multiply the multiplier by 1.15x
            if (!living.isOnGround) {
                multiplier *= JUMP_ATTACK_MULTIPLIER

                // Reduce their attack speed by 25% for 1 second
                val atkSpeed = living.getAttribute(Attribute.ATTACK_SPEED)
                if (atkSpeed != null) {
                    atkSpeed.removeModifier(ATTACK_SPEED_DEBUFF_KEY)
                    atkSpeed.addTransientModifier(AttributeModifier(
                        ATTACK_SPEED_DEBUFF_KEY,
                        -0.25,
                        AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    ))
                    Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable {
                        atkSpeed.removeModifier(ATTACK_SPEED_DEBUFF_KEY)
                    }, TickTime.seconds(1))
                }
            }
        }

        event.multiplyDamage(multiplier)
    }

    /**
     * Implements the visuals for when a critical occurs. When an entity is hit by a critical, the critical noise and
     * particles should play out to signal that the hit was critical. We also do not care to run this if the event is
     * cancelled for whatever reason, so we can ignoreCancelled.
     * @param event The [CustomEntityDamageByEntityEvent] that contains the necessary context for us.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onCriticalDisplay(event: CustomEntityDamageByEntityEvent) {

        if (!event.isCritical)
            return

        // Play sound and spawn particles.
        event.damaged.world.playSound(event.damaged.location, Sound.ENTITY_PLAYER_ATTACK_CRIT, .5f, 1f)
        event.damaged.world.spawnParticle(
            Particle.CRIT,
            event.damaged.location.add(0.0, 1.0, 0.0),
            10,
            0.25,
            0.1,
            0.25,
            0.25
        )
    }

    companion object {
        /**
         * The force requirement for an arrow to be considered critical.
         */
        const val CRITICAL_ARROW_FORCE_THRESHOLD: Float = .95f

        /**
         * The default "critical rating" to use for an entity if they don't have the Critical Rating attribute.
         */
        const val DEFAULT_CRITICAL_RATING: Float = .5f

        /**
         * The amount that crits are multiplied by on jump attacks
         */
        const val JUMP_ATTACK_MULTIPLIER: Float = 1.15f

        /**
         * The NamespacedKey that the transient attack speed modifier is stored in.
         */
        val ATTACK_SPEED_DEBUFF_KEY = NamespacedKey(SMPRPG.plugin, "jump_crit_attack_speed_debuff")
    }
}
