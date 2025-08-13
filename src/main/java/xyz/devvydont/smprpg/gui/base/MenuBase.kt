package xyz.devvydont.smprpg.gui.base

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.util.animations.AnimationService
import xyz.devvydont.smprpg.util.animations.blockers.WaitFor
import xyz.devvydont.smprpg.util.animations.iterators.AnimationFrame
import xyz.devvydont.smprpg.util.animations.playback.AnimationHandle
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * A menu that is displayed to a player.
 */
abstract class MenuBase @JvmOverloads constructor(@JvmField protected val player: Player, rows: Int, protected val parentMenu: MenuBase? = null) : Listener {

    @JvmField
    protected val inventory: Inventory = Bukkit.createInventory(player, 9 * rows)

    @JvmField
    protected val sounds: MenuSoundManager = MenuSoundManager(player)

    private var shouldPlayOpeningSound = false
    private var shouldPlayClosingSound = false
    private var activeAnimation: AnimationHandle? = null
    private val buttonSlots: MutableMap<Int, MenuButtonClickHandler> = HashMap<Int, MenuButtonClickHandler>()

    // --------------
    //   Visibility
    // --------------
    /**
     * Opens/updates the inventory UI to this menu.
     */
    fun openMenu() {
        this.openMenu(true)
    }

    /**
     * Opens/updates the inventory UI to this menu.
     * Used internally to manage the sounds that are played.
     *
     * @param playOpeningSound True if the opening sound should be played, otherwise false.
     */
    private fun openMenu(playOpeningSound: Boolean) {
        // The opening sound should only play if this is the initial menu.
        this.shouldPlayOpeningSound = playOpeningSound
        this.shouldPlayClosingSound = true

        // Open the UI
        Bukkit.getPluginManager().registerEvents(this, plugin)
        this.player.openInventory(this.inventory)
    }

    /**
     * Updates the inventory UI to the specified sub menu, closing this menu.
     */
    protected fun openSubMenu(subMenu: MenuBase) {
        // Play the transition sound
        this.sounds.playMenuOpenSub()

        // Block our closing sound and the sub menus opening sound.
        this.shouldPlayClosingSound = false
        subMenu.openMenu(false)
    }

    /**
     * Updates the inventory UI to the parent menu, closing this menu.
     */
    protected fun openParentMenu() {
        // Report if there's no parent menu.
        checkNotNull(this.parentMenu) { "No parent menu provided" }

        // Play the transition sound
        this.sounds.playMenuOpenParent()

        // Block our closing sound and the sub menus opening sound.
        this.shouldPlayClosingSound = false
        this.parentMenu.openMenu(false)
    }

    /**
     * Closes the inventory UI completely.
     */
    fun closeMenu() {
        this.player.closeInventory()
    }


    // ----------
    //   Events
    // ----------
    @EventHandler
    @Suppress("unused")
    fun onHandleInventoryOpened(event: InventoryOpenEvent) {
        val eventForOtherInventory = event.inventory != this.inventory
        if (eventForOtherInventory) {
            return
        }

        if (this.shouldPlayOpeningSound) {
            this.sounds.playMenuOpen()
        }

        this.handleInventoryOpened(event)
    }

    @EventHandler
    @Suppress("unused")
    fun onHandleInventoryClicked(event: InventoryClickEvent) {
        val eventForOtherInventory = event.inventory != this.inventory
        if (eventForOtherInventory) {
            return
        }

        // Explicitly disable number key modifications.
        if (event.click == ClickType.NUMBER_KEY) {
            event.isCancelled = true
            this.playInvalidAnimation()
            return
        }

        // Here we use the raw slot index instead of the slot index.
        // This is because buttons should only be inside the menu inventory.
        // The index returned by event.getSlot() is relative to the clicked inventory.
        // As the menu is slot index 0 to something, we can use the raw index and skip inventory checks.
        val clickHandler = this.buttonSlots.getOrDefault(event.rawSlot, null)
        if (clickHandler != null) {
            event.isCancelled = true
            clickHandler.handleClick(event)
            return
        }

        this.handleInventoryClicked(event)
    }

    @EventHandler
    @Suppress("unused")
    fun onHandleInventoryClosed(event: InventoryCloseEvent) {
        val eventForOtherInventory = event.inventory != this.inventory
        if (eventForOtherInventory) {
            return
        }

        if (this.shouldPlayClosingSound) {
            this.sounds.playMenuClose()
        }

        this.stopAnimation()
        this.handleInventoryClosed(event)
        HandlerList.unregisterAll(this)
    }


    // -------------
    //   Overrides
    // -------------
    /**
     * Called when the menu is displayed.
     */
    protected open fun handleInventoryOpened(event: InventoryOpenEvent) {
    }

    /**
     * Called when a non button slot is clicked on the menu.
     */
    protected open fun handleInventoryClicked(event: InventoryClickEvent) {
    }

    /**
     * Called when the menu is closed.
     */
    protected open fun handleInventoryClosed(event: InventoryCloseEvent) {
    } // ------------------------
    //   Inventory Operations
    // ------------------------


    /**
     * Gets the item stored in one of menu inventory slots.
     *
     * @param slotIndex The index of the inventory slot.
     * @return The item stored in the inventory slot or null if the slot is empty.
     */
    protected fun getItem(slotIndex: Int): ItemStack? {
        if (slotIndex < 0 || slotIndex >= this.inventory.size) {
            return null
        }
        return this.inventory.getItem(slotIndex)
    }

    protected val items: Array<ItemStack?>
        /**
         * Gets all the items stored in the menus inventory.
         *
         * @return An array containing all the item stacks stored in the menu.
         */
        get() = this.inventory.contents

    protected val inventorySize: Int
        /**
         * Returns the size of the underlying inventory.
         *
         * @return The size of menu inventory.
         */
        get() = this.inventory.size

    /**
     * Copies the item from the menu inventory to the players inventory.
     * Warning: This method does not delete the item from the menu inventory.
     *
     * @param slotIndex          The index of the item slot the item is in.
     * @param shouldDropOnGround True if any leftover items should be dropped onto the ground, otherwise false.
     */
    protected fun giveItemToPlayer(slotIndex: Int, shouldDropOnGround: Boolean) {
        val itemStack = this.inventory.getItem(slotIndex)
        if (itemStack == null) {
            return
        }

        // Give the maximum amount of items to the player.
        val overflowItems = this.player.inventory.addItem(itemStack).values
        if (!shouldDropOnGround) {
            return
        }

        // Throw any remaining items onto the ground.
        for (item in overflowItems) {
            this.player.world.dropItemNaturally(this.player.location, item)
        }
    }

    /**
     * Sends a message to the player that is viewing this menu inventory.
     *
     * @param component The component to send to the player's chat.
     */
    protected fun sendMessageToPlayer(component: Component) {
        this.player.sendMessage(component)
    }

    /**
     * Sets the maximum size a stack can be.
     *
     * @param maxStackSize The maximum stack size.
     */
    protected fun setMaxStackSize(maxStackSize: Int) {
        this.inventory.maxStackSize = maxStackSize
    }

    /**
     * Sets one of the menu inventory slots to the specified item.
     *
     * @param slotIndex The inventory slot to update.
     * @param material  The material to create an item stack out of.
     */
    protected fun setSlot(slotIndex: Int, material: Material) {
        this.setSlot(slotIndex, ItemStack(material))
    }

    /**
     * Sets one of the menu inventory slots to the specified item.
     *
     * @param slotIndex The inventory slot to update.
     * @param itemStack The item to insert.
     */
    protected fun setSlot(slotIndex: Int, itemStack: ItemStack) {
        check(!slotIndexOutsideMenuBounds(slotIndex)) { "Provided slot index is outside the bounds of the menu inventory." }

        this.inventory.setItem(slotIndex, itemStack)
    }

    /**
     * Sets all the menu inventory slots to the specified item.
     *
     * @param material The material to create an item stack out of.
     */
    protected fun setSlots(material: Material) {
        this.setSlots(ItemStack(material))
    }

    /**
     * Sets all the menu inventory slots to the specified item.
     *
     * @param itemStack The item to insert.
     */
    protected fun setSlots(itemStack: ItemStack) {
        for (slotIndex in 0..<this.inventory.size) {
            this.setSlot(slotIndex, itemStack)
        }
    }

    /**
     * Converts one of the menu inventory slots to be a button.
     *
     * @param slotIndex The inventory slot to convert.
     * @param itemStack The item to represent the button.
     * @param handler   The function to invoke when the button is pressed.
     */
    protected fun setButton(slotIndex: Int, itemStack: ItemStack, handler: MenuButtonClickHandler) {
        check(!slotIndexOutsideMenuBounds(slotIndex)) { "Provided slot index is outside the bounds of the menu inventory." }

        this.setSlot(slotIndex, itemStack)
        this.buttonSlots.put(slotIndex, handler)
    }

    /**
     * Replaces instances of an item in the menus inventory with another one.
     *
     * @param oldItem The item to replace.
     * @param newItem The item to replace the old item with.
     */
    protected fun replaceSlots(oldItem: ItemStack, newItem: ItemStack) {
        for (slotIndex in 0..<this.inventory.size) {
            val shouldReplace = oldItem == this.getItem(slotIndex)
            if (shouldReplace) {
                this.setSlot(slotIndex, newItem)
            }
        }
    }

    /**
     * Removes all items from the menus inventory.
     */
    fun clear() {
        this.inventory.clear()
        this.buttonSlots.clear()
    }

    /**
     * Removes an item from the menus inventory.
     *
     * @param slotIndex The index of the inventory slot to clear.
     */
    protected fun clearSlot(slotIndex: Int) {
        check(!slotIndexOutsideMenuBounds(slotIndex)) { "Provided slot index is outside the bounds of the menu inventory." }

        this.inventory.setItem(slotIndex, null)
        this.buttonSlots.remove(slotIndex)
    }


    // -----------
    //   Borders
    // -----------
    /**
     * Creates a border around the perimeter of the menus inventory.
     */
    fun setBorderEdge() {
        val canApplyBorder = this.inventory.size >= (3 * 9)
        require(canApplyBorder) { "Edge borders can only be applied to menus with 3 or more rows" }

        for (slotIndex in 0..<this.inventory.size) {
            val isTopSlot = slotIndex <= 8
            val isBottomSlot = this.inventory.size - slotIndex <= 9
            val isSideSlot = slotIndex % 9 == 0 || slotIndex % 9 == 8
            if (isTopSlot || isBottomSlot || isSideSlot) {
                this.setSlot(slotIndex, BORDER_NORMAL)
            }
        }
    }

    fun setBorderBottom() {
        // Make all the slots in the bottom row a border.

        for (slotIndex in this.inventory.size - 9..<this.inventory.size) this.setSlot(
            slotIndex,
            BORDER_NORMAL
        )
    }

    /**
     * Creates a border which covers every slot in the menus inventory.
     */
    protected fun setBorderFull() {
        this.setSlots(BORDER_NORMAL)
    }


    /**
     * Plays an animation which signifies the user performed a valid operation.
     *
     * @param playSound True if the success sound should be played, otherwise false.
     */
    // --------------
    //   Animations
    // --------------
    @JvmOverloads
    protected fun playSuccessAnimation(playSound: Boolean = true) {
        stopAnimation()
        val successBorder: ItemStack = createNamedItem(Material.LIME_STAINED_GLASS_PANE, Component.text(""))
        this.activeAnimation = SMPRPG.getService(AnimationService::class.java).playOnce(
            {
                if (playSound) this.sounds.playActionConfirm()
                this.replaceSlots(BORDER_NORMAL, successBorder)
                WaitFor.milliseconds(200)
            },
            {
                this.replaceSlots(successBorder, BORDER_NORMAL)
                WaitFor.nothing()
            }
        )
    }

    /**
     * Plays an animation which signifies the user performed an invalid operation.
     *
     * @param playSound True if the error sound should be played, otherwise false.
     */
    @JvmOverloads
    protected fun playInvalidAnimation(playSound: Boolean = true) {
        stopAnimation()
        val errorBorder: ItemStack = createNamedItem(Material.RED_STAINED_GLASS_PANE, Component.text(""))
        this.activeAnimation = SMPRPG.getService(AnimationService::class.java).playOnce(
            {
                if (playSound) this.sounds.playActionError()
                this.replaceSlots(BORDER_NORMAL, errorBorder)
                WaitFor.milliseconds(200)
            },
            {
                this.replaceSlots(errorBorder, BORDER_NORMAL)
                WaitFor.nothing()
            }
        )
    }

    /**
     * Stops and cleans up any animation that is currently active.
     */
    protected fun stopAnimation() {
        if (this.activeAnimation != null) {
            this.activeAnimation!!.stop()
            this.activeAnimation = null
        }
    }


    // -----------
    //   Presets
    // -----------
    /**
     * Creates a context aware back/close button.
     *
     * @param slotIndex The slot to place the button in.
     */
    protected fun setBackButton(slotIndex: Int) {
        if (this.parentMenu == null) {
            this.setButton(slotIndex, BUTTON_EXIT) { e: InventoryClickEvent -> this.closeMenu() }
        } else {
            this.setButton(slotIndex, BUTTON_BACK) { e: InventoryClickEvent -> this.openParentMenu() }
        }
    }

    /**
     * Creates a context aware back/close button by automatically assuming the caller wants to place it at the bottom
     * of the menu, in the center.
     */
    protected fun setBackButton() {
        this.setBackButton(this.inventorySize - 5) // Shift 5 slots from the end.
    }

    /**
     * Plays a one-shot sound on the player's client.
     *
     * @param sound  The sound effect to play.
     * @param volume How loud the sound should be.
     * @param pitch  The pitch of the sound effect.
     */
    // -----------
    //   Helpers
    // -----------
    /**
     * Plays a one-shot sound on the player's client.
     *
     * @param sound The sound effect to play.
     */
    @JvmOverloads
    protected fun playSound(sound: Sound, volume: Float = 1.0f, pitch: Float = 1.0f) {
        this.player.playSound(this.player.location, sound, volume, pitch)
    }

    /**
     * Attempts to add the specified item to the players inventory.
     *
     * @param item The item to give to the player.
     */
    protected fun giveItemToPlayer(item: ItemStack?) {
        val overflow = this.player.inventory.addItem(item!!)
        for (overflowItem in overflow.entries) {
            this.player.world.dropItemNaturally(this.player.eyeLocation, overflowItem.value)
        }
    }

    /**
     * Returns if the provided slot index is outside the bounds of the menu inventory.
     *
     * @param slotIndex The relative or raw slot index to check.
     * @return True if it's outside the bounds, otherwise false.
     */
    protected fun slotIndexOutsideMenuBounds(slotIndex: Int): Boolean {
        return slotIndex < 0 || this.inventory.size <= slotIndex
    }

    companion object {
        // -----------
        //   Presets
        // -----------
        @JvmField
        protected val BORDER_NORMAL: ItemStack = createNamedItem(Material.BLACK_STAINED_GLASS_PANE, Component.text(""))
        @JvmField
        protected val BUTTON_PAGE_NEXT: ItemStack =
            createNamedItem(Material.ARROW, Component.text("Next Page ->", NamedTextColor.BLUE))
        @JvmField
        protected val BUTTON_PAGE_PREVIOUS: ItemStack =
            createNamedItem(Material.ARROW, Component.text("<- Previous Page", NamedTextColor.BLUE))
        @JvmField
        protected val BUTTON_BACK: ItemStack =
            createNamedItem(Material.ARROW, Component.text("Back", NamedTextColor.BLUE))
        @JvmField
        protected val BUTTON_EXIT: ItemStack =
            createNamedItem(Material.BARRIER, Component.text("Exit", NamedTextColor.RED))

        /**
         * Creates an item stack with a custom name.
         *
         * @param material The type of the item to create an item stack of.
         * @param name     The name to apply to the item stack.
         * @return The named item stack.
         */
        @JvmStatic
        protected fun createNamedItem(material: Material, name: String?): ItemStack {
            val item = ItemStack(material)
            val meta = item.itemMeta
            meta.displayName(ComponentUtils.create(name, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
            item.setItemMeta(meta)
            return item
        }

        /**
         * Creates an item stack with a custom name.
         *
         * @param material The type of the item to create an item stack of.
         * @param name     The name to apply to the item stack.
         * @return The named item stack.
         */
        @JvmStatic
        protected fun createNamedItem(material: Material, name: Component): ItemStack {
            val item = ItemStack(material)
            val meta = item.itemMeta
            meta.displayName(name.decoration(TextDecoration.ITALIC, false))
            item.setItemMeta(meta)
            return item
        }
    }
}
