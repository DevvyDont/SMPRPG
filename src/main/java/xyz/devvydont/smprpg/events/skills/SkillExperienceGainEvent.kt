package xyz.devvydont.smprpg.events.skills

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import xyz.devvydont.smprpg.skills.SkillInstance
import xyz.devvydont.smprpg.skills.SkillType
import kotlin.math.max

class SkillExperienceGainEvent(
    var source: ExperienceSource,
    var experienceEarned: Int,
    val skill: SkillInstance
) : Event(), Cancellable {
    enum class ExperienceSource {
        KILL,
        ORE,
        SMELT,
        WOODCUTTING,
        HARVEST,
        BREED,
        TAME,
        FEED,
        LOOT,
        FISH,
        ENCHANT,
        BREW,
        FORGE,
        XP,
        COMMANDS,
        UNKNOWN
    }

    private var cancelled = false

    private fun validateExperience() {
        experienceEarned = max(0, experienceEarned)
    }

    val player: Player
        get() = this.skill.owner

    val skillType: SkillType
        get() = skill.type

    fun addExperienceEarned(experience: Int) {
        experienceEarned += experience
        validateExperience()
    }

    fun multiplyExperienceEarned(multiplier: Double) {
        experienceEarned = (experienceEarned * multiplier).toInt()
        validateExperience()
    }

    val isLevelUp: Boolean
        get() = skill.willLevelUp(experienceEarned)

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(b: Boolean) {
        this.cancelled = b
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }


    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}
