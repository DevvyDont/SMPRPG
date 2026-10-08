package xyz.devvydont.smprpg.recipe.campfire

import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemRarity

/**
 * Shared rules for breaking fish down into fish essence on a campfire. The live cooking driver
 * ([xyz.devvydont.smprpg.listeners.crafting.CustomCampfireController]) and the recipe browser both read
 * from here, so the rarity -> essence mapping and timing live in exactly one place.
 *
 * Fish teardown is derived from fish blueprints at runtime rather than authored as YAML, so it is not a
 * [xyz.devvydont.smprpg.recipe.core.SmeltingRecipe] in the registry; this object is its source of truth.
 */
object FishTeardown {

    // The original teardown cooked for (rarity factor)^3 * 20 + 5 ticks; preserved here.
    private const val COOK_TIME_SCALE = 20
    private const val COOK_TIME_BASE = 5

    /** The essence a fish of the given rarity breaks down into. */
    fun essenceFor(rarity: ItemRarity): CustomItemType = when (rarity) {
        ItemRarity.UNCOMMON -> CustomItemType.UNCOMMON_FISH_ESSENCE
        ItemRarity.RARE -> CustomItemType.RARE_FISH_ESSENCE
        ItemRarity.EPIC -> CustomItemType.EPIC_FISH_ESSENCE
        ItemRarity.LEGENDARY -> CustomItemType.LEGENDARY_FISH_ESSENCE
        ItemRarity.MYTHIC -> CustomItemType.MYTHIC_FISH_ESSENCE
        ItemRarity.DIVINE -> CustomItemType.DIVINE_FISH_ESSENCE
        ItemRarity.TRANSCENDENT -> CustomItemType.TRANSCENDENT_FISH_ESSENCE
        else -> CustomItemType.COMMON_FISH_ESSENCE
    }

    /** How long (in ticks) a fish of the given rarity takes to break down. */
    fun cookTimeTicks(rarity: ItemRarity): Int {
        val factor = rarity.ordinal + 1
        return factor * factor * factor * COOK_TIME_SCALE + COOK_TIME_BASE
    }

    /** Higher-rarity fish need the focus of a soul campfire; common/uncommon break on a normal one. */
    fun requiresSoulCampfire(rarity: ItemRarity): Boolean = when (rarity) {
        ItemRarity.COMMON, ItemRarity.UNCOMMON -> false
        else -> true
    }
}
