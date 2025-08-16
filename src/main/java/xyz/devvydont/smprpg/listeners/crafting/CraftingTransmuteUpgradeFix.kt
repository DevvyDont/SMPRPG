package xyz.devvydont.smprpg.listeners.crafting

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.events.crafting.PrepareItemCraftTransmuteEvent
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.services.ItemService.Companion.clean
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * The Bukkit recipe API has a serious issue. When using the [org.bukkit.inventory.RecipeChoice.ExactChoice]
 * recipe choice for recipes, which our plugin uses for custom item recipes, ANY sort of extra underlying item
 * component modifications will cause the test function to fail for a crafting ingredient. This is a problem, as it
 * makes certain items unable to be upgraded after being enchanted/reforged.
 * This goes for EVERYTHING, even smithing table recipes.
 * Now, this isn't a simple fix either. Unless we want to create wrappers for recipes, we have to analyze the
 * state of the crafting grid at all times, and "clean" every input to see if it results in a custom recipe.
 * If it does, that means that there are "customized" ingredients in the grid, but they pass a soft match as if
 * the ingredients were all freshly generated. This is great, but we need to transfer the data from the item
 * that is potentially being upgraded, so how do we find that? Here's the plan:
 * - Every time we attempt to craft something and there's no recipe output, try again with fresh versions of everything.
 * - Only continue if there's only one possible item that could be transmuted and there's a recipe for the clean items.
 * - - An item that "could be" transmuted means an item that differs from a clean version of itself. (Enchanted, reforged, etc.)
 * - Now we have the recipe and its result, and the transmutable item. We can continue.
 * - Make a copy of the transmutable item, but update the item class tag to be the class of the recipe result.
 * - Trigger an item data update on the new copy. This should completely recalculate the item state while keeping things such as enchantments and reforges.
 * - Manually set the crafting result.
 */
class CraftingTransmuteUpgradeFix : ToggleableListener() {
    /**
     * The logic for the fix outlined above.
     * @param event The [PrepareItemCraftEvent] event that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("unused")
    private fun onAttemptPerformTransmuteUpgradeRecipe(event: PrepareItemCraftEvent) {
        // If there's a recipe involved, there's nothing to check.

        if (event.recipe != null)
            return

        // Find potential transmutable items, and construct a new crafting matrix with clean versions of items.
        val transmutableIngredients = ArrayList<ItemStack?>()
        val originalGrid = event.inventory.matrix // Empty slots will be null. Can either have a length of 4 or 9.

        val air = ItemStack.of(Material.AIR)
        var cleanGrid: Array<ItemStack>
        cleanGrid = if (originalGrid.size == 4)
            arrayOf(air, air, air, air)
        else
            arrayOf(air, air, air, air, air, air, air, air, air)

        // Check the original matrix, and construct a copy that contains clean ingredients. Keep track of transmutable ingredients as well.
        for (i in originalGrid.indices) {
            val ingredient = originalGrid[i]

            // Check if the slot is air/empty.
            if (ingredient == null)
                continue

            // Check if it's transmutable. This can easily be figured out if it doesn't match its clean counterpart.
            val cleanVersion = clean(ingredient)
            if (!ingredient.isSimilar(cleanVersion))
                transmutableIngredients.add(ingredient)

            // Save the clean version.
            cleanGrid[i] = cleanVersion
        }

        // We cannot continue if there are multiple transmutable candidates.
        if (transmutableIngredients.size > 1)
            return

        // We have a recipe that is potentially a transmutable upgrade. In order to recipe check, we need a world instance.
        // First try the inventory location, but if that doesn't work, just grab a viewer location.
        var loc = event.inventory.location
        if (loc == null && !event.inventory.viewers.isEmpty()) loc =
            event.inventory.viewers.first().location

        // If we still don't have a location, just abort.
        if (loc == null) return

        // Now, perform a recipe check against the clean grid. This MUST be a 3x3 grid. If it's 2x2, convert it to 3x3.
        if (cleanGrid.size == 4)
            cleanGrid = arrayOf(cleanGrid[0], cleanGrid[1], air, cleanGrid[2], cleanGrid[3], air, air, air, air)

        val recipe = Bukkit.getCraftingRecipe(cleanGrid, loc.getWorld())

        // If null, then there's no recipe. We tried...
        if (recipe == null)
            return

        // We have a recipe! Given the recipe's result, find its blueprint so we transmute the item.
        val resultBlueprint = blueprint(recipe.result)
        val resultOverride = ItemService.transmute(transmutableIngredients.first()!!, resultBlueprint)

        // Call an event that the plugin can hook into since we don't want to repeat this gross logic again.
        val fixedEvent = PrepareItemCraftTransmuteEvent(
            recipe,
            event.inventory,
            event.view,
            transmutableIngredients.first()!!,
            resultOverride
        )
        if (!fixedEvent.callEvent())
            return

        event.inventory.result = fixedEvent.recipeResult
    }
}
