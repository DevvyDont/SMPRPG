package xyz.devvydont.smprpg.gui.items

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuButtonClickHandler
import xyz.devvydont.smprpg.gui.items.search.ItemBrowserCache
import xyz.devvydont.smprpg.gui.items.search.ItemSearchQuery
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.RecipeService.Companion.getRecipesFor
import xyz.devvydont.smprpg.services.RecipeService.Companion.getUsagesFor
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols

/*
 * Functions as a server side version of J/NEI. Players can view this interface to view custom items and their recipes.
 */
class MenuItemBrowser @JvmOverloads constructor(
    parent: MenuBase?,
    player: Player,
    /**
     * Retrieve the query that is currently being used for this display.
     * 
     * @return A string representing what the user input via command.
     */
    val query: String? = ""
) : MenuBase(player, ROWS) {
    private val queriedItems: MutableList<ItemStack> = ArrayList<ItemStack>()
    private var page = 0

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        super.handleInventoryOpened(event)
        this.queryItems()
        this.render()
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.RECIPE_MENU, NamedTextColor.WHITE),  // Background
                ComponentUtils.create(
                    Symbols.OFFSET_NEG_128 + Symbols.OFFSET_NEG_32 + Symbols.OFFSET_NEG_2 + "Item Directory: " + (if (query!!.isEmpty()) "All Items" else query),
                    Symbols.INVENTORY_TITLE_COLOR
                )
            )
        )
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        super.handleInventoryClicked(event)
        event.isCancelled = true
        //this.playInvalidAnimation();
    }

    /**
     * Use our currently defined query to return a fresh new list of items that we should render on the interface.
     * This only needs to be called when the query is either first set, or when we update the query.
     * After calling this method, the items can be viewed either via state or return value, as they will be the same list.
     * 
     * @return A list of item stacks representing items that we should render on the display.
     */
    private fun queryItems(): MutableList<ItemStack> {
        // First, we can throw out our old item query since we are overwriting it anyway.
        this.queriedItems.clear()

        // The cache already holds fully display-ready items with their searchable fields pre-indexed, so querying is
        // just running each item through the parsed query. We store references (not clones) since Bukkit copies items
        // on insert into the inventory, and the click handler clones before ever handing one to a player.
        val parsedQuery = ItemSearchQuery.parse(query ?: "")
        for (cached in ItemBrowserCache.items())
            if (parsedQuery.matches(cached))
                this.queriedItems.add(cached.displayItem)

        return queriedItems
    }

    /**
     * Determine what to do when a certain item stack is clicked. For example, if a craftable item was clicked, we
     * should display a sub menu containing the recipe of the item.
     * 
     * @param itemStack
     */
    private fun handleClick(event: InventoryClickEvent, itemStack: ItemStack) {
        this.sounds.playActionConfirm()

        // If the player is in creative mode and this is a shift click, give it to them.
        if (this.player.gameMode == GameMode.CREATIVE && event.isShiftClick) {
            this.playSound(Sound.ENTITY_ITEM_PICKUP, 1f, .5f)
            val item = itemStack.clone()
            SMPRPG.getService(ItemService::class.java).ensureItemStackUpdated(item)
            this.player.inventory.addItem(item)
            return
        }

        if (itemStack.type == Material.AIR) return
        // Get a clean version of the item w/o modified lore so that we can properly query recipes.
        val clean = ItemService.blueprint(itemStack).generate()

        // Right-click: show what this item is used in. Left-click: show how to make it.
        if (event.isRightClick) {
            val usages: MutableList<Recipe> = getUsagesFor(clean)
            if (usages.isEmpty()) return
            MenuUsageViewer(this.player, this, itemStack, usages).openMenu()
            return
        }

        val recipes: MutableList<Recipe> = getRecipesFor(clean)
        if (recipes.isEmpty()) return
        MenuRecipeViewer(this.player, this, recipes, itemStack).openMenu()
    }

    /**
     * Renders the inventory based on the state of this instance. Factors in current page and the current query
     * to decide what to display on the page.
     */
    fun render() {
        this.clear()
        this.setBorderEdge()

        // Pagination logic, do a bounds check on the current page and allow page wrapping.
        val totalItems = queriedItems.size
        val area: Int = (ROWS - 2) * 7 // Exclude top and bottom rows, 7 slots in each row
        val lastPage = totalItems / area
        var itemIndexOffset = page * area
        if (itemIndexOffset >= totalItems) {
            itemIndexOffset = 0
            page = 0
        }
        if (itemIndexOffset < 0) {
            page = lastPage
            itemIndexOffset = area * page
        }

        // Display items!
        for (slot in 0..<inventorySize) {
            // Is the item index out of bounds? This can happen on the last page.

            if (itemIndexOffset >= totalItems) break

            // Is this slot already occupied? Skip
            if (this.getItem(slot) != null) continue

            // Add the button. The item is already display-ready (lore and recipe tooltip were baked in when the
            // cache was built), so showing it is just a slot placement with no per-flip computation.
            val item = queriedItems[itemIndexOffset]
            this.setButton(
                slot,
                item
            ) { event: InventoryClickEvent -> this.handleClick(event, item) }
            itemIndexOffset++
        }

        // Extra utility buttons
        // Now set the slots for a next/prev page that increment/decrement the page and re-render
        val displayPage = page + 1
        val displayPageMax = lastPage + 1
        val previousButton = BUTTON_PAGE_PREVIOUS.clone()
        previousButton.editMeta {  meta -> meta.itemName(ComponentUtils.create("Previous Page ($displayPage/$displayPageMax)", NamedTextColor.GOLD))}
        this.setButton(
            (ROWS - 1) * 9,
            previousButton
        ) { _: InventoryClickEvent ->
            page--
            this.render()
            this.sounds.playPagePrevious()
        }

        val nextButton = BUTTON_PAGE_NEXT.clone()
        nextButton.editMeta {  meta -> meta.itemName(ComponentUtils.create("Next Page ($displayPage/$displayPageMax)", NamedTextColor.GOLD))}
        this.setButton(
            (ROWS - 1) * 9 + 8,
            nextButton
        ) { _: InventoryClickEvent ->
            page++
            this.render()
            this.sounds.playPageNext()
        }

        // Close button
        this.setButton((ROWS - 1) * 9 + 4, BUTTON_EXIT, MenuButtonClickHandler { _: InventoryClickEvent ->
            this.closeMenu()
            this.sounds.playMenuClose()
        })
    }

    companion object {
        const val ROWS: Int = 6
    }
}
