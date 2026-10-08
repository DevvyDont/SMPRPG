package xyz.devvydont.smprpg.recipe.crafting

import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe

/**
 * Lightweight display recipe for a data-driven shapeless crafting recipe, built on the fly from the recipe
 * registry for the recipe browser (the registry is the source of truth, so it covers recipes Bukkit can't
 * represent — per-slot counts greater than one). Each entry in [ingredients] is a display item whose stack
 * amount is the required count.
 */
class ShapelessDisplayRecipe(
    @JvmField val key: NamespacedKey,
    @JvmField val ingredients: List<ItemStack>,
    private val recipeResult: ItemStack,
) : Recipe, Keyed {

    override fun getKey() = key

    override fun getResult(): ItemStack = recipeResult
}
