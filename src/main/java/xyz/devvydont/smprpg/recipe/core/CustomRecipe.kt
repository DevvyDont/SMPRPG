package xyz.devvydont.smprpg.recipe.core

import org.bukkit.NamespacedKey

/**
 * The unified, Bukkit-API-independent representation of a single recipe in the SMPRPG recipe system.
 *
 * Every recipe — vanilla-style crafting, smelting, or a custom station — is one of the sealed subtypes
 * below. The shared contract exposes the data the registry and recipe browser need ([ingredients],
 * [outputs], [unlockedBy]); station drivers cast to the concrete type for station-specific fields.
 */
sealed interface CustomRecipe {
    /** Unique key for this recipe (namespace `smprpg`, path = the recipe id from its YAML file). */
    val key: NamespacedKey

    /** Which station this recipe belongs to. */
    val station: RecipeStationType

    /** All item inputs this recipe consumes (flattened; arrangement is type-specific). */
    val ingredients: List<Ingredient>

    /** All possible item results this recipe can produce. */
    val outputs: List<RecipeOutput>

    /** Items whose acquisition reveals this recipe in the recipe browser. */
    val unlockedBy: List<ItemIdentifier>

    /** Coins / skill XP granted to the producing player on a successful craft. Empty by default. */
    val rewards: RecipeRewards

    /** Conditions (e.g. minimum skill levels) a player must meet to craft this. Empty = always craftable. */
    val requirements: RecipeRequirements
}

/**
 * A crafting-table recipe with a fixed grid layout. [pattern] rows reference [keyMap] by character; a
 * space means an empty slot. Per-slot stack counts come from each [Ingredient.amount].
 */
data class ShapedRecipe(
    override val key: NamespacedKey,
    val pattern: List<String>,
    val keyMap: Map<Char, Ingredient>,
    val result: RecipeOutput,
    override val unlockedBy: List<ItemIdentifier> = emptyList(),
    /**
     * The pattern character of the "upgrade" ingredient, if this is an upgrade recipe. The actual item in
     * that single slot has its data (enchantments, reforges, stored contents, ...) transferred to the result
     * instead of producing a fresh one. Null for ordinary recipes. Validated at load to occupy one slot of
     * amount 1.
     */
    val upgradeChar: Char? = null,
    override val rewards: RecipeRewards = RecipeRewards(),
    override val requirements: RecipeRequirements = RecipeRequirements(),
) : CustomRecipe {
    override val station: RecipeStationType get() = RecipeStationType.CRAFTING_TABLE
    override val ingredients: List<Ingredient> get() = keyMap.values.toList()
    override val outputs: List<RecipeOutput> get() = listOf(result)
}

/**
 * A crafting-table recipe with no fixed layout: the inputs may be placed anywhere in the grid.
 */
data class ShapelessRecipe(
    override val key: NamespacedKey,
    override val ingredients: List<Ingredient>,
    val result: RecipeOutput,
    override val unlockedBy: List<ItemIdentifier> = emptyList(),
    /**
     * The identifier of the "upgrade" ingredient, if this is an upgrade recipe; the matching item in the grid
     * has its data transferred to the result. Null for ordinary recipes. Validated at load to be a single
     * ingredient of amount 1.
     */
    val upgradeIngredient: ItemIdentifier? = null,
    override val rewards: RecipeRewards = RecipeRewards(),
    override val requirements: RecipeRequirements = RecipeRequirements(),
) : CustomRecipe {
    override val station: RecipeStationType get() = RecipeStationType.CRAFTING_TABLE
    override val outputs: List<RecipeOutput> get() = listOf(result)
}

/** Which kind of vanilla cooking block a smelting recipe runs in (affects speed and which block accepts it). */
enum class SmeltingCookType {
    FURNACE,
    BLASTING,
    SMOKING,
    CAMPFIRE;

    companion object {
        fun fromId(id: String?): SmeltingCookType =
            entries.firstOrNull { it.name.equals(id, ignoreCase = true) } ?: FURNACE
    }
}

/**
 * A furnace smelting recipe: one input -> one output after [time] ticks, awarding [experience] vanilla XP.
 * [cook] selects the cooking block (furnace/blast furnace/smoker/campfire).
 */
data class SmeltingRecipe(
    override val key: NamespacedKey,
    val input: Ingredient,
    val time: Int,
    val result: RecipeOutput,
    val experience: Float = 0f,
    val cook: SmeltingCookType = SmeltingCookType.FURNACE,
    override val unlockedBy: List<ItemIdentifier> = emptyList(),
    override val rewards: RecipeRewards = RecipeRewards(),
    override val requirements: RecipeRequirements = RecipeRequirements(),
) : CustomRecipe {
    override val station: RecipeStationType get() = RecipeStationType.FURNACE
    override val ingredients: List<Ingredient> get() = listOf(input)
    override val outputs: List<RecipeOutput> get() = listOf(result)
}

/**
 * A cooking pot recipe: several ingredients simmer for [time] ticks into [result], optionally requiring a
 * [plating] vessel. Skill XP granted on completion lives in [rewards] (a legacy top-level `skill_xp` block
 * is folded into it on load).
 */
data class CookingPotRecipe(
    override val key: NamespacedKey,
    override val ingredients: List<Ingredient>,
    val time: Int,
    val result: RecipeOutput,
    val plating: ItemIdentifier? = null,
    override val unlockedBy: List<ItemIdentifier> = emptyList(),
    override val rewards: RecipeRewards = RecipeRewards(),
    override val requirements: RecipeRequirements = RecipeRequirements(),
) : CustomRecipe {
    override val station: RecipeStationType get() = RecipeStationType.COOKING_POT
    override val outputs: List<RecipeOutput> get() = listOf(result)
}

/**
 * A cutting board recipe: one input is processed (with a tool of [tool] tag) into one or more
 * probability-weighted [results].
 */
data class CuttingBoardRecipe(
    override val key: NamespacedKey,
    val input: Ingredient,
    val results: List<RecipeOutput>,
    val tool: String? = null,
    override val unlockedBy: List<ItemIdentifier> = emptyList(),
    override val rewards: RecipeRewards = RecipeRewards(),
    override val requirements: RecipeRequirements = RecipeRequirements(),
) : CustomRecipe {
    override val station: RecipeStationType get() = RecipeStationType.CUTTING_BOARD
    override val ingredients: List<Ingredient> get() = listOf(input)
    override val outputs: List<RecipeOutput> get() = results
}

/**
 * A freezer recipe: one input freezes over [time] ticks into [result].
 */
data class FreezerRecipe(
    override val key: NamespacedKey,
    val input: Ingredient,
    val time: Int,
    val result: RecipeOutput,
    override val unlockedBy: List<ItemIdentifier> = emptyList(),
    override val rewards: RecipeRewards = RecipeRewards(),
    override val requirements: RecipeRequirements = RecipeRequirements(),
) : CustomRecipe {
    override val station: RecipeStationType get() = RecipeStationType.FREEZER
    override val ingredients: List<Ingredient> get() = listOf(input)
    override val outputs: List<RecipeOutput> get() = listOf(result)
}

/**
 * A two-way stacking recipe. Declares the "compress" direction (e.g. 9 ingots -> 1 block via
 * [input].amount); the reverse decompression recipe is generated by the station driver. [family] is the
 * compression chain this edge belongs to (from its file name, e.g. "amethyst"), shared by every edge in the chain.
 */
data class CompressionRecipe(
    override val key: NamespacedKey,
    val input: Ingredient,
    val result: RecipeOutput,
    val family: String = "",
    override val unlockedBy: List<ItemIdentifier> = emptyList(),
    override val rewards: RecipeRewards = RecipeRewards(),
    override val requirements: RecipeRequirements = RecipeRequirements(),
) : CustomRecipe {
    override val station: RecipeStationType get() = RecipeStationType.COMPRESSOR
    override val ingredients: List<Ingredient> get() = listOf(input)
    override val outputs: List<RecipeOutput> get() = listOf(result)
}

/**
 * An enchanting-table recipe: the reagents (and required magic [power]) to apply a single level of a custom
 * [enchantment]. Unlike other stations this produces no item — it imbues an enchantment onto the input item —
 * so [outputs] is empty. Looked up by ([enchantment], [level]).
 */
data class EnchantingRecipe(
    override val key: NamespacedKey,
    val enchantment: String,
    val level: Int,
    val power: Int,
    override val ingredients: List<Ingredient>,
    override val unlockedBy: List<ItemIdentifier> = emptyList(),
    override val rewards: RecipeRewards = RecipeRewards(),
    override val requirements: RecipeRequirements = RecipeRequirements(),
) : CustomRecipe {
    override val station: RecipeStationType get() = RecipeStationType.ENCHANTING
    override val outputs: List<RecipeOutput> get() = emptyList()
}
