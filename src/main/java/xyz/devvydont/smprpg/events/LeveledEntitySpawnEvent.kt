package xyz.devvydont.smprpg.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import xyz.devvydont.smprpg.entity.base.LeveledEntity

class LeveledEntitySpawnEvent(@JvmField val entity: LeveledEntity<*>) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}
