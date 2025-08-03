package xyz.devvydont.smprpg.skills.utils

import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent.ExperienceSource
import xyz.devvydont.smprpg.skills.SkillType
import kotlin.math.max

/**
 * Used as a way to contain multiple sources of skill experience in one.
 */
class SkillExperienceReward {
    private val amounts: MutableMap<SkillType, Int> = HashMap()

    fun add(type: SkillType, amount: Int): SkillExperienceReward {
        amounts.put(type, max(0, amount))
        return this
    }

    /**
     * Shortcut method to apply all the skill experience in this container to a player.
     * @param player The player to apply skill experience to. Can be called multiple times.
     */
    fun apply(player: LeveledPlayer, source: ExperienceSource) {
        for (skill in player.skills) {
            val amount = amounts[skill.type]
            if (amount == null)
                continue

            skill.addExperience(amount, source)
        }
    }

    val isEmpty: Boolean
        get() {
            if (amounts.isEmpty())
                return true

            for (amount in amounts.values)
                if (amount > 0)
                    return false

            return true
        }

    fun multiply(multiplier: Double) {
        for (entry in amounts.entries)
            add(entry.key, (entry.value * multiplier).toInt())
    }

    companion object {
        @JvmStatic
        fun empty(): SkillExperienceReward {
            return SkillExperienceReward()
        }

        /**
         * Call when you want a simple experience reward for one experience type.
         * @param type The type of skill experience.
         * @param amount The amount of skill experience.
         * @return A built skill experience object.
         */
        @JvmStatic
        fun of(type: SkillType, amount: Int): SkillExperienceReward {
            return SkillExperienceReward().add(type, amount)
        }

        /**
         * Call when you want multiple skills to be awarded with the same amount.
         * @param amount The amount of skill experience.
         * @param types All the skills to get experience.
         * @return A built skill experience object.
         */
        fun withMultipleSkills(amount: Int, vararg types: SkillType): SkillExperienceReward {
            val instance = SkillExperienceReward()
            for (type in types)
                instance.add(type, amount)
            return instance
        }
    }
}
