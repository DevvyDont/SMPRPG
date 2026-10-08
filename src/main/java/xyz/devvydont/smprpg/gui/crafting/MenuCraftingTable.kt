package xyz.devvydont.smprpg.gui.crafting

import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.gui.InterfaceUtil
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.gui.base.IRecipeDependentMenu
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.recipe.CraftingRecipeMatcher
import xyz.devvydont.smprpg.recipe.core.RecipeRequirements
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import xyz.devvydont.smprpg.util.formatting.TooltipStyle

/**
 * A fully custom crafting table interface. The player fills a 3x3 grid and the result slot previews the
 * output, committing it when clicked. Matching is driven by [CraftingRecipeMatcher]: data-driven custom
 * recipes (count-aware) are tried first, with vanilla recipes as a fallback.
 */
class MenuCraftingTable(player: Player) : MenuBase(player, ROWS), IRecipeDependentMenu {

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        super.handleInventoryOpened(event)
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.CRAFTING_3X3_MENU, NamedTextColor.WHITE),
                ComponentUtils.create(
                    Symbols.OVERLAY_BG_OFFSET_STANDARD + "Crafting Table",
                    Symbols.INVENTORY_TITLE_COLOR
                )
            )
        )
        render()
    }

    private fun render() {
        setBorderFull()  // Remove Forced when UI is made. UI does not look good w/o a UI resource
        for (slot in GRID_SLOTS)
            clearSlot(slot)
        refreshResult()
    }

    /** Read the 3x3 grid into a row-major list of 9 cells. */
    private fun readGrid(): List<ItemStack?> = GRID_SLOTS.map { getItem(it) }

    private fun computeResult(): CraftingRecipeMatcher.Match? =
        CraftingRecipeMatcher.match(readGrid(), player.world)

    private fun refreshResult() {
        val match = computeResult()
        IDLE_INDICATOR.setData(DataComponentTypes.TOOLTIP_STYLE, TooltipStyle.INFO.key)
        IDLE_INDICATOR.setData(DataComponentTypes.ITEM_MODEL, NamespacedKey(plugin, "empty"))
        if (match == null) {
            setButton(RESULT_SLOT, IDLE_INDICATOR) { _: InventoryClickEvent -> }
            return
        }
        // A locked recipe (unmet requirements) previews as a barrier and can't be committed.
        val requirements = match.recipe?.requirements
        if (requirements != null && !requirements.meets(player)) {
            setButton(RESULT_SLOT, lockedIndicator(requirements)) { _: InventoryClickEvent -> playInvalidAnimation() }
            return
        }
        setButton(RESULT_SLOT, match.result) { event: InventoryClickEvent -> commit(event) }
    }

    /** A barrier preview listing the requirements the player has not yet met for a locked recipe. */
    private fun lockedIndicator(requirements: RecipeRequirements): ItemStack {
        val lore = mutableListOf<Component>()
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("You don't meet the requirements:", NamedTextColor.RED))
        for ((skill, level) in requirements.unmet(player))
            lore.add(ComponentUtils.create("- ${skill.displayName} level $level", NamedTextColor.GRAY))
        return InterfaceUtil.getNamedItemWithDescription(
            Material.BARRIER,
            ComponentUtils.create("Locked", NamedTextColor.RED),
            *lore.toTypedArray()
        )
    }

    /**
     * Commit a craft from a click on the result slot, mirroring vanilla: a normal click picks a single craft's
     * worth of items onto the cursor, while a shift-click crafts as many as the grid allows straight into the
     * inventory. The live grid is recomputed each step so a stale preview can never be committed.
     */
    private fun commit(event: InventoryClickEvent) {
        if (event.isShiftClick)
            commitToInventory()
        else
            commitToCursor(event)
    }

    /**
     * Vanilla single-click craft: place one craft's worth of result onto the cursor. An empty cursor simply
     * receives the result; an occupied cursor receives it only if the result can stack onto what's already held
     * (same item, with room for the full amount), exactly as vanilla lets you keep stacking crafts onto the
     * cursor and refuses the click otherwise.
     */
    private fun commitToCursor(event: InventoryClickEvent) {
        val match = craftable() ?: run { playInvalidAnimation(); return }
        val result = match.result
        val cursor = event.cursor
        val cursorOccupied = cursor != null && !cursor.isEmpty
        if (cursorOccupied && !canStackOnto(cursor!!, result)) {
            playInvalidAnimation()
            return
        }
        consumeMatch(match)
        val held = if (cursorOccupied) cursor!!.clone().apply { amount += result.amount } else result.clone()
        player.setItemOnCursor(held)
        // A cancelled click leaves the client thinking the cursor is empty; resync it next tick.
        Bukkit.getScheduler().runTaskLater(plugin, Runnable { player.updateInventory() }, 1L)
        grantRewards(match)
        playSound(Sound.ENTITY_ITEM_PICKUP, 0.6f, 1.0f)
        refreshResult()
    }

    /** Whether a freshly crafted [result] can be added in full onto an already-held [cursor] stack. */
    private fun canStackOnto(cursor: ItemStack, result: ItemStack): Boolean =
        cursor.isSimilar(result) && cursor.amount + result.amount <= result.maxStackSize

    /**
     * Vanilla shift-click craft: repeatedly craft the SAME recipe into the inventory until the grid no longer
     * produces it. Locking onto the first result prevents the leftovers from silently crafting a different
     * recipe (e.g. finishing pressure plates and then turning the last plank into a button).
     */
    private fun commitToInventory() {
        val first = craftable() ?: run { playInvalidAnimation(); return }
        val target = resultIdentifier(first.result)
        var crafted = 0
        var match: CraftingRecipeMatcher.Match? = first
        while (match != null && crafted < SHIFT_CRAFT_LIMIT) {
            if (resultIdentifier(match.result) != target) break
            if (!craftAllowed(match)) break
            consumeMatch(match)
            giveItemToPlayer(match.result.clone())
            grantRewards(match)
            crafted++
            match = computeResult()
        }
        if (crafted == 0) {
            playInvalidAnimation()
            return
        }
        playSound(Sound.ENTITY_ITEM_PICKUP, 0.6f, 1.0f)
        refreshResult()
    }

    /** The current committable match (non-empty, requirements met), or null if nothing can be crafted now. */
    private fun craftable(): CraftingRecipeMatcher.Match? {
        val match = computeResult() ?: return null
        return if (craftAllowed(match)) match else null
    }

    /** A match is craftable when it consumes something and the player meets any recipe requirements. */
    private fun craftAllowed(match: CraftingRecipeMatcher.Match): Boolean {
        if (match.consumption.isEmpty()) return false
        val recipe = match.recipe
        return recipe == null || recipe.requirements.meets(player)
    }

    /** Remove the matched ingredients from the grid, returning vanilla container remainders (buckets, bottles). */
    private fun consumeMatch(match: CraftingRecipeMatcher.Match) {
        val itemService = SMPRPG.getService(ItemService::class.java)
        for ((cell, amount) in match.consumption) {
            val slot = GRID_SLOTS[cell]
            val item = getItem(slot)
            consume(slot, amount)
            if (item != null && itemService.getItemKey(item) == null) {
                val remainder = item.type.craftingRemainingItem
                if (remainder != null) giveItemToPlayer(ItemStack(remainder, amount))
            }
        }
    }

    private fun grantRewards(match: CraftingRecipeMatcher.Match) {
        match.recipe?.rewards?.grant(player, SkillExperienceGainEvent.ExperienceSource.FORGE)
    }

    /** The `namespace:path` identity of a result stack, used to detect when a shift-craft would change recipes. */
    private fun resultIdentifier(stack: ItemStack): String =
        SMPRPG.getService(ItemService::class.java).getIdentifier(stack)

    /** Remove [amount] items from the given slot, clearing it entirely if nothing remains. */
    private fun consume(slotIndex: Int, amount: Int) {
        val item = getItem(slotIndex) ?: return
        val remaining = item.amount - amount
        if (remaining <= 0) {
            clearSlot(slotIndex)
            return
        }
        item.amount = remaining
        setSlot(slotIndex, item)
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        super.handleInventoryClicked(event)

        // Recompute the result once the click has settled and the grid contents have updated.
        Bukkit.getScheduler().runTaskLater(plugin, Runnable { refreshResult() }, 1L)

        // Players may freely manage their own inventory (shift-clicks flow into the grid).
        val clicked = event.clickedInventory
        if (clicked != null && clicked.type == InventoryType.PLAYER) return

        // Inside the menu, only the 3x3 grid slots may be edited directly.
        if (clicked == inventory && event.rawSlot in GRID_SLOTS) return

        // Clicking outside the window throws the held cursor item into the world, just like vanilla.
        if (event.slotType == InventoryType.SlotType.OUTSIDE) return

        event.isCancelled = true
    }

    /**
     * Prevents dragging items onto protected slots (borders and the result), which would otherwise strand items
     * that never return to the player. Drags across the grid are allowed and refresh the result.
     */
    @EventHandler
    @Suppress("unused")
    private fun onItemDragged(event: InventoryDragEvent) {
        if (event.inventory != inventory) return
        val touchesProtectedSlot = event.rawSlots.any { it < inventorySize && it !in GRID_SLOTS }
        if (touchesProtectedSlot) {
            event.isCancelled = true
            return
        }
        Bukkit.getScheduler().runTaskLater(plugin, Runnable { refreshResult() }, 1L)
    }

    /** Return the grid items to the player so nothing is lost when the menu closes. */
    override fun handleInventoryClosed(event: InventoryCloseEvent) {
        super.handleInventoryClosed(event)
        for (slot in GRID_SLOTS)
            giveItemToPlayer(slot, true)
    }

    companion object {
        const val ROWS = 5

        /** Upper bound on a single shift-click's crafts; a full grid of single-item stacks can never exceed this. */
        const val SHIFT_CRAFT_LIMIT = 64

        // 3x3 grid laid out row-major in the upper-left, with the result to the right of center.
        val GRID_SLOTS = listOf(10, 11, 12, 19, 20, 21, 28, 29, 30)
        const val RESULT_SLOT = 24

        private val IDLE_INDICATOR: ItemStack = InterfaceUtil.getNamedItemWithDescription(
            Material.GRAY_STAINED_GLASS_PANE,
            ComponentUtils.create("Result", NamedTextColor.WHITE),
            ComponentUtils.EMPTY,
            ComponentUtils.create("Place ingredients in the grid to craft.", NamedTextColor.GRAY)
        )
    }
}
