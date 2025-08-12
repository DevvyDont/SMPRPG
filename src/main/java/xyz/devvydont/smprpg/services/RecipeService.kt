package xyz.devvydont.smprpg.services

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.inventory.CampfireRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice.ExactChoice
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.blueprints.fishing.FishBlueprint
import xyz.devvydont.smprpg.items.interfaces.ISmeltable
import xyz.devvydont.smprpg.listeners.crafting.CraftingTransmuteUpgradeFix
import xyz.devvydont.smprpg.listeners.crafting.NormalFishCampfireBlacklist
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.listeners.ToggleableListener


/**
 * Handles all recipe logic on the server. This includes crafting, smelting, etc.
 */
class RecipeService : IService, Listener {
    private val listeners: MutableList<ToggleableListener> = ArrayList()

    @Throws(RuntimeException::class)
    override fun setup() {

        registerFurnaceRecipes()
        registerFishTeardownRecipes()

        // Start listeners.
        listeners.add(CraftingTransmuteUpgradeFix())
        listeners.add(NormalFishCampfireBlacklist())
        for (listener in listeners)
            listener.start()
    }

    override fun cleanup() {
        for (listener in listeners)
            listener.stop()
    }

    /**
     * Registers furnace recipes. These are blueprints that implement [ISmeltable].
     */
    private fun registerFurnaceRecipes() {
        // Loop through every item in the item service. If it's smeltable, add a smelt recipe for it.
        for (item in SMPRPG.getService(ItemService::class.java).customBlueprints) {
            if (item !is ISmeltable)
                continue
            Bukkit.addRecipe(ISmeltable.generateRecipe(item, item))
        }
    }


    /**
     * Registers every fish blueprint to have a teardown recipe into essence.
     */
    private fun registerFishTeardownRecipes() {

        val fishToRarity: Multimap<ItemRarity, CustomItemType> = HashMultimap.create<ItemRarity, CustomItemType>()

        // Loop through every fish blueprint and map its rarity.
        for (item in SMPRPG.getService(ItemService::class.java).customBlueprints) {
            if (item !is FishBlueprint)
                continue
            fishToRarity.put(item.defaultRarity, item.customItemType)
        }

        // Create a recipe for every rarity we discovered for a teardown.
        for (entry in fishToRarity.asMap().entries) {
            val essence = when (entry.key) {
                ItemRarity.UNCOMMON -> CustomItemType.UNCOMMON_FISH_ESSENCE
                ItemRarity.RARE -> CustomItemType.RARE_FISH_ESSENCE
                ItemRarity.EPIC -> CustomItemType.EPIC_FISH_ESSENCE
                ItemRarity.LEGENDARY -> CustomItemType.LEGENDARY_FISH_ESSENCE
                ItemRarity.MYTHIC -> CustomItemType.MYTHIC_FISH_ESSENCE
                ItemRarity.DIVINE -> CustomItemType.DIVINE_FISH_ESSENCE
                ItemRarity.TRANSCENDENT -> CustomItemType.TRANSCENDENT_FISH_ESSENCE
                else -> CustomItemType.COMMON_FISH_ESSENCE
            }

            val choices = ArrayList<ItemStack?>()
            val cookingTimeFactor = (entry.key.ordinal + 1)

            for (choice in entry.value)
                choices.add(generate(choice))

            val recipe = CampfireRecipe(
                NamespacedKey(SMPRPG.plugin, entry.key.toString() + "_campfire"),
                generate(essence),
                ExactChoice(choices),
                (cookingTimeFactor * cookingTimeFactor).toFloat(),
                cookingTimeFactor * cookingTimeFactor * cookingTimeFactor * 20 + 5
            )

            Bukkit.addRecipe(recipe)
        }
    }

    companion object {
        /**
         * Get a list of recipes for a specific item.
         * This operation will filter out vanilla recipes that think they can craft custom items, due to a
         * Material recipe match.
         * @param item
         * @return
         */
        @JvmStatic
        fun getRecipesFor(item: ItemStack): MutableList<Recipe> {
            val allRecipes = Bukkit.getRecipesFor(item)
            // Filter out recipes that have the minecraft namespace that think they can craft custom items.
            // Filter out items that do not match. This function has this lovely behavior of giving us ALL recipes that give us the same underlying vanilla material.
            // Another level of filtering. Filter out custom recipes that craft vanilla items. The only time this should
            // ever really be possible is with compression recipes, and they are annoying to display anyway...
            for (recipe in allRecipes.stream().toList()) {

                // Filter out items that simply do not match. An iron ingot cannot be crafted by a recipe that is a boiling ingot.
                if (!recipe.result.isSimilar(item)) {
                    allRecipes.remove(recipe)
                    continue
                }

                if (recipe !is Keyed)
                    continue

                // Filter out a recipe if it is vanilla, but thinks it can craft a custom item.
                val recipeIsVanilla = recipe.key.namespace == NamespacedKey.MINECRAFT_NAMESPACE

                val resultBlueprint = blueprint(recipe.result)
                if (recipeIsVanilla && resultBlueprint.isCustom()) {
                    allRecipes.remove(recipe)
                    continue
                }

                // Filter out a recipe if it is one of our recipes, but a vanilla item is generated. This could potentially
                // filter out recipes we want to consider valid, but there are more "lying" recipes if we allow them.
                if (!recipeIsVanilla && resultBlueprint.isVanilla) {
                    allRecipes.remove(recipe)
                }
            }
            return allRecipes
        }
    }


}
