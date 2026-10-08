package xyz.devvydont.smprpg.gui.anvil.operations

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.gui.anvil.AnvilOperation
import xyz.devvydont.smprpg.gui.anvil.AnvilResult
import xyz.devvydont.smprpg.items.blueprints.resources.slayer.drops.NecronomiconExcerpts
import xyz.devvydont.smprpg.items.blueprints.tomes.TomeBlueprint
import xyz.devvydont.smprpg.services.ItemService

/**
 * Adds a spell slot to a tome by consuming a single Necronomicon Excerpt, up to the tome's excerpt cap.
 */
object NecronomiconExcerptOperation : AnvilOperation {

    private const val EXCERPTS_CONSUMED = 1

    override fun tryApply(player: LeveledPlayer, primary: ItemStack, secondary: ItemStack): AnvilResult? {
        // The secondary item must be a Necronomicon Excerpt and the primary must be a tome.
        if (ItemService.blueprint(secondary) !is NecronomiconExcerpts) return null
        val blueprint = ItemService.blueprint(primary)
        if (blueprint !is TomeBlueprint) return null

        // The tome must not already be maxed out on excerpts.
        val excerpts = primary.persistentDataContainer.getOrDefault(
            NecronomiconExcerpts.TOME_SPELL_COUNT_MODIFIER, PersistentDataType.INTEGER, 0
        )
        if (excerpts >= NecronomiconExcerpts.MAX_EXCERPTS) return null

        val result = primary.clone()
        NecronomiconExcerpts.addExcerptsToTome(result)
        blueprint.updateItemData(result)
        return AnvilResult(result, EXCERPTS_CONSUMED)
    }
}
