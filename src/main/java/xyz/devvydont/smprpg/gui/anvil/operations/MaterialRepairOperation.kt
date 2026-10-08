package xyz.devvydont.smprpg.gui.anvil.operations

import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.gui.anvil.AnvilExperience
import xyz.devvydont.smprpg.gui.anvil.AnvilOperation
import xyz.devvydont.smprpg.gui.anvil.AnvilResult
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.services.ItemService
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Repairs an item using its designated repair material (e.g. iron ingots for an iron sword). Each material
 * restores a percentage of the item's max durability that scales with the player's mining skill. Only as many
 * materials as are needed to fully repair the item are consumed, capped by how many are supplied.
 *
 * The valid materials for an item are declared by its blueprint through [IRepairable.repairMaterial].
 */
object MaterialRepairOperation : AnvilOperation {

    // Fraction of max durability restored per material, plus the bonus granted per mining level.
    private const val BASE_REPAIR_PERCENT = 0.1
    private const val MINING_REPAIR_BONUS_PER_LEVEL = 0.005

    override fun tryApply(player: LeveledPlayer, primary: ItemStack, secondary: ItemStack): AnvilResult? {
        // The primary item must declare repair materials.
        val blueprint = ItemService.blueprint(primary)
        if (blueprint !is IRepairable) return null

        // The item must actually be damaged.
        val damage = primary.getDataOrDefault(DataComponentTypes.DAMAGE, 0)!!
        if (damage <= 0) return null

        // The secondary item must be one of the item's accepted repair materials.
        if (!isRepairMaterial(blueprint, secondary)) return null

        val maxDurability = primary.getDataOrDefault(DataComponentTypes.MAX_DAMAGE, 0)!!
        val repairPercentPerMaterial = BASE_REPAIR_PERCENT + MINING_REPAIR_BONUS_PER_LEVEL * player.miningSkill.level
        val repairPerMaterial = (maxDurability * repairPercentPerMaterial).roundToInt()
        if (repairPerMaterial <= 0) return null

        // Consume only as many materials as the repair needs, never more than are supplied.
        val materialsNeeded = ceil(damage.toDouble() / repairPerMaterial).toInt()
        val materialsConsumed = min(secondary.amount, materialsNeeded)

        val result = primary.clone()
        result.setData(DataComponentTypes.DAMAGE, max(damage - repairPerMaterial * materialsConsumed, 0))
        blueprint.updateItemData(result)

        val experience = AnvilExperience.repairReward(player, blueprint, primary, result)
        return AnvilResult(result, materialsConsumed, experience)
    }

    private fun isRepairMaterial(repairable: IRepairable, secondary: ItemStack): Boolean =
        repairable.repairMaterial.any { ItemService.isInternalIdMatch(it, secondary) }
}
