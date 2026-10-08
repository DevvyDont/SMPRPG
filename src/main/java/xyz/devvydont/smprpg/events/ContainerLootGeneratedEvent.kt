package xyz.devvydont.smprpg.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.loot.LootTable

class ContainerLootGeneratedEvent(@JvmField val player: Player, @JvmField val lootTable: LootTable) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}
