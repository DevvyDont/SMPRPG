package xyz.devvydont.smprpg.events.block

import org.bukkit.entity.FallingBlock
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class AscendingBlockRegisterEvent(@JvmField val fallingBlock: FallingBlock) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}