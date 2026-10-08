package xyz.devvydont.smprpg.gui.anvil.operations

import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.gui.anvil.AnvilExperience
import xyz.devvydont.smprpg.gui.anvil.AnvilOperation
import xyz.devvydont.smprpg.gui.anvil.AnvilResult
import xyz.devvydont.smprpg.items.blueprints.equipment.MagicMirror
import xyz.devvydont.smprpg.items.blueprints.equipment.MagicMirrorShard
import xyz.devvydont.smprpg.items.blueprints.tools.augments.RepairCore
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.services.ItemService
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Repairs an item using a repair core. A single core restores a percentage of the item's max durability
 * based on the core's rarity relative to the item's rarity. The mining skill bonus does NOT apply here.
 */
object MagicMirrorOperation : AnvilOperation {

    private const val SHARDS_CONSUMED = 1

    override fun tryApply(player: LeveledPlayer, primary: ItemStack, secondary: ItemStack): AnvilResult? {
        // The primary item must be a magic mirror
        val blueprint = ItemService.blueprint(primary)
        if (blueprint !is MagicMirror) return null

        // The secondary item must be a magic mirror shard
        val secondBlueprint = ItemService.blueprint(secondary)
        if (secondBlueprint !is MagicMirrorShard) return null

        // Return null if we already have the mode unlocked.
        val mode = secondBlueprint.mode
        if (blueprint.hasModeUnlocked(primary, mode)) return null

        // We have passed all checks, create a cloned mirror with this mode unlocked.
        val result = primary.clone()
        blueprint.withModeUnlocked(result, mode)
        return AnvilResult(result, SHARDS_CONSUMED)
    }
}
