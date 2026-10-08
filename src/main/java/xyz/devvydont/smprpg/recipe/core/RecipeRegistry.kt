package xyz.devvydont.smprpg.recipe.core

/**
 * In-memory store of every loaded [CustomRecipe], indexed for the lookups the rest of the plugin needs:
 * by station (station drivers iterate their own recipes), by result identifier (the browser asks "how do I
 * make this item?"), and by ingredient identifier (the browser asks "what is this item used in?"). Pure data
 * — holds no Bukkit listeners and performs no I/O.
 *
 * A registry is built fresh by [RecipeLoader] on every (re)load and swapped in atomically, so callers
 * always see a fully-populated, consistent view.
 */
class RecipeRegistry {

    private val byStation: MutableMap<RecipeStationType, MutableList<CustomRecipe>> = HashMap()
    private val byResult: MutableMap<String, MutableList<CustomRecipe>> = HashMap()
    private val byIngredient: MutableMap<String, MutableList<CustomRecipe>> = HashMap()
    private val byKey: MutableMap<String, CustomRecipe> = HashMap()
    private val byEnchantment: MutableMap<String, EnchantingRecipe> = HashMap()

    fun register(recipe: CustomRecipe) {
        byStation.getOrPut(recipe.station) { ArrayList() }.add(recipe)
        byKey[recipe.key.asString()] = recipe
        for (output in recipe.outputs)
            byResult.getOrPut(output.identifier.asString()) { ArrayList() }.add(recipe)
        // Index by each distinct ingredient so a recipe using the same item in several slots is listed once.
        for (ingredientId in recipe.ingredients.map { it.identifier.asString() }.distinct())
            byIngredient.getOrPut(ingredientId) { ArrayList() }.add(recipe)
        if (recipe is EnchantingRecipe)
            byEnchantment["${recipe.enchantment}:${recipe.level}"] = recipe
    }

    /** The enchanting recipe (reagents + power) for a given enchantment id and level, or null. */
    fun enchantingRecipe(enchantment: String, level: Int): EnchantingRecipe? =
        byEnchantment["${enchantment.lowercase()}:$level"]

    /** All recipes for a given station, in load order. */
    fun byStation(station: RecipeStationType): List<CustomRecipe> = byStation[station] ?: emptyList()

    /** All recipes whose output includes the given `namespace:path` identifier. */
    fun byResult(identifier: String): List<CustomRecipe> = byResult[identifier] ?: emptyList()

    /** All recipes that consume the given `namespace:path` identifier as an ingredient. */
    fun byIngredient(identifier: String): List<CustomRecipe> = byIngredient[identifier] ?: emptyList()

    /** Every identifier produced by at least one recipe — a fast set for "is this craftable?" checks. */
    fun resultIdentifiers(): Set<String> = byResult.keys

    /** Every identifier consumed by at least one recipe — a fast set for "is this used in anything?" checks. */
    fun ingredientIdentifiers(): Set<String> = byIngredient.keys

    /** A recipe by its `namespace:path` key, or null. */
    fun byKey(key: String): CustomRecipe? = byKey[key]

    /** Every recipe in the registry. */
    fun all(): List<CustomRecipe> = byKey.values.toList()

    /** Number of registered recipes. */
    val size: Int
        get() = byKey.size
}
