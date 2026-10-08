package xyz.devvydont.smprpg.skills.rewards.definitions

import org.bukkit.attribute.AttributeModifier
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.skills.SkillGlobals
import xyz.devvydont.smprpg.skills.rewards.SkillRewardContainer

/**
 * The rewards received from combat. Combat related stats should be given, like damage and criticals.
 */
class SlayerSkillRewards : SkillRewardContainer() {
    init {
        // Slayer gives a tiny amount of global proficiency per level

        this.addAttributeRewardEveryLevel(
            AttributeWrapper.PROFICIENCY,
            AttributeModifier.Operation.ADD_NUMBER,
            SkillGlobals.PROFICIENCY_PER_LEVEL
        )

        // Add luck every 10 levels. Ideally, this doesn't get out of control.
        this.addAttributeRewardEveryXLevels(
            AttributeWrapper.LUCK,
            AttributeModifier.Operation.ADD_NUMBER,
            SkillGlobals.SLAYER_LUCK_PER_10_LEVELS,
            SkillGlobals.SLAYER_LUCK_LEVEL_FREQUENCY
        )

        // Typical HP every level
        this.addFlatAttributeRewardEveryLevel(
            AttributeWrapper.HEALTH,
            AttributeModifier.Operation.ADD_NUMBER,
            SkillGlobals.HP_PER_LEVEL
        )

        // Give coins for every level.
        this.addCoinsEveryLevel()
    }
}
