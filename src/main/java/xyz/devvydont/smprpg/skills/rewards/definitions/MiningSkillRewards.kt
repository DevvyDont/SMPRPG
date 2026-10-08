package xyz.devvydont.smprpg.skills.rewards.definitions

import org.bukkit.attribute.AttributeModifier
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.skills.SkillGlobals
import xyz.devvydont.smprpg.skills.rewards.SkillRewardContainer

/**
 * Rewards to get from mining. We should give rewards like mining efficiency and fortune.
 */
class MiningSkillRewards : SkillRewardContainer() {

    init {
        this.addAttributeRewardEveryLevel(
            AttributeWrapper.MINING_FORTUNE,
            AttributeModifier.Operation.ADD_NUMBER,
            SkillGlobals.FORTUNE_PER_LEVEL
        )

        // Loop every 4 levels and add mining eff.
        this.addAttributeRewardEveryXLevels(
            AttributeWrapper.MINING_SPEED,
            AttributeModifier.Operation.ADD_NUMBER,
            SkillGlobals.MINING_SPEED_PER_4_LEVELS.toDouble(),
            SkillGlobals.MINING_SPEED_LEVEL_FREQUENCY
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
