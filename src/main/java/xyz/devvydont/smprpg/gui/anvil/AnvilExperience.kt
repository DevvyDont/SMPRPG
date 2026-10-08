package xyz.devvydont.smprpg.gui.anvil

import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.items.base.SMPItemBlueprint
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Builds the skill experience rewards granted by anvil operations. Centralized so that every operation
 * that repairs durability or applies a reforge shares the exact same reward formula.
 */
object AnvilExperience {

    private const val REFORGE_XP_BASE = 5.0
    private const val REPAIR_XP_PER_DURABILITY = 3

    /**
     * Reward for restoring durability on an item, scaled by rarity and how much durability was restored.
     */
    fun repairReward(
        player: LeveledPlayer,
        blueprint: SMPItemBlueprint,
        before: ItemStack,
        after: ItemStack
    ): SkillExperienceReward {
        val restoredDurability = usableDurability(after) - usableDurability(before)
        val rarityFactor = blueprint.getRarity(before).ordinal + 1
        return player.generateSkillExperienceReward()
            .add(SkillType.MINING, rarityFactor * (restoredDurability * REPAIR_XP_PER_DURABILITY))
    }

    /**
     * Reward for applying a reforge to an item, scaled by rarity and the reforge's power rating.
     */
    fun reforgeReward(
        player: LeveledPlayer,
        blueprint: SMPItemBlueprint,
        item: ItemStack,
        reforge: ReforgeBase
    ): SkillExperienceReward {
        val rarityFactor = blueprint.getRarity(item).ordinal + 1
        val baseExperience = (rarityFactor * REFORGE_XP_BASE.pow(reforge.powerRating)).toInt()
        return player.generateSkillExperienceReward()
            .add(SkillType.MAGIC, baseExperience)
            .add(SkillType.MINING, (baseExperience / 2.0).roundToInt())
    }

    private fun usableDurability(item: ItemStack): Int {
        val maxDurability = item.getDataOrDefault(DataComponentTypes.MAX_DAMAGE, 0)!!
        val damage = item.getDataOrDefault(DataComponentTypes.DAMAGE, 0)!!
        return maxDurability - damage
    }
}
