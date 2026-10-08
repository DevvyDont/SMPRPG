package xyz.devvydont.smprpg.gui.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.base.IRecipeDependentMenu
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.RecipeService.Companion.getRecipesFor
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Shows what a given item is "used in" — the distinct results of every recipe that consumes it (the inverse of
 * the recipe viewer). Each result is clickable to open its own recipe via [MenuRecipeViewer]. Opened by
 * right-clicking an item in the [MenuItemBrowser]; the consuming recipes come from the registry's ingredient
 * index ([xyz.devvydont.smprpg.services.RecipeService.getUsagesFor]).
 */
class MenuUsageViewer(
    player: Player,
    parentMenu: MenuBase?,
    private val sourceItem: ItemStack,
    usages: List<Recipe>,
) : MenuBase(player, ROWS, parentMenu), IRecipeDependentMenu {

    // The distinct result items of the consuming recipes — what you can make with the source item.
    private val results: List<ItemStack> = buildResultList(usages)
    private var page = 0

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        super.handleInventoryOpened(event)
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create("Used in: "),
                ItemService.blueprint(sourceItem).getNameComponent(sourceItem)
            )
        )
        render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        super.handleInventoryClicked(event)
        event.isCancelled = true
    }

    /** One display item per distinct consuming-recipe result, each hinting that it can be clicked for its recipe. */
    private fun buildResultList(usages: List<Recipe>): List<ItemStack> {
        val itemService = SMPRPG.getService(ItemService::class.java)
        val seen = HashSet<String>()
        val out = ArrayList<ItemStack>()
        for (recipe in usages) {
            val result = recipe.result
            if (result.type == Material.AIR) continue
            if (!seen.add(itemService.getIdentifier(result))) continue
            val display = result.clone()
            val lore = mutableListOf<Component>(
                ComponentUtils.EMPTY,
                ComponentUtils.create("Click to view recipe!", NamedTextColor.YELLOW),
                ComponentUtils.EMPTY
            )
            display.lore()?.let { lore.addAll(it) }
            display.lore(lore)
            out.add(display)
        }
        return out
    }

    private fun render() {
        clear()
        setBorderEdgeForced()  // Remove forced when UI is made.

        val area = (ROWS - 2) * 7
        val lastPage = if (results.isEmpty()) 0 else (results.size - 1) / area
        if (page > lastPage) page = 0
        if (page < 0) page = lastPage

        var index = page * area
        for (slot in 0 until inventorySize) {
            if (index >= results.size) break
            if (getItem(slot) != null) continue  // skip border slots
            val result = results[index]
            setButton(slot, result) { _: InventoryClickEvent -> openRecipe(result) }
            index++
        }

        val displayPage = page + 1
        val displayPageMax = lastPage + 1
        val previous = BUTTON_PAGE_PREVIOUS.clone()
        previous.editMeta { it.itemName(ComponentUtils.create("Previous Page ($displayPage/$displayPageMax)", NamedTextColor.GOLD)) }
        setButton((ROWS - 1) * 9, previous) { _: InventoryClickEvent -> page--; render(); sounds.playPagePrevious() }

        val next = BUTTON_PAGE_NEXT.clone()
        next.editMeta { it.itemName(ComponentUtils.create("Next Page ($displayPage/$displayPageMax)", NamedTextColor.GOLD)) }
        setButton((ROWS - 1) * 9 + 8, next) { _: InventoryClickEvent -> page++; render(); sounds.playPageNext() }

        setBackButton((ROWS - 1) * 9 + 4)
    }

    /** Open the recipe viewer for one of the result items. */
    private fun openRecipe(result: ItemStack) {
        val recipes = getRecipesFor(ItemService.blueprint(result).generate())
        if (recipes.isEmpty()) return
        openSubMenu(MenuRecipeViewer(player, this, recipes, result))
    }

    companion object {
        const val ROWS: Int = 6
    }
}
