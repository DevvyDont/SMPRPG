package xyz.devvydont.smprpg.ability.handlers.passive

import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.inventory.EntityEquipment
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.ability.Passive
import xyz.devvydont.smprpg.entity.fishing.SeaCreature
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.items.interfaces.IPassiveProvider
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

class AnglerListener : ToggleableListener() {
    /**
     * When a sea creature takes damage from an item with angler.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onTakeDamage(event: CustomEntityDamageByEntityEvent) {
        if (event.dealer !is LivingEntity)
            return

        if (event.damaged !is LivingEntity)
            return

        if (SMPRPG.getService(EntityService::class.java).getEntityInstance(event.damaged) !is SeaCreature<*>)
            return

        // Edge case. Is this a trident that has the item? This is valid.
        if (event.projectile is Trident) {
            val blueprint = blueprint(event.projectile.itemStack)
            if (blueprint is IPassiveProvider) {
                if (blueprint.getPassives().contains(Passive.ANGLER)) {
                    event.multiplyDamage(AbyssalAnnihilationListener.Companion.MULTIPLIER.toDouble())
                    return
                }
            }
        }

        val equipment: EntityEquipment? = event.dealer.equipment
        if (equipment == null)
            return

        val mainItem = equipment.itemInMainHand
        if (mainItem.type == Material.AIR) return

        val blueprint = blueprint(mainItem)
        if (blueprint is IPassiveProvider)
            if (blueprint.getPassives().contains(Passive.ANGLER))
                event.multiplyDamage(MULTIPLIER.toDouble())
    }

    companion object {
        const val MULTIPLIER: Int = 5
    }
}
