package xyz.devvydont.smprpg.gui.base

/**
 * Marks a [MenuBase] whose contents are derived from the recipe registry (crafting, compression, enchanting,
 * the recipe browser, ...).
 *
 * These menus are force-closed at the start of a recipe reload via [MenuBase.closeMatching], so a player can
 * never interact with a half-swapped recipe set while [xyz.devvydont.smprpg.services.RecipeService.reload]
 * rebuilds it. Stations that read the registry live each tick (furnaces, the cooking pot, etc.) survive the
 * atomic registry swap on their own and intentionally do NOT implement this.
 */
interface IRecipeDependentMenu
