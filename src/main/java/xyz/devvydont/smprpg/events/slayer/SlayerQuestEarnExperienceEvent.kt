package xyz.devvydont.smprpg.events.slayer

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import xyz.devvydont.smprpg.entity.base.LeveledEntity
import xyz.devvydont.smprpg.entity.player.LeveledPlayer

class SlayerQuestEarnExperienceEvent(val player : LeveledPlayer, val mobKilled : LeveledEntity<*>, val experience : Int) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}