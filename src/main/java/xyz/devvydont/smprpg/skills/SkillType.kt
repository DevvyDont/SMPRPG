package xyz.devvydont.smprpg.skills

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.NamespacedKey
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.skills.rewards.SkillRewardContainer
import xyz.devvydont.smprpg.skills.rewards.definitions.*
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils

enum class SkillType(val rewards: SkillRewardContainer, val proficiencyAttribute : AttributeWrapper, val color : TextColor) {
    COMBAT(CombatSkillRewards(), AttributeWrapper.COMBAT_PROFICIENCY, NamedTextColor.DARK_RED),
    WOODCUTTING(WoodcuttingSkillRewards(), AttributeWrapper.WOODCUTTING_PROFICIENCY, NamedTextColor.GOLD),
    MINING(MiningSkillRewards(), AttributeWrapper.MINING_PROFICIENCY, NamedTextColor.GRAY),
    MAGIC(MagicSkillRewards(), AttributeWrapper.MAGIC_PROFICIENCY, NamedTextColor.LIGHT_PURPLE),
    FARMING(FarmingSkillRewards(), AttributeWrapper.FARMING_PROFICIENCY, NamedTextColor.DARK_GREEN),
    FISHING(FishingSkillRewards(), AttributeWrapper.FISHING_PROFICIENCY, NamedTextColor.AQUA),
    SLAYER(SlayerSkillRewards(), AttributeWrapper.SLAYER_PROFICIENCY, NamedTextColor.DARK_PURPLE);

    val identifier: String = "skill_${name.lowercase()}"

    val key: NamespacedKey = NamespacedKey("smprpg", identifier)

    val displayName: String get() = MinecraftStringUtils.getTitledString(this.name)
}
