package xyz.devvydont.smprpg.gui.anvil.operations

import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.gui.anvil.AnvilExperience
import xyz.devvydont.smprpg.gui.anvil.AnvilOperation
import xyz.devvydont.smprpg.gui.anvil.AnvilResult
import xyz.devvydont.smprpg.items.blueprints.tools.augments.RepairCore
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.services.ItemService
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Repairs an item using a repair core. A single core restores a percentage of the item's max durability
 * based on the core's rarity relative to the item's rarity. The mining skill bonus does NOT apply here.
 */
object RepairCoreOperation : AnvilOperation {

    private const val CORES_CONSUMED = 1

    override fun tryApply(player: LeveledPlayer, primary: ItemStack, secondary: ItemStack): AnvilResult? {
        // The secondary item must be a repair core.
        if (ItemService.blueprint(secondary) !is RepairCore) return null

        // The primary item must be a piece of equipment that takes durability damage, and actually be damaged.
        val blueprint = ItemService.blueprint(primary)
        if (blueprint !is IBreakableEquipment) return null
        val damage = primary.getDataOrDefault(DataComponentTypes.DAMAGE, 0)!!
        if (damage <= 0) return null

        val maxDurability = primary.getDataOrDefault(DataComponentTypes.MAX_DAMAGE, 0)!!
        val repairAmount = (RepairCore.getRepairValue(secondary, primary) * maxDurability).roundToInt()

        val result = primary.clone()
        result.setData(DataComponentTypes.DAMAGE, max(damage - repairAmount, 0))
        blueprint.updateItemData(result)

        val experience = AnvilExperience.repairReward(player, blueprint, primary, result)
        return AnvilResult(result, CORES_CONSUMED, experience)
    }
}
