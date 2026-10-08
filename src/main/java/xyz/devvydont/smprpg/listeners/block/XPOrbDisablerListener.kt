package xyz.devvydont.smprpg.listeners.block

import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExpEvent
import org.bukkit.event.entity.EntityBreedEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.inventory.FurnaceExtractEvent
import org.bukkit.event.inventory.PrepareGrindstoneEvent
import org.bukkit.event.player.PlayerFishEvent
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

class XPOrbDisablerListener : ToggleableListener() {
    /**
     * Prevents xp orbs from spawning in most conditions.
     */
    @EventHandler
    @Suppress("unused")
    private fun onBlockGivesExp(event: BlockExpEvent) {
        event.expToDrop = 0;
    }

    @EventHandler
    private fun onMobKill(event: EntityDeathEvent) {
        event.setDroppedExp(0);
    }

    @EventHandler
    private fun onCatchFish(event: PlayerFishEvent) {
        event.expToDrop = 0;
    }

    @EventHandler
    private fun onBreedAnimals(event: EntityBreedEvent) {
        event.experience = 0;
    }
}
