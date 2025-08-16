package xyz.devvydont.smprpg.listeners.crafting

import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerInteractEvent
import xyz.devvydont.smprpg.items.blueprints.fishing.FishBlueprint
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * Restricts cooking fish in normal campfires. They should use soul campfires instead.
 */
class NormalFishCampfireBlacklist : ToggleableListener() {

    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    private fun onInteractWithNormalCampfireWithFish(event: PlayerInteractEvent) {
        val block = event.clickedBlock
        if (block == null)
            return

        if (block.type != Material.CAMPFIRE)
            return

        if (event.item == null || event.item!!.type == Material.AIR)
            return

        val blueprint = ItemService.blueprint(event.item!!)
        if (blueprint !is FishBlueprint)
            return

        event.setCancelled(true)
        val phrase: String? = ERRORS[(Math.random() * ERRORS.size).toInt()]
        event.getPlayer().sendMessage(ComponentUtils.error(phrase))
        event.getPlayer().world.playSound(block.location, Sound.BLOCK_SOUL_SAND_BREAK, 1f, 1.5f)
        ParticleBuilder(Particle.SOUL_FIRE_FLAME)
            .location(block.location.toCenterLocation())
            .count(10)
            .offset(.2, .1, .2)
            .spawn()
    }

    companion object {
        var ERRORS: Array<String> = arrayOf<String>(
            "This flame rejects your fishy friend.",
            "This flame lacks the whisper of souls.",
            "Only a flame touched by the beyond can unbind essence.",
            "The embers know nothing of the deep.",
            "The essence remains bound - The flame is wrong.",
            "This fire burns, but it does not understand.",
            "This flame does not resonate with the tune of its essence."
        )
    }
}
