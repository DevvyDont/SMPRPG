package xyz.devvydont.smprpg.items.listeners

import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.items.MenuCompressor
import xyz.devvydont.smprpg.items.blueprints.equipment.PocketCompressorBlueprint
import xyz.devvydont.smprpg.recipe.CompressionGraph
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.extensions.takeIfPresent
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

class PocketCompressorListener: ToggleableListener() {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPickupItem(event: EntityPickupItemEvent) {
        // Ignore the event if its cancelled
        if (event.isCancelled) return

        if (event.entity !is Player) return
        val player = event.entity as Player
        val itemService = SMPRPG.getService(ItemService::class.java)

        // If this item is owned by someone and we are trying to pick it up, then stop.
        if (event.item.owner != null && event.item.owner != player.uniqueId) return

        // Collect all of our compressors to go through.
        val compressors = mutableListOf<ItemStack>()
        val contents = player.inventory.contents
        for (invItem in contents) {
            if (invItem != null) {
                if (itemService.getBlueprint(invItem) is PocketCompressorBlueprint) compressors.add(invItem)
            }
        }
        for (compressor in compressors) {
            val compressionItems = compressor.getData(DataComponentTypes.CONTAINER)
            for (itemToCompressTo in compressionItems!!.contents()) {
                if (itemToCompressTo.type != PocketCompressorBlueprint.DUMMY_MATERIAL) {
                    // Determine what the configured item decompresses into (the tier directly below it) and how
                    // many of that lower item a single pickup-batch must accumulate to compress back up into one.
                    val compressTargetId = itemService.getIdentifier(itemToCompressTo)
                    val (stepBelowId, decompressRatio) = CompressionGraph.decompressStep(compressTargetId) ?: continue
                    val stepBelowItem: ItemStack = itemService.resolveIdentifier(stepBelowId)?.clone() ?: continue
                    stepBelowItem.amount = decompressRatio

                    // We need to put our dropped item into an inventory and parse through it,
                    // as it is not a part of the player inventory yet.
                    val inventorizedItem = Bukkit.createInventory(player, 9)
                    inventorizedItem.addItem(event.item.itemStack.clone())
                    val overflow = mutableListOf<ItemStack>()
                    val itemsToAdd = mutableListOf<ItemStack>()
                    while (inventorizedItem.takeIfPresent(stepBelowItem)) {
                        itemsToAdd.add(itemToCompressTo)
                        //player.playSound(player, Sound.BLOCK_PISTON_EXTEND, 0.05f, 2.0f)  // This is driving me crazy
                    }

                    if (inventorizedItem.getItem(0) != null)
                        event.item.itemStack = inventorizedItem.getItem(0)!!.clone()
                    else event.item.itemStack = ItemStack.empty() // We exhausted the stack completely.


                    while (player.inventory.takeIfPresent(stepBelowItem)) {
                        itemsToAdd.add(itemToCompressTo)
                        //player.playSound(player, Sound.BLOCK_PISTON_EXTEND, 0.2f, 2.0f)
                    }
                    for (item in itemsToAdd) {
                        val overflowMap = player.inventory.addItem(item)
                        if (!overflowMap.isEmpty()) {
                            for (entry in overflowMap.entries) {
                                for (i in 0..<entry.key)
                                    overflow.add(entry.value)
                            }
                        }
                    }

                    for (item in overflow)
                        event.item.world.dropItem(event.entity.location, item)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerUseCompressor(event: PlayerInteractEvent) {
        if (event.item != null && (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)) {
            val bp = SMPRPG.getService(ItemService::class.java).getBlueprint(event.item!!)
            if (bp is PocketCompressorBlueprint) {
                MenuCompressor(event.player, event.item!!, bp).openMenu()
            }
        }
    }
}