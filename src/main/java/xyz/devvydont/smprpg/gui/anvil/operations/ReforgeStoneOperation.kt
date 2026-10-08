package xyz.devvydont.smprpg.gui.anvil.operations

import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.gui.anvil.AnvilExperience
import xyz.devvydont.smprpg.gui.anvil.AnvilOperation
import xyz.devvydont.smprpg.gui.anvil.AnvilResult
import xyz.devvydont.smprpg.items.interfaces.ReforgeApplicator
import xyz.devvydont.smprpg.services.ItemService

/**
 * Applies a reforge to an item by consuming a reforge applicator (such as a reforge stone), provided the
 * reforge is allowed on the item's classification.
 */
object ReforgeStoneOperation : AnvilOperation {

    private const val APPLICATORS_CONSUMED = 1

    override fun tryApply(player: LeveledPlayer, primary: ItemStack, secondary: ItemStack): AnvilResult? {
        // The secondary item must be able to apply a reforge.
        val applicator = ItemService.blueprint(secondary)
        if (applicator !is ReforgeApplicator) return null

        val reforgeType = applicator.reforgeType
        val reforge = SMPRPG.getService(ItemService::class.java).getReforge(reforgeType) ?: return null

        // The reforge must be valid for this type of item.
        val blueprint = ItemService.blueprint(primary)
        if (!reforgeType.isAllowed(blueprint.itemClassification)) return null

        // Apply the reforge. ReforgeBase#apply already refreshes the item's data.
        val result = primary.clone()
        reforge.apply(result)

        val experience = AnvilExperience.reforgeReward(player, blueprint, primary, reforge)
        return AnvilResult(result, APPLICATORS_CONSUMED, experience)
    }
}
