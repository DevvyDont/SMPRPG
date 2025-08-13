package xyz.devvydont.smprpg.listeners.damage

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.TextDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.events.damage.AbsorptionDamageDealtEvent
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentDecorator
import xyz.devvydont.smprpg.util.formatting.Symbols
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.time.TickTime
import java.text.DecimalFormat
import java.util.function.Consumer
import kotlin.math.max
import kotlin.math.roundToLong

/**
 * This listener is in charge of the "damage popups" you see in the world when entities take damage.
 * All these listeners are responsible for listening to any relevant events to spawn in damage popups.
 */
class DamagePopupListener : ToggleableListener() {
    /**
     * The sole purpose of the popup type is to assign behavior to specific popups.
     */
    enum class PopupType(val decorator: ComponentDecorator) {
        DAMAGE_ARMOR(ComponentDecorator.color(NamedTextColor.GOLD)),
        GENERIC(ComponentDecorator.color(NamedTextColor.GRAY)),
        DAMAGE(ComponentDecorator.color(TextColor.color(180, 100, 100))),
        CRITICAL(
            ComponentDecorator.symbolizedGradient(
                Symbols.POWER,
                TextColor.color(255, 0, 60),
                TextColor.color(255, 138, 0)
            )
        ),
        GAIN_ARMOR(ComponentDecorator.color(NamedTextColor.YELLOW)),
        HEAL(ComponentDecorator.color(NamedTextColor.GREEN))
    }

    /**
     * Hook into the custom damage event. We need to use this one to determine if it was a critical or not.
     * It must be noted however that this event will not hook into self-inflicted damage, such as fall damage etc.
     * This is explicitly entity vs. entity damage.
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
        val living = event.damaged as LivingEntity

        if (living.maximumNoDamageTicks > 0 && living.noDamageTicks * 2 > living.maximumNoDamageTicks)
            return

        // First, determine the type. We only care about two here, with that being damage and critical.
        val type = if (event.isCritical) PopupType.CRITICAL else PopupType.DAMAGE

        // We can spawn the popup. The final damage will essentially be what displays.
        spawnTextPopup(living.eyeLocation, event.finalDamage, type)
    }

    /**
     * Hook into damage events only where the entity wasn't involved with another entity. This will include things such
     * as drowning, fall damage, cactus, etc. This paired with the handler above should handle all cases.
     * @param event The [EntityDamageEvent] that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onEntityTakeGenericDamage(event: EntityDamageEvent) {

        // Only worry about living entities.
        if (event.getEntity() !is LivingEntity)
            return
        val living = event.getEntity() as LivingEntity

        // Analyze the cause. This needs to be harm that is not from an entity.
        if (event.damageSource.causingEntity != null)
            return

        // Should be good to spawn a popup!
        spawnTextPopup(living.eyeLocation, event.getFinalDamage(), PopupType.GENERIC)
    }

    /**
     * Hook into when entities take absorption damage. Our plugin handles absorption in a strange way, by setting the
     * actual event damage to 0 then subtracting absorption. This means we have to spawn absorption popups differently.
     * @param event The [AbsorptionDamageDealtEvent] event that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onEntityTakeAbsorptionDamage(event: AbsorptionDamageDealtEvent) {
        // This event already handles all the relevant checking. Simply spawn the popup.
        spawnTextPopup(event.victim.eyeLocation, event.damage, PopupType.DAMAGE_ARMOR)
    }

    /**
     * Let's display popups for when entities heal. This is really simple, when an entity gains health, show it.
     * @param event The [EntityRegainHealthEvent] that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onRegenerate(event: EntityRegainHealthEvent) {

        // We only care about living entities.
        if (event.getEntity() !is LivingEntity)
            return
        val living = event.getEntity() as LivingEntity
        spawnTextPopup(living.eyeLocation, event.amount, PopupType.HEAL)
    }

    /**
     * Let's display a popup when a user gains absorption hearts. Our plugin treats this as a "temporary armor" mechanic,
     * which scales with their health which contributes to their health pool.
     * @param event The [EntityPotionEffectEvent] event that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onPotionEffectUpdate(event: EntityPotionEffectEvent) {
        // We are only concerned with the absorption potion effect.

        if (event.newEffect == null || event.newEffect!!.type != PotionEffectType.ABSORPTION)
            return

        // Only worry about living entities. We need information about their max health.
        if (event.getEntity() !is LivingEntity)
            return
        val living = event.getEntity() as LivingEntity

        // Retrieve the wrapper so we can easily extract health information.
        val leveled = SMPRPG.getService(EntityService::class.java).getEntityInstance(living)

        // We need to calculate how much absorption health they gained. This may need to be standardized later...
        // The idea is that their absorption hearts are equal in health as their normal hearts.
        val amount = (event.newEffect!!.amplifier + 1.0) * leveled.halfHeartValue * 4

        // Spawn the popup!
        spawnTextPopup(living.eyeLocation, amount, PopupType.GAIN_ARMOR)
    }

    companion object {
        // Number formatter to make popups prettier.
        private val NUMBER_FORMATTER = DecimalFormat("#,###,###")

        /**
         * This is a helper method to spawn a simple text popup that cleans itself up later.
         * @param location The location to spawn the text popup.
         * @param amount The 'amount' to display within the popup. It will automatically be formatted.
         * @param type The popup type. This affects how to display it.
         */
        fun spawnTextPopup(location: Location, amount: Double, type: PopupType) {
            // It's pointless to display nothing...
            val rounded = amount.roundToLong()
            if (rounded == 0L)
                return

            // Format the amount. We want to make sure we are at least displaying 1. We also want to make this more readable.
            val finalAmount = max(1, rounded).toDouble()
            val text: String = NUMBER_FORMATTER.format(finalAmount)

            // Retrieve the component based on the popup type.
            val component = type.decorator.decorate(text)

            // Now actually spawn the text display entity.
            val spawnLoc = location.add(Math.random() - .5, Math.random() + .3, Math.random() - .5)
            val display =
                location.getWorld().spawn(spawnLoc, TextDisplay::class.java, Consumer { e: TextDisplay ->
                    e.isPersistent = false
                    e.text(component)
                    e.billboard = Display.Billboard.CENTER
                    e.isShadowed = true
                    e.isSeeThrough = false
                    e.backgroundColor = Color.fromARGB(0, 0, 0, 0)
                })
            object : BukkitRunnable() {
                override fun run() {
                    display.remove()
                }
            }.runTaskLater(plugin, TickTime.seconds(2))
        }
    }
}
