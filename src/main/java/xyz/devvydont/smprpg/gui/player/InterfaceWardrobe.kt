package xyz.devvydont.smprpg.gui.player

import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.util.TriState
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.*
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.player.EquipmentSet
import xyz.devvydont.smprpg.entity.player.PlayerWardrobe
import xyz.devvydont.smprpg.entity.player.UpgradeCategory
import xyz.devvydont.smprpg.entity.player.WardrobeUpgrade
import xyz.devvydont.smprpg.gui.InterfaceUtil
import xyz.devvydont.smprpg.gui.InterfaceUtil.getNamedItem
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.WardrobeService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import xyz.devvydont.smprpg.util.persistence.KeyStore
import xyz.devvydont.smprpg.util.persistence.PDCAdapters

/**
 * The top left corner of the UI. Used as an anchor. Maps to the first helmet slot for the first set.
 */
const val TOP_LEFT_CORNER = 10

/**
 * How many sets to render per page. Make sure to use a number that makes sense to prevent overflow.
 */
const val SETS_PER_PAGE = 7

const val MAX_PAGES = 4

class InterfaceWardrobe(parent: MenuBase?, viewer: Player, val target: Player) : MenuBase(viewer, 6, parent) {

    private var page = 0
    private lateinit var wardrobe: PlayerWardrobe

    override fun handleInventoryClicked(event: InventoryClickEvent) {

        // Someone viewing someone else's wardrobe can't interact with it.
        if (target != this.player) {
            event.isCancelled = true
            this.playInvalidAnimation()
            return
        }

        // Blacklist events where the player is performing the offhand swap keybind. This is hard to tighten down.
        if (event.action == InventoryAction.HOTBAR_SWAP) {
            event.isCancelled = true
            this.playInvalidAnimation()
            return
        }

        // If we shift click in the player inventory, and the clicked item happens to be an armor piece,
        // we need to manually place that piece into the next available slot. Cancel the event if there isn't one.
        val clickedItem = event.currentItem
        val clickedInventory = event.clickedInventory
        if (clickedItem != null && event.isShiftClick && clickedInventory != null && clickedInventory.type == InventoryType.PLAYER) {
            event.isCancelled = true
            val blueprint = ItemService.blueprint(clickedItem)
            val slot = findNextAvailableSlot(blueprint.itemClassification)
            if (slot == null) {
                this.playInvalidAnimation()
                return
            }
            this.setSlot(slot, clickedItem)
            clickedInventory.setItem(event.slot, null)
            savePage()
            render()
            this.playSuccessAnimation()
            return
        }

        // Blacklist any click events that ISN'T armor. This will cut out a lot of cases we don't want.
        if (clickedItem != null && !ItemService.blueprint(clickedItem).itemClassification.isArmor) {
            event.isCancelled = true
            this.playInvalidAnimation()
            return
        }

        // Blacklist any click events that line up with the currently equipped set. There is no reason to click it.
        if (clickedInventory == this.inventory && convertInventorySlotToWardrobeSlot(event.slot) == wardrobe.currentlyEquipped) {
            event.isCancelled = true
            this.playInvalidAnimation()
            return
        }

        // Allow the case where a player is clicking a slot in the interface where the slot type matches while holding an item in their cursor.
        val cursor = event.cursor
        val cursorBlueprint = ItemService.blueprint(cursor)
        if (clickedInventory == this.inventory && cursor.type != Material.AIR) {
            if (isValidSlot(cursorBlueprint.itemClassification, event.slot)) {
                this.playSuccessAnimation()
                Bukkit.getScheduler().runTask(SMPRPG.plugin, Runnable { savePage() })
            } else {
                this.playInvalidAnimation()
                event.isCancelled = true
            }
            return
        }

        // Allow general armor slot clicks. (Air, or armor item.)
        if (event.clickedInventory == inventory && isArmorSlot(event.slot)) {
            Bukkit.getScheduler().runTask(SMPRPG.plugin, Runnable { savePage() })
            return
        }

        // Allow any armor items to be clicked.
        if (clickedItem != null && ItemService.blueprint(clickedItem).itemClassification.isArmor)
            return

        // Allow air to be clicked.
        if (clickedItem == null || clickedItem.type == Material.AIR)
            return

        // Unknown action. Don't allow it.
        this.playInvalidAnimation()
        event.isCancelled = true
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(ComponentUtils.clean("${this.target.name}'s Wardrobe"))
        wardrobe = this.target.persistentDataContainer.getOrDefault(
            KeyStore.PLAYER_WARDROBE, PDCAdapters.WARDROBE_ADAPTER, PlayerWardrobe()
        )
        render()
    }

    override fun handleInventoryClosed(event: InventoryCloseEvent) {
        savePage()
    }

    fun render() {
        setBorderEdge()
        this.setBackButton()

        if (this.player == this.target) {
            val wardrobeService = SMPRPG.getService(WardrobeService::class.java)
            val completed = wardrobeService.getCompletedUpgrades(this.player)
            val totalSlots = WardrobeUpgrade.DEFAULT_SLOTS + completed.size

            val maxSlots = MAX_PAGES * SETS_PER_PAGE
            val lore = mutableListOf<net.kyori.adventure.text.Component>()
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Total Wardrobe Slots: $totalSlots/$maxSlots", NamedTextColor.GRAY))
            lore.add(ComponentUtils.EMPTY)
            for (category in UpgradeCategory.entries) {
                val upgrades = WardrobeUpgrade.byCategory(category)
                val count = upgrades.count { it in completed }
                val total = upgrades.size
                lore.add(
                    ComponentUtils.merge(
                        ComponentUtils.create("${Symbols.POINT} ", NamedTextColor.DARK_GRAY),
                        ComponentUtils.create("${category.displayName}: ", category.color),
                        ComponentUtils.create("$count/$total", NamedTextColor.WHITE)
                    )
                )
            }
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Click to view upgrades!", NamedTextColor.YELLOW))

            val upgradeButton = InterfaceUtil.getNamedItemWithDescription(
                Material.CLAY_BALL,
                ComponentUtils.create("Upgrade Slots", NamedTextColor.GREEN),
                lore
            )
            upgradeButton.setData(
                DataComponentTypes.ITEM_MODEL,
                IModelOverridden.ofMaterial(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
            )
            this.setButton(
                47,
                upgradeButton
            ) { _: InventoryClickEvent ->
                savePage()
                this.openSubMenu(InterfaceWardrobeUpgrades(this.player, this))
            }
        }

        this.setButton(
            45,
            getNamedItem(Material.SPECTRAL_ARROW, ComponentUtils.create("Previous Page (${page + 1}/$MAX_PAGES)", NamedTextColor.AQUA))
        ) { _: InventoryClickEvent ->
            changePage(-1)
            this.sounds.playPagePrevious()
        }
        this.setButton(
            53,
            getNamedItem(Material.SPECTRAL_ARROW, ComponentUtils.create("Next Page (${page + 1}/$MAX_PAGES)", NamedTextColor.AQUA))
        ) { _: InventoryClickEvent ->
            changePage(1)
            this.sounds.playPageNext()
        }

        if (this.player.permissionValue("smprpg.wardrobe.admin") == TriState.TRUE) {
            this.setButton(
                51,
                InterfaceUtil.getNamedItemWithDescription(
                    Material.BEDROCK,
                    ComponentUtils.create(
                        "Wipe Wardrobe",
                        NamedTextColor.RED
                    ),
                    ComponentUtils.EMPTY,
                    ComponentUtils.create("You have permission to wipe this player's"),
                    ComponentUtils.create("entire wardrobe. SHIFT CLICK this button"),
                    ComponentUtils.create("if you would like to proceed."),
                    ComponentUtils.create("NOTE: THIS IS IRREVERSIBLE!", NamedTextColor.RED)
                        .decoration(TextDecoration.BOLD, true)
                )
            ) { e: InventoryClickEvent ->
                if (this.player.permissionValue("smprpg.wardrobe.admin") != TriState.TRUE) {
                    e.isCancelled = true
                    this.playInvalidAnimation()
                    return@setButton
                }
                if (!e.click.isShiftClick) {
                    this.playInvalidAnimation()
                    return@setButton
                }
                this.closeMenu()
                PlayerWardrobe().save(this.target)
                this.target.playSound(this.target.location, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
                this.player.playSound(this.target.location, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
                if (this.target == this.player) {
                    player.sendMessage(ComponentUtils.success("Wardrobe wiped!"))
                    return@setButton
                }
                this.target.sendMessage(ComponentUtils.error("${this.player.name} wiped your wardrobe!"))
                player.sendMessage(ComponentUtils.success("Wiped ${this.target.name}'s wardrobe!"))
            }
        }

        // Clear stale equip button handlers from the previous page render.
        for (col in 0 until SETS_PER_PAGE) {
            clearSlot(TOP_LEFT_CORNER - 9 + col)
        }

        // Loop through every possible wardrobe slot, even if we aren't on that page. We do this so we can easily fill
        // in blanks where the wardrobe may not have an entry defined. Since we can simply just skip logic completely for
        // entries where we are out of bounds, this isn't a big deal.
        val startToShowIndex = page * SETS_PER_PAGE
        val stopShowingIndex = startToShowIndex + SETS_PER_PAGE
        for (index in 0 until MAX_PAGES * SETS_PER_PAGE) {

            if (index !in startToShowIndex..<stopShowingIndex)
                continue

            if (index >= wardrobe.maxCapacity) {
                renderNotUnlocked(index)
                renderEquipButton(index)
                continue
            }
            renderSet(index, wardrobe.query(index))

            // Now render the button that will actually switch to this set.
            renderEquipButton(index)
        }

        // Now, whatever is currently equipped is going to be the set that this person is currently wearing.
        // Obviously, we only need to do this if the index is on screen.
        if (wardrobe.currentlyEquipped !in startToShowIndex..<stopShowingIndex)
            return
        val currentEquipment = EquipmentSet(this.target.equipment)
        renderSet(wardrobe.currentlyEquipped, currentEquipment)
    }

    private fun renderNotUnlocked(index: Int) {
        val guiIndex = index % SETS_PER_PAGE + TOP_LEFT_CORNER
        val item = getNamedItem(Material.CLAY_BALL, ComponentUtils.create("Locked", NamedTextColor.RED))
        this.setSlot(guiIndex, item)
        this.setSlot(guiIndex+9, item)
        this.setSlot(guiIndex+18, item)
        this.setSlot(guiIndex+27, item)
    }

    private fun renderSet(index: Int, set: EquipmentSet?) {
        val guiIndex = index % SETS_PER_PAGE + TOP_LEFT_CORNER
        val fallback = ItemStack.of(Material.AIR)
        this.setSlot(guiIndex, set?.helmet ?: fallback)
        this.setSlot(guiIndex+9, set?.chestplate ?: fallback)
        this.setSlot(guiIndex+18, set?.leggings ?: fallback)
        this.setSlot(guiIndex+27, set?.boots ?: fallback)
    }

    private fun renderEquipButton(index: Int) {

        val inventorySlot = TOP_LEFT_CORNER - 9 + (index%SETS_PER_PAGE)

        // There are many complex cases that determine what the item should be, and what it should say.
        // If the owner is viewing the wardrobe, and they do not have this set equipped, and it's unlocked,
        // it should be a swap to set button.
        if (this.player == this.target && index != wardrobe.currentlyEquipped && index < wardrobe.maxCapacity){
            this.setButton(inventorySlot, InterfaceUtil.getNamedItemWithDescription(
                Material.ARMOR_STAND,
                ComponentUtils.create("Equip Set", NamedTextColor.GREEN),
                ComponentUtils.EMPTY,
                ComponentUtils.create("You currently do not have"),
                ComponentUtils.create("this set equipped"),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Click to equip this set of armor!", NamedTextColor.YELLOW)
            )){ _ -> handleEquip(index)}
            return
        }

        // Now this case where the player is viewing their own wardrobe, and it's already equipped.
        if (this.player == this.target && index == wardrobe.currentlyEquipped) {
            this.setSlot(inventorySlot, InterfaceUtil.getNamedItemWithDescription(
                Material.LIME_DYE,
                ComponentUtils.create("Already Equipped", NamedTextColor.RED),
                ComponentUtils.EMPTY,
                ComponentUtils.create("You currently have this"),
                ComponentUtils.create("set already equipped!"),
                ComponentUtils.EMPTY,
                ComponentUtils.create("To modify, adjust the armor you are wearing!", NamedTextColor.DARK_GRAY)
            ))
            return
        }

        // And the case where the player is viewing their own wardrobe, and the slot is locked.
        if (this.player == this.target && index >= wardrobe.maxCapacity) {
            this.setSlot(inventorySlot, InterfaceUtil.getNamedItemWithDescription(
                Material.LIME_DYE,
                ComponentUtils.create("Locked Set", NamedTextColor.RED),
                ComponentUtils.EMPTY,
                ComponentUtils.create("You currently don't have"),
                ComponentUtils.create("this slot unlocked!"),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Open Upgrade Slots below to learn how!", NamedTextColor.DARK_GRAY)
            ))
            return
        }

        // Finally, this is not the player's wardrobe. The only difference between buttons is the equipped set
        // will be green dye instead of grey.
        val material = if (index == wardrobe.currentlyEquipped)
            Material.LIME_DYE
        else
            Material.GRAY_DYE

        this.setSlot(inventorySlot, InterfaceUtil.getNamedItemWithDescription(
            material,
            ComponentUtils.create("Set #${index+1}", NamedTextColor.GREEN),
            ComponentUtils.create("This is not your wardrobe!")
        ))
    }

    private fun handleEquip(wardrobeIndex: Int) {
        savePage()
        val success = wardrobe.equip(this.target, wardrobeIndex)
        wardrobe.save(this.target)
        if (success)
            this.playSuccessAnimation()
        else
            this.playInvalidAnimation()
        render()
    }

    /**
     * Saves the current page to the target's wardrobe. This will keep changes in sync.
     */
    private fun savePage() {

        // Never save the wardrobe if the player viewing is not the target. They can't make changes.
        if (this.player != this.target)
            return

        val start = page * SETS_PER_PAGE
        for (column in 0 until SETS_PER_PAGE) {
            val wardrobeIndex = start+column
            if (wardrobeIndex == wardrobe.currentlyEquipped)
                continue
            val anchor = TOP_LEFT_CORNER + column
            val set = EquipmentSet(
                this.getItem(anchor) ?: ItemStack.of(Material.AIR),
                this.getItem(anchor+9) ?: ItemStack.of(Material.AIR),
                this.getItem(anchor+18) ?: ItemStack.of(Material.AIR),
                this.getItem(anchor+27) ?: ItemStack.of(Material.AIR),
            )
            wardrobe.set(wardrobeIndex, set)
        }
        wardrobe.save(target)
    }

    private fun changePage(delta: Int) {
        savePage()
        page += delta
        if (page >= MAX_PAGES )
            page = 0
        if (page < 0)
            page = MAX_PAGES - 1
        render()
    }

    /**
     * Checks if the given slot falls within an armor row/column, regardless of occupancy.
     */
    private fun isArmorPosition(index: Int): Boolean {
        if (index >= TOP_LEFT_CORNER && index < TOP_LEFT_CORNER + SETS_PER_PAGE) return true
        if (index >= TOP_LEFT_CORNER + 9 && index < TOP_LEFT_CORNER + SETS_PER_PAGE + 9) return true
        if (index >= TOP_LEFT_CORNER + 18 && index < TOP_LEFT_CORNER + SETS_PER_PAGE + 18) return true
        if (index >= TOP_LEFT_CORNER + 27 && index < TOP_LEFT_CORNER + SETS_PER_PAGE + 27) return true
        return false
    }

    /**
     * Checks if the given slot is allowed for armor to take up.
     */
    private fun isArmorSlot(index: Int): Boolean {
        return isArmorPosition(index)
    }

    /**
     * Checks if the given slot in the inventory is a suitable position for the given item class.
     */
    private fun isValidSlot(clazz: ItemClassification, index: Int): Boolean {
        if (!clazz.isArmor)
            return false
        if (getItem(index) != null)
            return false
        if (clazz == ItemClassification.HELMET)
            return index >= TOP_LEFT_CORNER && index < (TOP_LEFT_CORNER+SETS_PER_PAGE)
        if (clazz == ItemClassification.CHESTPLATE)
            return index >= (TOP_LEFT_CORNER+9) && index < (TOP_LEFT_CORNER+SETS_PER_PAGE+9)
        if (clazz == ItemClassification.LEGGINGS)
            return index >= (TOP_LEFT_CORNER+18) && index < (TOP_LEFT_CORNER+SETS_PER_PAGE+18)
        if (clazz == ItemClassification.BOOTS)
            return index >= (TOP_LEFT_CORNER+27) && index < (TOP_LEFT_CORNER+SETS_PER_PAGE+27)
        return false
    }

    /**
     * Given an inventory slot in the interface, convert it to a wardrobe slot.
     * If there is no suitable match (doesn't resolve to wardrobe item, etc.) then null is returned.
     *
     * This function is considered pretty fragile, as it may not behave if you change the layout of the ui.
     * There isn't really a proper way to solve this.
     */
    private fun convertInventorySlotToWardrobeSlot(index: Int): Int? {

        if (!isArmorPosition(index))
            return null

        val column = index % 9 - 1
        val wardrobeIndex = column + (page * SETS_PER_PAGE)
        return wardrobeIndex
    }

    /**
     * Loops through the entire inventory and finds the first slot that is suitable for this item class.
     */
    private fun findNextAvailableSlot(clazz: ItemClassification): Int? {

        if (!clazz.isArmor)
            return null

        for (index in 0 until inventory.size) {
            if (!isValidSlot(clazz, index))
                continue

            if (convertInventorySlotToWardrobeSlot(index) == wardrobe.currentlyEquipped)
                continue

            val current = inventory.getItem(index)
            if (current == null || current.type == Material.AIR)
                return index
        }

        return null
    }

}
