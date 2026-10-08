package xyz.devvydont.smprpg.recipe.crafting

import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe

/**
 * Lightweight display recipe for a data-driven shaped crafting recipe, built on the fly from the recipe
 * registry for the recipe browser. The registry — not Bukkit — is the source of truth, so this also covers
 * recipes Bukkit can't represent (per-slot counts greater than one).
 *
 * [ingredients] maps each pattern character to the display item for that slot, whose stack amount is the
 * required per-slot count. [upgradeChar] flags the slot whose data carries over for an upgrade recipe, if any.
 */
class ShapedDisplayRecipe(
    @JvmField val key: NamespacedKey,
    @JvmField val pattern: List<String>,
    @JvmField val ingredients: Map<Char, ItemStack>,
    private val recipeResult: ItemStack,
    @JvmField val upgradeChar: Char? = null,
) : Recipe, Keyed {

    override fun getKey() = key

    override fun getResult(): ItemStack = recipeResult
}
