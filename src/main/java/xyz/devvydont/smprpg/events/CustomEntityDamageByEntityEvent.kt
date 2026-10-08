package xyz.devvydont.smprpg.events

import net.kyori.adventure.audience.Audience
import org.bukkit.entity.Entity
import org.bukkit.entity.Projectile
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause

class CustomEntityDamageByEntityEvent(
    val originalEvent: EntityDamageByEntityEvent,
    @JvmField val damaged: Entity,
    @JvmField val dealer: Entity,
    @JvmField val projectile: Projectile?
) : Event(), Cancellable {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    var isCritical: Boolean = false

    /**
     * The Warframe-style "tier" of this critical hit, populated during critical damage calculation.
     * A value of 0 means this hit was not critical. A value of 1 is a standard critical, and higher
     * values represent additional critical rolls earned from excess critical chance (e.g. 250% crit
     * chance yields at least tier 2). Read by the damage popup system to escalate the visuals.
     */
    var criticalTier: Int = 0

    val audience: Audience = Audience.audience(damaged, dealer)

    var originalDamage: Double = originalEvent.damage

    private var additiveDamage = 0.0
    private var scalarDamage = 1.0
    private var multiplicativeDamage = 1.0

    val vanillaCause: DamageCause
        get() = originalEvent.cause

    fun isProjectile(): Boolean {
        return projectile != null
    }

    val isMelee: Boolean
        /**
         * Checks if this damage event is considered a melee event.
         * Melee events are defined as events where the direct entity IS the causing entity.
         * @return True if this is a melee interaction.
         */
        get() = !this.originalEvent.damageSource.isIndirect

    val isIndirect: Boolean
        /**
         * Checks if this damage event is considered an indirect damage event.
         * Indirect events are defined as events where the direct entity IS NOT the causing entity.
         * @return True if this is an indirect damage interaction.
         */
        get() = this.originalEvent.damageSource.isIndirect

    fun addDamage(damage: Double) {
        additiveDamage += damage
    }

    fun removeDamage(damage: Double) {
        additiveDamage -= damage
    }

    fun addScalarDamage(multiplier: Double) {
        scalarDamage += multiplier
    }

    fun removeScalarDamage(multiplier: Double) {
        scalarDamage -= multiplier
    }

    fun multiplyDamage(multiplier: Double) {
        multiplicativeDamage *= multiplier
    }

    /**
     * Completely overrides any sort of modifications done so far and sets the damage to an explicit value.
     */
    fun setDamage(damage: Double) {
        additiveDamage = 0.0
        scalarDamage = 1.0
        multiplicativeDamage = 1.0
        originalDamage = damage
    }

    val finalDamage: Double
        get() {
            var damage = originalDamage
            damage += additiveDamage
            damage *= scalarDamage
            damage *= multiplicativeDamage
            return damage
        }

    override fun isCancelled(): Boolean {
        return originalEvent.isCancelled
    }

    override fun setCancelled(b: Boolean) {
        originalEvent.isCancelled = true
    }

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}
