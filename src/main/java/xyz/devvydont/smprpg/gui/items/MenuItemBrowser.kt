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
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuButtonClickHandler
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.interfaces.ICraftable
import xyz.devvydont.smprpg.items.interfaces.ISmeltable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.services.RecipeService.Companion.getRecipesFor
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.*
import java.util.function.Consumer

/*
 * Functions as a server side version of J/NEI. Players can view this interface to view custom items and their recipes.
 */
class MenuItemBrowser @JvmOverloads constructor(
    player: Player,
    /**
     * Retrieve the query that is currently being used for this display.
     *
     * @return A string representing what the user input via command.
     */
    val query: String = ""
) : MenuBase(player, ROWS) {
    private val queriedItems: MutableList<ItemStack> = ArrayList<ItemStack>()
    private var page = 0

    /**
     * Alternative constructor if the user is querying for a specific item. We only want to display items that have
     * some sort of matching string pattern with their query.
     *
     * @param player The player who wants to view items
     * @param query The string query the player provided within the command
     */
    /**
     * Default constructor. Used for general queries when we want to just display every single item in the game.
     *
     * @param player The player who wants to view items
     */
    init {

        // If the item cache hasn't initialized yet, go ahead and do that.
        if (ITEM_CACHE.isEmpty()) {
            for (type in CustomItemType.entries) ITEM_CACHE.add(generate(type))
            for (material in Material.entries) if (!material.isLegacy && material.isItem) ITEM_CACHE.add(
                generate(
                    material
                )
            )
        }
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        super.handleInventoryOpened(event)
        this.queryItems()
        this.render()
        event.titleOverride(
            ComponentUtils.create(
                "Item Directory: " + (query.ifEmpty { "All Items" }),
                NamedTextColor.BLACK
            )
        )
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        super.handleInventoryClicked(event)
        event.isCancelled = true
        this.playInvalidAnimation()
    }

    /**
     * A query is empty if the user didn't define something to search for. Determine if we are actually querying
     * for something specific or not
     *
     * @return true if a query is set, false if we are just viewing everything
     */
    fun hasQuery(): Boolean {
        return !query.isEmpty()
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

        // Do we not have a query and just want to show everything? If that is the case we can return one of everything.
        if (!hasQuery()) {
            for (item in ITEM_CACHE) this.queriedItems.add(item.clone())
            return this.queriedItems
        }

        // When querying for items, we want to make the process as painless as possible, ignore spaces underscores etc.
        // todo use regex (i dont know it >_<) also capture more character patterns that may be present in item names
        val simpleQuery = query.lowercase(Locale.getDefault()).replace(" ", "").replace("_", "").replace("-", "")

        // Loop through every custom item in the game and see if the query makes this something of interest.
        for (itemType in CustomItemType.entries) {
            val simpleName = itemType.ItemName.lowercase(Locale.getDefault())

            // Blacklisting process, does this item's name not contain any similar character patterns as the query?
            if (!simpleName.contains(simpleQuery)) continue

            // Valid!
            this.queriedItems.add(generate(itemType))
        }

        // Do vanilla items too.
        for (material in Material.entries) {
            if (material.isLegacy || !material.isItem || material == Material.AIR) continue

            val simpleName =
                material.name.lowercase(Locale.getDefault()).replace(" ", "").replace("_", "").replace("-", "")

            // Blacklisting process, does this item's name not contain any similar character patterns as the query?
            if (!simpleName.contains(simpleQuery)) continue

            // Valid!
            this.queriedItems.add(generate(material))
        }

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

        // Get a clean version of the item w/o modified lore so that we can properly query recipes.
        val clean = blueprint(itemStack)
        val recipes: MutableList<Recipe> = getRecipesFor(clean.generate())
        if (recipes.isEmpty() || itemStack.type == Material.AIR) {
            this.playInvalidAnimation()
            return
        }

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

            // Add the button
            val item = queriedItems[itemIndexOffset]

            // Re-render the lore on the item. This needs to be done so we don't duplicate injected lore.
            val blueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(item)
            val lore = SMPRPG.getService(ItemService::class.java).renderItemStackLore(item)
            lore.addFirst(ComponentUtils.EMPTY)
            lore.addFirst(ComponentUtils.create("Click to view recipe!", NamedTextColor.YELLOW))
            lore.addFirst(ComponentUtils.EMPTY)

            // If this ingredient can be crafted, insert the craftable tooltip.
            if (blueprint is ICraftable || blueprint is ISmeltable)
                item.editMeta(Consumer { meta: ItemMeta ->
                meta.lore(
                    lore
                )
            })

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
        this.setButton(
            (ROWS - 1) * 9,
            createNamedItem(
                Material.ARROW,
                ComponentUtils.create("Previous Page ($displayPage/$displayPageMax)", NamedTextColor.GOLD)
            )
        ) { e: InventoryClickEvent ->
            page--
            this.render()
            this.sounds.playPagePrevious()
        }

        this.setButton(
            (ROWS - 1) * 9 + 8,
            createNamedItem(
                Material.ARROW,
                ComponentUtils.create("Next Page ($displayPage/$displayPageMax)", NamedTextColor.GOLD)
            )
        ) { e: InventoryClickEvent ->
            page++
            this.render()
            this.sounds.playPageNext()
        }

        // Close button
        this.setButton((ROWS - 1) * 9 + 4, BUTTON_EXIT) { e: InventoryClickEvent ->
            this.closeMenu()
            this.sounds.playMenuClose()
        }
    }

    companion object {
        private val ITEM_CACHE: MutableList<ItemStack> = ArrayList<ItemStack>()
        const val ROWS: Int = 6
    }
}
