package xyz.devvydont.smprpg.recipe.core

import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.services.ItemService

/**
 * A recipe result: an item, how many to produce, and the probability it is produced at all
 * (`chance` of 1.0 = always). Probability supports stations like the cutting board with multiple
 * chance-based outputs.
 */
data class RecipeOutput(val identifier: ItemIdentifier, val amount: Int = 1, val chance: Double = 1.0) {

    /** Build the result stack at the configured amount, or null if the item does not resolve. */
    fun generate(): ItemStack? {
        val stack = SMPRPG.getService(ItemService::class.java).resolveIdentifier(identifier.asString()) ?: return null
        stack.amount = amount
        return stack
    }

    companion object {
        /**
         * Deserialize from a parsed YAML value. Accepts a bare string or a map
         * (`{ item: "...", amount: N, chance: D }`). Returns null if malformed.
         */
        fun deserialize(raw: Any?): RecipeOutput? = when (raw) {
            is String -> RecipeOutput(ItemIdentifier.parse(raw))
            is Map<*, *> -> {
                val item = raw["item"] as? String ?: return null
                val amount = (raw["amount"] as? Number)?.toInt() ?: 1
                val chance = (raw["chance"] as? Number)?.toDouble() ?: 1.0
                RecipeOutput(ItemIdentifier.parse(item), amount, chance)
            }
            else -> null
        }
    }
}
