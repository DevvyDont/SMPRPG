package xyz.devvydont.smprpg.gui.items

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols

class MenuTrashItems(player: Player) : MenuBase(player, 6) {
    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        // Prepare the inventory
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(
                    Symbols.OFFSET_NEG_32 + Symbols.OFFSET_NEG_16 + Symbols.TRASH_MENU,
                    NamedTextColor.WHITE
                ),
                ComponentUtils.create(
                    Symbols.OFFSET_NEG_128 + Symbols.OFFSET_NEG_32 + Symbols.OFFSET_NEG_8 + Symbols.OFFSET_NEG_4 + "Trash Items",
                    Symbols.INVENTORY_TITLE_COLOR
                )
            )
        )
        this.setMaxStackSize(100)

        // Render the UI
        this.clear()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = false

        if (event.currentItem == null) return

        val itemBlueprint =
            SMPRPG.getService(ItemService::class.java).getBlueprint(event.currentItem!!)
        // If the item clicked is enchanted or reforged, we should prevent the item from being shift clicked.
        if (event.isShiftClick && (!event.currentItem!!.enchantments.isEmpty() || itemBlueprint.isReforged(
                event.currentItem
            ))
        ) {
            val error = ComponentUtils.merge(
                ComponentUtils.create("WHOA THERE!", NamedTextColor.RED, TextDecoration.BOLD),
                ComponentUtils.create(" Do you really want to trash this? Manually drag the item in the menu if so.")
            )
            player.sendMessage(ComponentUtils.alert(error, NamedTextColor.RED))
            player.playSound(player.location, Sound.ENTITY_VILLAGER_DEATH, 1f, 1.5f)
            event.isCancelled = true
        }
    }

    override fun handleInventoryClosed(event: InventoryCloseEvent) {
        var itemCount = 0
        for (itemStack in this.items) {
            if (itemStack == null) {
                continue
            }

            itemCount += itemStack.amount
            itemStack.amount = 0
        }

        this.player.sendMessage(
            ComponentUtils.success(
                ComponentUtils.merge(
                    ComponentUtils.create("You trashed ", NamedTextColor.GREEN),
                    ComponentUtils.create(itemCount.toString(), NamedTextColor.AQUA),
                    ComponentUtils.create(" items!", NamedTextColor.GREEN)
                )
            )
        )
    }
}
