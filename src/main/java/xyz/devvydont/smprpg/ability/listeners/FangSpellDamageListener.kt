package xyz.devvydont.smprpg.ability.listeners

import org.bukkit.entity.EvokerFangs
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import xyz.devvydont.smprpg.ability.handlers.FangStrikeAbilityHandler.Companion.ABILITY_SCALING
import xyz.devvydont.smprpg.ability.handlers.FangStrikeAbilityHandler.Companion.DAMAGE
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * Handles ability damage scaling for evoker fangs.
 */
class FangSpellDamageListener: ToggleableListener() {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onFangDamageFromPlayer(event: EntityDamageByEntityEvent) {
        if (event.damager is EvokerFangs) {
            val fangs = event.damager as EvokerFangs
            if (fangs.owner is Player) {
                val player = fangs.owner as Player
                val dmg = EntityDamageCalculatorService.getIntelligenceScaledDamage(
                    DAMAGE.toDouble() + instance.getOrCreateAttribute(player,
                        AttributeWrapper.STRENGTH).value,
                    instance.getOrCreateAttribute(player, AttributeWrapper.INTELLIGENCE).value,
                    ABILITY_SCALING + instance.getOrCreateAttribute(player, AttributeWrapper.ARCANE_RATING).value)
                event.damage = dmg
            }
        }
    }
}