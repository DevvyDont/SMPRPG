package xyz.devvydont.smprpg.recipe.core

/**
 * Every kind of station that can hold recipes in the unified recipe system. Recipes declare their station
 * indirectly through their `type:` field; this enum is the classification the registry groups recipes by
 * (so a station driver can fetch just its own recipes). The [id] is a stable string for display/debugging.
 */
enum class RecipeStationType(val id: String) {
    CRAFTING_TABLE("crafting_table"),
    FURNACE("furnace"),
    COOKING_POT("cooking_pot"),
    CUTTING_BOARD("cutting_board"),
    FREEZER("freezer"),
    COMPRESSOR("compression"),
    ENCHANTING("enchanting");

    companion object {
        fun fromId(id: String): RecipeStationType? = entries.firstOrNull { it.id == id }
    }
}
