package xyz.devvydont.smprpg.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.persistence.PersistentDataHolder
import xyz.devvydont.smprpg.attribute.CustomAttributeContainer

class CustomAttributeContainerUpdatedEvent(
    val holder: PersistentDataHolder,
    val container: CustomAttributeContainer
) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}
