package xyz.devvydont.smprpg.recipe.core

import org.bukkit.inventory.ItemStack

/**
 * A single recipe input: an item type plus the number of that item required.
 *
 * Matching is type-level (via [ItemIdentifier]), so enchanted/reforged custom items still satisfy the
 * ingredient. The [amount] is the per-slot stack count requirement (e.g. "needs 4 of X").
 */
data class Ingredient(val identifier: ItemIdentifier, val amount: Int = 1) {

    /** True if the stack is the right item type, ignoring how many there are. */
    fun matchesType(item: ItemStack): Boolean = identifier.matches(item)

    /** True if the stack is the right type AND carries at least [amount]. */
    fun isSatisfiedBy(item: ItemStack): Boolean = matchesType(item) && item.amount >= amount

    companion object {
        /**
         * Deserialize from a parsed YAML value. Accepts either a bare string (`"minecraft:blaze_powder"`,
         * amount defaults to 1) or a map (`{ item: "...", amount: N }`). Returns null if malformed.
         */
        fun deserialize(raw: Any?): Ingredient? = when (raw) {
            is String -> Ingredient(ItemIdentifier.parse(raw))
            is Map<*, *> -> {
                val item = raw["item"] as? String ?: return null
                val amount = (raw["amount"] as? Number)?.toInt() ?: 1
                Ingredient(ItemIdentifier.parse(item), amount)
            }
            else -> null
        }
    }
}
