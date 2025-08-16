package xyz.devvydont.smprpg.skills

import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent.ExperienceSource
import xyz.devvydont.smprpg.events.skills.SkillExperiencePostGainEvent
import xyz.devvydont.smprpg.events.skills.SkillLevelUpEvent
import xyz.devvydont.smprpg.services.AttributeService
import xyz.devvydont.smprpg.skills.SkillGlobals.getCumulativeExperienceForLevel
import xyz.devvydont.smprpg.skills.SkillGlobals.getExperienceForLevel
import xyz.devvydont.smprpg.skills.SkillGlobals.getLevelForExperience
import xyz.devvydont.smprpg.skills.SkillGlobals.totalExperienceCap
import xyz.devvydont.smprpg.skills.rewards.ISkillReward
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Skill instances are helper instances to interface with skill modification on entities.
 * They handle things such as skill experience combos, and provide useful helper methods for
 * common experience calculations that can make recursive experience formulas easier to work with.
 * In most instances, you only really have to call [SkillInstance.addExperience]
 */
class SkillInstance(@JvmField val owner: Player, @JvmField val type: SkillType) {
    // Combo related things, certain effects could use these but mainly used as a display thing
    var currentCombo: Int = 0
    var expireComboAt: Long = 0
    var comboLengthMs: Long = 2000L

    fun checkValidCombo() {
        // Combo is valid if the expiry time is larger than the current time
        if (expireComboAt > System.currentTimeMillis())
            return

        currentCombo = 0
    }

    fun increaseCombo(amount: Int) {
        currentCombo += amount
        expireComboAt = System.currentTimeMillis() + comboLengthMs
    }

    fun getCombo(): Int {
        checkValidCombo()
        return currentCombo
    }

    var experience: Int
        /**
         * Query how much total cumulative experience the owning player has.
         * @return The amount of experience.
         */
        get() = owner.persistentDataContainer.getOrDefault(type.key, PersistentDataType.INTEGER, 0)
        /**
         * Set the total cumulative experience the owning player has.
         * @param experience The amount of experience to set.
         */
        set(experience) {
            owner.persistentDataContainer.set(type.key, PersistentDataType.INTEGER, experience)
        }

    /**
     * Add experience to the owning player. Automatically handles level up and event calling logic for you.
     * @param experience The amount of experience to add.
     * @param source The source of the experience.
     */
    @JvmOverloads
    fun addExperience(experience: Int, source: ExperienceSource = ExperienceSource.UNKNOWN) {
        // Construct an experience gain event. This allows listeners to modify the experience we are gaining.

        val event = SkillExperienceGainEvent(source, experience, this)
        event.callEvent()
        if (event.isCancelled || event.experienceEarned <= 0)
            return

        // Add the experience and take note of what level we are before and after
        val oldLevel = this.level
        val expCap = totalExperienceCap
        var expEarned = event.experienceEarned.toDouble()
        if (source != ExperienceSource.COMMANDS)
            expEarned *= 1.0 + (getProficiencyStacks() / 100.0)
        event.experienceEarned = expEarned.roundToInt()
        val newExp: Double = this.experience + expEarned
        this.experience = min(expCap, newExp.roundToInt())
        val newLevel = this.level

        // Combo increasing
        checkValidCombo()
        increaseCombo(event.experienceEarned)

        // Another event for post experience gains where no modifications can be made, but we have real XP values
        val leveledUp = oldLevel < newLevel
        SkillExperiencePostGainEvent(source, event.experienceEarned, this, leveledUp).callEvent()

        // If we happened to increase our level, call another event for listeners to react to
        if (leveledUp)
            SkillLevelUpEvent(source, this, oldLevel).callEvent()
    }

    val level: Int
        /**
         * Check what skill level the owning player is for this skill.
         * @return The skill level.
         */
        get() = getLevelForExperience(this.experience)

    val nextLevel: Int
        /**
         * Check what skill level is next. This is effectively the same as [SkillInstance.level] + 1.
         * @return The next level.
         */
        get() = this.level + 1

    val experienceProgress: Int
        /**
         * Calculates the experience gained for ONLY the current level. Any cumulative experience gained
         * from previous levels is ignored.
         * @return The amount of experience gained only for the current level.
         */
        get() = this.experience - getCumulativeExperienceForLevel(this.level)

    val nextExperienceThreshold: Int
        /**
         * Check the cumulative experience threshold to reach the next level. This is a total experience check.
         * @return The amount of experience required (total!) to level up.
         */
        get() = getExperienceForLevel(this.nextLevel)

    val experienceForNextLevel: Int
        /**
         * Returns the amount of experience necessary to cause a level up.
         * @return An amount of experience.
         */
        get() = getCumulativeExperienceForLevel(this.nextLevel) - this.experience

    /**
     * Checks if a certain experience reward will trigger a level up.
     * @param experience The experience to check.
     * @return True if this player will level up from the experience.
     */
    fun willLevelUp(experience: Int): Boolean {
        return experience >= this.experienceForNextLevel
    }

    /**
     * Retrieve the rewards that a certain skill level will grant.
     * @param level The level to check rewards for.
     * @return A collection of rewards.
     */
    fun getRewards(level: Int): MutableCollection<ISkillReward> {
        return this.type.rewards.getRewardsForLevel(level)
    }

    fun getProficiencyStacks(): Double {
        val proficiencyInstance = AttributeService.instance.getOrCreateAttribute(owner, AttributeWrapper.PROFICIENCY);
        return proficiencyInstance.getValue();
    }
}
