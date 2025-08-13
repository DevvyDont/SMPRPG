package xyz.devvydont.smprpg.crafting

import org.bukkit.Material
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice

object CraftingGlobals {
    /**
     * A shortcut to "wildcard" wood plank selections for crafting recipes.
     */
    @JvmField
    val WOODEN_PLANKS_WILDCARD: RecipeChoice = MaterialChoice(
        Material.OAK_PLANKS,
        Material.SPRUCE_PLANKS,
        Material.BIRCH_PLANKS,
        Material.JUNGLE_PLANKS,
        Material.ACACIA_PLANKS,
        Material.DARK_OAK_PLANKS,
        Material.BAMBOO_PLANKS,
        Material.CHERRY_PLANKS,
        Material.MANGROVE_PLANKS,
        Material.CRIMSON_PLANKS,
        Material.WARPED_PLANKS,
        Material.PALE_OAK_PLANKS
    )

    /**
     * A shortcut to "wildcard" log selections for crafting recipes.
     */
    val WOODEN_LOGS_WILDCARD: RecipeChoice = MaterialChoice(
        Material.OAK_LOG,
        Material.SPRUCE_LOG,
        Material.BIRCH_LOG,
        Material.JUNGLE_LOG,
        Material.ACACIA_LOG,
        Material.DARK_OAK_LOG,
        Material.BAMBOO_BLOCK,
        Material.CHERRY_LOG,
        Material.MANGROVE_LOG,
        Material.CRIMSON_STEM,
        Material.WARPED_STEM,
        Material.PALE_OAK_LOG
    )

    /**
     * A shortcut to "wildcard" cobblestone selections for crafting recipes.
     */
    val COBBLESTONE_WILDCARD: RecipeChoice = MaterialChoice(
        Material.COBBLESTONE,
        Material.BLACKSTONE,
        Material.COBBLED_DEEPSLATE
    )
}
