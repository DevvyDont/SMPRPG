package xyz.devvydont.smprpg.recipe.cookingpot

import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward

/**
 * Lightweight display recipe for the recipe browser. The actual cooking pot matching/consumption is driven
 * by the data-driven registry (see [xyz.devvydont.smprpg.recipe.core.CookingPotRecipe] and
 * [xyz.devvydont.smprpg.block.entity.CookingPotBlockEntityController]); these instances are built on the
 * fly from the registry purely for rendering.
 */
class CookingPotRecipe(@JvmField val key: NamespacedKey,
                       val inputs: List<ItemStack>,
                       val cookTime: Int,
                       val recipeResult: ItemStack,
                       val skillXpReward: SkillExperienceReward?,
                       val platingItem: ItemStack?) : Recipe, Keyed {

    override fun getKey() = key

    constructor(key: NamespacedKey, inputs: List<ItemStack>, cookTime: Int, recipeResult: ItemStack, skillXpReward: SkillExperienceReward?) : this(key, inputs, cookTime, recipeResult, skillXpReward, null)
    constructor(key: NamespacedKey, inputs: List<ItemStack>, cookTime: Int, recipeResult: ItemStack) : this(key, inputs, cookTime, recipeResult, null, null)

    override fun getResult(): ItemStack {
        return recipeResult
    }
}
