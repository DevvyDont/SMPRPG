package xyz.devvydont.smprpg.util.extensions

import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.base.SMPItemBlueprint
import xyz.devvydont.smprpg.recipe.CompressionGraph

/**
 * Calculates the worth of a compressed item by walking down to the base item's per-item sell value, then
 * multiplying by the total compression ratio and the stack size. Returns the stack amount as a fallback when
 * no base worth can be determined.
 *
 * The chain data comes entirely from the unified recipe registry (via [CompressionGraph]); the receiver is
 * any item blueprint so resource families can keep delegating their worth here.
 */
fun SMPItemBlueprint.calculateCompressedWorth(itemStack: ItemStack): Int {
    return CompressionGraph.worth(itemStack)
}
