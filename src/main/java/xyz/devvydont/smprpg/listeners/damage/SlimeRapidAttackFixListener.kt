package xyz.devvydont.smprpg.listeners.damage

import org.bukkit.entity.Entity
import org.bukkit.entity.Slime
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.metadata.FixedMetadataValue
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * For whatever reason, slimes and magma cubes have insane attack speeds since their attack logic
 * fires simply for just touching their hitbox. This is a vanilla issue.
 * This listener simply just adds cooldowns to slime attacks.
 */
class SlimeRapidAttackFixListener : ToggleableListener() {
    private fun getCooldown(entity: Entity): Long {
        for (meta in entity.getMetadata(COOLDOWN_KEY)) if (meta.owningPlugin === plugin) return meta.asLong()
        return NO_COOLDOWN
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    fun onSlimeDealtDamage(event: EntityDamageByEntityEvent) {
        if (event.damager !is Slime)
            return
        val slime = event.damager as Slime
        // Check if the slime is on cooldown. If not, set one and allow the event to occur.
        // If they are on cooldown, cancel the event.
        val cooldown = getCooldown(slime)
        if (cooldown > System.currentTimeMillis()) {
            event.isCancelled = true
            return
        }

        slime.setMetadata(
            COOLDOWN_KEY,
            FixedMetadataValue(plugin, System.currentTimeMillis() + SLIME_ATTACK_COOLDOWN_MS)
        )
    }

    companion object {
        private const val COOLDOWN_KEY = "attack_cooldown"
        private const val SLIME_ATTACK_COOLDOWN_MS: Long = 1000
        private const val NO_COOLDOWN: Long = 0
    }
}
