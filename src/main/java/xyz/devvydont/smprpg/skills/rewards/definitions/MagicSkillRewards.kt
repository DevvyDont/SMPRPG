package xyz.devvydont.smprpg.skills.rewards.definitions

import org.bukkit.attribute.AttributeModifier
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.skills.SkillGlobals
import xyz.devvydont.smprpg.skills.rewards.EnchantmentSkillReward
import xyz.devvydont.smprpg.skills.rewards.SkillRewardContainer

class MagicSkillRewards : SkillRewardContainer() {
    init {
        // Loop through all the enchantments in the game. Add the enchantment unlock to the rewards
        for (enchantment in SMPRPG.getService(EnchantmentService::class.java).customEnchantments)
            if (enchantment.getSkillRequirement() > 0)
                addReward(enchantment.getSkillRequirement(), EnchantmentSkillReward(enchantment))

        // Add intelligence to every level.
        this.addAttributeRewardEveryLevel(
            AttributeWrapper.INTELLIGENCE,
            AttributeModifier.Operation.ADD_NUMBER,
            SkillGlobals.INT_PER_LEVEL
        )

        // Add luck every 4 levels. Ideally, this doesn't get out of control.
        this.addAttributeRewardEveryXLevels(
            AttributeWrapper.LUCK,
            AttributeModifier.Operation.ADD_NUMBER,
            SkillGlobals.LUCK_PER_4_LEVELS,
            SkillGlobals.LUCK_LEVEL_FREQUENCY
        )

        // Typical HP every level
        this.addScalingAttributeRewardEveryXLevels(
            AttributeWrapper.HEALTH,
            AttributeModifier.Operation.ADD_NUMBER,
            SkillGlobals.HP_PER_5_LEVELS,
            SkillGlobals.HP_LEVEL_FREQUENCY
        )

        // Give coins for every level.
        this.addCoinsEveryLevel()
    }
}
