package xyz.devvydont.smprpg.gui.items

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuButtonClickHandler
import xyz.devvydont.smprpg.items.interfaces.IItemContainer
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint

class MenuContainer(player: Player, private val blueprint: IItemContainer, private val backpack: ItemStack) :
    MenuBase(player, 6) {
    private var currentPage = 0

    init {
        this.sounds.setMenuOpen(Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, .5f)
        this.sounds.setMenuClose(Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, .5f)
        this.sounds.setPageNext(Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, .8f)
        this.sounds.setPageNext(Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, 1.3f)
        render()
    }

    fun render() {
        this.setBorderFull()

        // Clone all the items stored in the meta to this inventory.
        // Once we do this, this inventory becomes the source of truth!
        val items = blueprint.getStoredItems(backpack, true)

        // Check if this page is out of bounds.
        val lastPage = this.lastPage
        if (currentPage > lastPage) currentPage = 0
        if (currentPage < 0) currentPage = lastPage

        // We only need to query for items that fall in the range of the subarray that we are actually looking at.
        // There's a lot of iterating variables going on here, but we have:
        // - itemIndex: the index of the item stored on the container that is offset from i.
        // - i: the index of the item in the item stored.
        var itemIndex = 0
        for (i in currentPage * PAGE_CAPACITY..<blueprint.getSlots()) {
            // If we ran out of space for this page, stop.

            if (i >= (currentPage + 1) * PAGE_CAPACITY) break

            // If we ran out of items, stop.
            if (i >= items.size) break

            // Place the item.
            this.setSlot(INVENTORY_SLOTS[itemIndex], items[i])
            itemIndex++
        }

        // Now add some buttons. It is important these don't interfere with the storage.
        this.setBackButton(49)
        val page = "(" + (currentPage + 1) + "/" + (lastPage + 1) + ")"
        this.setButton(
            53,
            createNamedItem(Material.ARROW, "Next Page $page")
        ) { e: InventoryClickEvent -> changePage(1) }
        this.setButton(
            45,
            createNamedItem(Material.ARROW, "Previous Page $page")
        ) { e: InventoryClickEvent -> changePage(-1) }
    }

    fun savePage() {
        // Query the items so we can replace them.

        val items = blueprint.getStoredItems(backpack, true)

        // Whatever is present in our interface is going to replace the items we just retrieved.
        var containerIndex: Int = currentPage * PAGE_CAPACITY
        for (interfaceSlotIndex in INVENTORY_SLOTS) {
            // If we are out of bounds of the container, we should stop.

            if (containerIndex >= blueprint.getSlots()) break

            // Whatever is stored in the interface should replace the item in the list.
            var itemInInterface = getItem(interfaceSlotIndex)
            if (itemInInterface == null) itemInInterface = ItemStack.of(Material.AIR)

            items[containerIndex] = itemInInterface
            containerIndex++
        }

        // Items have been replaced for the subsection we care about. We can update the items.
        blueprint.setStoredItems(backpack, items)
    }

    fun changePage(pageDelta: Int) {
        // Save the page before changing it.

        savePage()

        // Change the page and re-render the menu.
        currentPage += pageDelta
        render()
    }

    val lastPage: Int
        get() = (blueprint.getSlots() - 1) / PAGE_CAPACITY

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        // Under any circumstances, NEVER let any other backpacks (or gui elements) be clicked or modified.

        val clicked = event.getCurrentItem()
        if (clicked == null) return

        if (blueprint(clicked) is IItemContainer
            || clicked.type == Material.BLACK_STAINED_GLASS_PANE
            || clicked.type == Material.RED_STAINED_GLASS_PANE
            || clicked.type == Material.GREEN_STAINED_GLASS_PANE
        ) {
            event.isCancelled = true
            this.playInvalidAnimation()
        }
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(blueprint.getInterfaceTitleComponent())
        event.inventory.maxStackSize = blueprint.getStackSize()
    }

    override fun handleInventoryClosed(event: InventoryCloseEvent) {
        // When this inventory closes, our inventory is the source of truth, so we should copy everything we have over.
        this.savePage()
    }

    companion object {
        // The inventory slots that can be used to interact with items. Other slots are reserved for buttons and decoration.
        private val INVENTORY_SLOTS = intArrayOf(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43,
        )

        private val PAGE_CAPACITY: Int = INVENTORY_SLOTS.size
    }
}
