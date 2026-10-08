package xyz.devvydont.smprpg.recipe.freezer

import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe

class FreezerRecipe(@JvmField val key: NamespacedKey, val input: ItemStack, val freezeTime: Int, val recipeResult: ItemStack) : Recipe, Keyed {

    override fun getKey() = key

    override fun getResult(): ItemStack {
        return recipeResult
    }
}