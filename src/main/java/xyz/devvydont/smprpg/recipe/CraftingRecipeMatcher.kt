package xyz.devvydont.smprpg.recipe

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.recipe.core.CompressionRecipe
import xyz.devvydont.smprpg.recipe.core.CustomRecipe
import xyz.devvydont.smprpg.recipe.core.Ingredient
import xyz.devvydont.smprpg.recipe.core.RecipeStationType
import xyz.devvydont.smprpg.recipe.core.ShapedRecipe
import xyz.devvydont.smprpg.recipe.core.ShapelessRecipe
import xyz.devvydont.smprpg.services.RecipeService
import kotlin.math.max
import kotlin.math.min

/**
 * Matches a 3x3 crafting grid against the data-driven recipe registry, then falls back to vanilla Bukkit
 * recipes. Custom registry recipes are checked first and are count-aware (a slot can require several of an
 * item via [Ingredient.amount]); vanilla recipes are resolved through [Bukkit.getCraftingRecipe].
 *
 * The grid is a row-major list of 9 cells (indices 0..8, row = i/3, col = i%3); empty cells are null/air.
 */
object CraftingRecipeMatcher {

    /**
     * A successful match: the produced [result], how many items to remove from each grid cell, and the
     * registry [recipe] that matched (null for a pure vanilla recipe, which carries no rewards/requirements).
     */
    class Match(val result: ItemStack, val consumption: Map<Int, Int>, val recipe: CustomRecipe? = null)

    private class Box(val minR: Int, val minC: Int, val height: Int, val width: Int)

    fun match(grid: List<ItemStack?>, world: World): Match? {
        matchCustom(grid)?.let { return it }
        return matchVanilla(grid, world)
    }

    private fun matchCustom(grid: List<ItemStack?>): Match? {
        val registry = SMPRPG.getService(RecipeService::class.java).getRegistry()
        for (recipe in registry.byStation(RecipeStationType.CRAFTING_TABLE)) {
            val match = when (recipe) {
                is ShapedRecipe -> matchShaped(grid, recipe)
                is ShapelessRecipe -> matchShapeless(grid, recipe)
                else -> null
            } ?: continue
            return match
        }
        return matchCompression(grid)
    }

    /**
     * Match the grid against the compression edges. These live on the [RecipeStationType.COMPRESSOR] station
     * (not [RecipeStationType.CRAFTING_TABLE]), so the loop above skips them. Each edge works both ways:
     * compressing [CompressionRecipe.input].amount of the lower item into one higher item, or decompressing one
     * higher item back into that many lower items. Matching is by real item identity, so a vanilla recipe that
     * shares the base material (e.g. pumpkin -> seeds) can never hijack a custom item here.
     */
    private fun matchCompression(grid: List<ItemStack?>): Match? {
        val registry = SMPRPG.getService(RecipeService::class.java).getRegistry()
        for (recipe in registry.byStation(RecipeStationType.COMPRESSOR).filterIsInstance<CompressionRecipe>()) {
            matchDecompress(grid, recipe)?.let { return it }
            matchCompress(grid, recipe)?.let { return it }
        }
        return null
    }

    /** Compress: exactly [CompressionRecipe.input].amount occupied cells, each holding the lower item. */
    private fun matchCompress(grid: List<ItemStack?>, recipe: CompressionRecipe): Match? {
        val occupiedCells = (0..8).filter { occupied(grid[it]) }
        if (occupiedCells.size != recipe.input.amount) return null
        for (cell in occupiedCells)
            if (!recipe.input.matchesType(grid[cell]!!)) return null
        val result = recipe.result.generate() ?: return null
        return Match(result, occupiedCells.associateWith { 1 }, recipe)
    }

    /** Decompress: a single occupied cell holding the higher item yields [CompressionRecipe.input].amount of the lower item. */
    private fun matchDecompress(grid: List<ItemStack?>, recipe: CompressionRecipe): Match? {
        val occupiedCells = (0..8).filter { occupied(grid[it]) }
        if (occupiedCells.size != 1) return null
        val cell = occupiedCells[0]
        if (!recipe.result.identifier.matches(grid[cell]!!)) return null
        val result = recipe.input.identifier.resolve() ?: return null
        result.amount = recipe.input.amount
        return Match(result, mapOf(cell to recipe.result.amount), recipe)
    }

    /**
     * Build the result for an upgrade recipe: transfer the data of the item in the designated [sourceCell]
     * (enchantments, reforges, stored contents, ...) onto the recipe result, then normalize the stack count to
     * the recipe's output amount. Returns the fresh [result] unchanged when there is no upgrade source.
     */
    private fun upgradeResult(grid: List<ItemStack?>, sourceCell: Int?, result: ItemStack): ItemStack {
        val source = sourceCell?.let { grid[it] } ?: return result
        val upgraded = ItemService.transmute(source, ItemService.blueprint(result))
        upgraded.amount = result.amount
        return upgraded
    }

    private fun matchShaped(grid: List<ItemStack?>, recipe: ShapedRecipe): Match? {
        // Expand the pattern into a 3x3 grid of ingredients (null = empty cell), tracking each cell's character.
        val pattern = arrayOfNulls<Ingredient>(9)
        val patternChars = arrayOfNulls<Char>(9)
        for (r in recipe.pattern.indices) {
            val row = recipe.pattern[r]
            for (c in row.indices) {
                val ch = row[c]
                if (ch == ' ') continue
                pattern[r * 3 + c] = recipe.keyMap[ch] ?: return null
                patternChars[r * 3 + c] = ch
            }
        }

        val patternBox = boundingBox { pattern[it] != null } ?: return null
        val gridBox = boundingBox { occupied(grid[it]) } ?: return null
        if (patternBox.height != gridBox.height || patternBox.width != gridBox.width) return null

        val consumption = HashMap<Int, Int>()
        var upgradeCell: Int? = null
        for (dr in 0 until patternBox.height) {
            for (dc in 0 until patternBox.width) {
                val patternCell = (patternBox.minR + dr) * 3 + (patternBox.minC + dc)
                val gridCell = (gridBox.minR + dr) * 3 + (gridBox.minC + dc)
                val ingredient = pattern[patternCell]
                val stack = grid[gridCell]
                if (ingredient == null) {
                    if (occupied(stack)) return null
                } else {
                    if (!occupied(stack)) return null
                    if (!ingredient.matchesType(stack!!) || stack.amount < ingredient.amount) return null
                    consumption[gridCell] = ingredient.amount
                    if (patternChars[patternCell] == recipe.upgradeChar) upgradeCell = gridCell
                }
            }
        }

        val result = recipe.result.generate() ?: return null
        return Match(upgradeResult(grid, upgradeCell, result), consumption, recipe)
    }

    private fun matchShapeless(grid: List<ItemStack?>, recipe: ShapelessRecipe): Match? {
        val available = IntArray(9) { if (occupied(grid[it])) grid[it]!!.amount else 0 }
        val consumption = HashMap<Int, Int>()
        for (ingredient in recipe.ingredients) {
            var toConsume = ingredient.amount
            for (cell in 0..8) {
                if (toConsume <= 0) break
                if (available[cell] <= 0) continue
                val stack = grid[cell] ?: continue
                if (!ingredient.matchesType(stack)) continue
                val take = min(toConsume, available[cell])
                available[cell] -= take
                toConsume -= take
                consumption[cell] = (consumption[cell] ?: 0) + take
            }
            if (toConsume > 0) return null
        }
        // Shapeless recipes must use every item in the grid — no leftovers.
        if ((0..8).any { available[it] > 0 }) return null

        val result = recipe.result.generate() ?: return null
        val upgradeCell = recipe.upgradeIngredient?.let { id -> consumption.keys.firstOrNull { id.matches(grid[it]!!) } }
        return Match(upgradeResult(grid, upgradeCell, result), consumption, recipe)
    }

    private fun matchVanilla(grid: List<ItemStack?>, world: World): Match? {
        // Vanilla recipes match purely by material and ignore our custom item data, so a custom item (e.g. an
        // enchanted pumpkin) would otherwise satisfy a vanilla recipe (pumpkin -> seeds). Only our own recipes
        // may consume custom items; if the grid holds any, decline the vanilla fallback entirely.
        if (grid.any { it != null && isCustomItem(it) }) return null
        // Bukkit's matrix is an ItemStack[] whose empty slots are null; the cast satisfies Kotlin's nullability.
        @Suppress("UNCHECKED_CAST")
        val matrix = Array(9) { grid[it] } as Array<ItemStack>
        val recipe = Bukkit.getCraftingRecipe(matrix, world) ?: return null
        val consumption = HashMap<Int, Int>()
        for (i in 0..8) if (occupied(grid[i])) consumption[i] = 1
        return Match(recipe.result.clone(), consumption, null)
    }

    private fun occupied(stack: ItemStack?): Boolean = stack != null && !stack.type.isAir

    /** True if the stack is one of our custom items (carries an smprpg item-type key), not a plain vanilla item. */
    private fun isCustomItem(stack: ItemStack): Boolean =
        SMPRPG.getService(ItemService::class.java).getItemKey(stack) != null

    private fun boundingBox(occupied: (Int) -> Boolean): Box? {
        var minR = 3; var maxR = -1; var minC = 3; var maxC = -1
        for (i in 0..8) {
            if (!occupied(i)) continue
            val r = i / 3; val c = i % 3
            minR = min(minR, r); maxR = max(maxR, r)
            minC = min(minC, c); maxC = max(maxC, c)
        }
        if (maxR < 0) return null
        return Box(minR, minC, maxR - minR + 1, maxC - minC + 1)
    }
}
