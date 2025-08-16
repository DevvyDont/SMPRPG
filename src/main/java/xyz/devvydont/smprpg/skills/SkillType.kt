package xyz.devvydont.smprpg.skills

import org.bukkit.NamespacedKey
import xyz.devvydont.smprpg.skills.rewards.SkillRewardContainer
import xyz.devvydont.smprpg.skills.rewards.definitions.*
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils

enum class SkillType(val rewards: SkillRewardContainer) {
    COMBAT(CombatSkillRewards()),
    WOODCUTTING(WoodcuttingSkillRewards()),
    MINING(MiningSkillRewards()),
    MAGIC(MagicSkillRewards()),
    FARMING(FarmingSkillRewards()),
    FISHING(FishingSkillRewards());

    val identifier: String = "skill_${name.lowercase()}"

    val key: NamespacedKey = NamespacedKey("smprpg", identifier)

    val displayName: String
        get() = MinecraftStringUtils.getTitledString(this.name)
}
