package xyz.devvydont.smprpg.events.slayer

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import xyz.devvydont.smprpg.entity.base.LeveledEntity
import xyz.devvydont.smprpg.entity.slayer.SlayerBossInstance

class SlayerBossDeathEvent(@JvmField val slayer: SlayerBossInstance<*>) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}