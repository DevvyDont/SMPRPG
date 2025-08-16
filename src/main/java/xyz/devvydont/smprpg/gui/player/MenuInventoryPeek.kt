package xyz.devvydont.smprpg.gui.player

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.scheduler.BukkitTask
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuButtonClickHandler
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/*
 * Represents a viewable inventory of another player. Used as a way for admins to "peek" into someone's inventory to
 * see what they have.
 */
class MenuInventoryPeek @JvmOverloads constructor(
    owner: Player,
    private val targetPlayer: Player,
    parentMenu: MenuBase? = null
) : MenuBase(owner, 6, parentMenu) {
    private var useEnderChest = false // A flag that can be set to instead read the ender chest instead of the inventory.
    private var inventoryUpdateTask: BukkitTask? = null

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        // Prepare the inventory
        event.titleOverride(ComponentUtils.create(targetPlayer.name + "'s Inventory", NamedTextColor.BLACK))

        // Update the inventory layout every tick to match the player.
        inventoryUpdateTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable { this.renderInventory() }, 0, 10)
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
        this.playInvalidAnimation()
    }

    override fun handleInventoryClosed(event: InventoryCloseEvent) {
        if (inventoryUpdateTask != null) {
            inventoryUpdateTask!!.cancel()
            inventoryUpdateTask = null
        }
    }

    /*
     * Updates the interface to match the inventory of the player that we are currently viewing, if there is one.
     */
    private fun renderInventory() {
        // Prepare the inventory
        this.clear()
        this.setBorderEdge()

        // Render the players inventory
        val playersInventory =
            if (this.useEnderChest) this.targetPlayer.enderChest else this.targetPlayer.inventory
        val inventoryItems = playersInventory.contents
        for (i in inventoryItems.indices) {
            // Render nothing if there's no items.
            val item = inventoryItems[i]
            val slotIndex =
                (if (this.useEnderChest) i else slotOverrides.getOrDefault(i, i))
            if (item == null) {
                this.clearSlot(slotIndex)
                continue
            }

            // Render the item into the slot
            this.setSlot(slotIndex, item.clone())
        }

        // Render the swap chest button
        val chestButton =
            createNamedItem(Material.CHEST, ComponentUtils.create("Switch to Inventory", NamedTextColor.GOLD))
        val enderButton = createNamedItem(
            Material.ENDER_CHEST,
            ComponentUtils.create("Switch to Ender Chest", NamedTextColor.LIGHT_PURPLE)
        )
        val activeButton = if (this.useEnderChest) chestButton else enderButton
        this.setButton(CHEST_TOGGLE_SLOT, activeButton) { e: InventoryClickEvent ->
            this.useEnderChest = !this.useEnderChest
            this.playSound(if (this.useEnderChest) Sound.BLOCK_ENDER_CHEST_OPEN else Sound.BLOCK_ENDER_CHEST_CLOSE)
            this.renderInventory()
            this.playSuccessAnimation(false)
        }

        // Render the back/close button
        this.setBackButton(CLOSE_BUTTON)
    }

    companion object {
        // In order to prettily display the inventory, we override some slot positions in this interface to make it make
        // more sense. Keys are indexes of the player's inventory we are viewing, values are indexes of our GUI.
        private val slotOverrides = HashMap<Int, Int>()

        init {

            // First, map the hotbar to be in the 4th row by shifting it down 3 rows.
            for (i in 0..8) slotOverrides.put(i, i + 27)

            // Now shift the next 3 rows up by 1 row. This is the inventory of the player excluding the hotbar.
            for (i in 9..35) slotOverrides.put(i, i - 9)

            // Armor slots, these simply just need to be reversed. We can also shift them so offhand can be on the left
            slotOverrides.put(36, 39 + 5)
            slotOverrides.put(37, 38 + 5)
            slotOverrides.put(38, 37 + 5)
            slotOverrides.put(39, 36 + 5)

            // Now finally offhand. We are just shifting it so there's a gap between it and the armor.
            slotOverrides.put(40, 39)
        }

        const val CHEST_TOGGLE_SLOT: Int = 46
        const val CLOSE_BUTTON: Int = 49
    }
}
