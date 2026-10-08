package xyz.devvydont.smprpg.events.slayer

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import xyz.devvydont.smprpg.entity.base.LeveledEntity

class SlayerSpawnBossEvent(@JvmField val entity: LeveledEntity<*>?, val spawner: Player) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}