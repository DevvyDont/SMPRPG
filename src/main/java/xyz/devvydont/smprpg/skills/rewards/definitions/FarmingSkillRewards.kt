package xyz.devvydont.smprpg.skills.rewards.definitions

import org.bukkit.attribute.AttributeModifier
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.skills.SkillGlobals
import xyz.devvydont.smprpg.skills.rewards.SkillRewardContainer

/**
 * The rewards received from leveling up farming. Farming related skills should be given, like farming fortune.
 */
class FarmingSkillRewards : SkillRewardContainer() {
    init {
        // Add farming fortune every level
        this.addAttributeRewardEveryLevel(
            AttributeWrapper.FARMING_FORTUNE,
            AttributeModifier.Operation.ADD_NUMBER,
            SkillGlobals.FORTUNE_PER_LEVEL
        )

        // Loop every 4 levels and add REGEN
        this.addAttributeRewardEveryXLevels(
            AttributeWrapper.REGENERATION,
            AttributeModifier.Operation.ADD_NUMBER,
            SkillGlobals.REGENERATION_PER_2_LEVELS,
            SkillGlobals.REGENERATION_LEVEL_FREQUENCY
        )

        // Loop every 10 levels and add Critter Chance
        this.addAttributeRewardEveryXLevels(
            AttributeWrapper.CRITTER_CHANCE,
            AttributeModifier.Operation.ADD_NUMBER,
            SkillGlobals.CRITTER_CHANCE_PER_10_LEVELS,
            SkillGlobals.CRITTER_CHANCE_LEVEL_FREQUENCY
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
