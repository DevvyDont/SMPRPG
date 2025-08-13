package xyz.devvydont.smprpg.events.skills

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent.ExperienceSource
import xyz.devvydont.smprpg.skills.SkillInstance
import xyz.devvydont.smprpg.skills.SkillType

class SkillLevelUpEvent(val source: ExperienceSource, val skill: SkillInstance, val oldLevel: Int) : Event() {

    val player: Player
        get() = this.skill.owner

    val skillType: SkillType
        get() = skill.type

    val newLevel: Int
        get() = skill.level

    override fun getHandlers(): HandlerList {
        return handlerList
    }


    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}
