package xyz.devvydont.smprpg.events.skills

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent.ExperienceSource
import xyz.devvydont.smprpg.skills.SkillInstance
import xyz.devvydont.smprpg.skills.SkillType

class SkillExperiencePostGainEvent(
    val source: ExperienceSource,
    val experienceEarned: Int,
    val skill: SkillInstance,
    val isLevelUp: Boolean
) : Event() {
    private val cancelled = false

    val player: Player
        get() = this.skill.owner

    val skillType: SkillType
        get() = skill.type

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}
