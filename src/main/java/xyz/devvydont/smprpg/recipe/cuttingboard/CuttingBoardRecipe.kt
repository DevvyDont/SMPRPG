package xyz.devvydont.smprpg.recipe.cuttingboard

import net.momirealms.craftengine.core.util.Key
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe

/**
 * Tool tags a cutting board recipe can require. Kept here (rather than alongside the old hardcoded recipe
 * enum, which has been removed) because the display recipe and recipe browser still reference these.
 */
class CuttingBoardToolTags {
    companion object {
        val KNIVES: Key = Key.of("smprpg:knives")
        val AXES: Key = Key.of("minecraft:axes")
        val SHOVELS: Key = Key.of("minecraft:shovels")
    }
}

class CuttingBoardRecipe(@JvmField val key: NamespacedKey, val input: ItemStack, val recipeResult: List<Pair<ItemStack, Double>>, val processToolTag: Key) :
    Recipe, Keyed {

    override fun getKey() = key

    override fun getResult(): ItemStack {
        throw IllegalStateException("getResult should not be used on CuttingBoardRecipes. Use recipeResult instead.")
    }
}