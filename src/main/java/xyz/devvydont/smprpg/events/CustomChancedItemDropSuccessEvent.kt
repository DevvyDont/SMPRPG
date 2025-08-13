package xyz.devvydont.smprpg.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.util.items.LootSource

// Called when a player successfully rolls for an item from a ChancedItemDrop roll
class CustomChancedItemDropSuccessEvent(
    val player: Player,
    val chance: Double,
    val item: ItemStack,
    val source: LootSource
) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    val formattedChance: String
        get() = String.format("%.4f%%", chance * 100)

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}
