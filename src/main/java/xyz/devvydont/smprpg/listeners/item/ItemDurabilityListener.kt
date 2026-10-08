package xyz.devvydont.smprpg.listeners.item

import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerHarvestBlockEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.interfaces.IDamageFromCrops
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.time.TickTime

class ItemDurabilityListener : ToggleableListener() {
    /**
     * Listens for item damage events, and locks item durability at 1 so that vanilla breaking does not occur.
     */
    @EventHandler
    @Suppress("unused")
    private fun onItemDamage(event: PlayerItemDamageEvent) {

        // Durability changes are always 1, unless we want to force an instabreak.
        if (event.damage > 0 && event.damage != INSTA_BREAK_ITEM_AMT)
            event.damage = 1

        if (event.damage != INSTA_BREAK_ITEM_AMT) {
            Bukkit.getScheduler().runTaskLater(
                SMPRPG.plugin,
                Runnable { ItemService.blueprint(event.item).updateItemData(event.item) },
                TickTime.INSTANTANEOUSLY
            )
        }

        val item = event.item
        var currItemDamage: Int
        var maxAllowedDamage: Int
        if (event.damage == INSTA_BREAK_ITEM_AMT) {
            maxAllowedDamage = item.getData(DataComponentTypes.MAX_DAMAGE) as Int
            currItemDamage = maxAllowedDamage
        }
        else {
            currItemDamage = item.getData(DataComponentTypes.DAMAGE) as Int
            maxAllowedDamage = item.getData(DataComponentTypes.MAX_DAMAGE) as Int - 1
        }

        // If we are at x-1/x durability or about to pass it
        if (currItemDamage + event.damage >= maxAllowedDamage) {
            item.setData(DataComponentTypes.DAMAGE, maxAllowedDamage)

            // Force an update to our item stack to get our attributes to recalculate.
            val itemBp = ItemService.blueprint(item)
            itemBp.updateItemData(item)
            if (event.damage != INSTA_BREAK_ITEM_AMT) event.isCancelled = true

            // Play the break sound of the item at the player to notify them
            val player = event.player
            player.playSound(player.location, item.getData(DataComponentTypes.BREAK_SOUND).toString(), 1f, 1f)
            player.world.spawnParticle(Particle.ITEM, player.eyeLocation, 10, 0.2, 0.0, 0.2, 0.1, item)

        }
    }

    /**
     * Damage hoes if they are harvesting mature crops. This applies to crops
     * that are actually broken (wheat, carrots, etc.)
     */
    @EventHandler(priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onCropBreak(event: BlockBreakEvent) {
        if (event.block.type in CROP_BLOCKS) {
            val player = event.player
            val itemInHand = player.inventory.itemInMainHand
            val itemBp = ItemService.blueprint(itemInHand)

            if (itemBp is IDamageFromCrops) {

                // Is our crop fully mature?
                if (event.block.blockData is Ageable || event.block.type == Material.SUGAR_CANE) {
                    val ageable = event.block.blockData as Ageable
                    if (ageable.age >= ageable.maximumAge)
                        event.player.damageItemStack(event.player.inventory.itemInMainHand, 1)
                }
            }
        }
    }

    companion object {
        val CROP_BLOCKS = listOf<Material>(
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.TORCHFLOWER_CROP,
            Material.PITCHER_CROP,
            Material.NETHER_WART,
            Material.SWEET_BERRY_BUSH,
            Material.GLOW_BERRIES,
            Material.SUGAR_CANE
        )

        val INSTA_BREAK_ITEM_AMT = 999
    }

}