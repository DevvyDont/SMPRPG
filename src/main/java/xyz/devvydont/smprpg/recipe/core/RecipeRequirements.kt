package xyz.devvydont.smprpg.recipe.core

import org.bukkit.entity.Player
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.skills.SkillType

/**
 * Gating conditions a player must satisfy to craft a recipe. Currently a set of minimum skill levels
 * (`skill -> level`); this is what "locks" a recipe.
 *
 * Enforced on the crafting paths only (the custom crafting GUI and the vanilla grid / recipe book) — passive
 * stations do not gate on requirements. Recipe-book auto-discovery is a separate mechanism (`unlocked_by`).
 */
data class RecipeRequirements(
    val skillLevels: Map<SkillType, Int> = emptyMap(),
) {

    val isEmpty: Boolean
        get() = skillLevels.isEmpty()

    /** True if [player] satisfies every requirement. */
    fun meets(player: Player): Boolean = unmet(player).isEmpty()

    /**
     * The `skill -> required level` entries [player] does not yet satisfy (empty if they meet everything).
     * Used both to gate crafting and to describe what is missing in the locked-recipe tooltip.
     */
    fun unmet(player: Player): Map<SkillType, Int> {
        if (skillLevels.isEmpty()) return emptyMap()
        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        val currentLevels = leveledPlayer.skills.associate { it.type to it.level }
        return skillLevels.filter { (type, required) -> (currentLevels[type] ?: 0) < required }
    }
}
