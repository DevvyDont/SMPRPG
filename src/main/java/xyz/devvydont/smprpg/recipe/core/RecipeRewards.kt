package xyz.devvydont.smprpg.recipe.core

import org.bukkit.entity.Player
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent.ExperienceSource
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward

/**
 * Rewards granted to a player when they successfully produce a recipe — coins and/or skill experience.
 *
 * Reusable across every recipe type. A station applies it via [grant] wherever it has an attributable player
 * (the crafter for the crafting table, the bound player for the cooking pot, the placer for a campfire);
 * stations with no clear recipient (e.g. a hopper-fed furnace) simply never call it.
 */
data class RecipeRewards(
    val coins: Long = 0,
    val skillXp: Map<SkillType, Int> = emptyMap(),
) {

    val isEmpty: Boolean
        get() = coins <= 0 && skillXp.values.none { it > 0 }

    /**
     * Grant these rewards to [player]. [source] tags the skill-XP gain for the skill system (e.g. `FORGE` for
     * the crafting table, `COOK` for the cooking pot / campfire). A no-op when [isEmpty].
     */
    fun grant(player: Player, source: ExperienceSource) {
        if (isEmpty) return

        if (coins > 0)
            SMPRPG.getService(EconomyService::class.java).addMoney(player, coins.toDouble())

        if (skillXp.isNotEmpty()) {
            val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
            val reward = SkillExperienceReward()
            for ((skill, xp) in skillXp)
                reward.add(skill, xp)
            reward.apply(leveledPlayer, source)
        }
    }
}
