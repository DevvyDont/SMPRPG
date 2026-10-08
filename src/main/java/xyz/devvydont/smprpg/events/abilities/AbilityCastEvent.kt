package xyz.devvydont.smprpg.events.abilities

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.ability.AbilityCost
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.items.interfaces.IAbilityCaster

class AbilityCastEvent(val ability: IAbilityCaster.AbilityEntry,
                       val player: LeveledPlayer,
                       val item: ItemStack,
                       val cooldown: Long) : Event() {

   var abilityCost : AbilityCost = ability.cost

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}