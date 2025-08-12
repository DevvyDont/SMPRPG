package xyz.devvydont.smprpg.listeners.damage

import org.bukkit.attribute.Attributable
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.entity.EnderPearl
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.base.BossInstance
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import kotlin.math.max

/**
 * This listener is in charge of scaling environmental damage to make more sense in contexts where entities have A LOT
 * of health as compared to vanilla Minecraft. Things like fall damage, drowning, and suffocation etc. need to be
 * health percentage based otherwise you would never die.
 */
class EnvironmentalDamageListener : ToggleableListener() {
    /**
     * Determines if a specific damage cause uses % of max HP instead of flat damage.
     * @param cause The damage cause.
     * @return True if the cause should be percentage based.
     */
    fun causeIsPercentage(cause: DamageCause): Boolean {
        return getEnvironmentalDamagePercentage(cause) > 0
    }

    /*
     * Only used to remove vanilla damage modifiers from the game. We are in full control of how damage is calculated.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    private fun onEntityTakeDamage(event: EntityDamageEvent) {
        clearVanillaDamageModifiers(event)
    }

    /*
     * Do it again after all calculations are done to reset any modifiers that were applied.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("unused")
    private fun onEntityTakeDamageFinal(event: EntityDamageEvent) {
        clearVanillaDamageModifiers(event)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    private fun onExplosiveDamage(event: EntityDamageEvent) {
        
        if (event.getEntity() !is LivingEntity) 
            return

        if (event.cause != DamageCause.BLOCK_EXPLOSION) return

        // Take the vanilla damage and 5x it
        event.setDamage(DamageModifier.BASE, event.damage * 5)
    }

    @EventHandler(priority = EventPriority.LOW)
    @Suppress("unused")
    private fun onPercentageBasedEnvironmentalDamage(event: EntityDamageEvent) {
        if (event.getEntity() !is LivingEntity) 
            return

        if (!causeIsPercentage(event.cause)) return

        val entity = SMPRPG.getService(EntityService::class.java).getEntityInstance(event.getEntity())

        // Bosses don't take env damage as long as it isn't explosive
        val isExplosive =
            event.cause == DamageCause.BLOCK_EXPLOSION || event.cause == DamageCause.ENTITY_EXPLOSION
        if (entity is BossInstance<*> && !isExplosive) {
            event.isCancelled = true
            return
        }

        val damage: Double = entity.halfHeartValue * getEnvironmentalDamagePercentage(event.cause)
        event.setDamage(DamageModifier.BASE, damage)
    }

    @EventHandler
    @Suppress("unused")
    private fun onFallDamage(event: EntityDamageEvent) {

        if (event.getEntity() !is LivingEntity)
            return

        if (event.cause != DamageCause.FALL)
            return

        val distance = event.getEntity().fallDistance
        val entity = SMPRPG.getService(EntityService::class.java).getEntityInstance(event.getEntity())

        var safeFall = 3.0
        if (entity.getEntity() is Attributable) {
            val attributable = entity.getEntity() as Attributable
            val safeFallAttribute: AttributeInstance? = attributable.getAttribute(Attribute.SAFE_FALL_DISTANCE)
            if (safeFallAttribute != null) safeFall = safeFallAttribute.value
        }

        // Add a half heart of fall damage per block fallen past the safe fall distance
        val damage = entity.halfHeartValue * (distance - safeFall) / 2
        if (damage <= 0) {
            event.isCancelled = true
            return
        }
        event.setDamage(DamageModifier.BASE, damage)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onPoisonDeath(event: EntityDamageEvent) {

        // Save players from getting poisoned to death
        if (event.cause != DamageCause.POISON)
            return

        if (event.entity !is LivingEntity)
            return
        val living = event.entity as LivingEntity

        if (event.damage > living.health) event.setDamage(
            DamageModifier.BASE,
            max(0.0, living.health - 1)
        )
    }

    /*
     * Since entities can take lots of damage very rapidly, we need to add some iframes to certain damage events so
     * they don't take an absurd amount of damage very quickly.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onTakeRapidEnvironmentalDamage(event: EntityDamageEvent) {
        
        if (event.getEntity() !is LivingEntity)
            return
        val living = event.entity as LivingEntity

        // If this is a boss, don't do it
        val wrapper = SMPRPG.getService(EntityService::class.java).getEntityInstance(living)
        if (wrapper is BossInstance<*>) return

        if (shouldGiveIFrames(event.cause)) {
            living.noDamageTicks = 20
            if (living.maximumNoDamageTicks == 0) living.maximumNoDamageTicks = 20
        }

        if (!shouldGiveIFrames(event.cause)) {
            val armor: AttributeInstance? = living.getAttribute(Attribute.ARMOR)
            var iframes = 0
            if (armor != null) iframes += (armor.value * 2).toInt()
            living.maximumNoDamageTicks = wrapper.getInvincibilityTicks() + iframes
        }
    }

    /**
     * Ender pearl damage is considered an entity vs entity damage event. This is counter-intuitive, as you would think
     * that ender pearling is environmental damage. This event overrides the damage that ender pearls deal.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onEnderPearlDealDamage(event: EntityDamageByEntityEvent) {
        // Only listen if the attacker is a pearl.

        if (event.damager !is EnderPearl) return

        // Since the damager is a pearl, we can pretty much guarantee this is a typical ender pearl event.
        // Deal damage based on their max HP that falls in line with what you would see in vanilla.
        val receiver =
            SMPRPG.getService(EntityService::class.java).getEntityInstance(event.getEntity())
        val damage = receiver.halfHeartValue * 3
        event.setDamage(damage)
    }

    companion object {
        /**
         * Check if a damage type should give i-frames no matter what. We cannot let entities take damage like suffocation
         * every tick, otherwise they would instantly die in like a second.
         * @param cause The damage cause.
         * @return True if the type should give i-frames no matter what.
         */
        fun shouldGiveIFrames(cause: DamageCause): Boolean {
            return when (cause) {
                DamageCause.FIRE, DamageCause.LAVA, DamageCause.MELTING, DamageCause.HOT_FLOOR, DamageCause.FIRE_TICK, DamageCause.CRAMMING, DamageCause.SUFFOCATION, DamageCause.CONTACT, DamageCause.CAMPFIRE, DamageCause.WORLD_BORDER, DamageCause.STARVATION, DamageCause.VOID, DamageCause.DRYOUT, DamageCause.DROWNING, DamageCause.WITHER, DamageCause.FREEZE, DamageCause.POISON -> true
                else -> false
            }
        }

        /**
         * Environmental damage causes % damage to health since health can get out of control
         * This multiplier is a multiplier on top of the half heart percentage
         * @param cause The damage cause.
         * @return The amount of damage.
         */
        fun getEnvironmentalDamagePercentage(cause: DamageCause): Double {
            return when (cause) {
                DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.DROWNING, DamageCause.CAMPFIRE, DamageCause.HOT_FLOOR, DamageCause.STARVATION, DamageCause.FREEZE -> 1.0
                DamageCause.LAVA -> 3.0
                DamageCause.VOID -> 5.0
                DamageCause.POISON, DamageCause.WORLD_BORDER, DamageCause.SUFFOCATION, DamageCause.CRAMMING -> 2.0
                DamageCause.WITHER -> 2.5
                else -> -1
            }.toDouble()
        }

        /**
         * Hopefully this becomes fully deprecated out of the API soon, very annoying to deal with.
         * We call this to completely ignore any sort of vanilla damage interaction. We are in charge of everything
         */
        fun clearVanillaDamageModifiers(event: EntityDamageEvent) {
            // Attempt to set the all vanilla modifications to 0, if this fails then the entity couldn't have had armor anyway
            for (mod in DamageModifier.entries.toTypedArray()) {
                if (mod == DamageModifier.BASE) continue
                if (!event.isApplicable(mod)) continue
                event.setDamage(mod, 0.0)
            }
        }
    }
}
