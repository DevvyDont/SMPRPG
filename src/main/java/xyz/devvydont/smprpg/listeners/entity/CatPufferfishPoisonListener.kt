package xyz.devvydont.smprpg.listeners.entity

import org.bukkit.entity.Cat
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.time.TickTime

/**
 * Pufferfish are poisonous, so handing our custom [CustomItemType.PUFFERFISH] to a cat backfires: the cat is
 * poisoned rather than pleased. The normal feeding/taming still happens (pufferfish is a tropical fish and lives
 * in the cat_food tag); this simply adds a nasty surprise on top.
 */
class CatPufferfishPoisonListener : ToggleableListener() {

    @EventHandler
    @Suppress("unused")
    private fun onFeedCat(event: PlayerInteractEntityEvent) {
        val cat = event.rightClicked as? Cat ?: return

        val usedItem = event.player.inventory.getItem(event.hand)
        if (!isCustomPufferfish(usedItem)) return

        cat.addPotionEffect(poisonEffect())
    }

    /**
     * Whether the item is our custom pufferfish, as opposed to a vanilla tropical fish or another custom fish
     * sharing the same base material.
     */
    private fun isCustomPufferfish(item: ItemStack?): Boolean {
        if (item == null) return false
        val blueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(item)
        return blueprint is CustomItemBlueprint && blueprint.customItemType == CustomItemType.PUFFERFISH
    }

    private fun poisonEffect(): PotionEffect =
        PotionEffect(PotionEffectType.POISON, POISON_DURATION_TICKS, POISON_AMPLIFIER)

    companion object {
        // Poison II (amplifier 1 == level II) for five minutes.
        private const val POISON_AMPLIFIER = 1
        private val POISON_DURATION_TICKS = TickTime.minutes(5).toInt()
    }
}
