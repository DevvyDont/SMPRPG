package xyz.devvydont.smprpg.gui.anvil

import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward

/**
 * The outcome of a successful anvil operation, produced by an [AnvilOperation] when its inputs are valid.
 *
 * @property result The finished item to preview in the result slot and hand to the player upon committing.
 * @property secondaryConsumed How many items to remove from the secondary input slot when committed.
 * @property experience Skill experience to award to the player when the operation is committed.
 */
data class AnvilResult(
    val result: ItemStack,
    val secondaryConsumed: Int,
    val experience: SkillExperienceReward = SkillExperienceReward.empty()
)

/**
 * A single behavior an anvil can perform on a pair of inputs (repairing, applying a reforge, etc.).
 *
 * Operations are evaluated in registration order (see [AnvilOperations]); the first one to return a
 * non-null [AnvilResult] wins. To add a new anvil behavior, implement this interface and register the
 * implementation in [AnvilOperations].
 */
interface AnvilOperation {

    /**
     * Attempts to build a result for the given inputs.
     *
     * @param player The player using the anvil, used for skill-based bonuses and experience rewards.
     * @param primary The item in the primary (left) input slot. Never empty.
     * @param secondary The item in the secondary (right) input slot. Never empty.
     * @return A result if this operation applies to the inputs, otherwise null.
     */
    fun tryApply(player: LeveledPlayer, primary: ItemStack, secondary: ItemStack): AnvilResult?
}
