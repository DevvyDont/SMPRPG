package xyz.devvydont.smprpg.gui.anvil

import net.kyori.adventure.text.format.NamedTextColor
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.data.Directional
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
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols

/**
 * Our own anvil interface, replacing the vanilla anvil GUI entirely. The player places an item to modify in
 * the primary slot and a material/core/reforge stone/excerpt in the secondary slot; the result slot previews
 * the outcome and commits it when clicked. All anvil behavior is driven by [AnvilOperations], keeping the menu
 * agnostic of any specific operation.
 *
 * Note: enchantment combining is intentionally NOT supported here. The result always keeps only the primary
 * item's enchantments; enchantments are managed exclusively through the dedicated enchantment systems.
 *
 * @param anvilLocation The world location of the anvil block that opened this menu, if any. Used to mimic
 *                      vanilla anvil degradation. The custom netherite anvil is exempt.
 */
class MenuAnvil(player: Player, private val anvilLocation: Location? = null) : MenuBase(player, ROWS) {

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        super.handleInventoryOpened(event)
        event.titleOverride(ComponentUtils.merge(
            ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.ANVIL_MENU, NamedTextColor.WHITE),
            ComponentUtils.create(Symbols.OVERLAY_BG_OFFSET_STANDARD + "Anvil", Symbols.INVENTORY_TITLE_COLOR)))
        render()
    }

    /**
     * Lays out the static menu and clears the input slots so the player can fill them.
     */
    private fun render() {
        setBorderFull()
        clearSlot(PRIMARY_SLOT)
        clearSlot(SECONDARY_SLOT)
        refreshResult()
    }

    /**
     * Evaluates the current inputs against every registered operation, returning the first valid result.
     */
    private fun computeResult(): AnvilResult? {
        val primary = getItem(PRIMARY_SLOT)
        val secondary = getItem(SECONDARY_SLOT)
        if (primary == null || primary.type.isAir) return null
        if (secondary == null || secondary.type.isAir) return null

        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        for (operation in AnvilOperations.ALL) {
            val result = operation.tryApply(leveledPlayer, primary, secondary)
            if (result != null) return result
        }
        return null
    }

    /**
     * Updates the result slot to reflect the current inputs. Shows a preview the player can click to commit,
     * or an idle indicator when no operation applies.
     */
    private fun refreshResult() {
        val result = computeResult()
        if (result == null) {
            setButton(RESULT_SLOT, IDLE_INDICATOR) { _: InventoryClickEvent -> }
            return
        }
        setButton(RESULT_SLOT, result.result) { _: InventoryClickEvent -> commit() }
    }

    /**
     * Finalizes the previewed operation: consumes the inputs, hands the result to the player, and awards
     * skill experience. Recomputes from the live inputs so a stale preview can never be committed.
     */
    private fun commit() {
        val result = computeResult()
        if (result == null) {
            playInvalidAnimation()
            return
        }

        consume(PRIMARY_SLOT, PRIMARY_CONSUMED)
        consume(SECONDARY_SLOT, result.secondaryConsumed)
        giveItemToPlayer(result.result)

        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        result.experience.apply(leveledPlayer, SkillExperienceGainEvent.ExperienceSource.ANVIL)

        playSound(Sound.BLOCK_ANVIL_USE, 0.7f, 1.0f)
        playSuccessAnimation(false)
        damageAnvil()
        refreshResult()
    }

    /**
     * Mimics vanilla anvil wear: on each successful use there is a small chance the anvil block degrades one
     * stage (and eventually breaks). The custom netherite anvil is exempt, as are any non-anvil blocks.
     */
    private fun damageAnvil() {
        val location = anvilLocation ?: return
        if (Math.random() >= ANVIL_DAMAGE_CHANCE) return

        val block = location.block

        // Never wear down custom blocks, such as the netherite anvil.
        if (CraftEngineBlocks.isCustomBlock(block)) return

        val nextState = nextAnvilState(block.type) ?: return
        val world = block.world

        if (nextState == Material.AIR) {
            block.type = Material.AIR
            world.playSound(location, Sound.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f)
            closeMenu()
            return
        }

        // Preserve the anvil's facing direction across the material change.
        val facing = (block.blockData as? Directional)?.facing
        block.type = nextState
        if (facing != null) {
            val data = block.blockData
            if (data is Directional) {
                data.facing = facing
                block.blockData = data
            }
        }
        world.playSound(location, Sound.BLOCK_ANVIL_LAND, 0.5f, 1.0f)
    }

    /**
     * Returns the next stage in the anvil degradation chain, or null if the material is not a vanilla anvil.
     * [Material.AIR] indicates the anvil should break.
     */
    private fun nextAnvilState(material: Material): Material? = when (material) {
        Material.ANVIL -> Material.CHIPPED_ANVIL
        Material.CHIPPED_ANVIL -> Material.DAMAGED_ANVIL
        Material.DAMAGED_ANVIL -> Material.AIR
        else -> null
    }

    /**
     * Removes [amount] items from the given slot, clearing it entirely if nothing remains.
     */
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

        // Recompute the result once the click has settled and the inventory contents have updated.
        Bukkit.getScheduler().runTaskLater(plugin, Runnable { refreshResult() }, 1L)

        // Players may freely manage their own inventory. Shift-clicks naturally flow into the two empty input
        // slots, since every other menu slot is occupied by a border or the result button.
        val clicked = event.clickedInventory
        if (clicked != null && clicked.type == InventoryType.PLAYER) return

        // Inside the menu, only the two input slots may be edited directly.
        val isInputSlot = event.rawSlot == PRIMARY_SLOT || event.rawSlot == SECONDARY_SLOT
        if (clicked == inventory && isInputSlot) return

        event.isCancelled = true
    }

    /**
     * Prevents dragging items onto protected menu slots (borders and the result slot), which would otherwise
     * strand items that are never returned to the player.
     */
    @EventHandler
    @Suppress("unused")
    private fun onItemDragged(event: InventoryDragEvent) {
        if (event.inventory != inventory) return

        val touchesProtectedSlot = event.rawSlots.any { slot ->
            slot < inventorySize && slot != PRIMARY_SLOT && slot != SECONDARY_SLOT
        }
        if (touchesProtectedSlot) {
            event.isCancelled = true
            return
        }

        Bukkit.getScheduler().runTaskLater(plugin, Runnable { refreshResult() }, 1L)
    }

    /**
     * Returns the input items to the player so nothing is lost when the menu closes.
     */
    override fun handleInventoryClosed(event: InventoryCloseEvent) {
        super.handleInventoryClosed(event)
        giveItemToPlayer(PRIMARY_SLOT, true)
        giveItemToPlayer(SECONDARY_SLOT, true)
    }

    companion object {
        const val ROWS = 3

        const val PRIMARY_SLOT = 11
        const val SECONDARY_SLOT = 13
        const val RESULT_SLOT = 15

        private const val PRIMARY_CONSUMED = 1

        // Vanilla per-use chance for an anvil to degrade one stage.
        private const val ANVIL_DAMAGE_CHANCE = 0.12

        private val IDLE_INDICATOR: ItemStack = InterfaceUtil.getNamedItemWithDescription(
            Material.GRAY_STAINED_GLASS_PANE,
            ComponentUtils.create("Result", NamedTextColor.GRAY),
            ComponentUtils.EMPTY,
            ComponentUtils.create("Place an item to repair or modify on", NamedTextColor.DARK_GRAY),
            ComponentUtils.create("the left, and a material, repair core,", NamedTextColor.DARK_GRAY),
            ComponentUtils.create("reforge stone, or augment stone on the right.", NamedTextColor.DARK_GRAY)
        )
    }
}
