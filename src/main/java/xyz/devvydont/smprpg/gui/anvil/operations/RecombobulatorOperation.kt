package xyz.devvydont.smprpg.gui.anvil.operations

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.gui.anvil.AnvilOperation
import xyz.devvydont.smprpg.gui.anvil.AnvilResult
import xyz.devvydont.smprpg.items.blueprints.augment.Recombobulator
import xyz.devvydont.smprpg.items.blueprints.resources.scrolls.DynamicEnchantingScroll
import xyz.devvydont.smprpg.items.blueprints.tools.augments.RepairCore
import xyz.devvydont.smprpg.services.ItemService

/**
 * Adds a spell slot to a tome by consuming a single Necronomicon Excerpt, up to the tome's excerpt cap.
 */
object RecombobulatorOperation : AnvilOperation {

    override fun tryApply(player: LeveledPlayer, primary: ItemStack, secondary: ItemStack): AnvilResult? {
        // The secondary item must be a Recombobulator, and the primary item must be an SMPRPG blueprinted item.
        if (ItemService.blueprint(secondary) !is Recombobulator) return null
        val isRecombed = primary.persistentDataContainer.getOrDefault(Recombobulator.RECOMBOBULATOR_KEY,
            PersistentDataType.BOOLEAN, false)
        if (isRecombed) return null
        val blueprint = ItemService.blueprint(primary)
        when (blueprint) {
            is DynamicEnchantingScroll, is RepairCore -> { return null }
        }
        val result = primary.clone()
        result.amount = 1
        Recombobulator.addRecombToITem(result)
        blueprint.updateItemData(result)
        return AnvilResult(result, 1)
    }
}
