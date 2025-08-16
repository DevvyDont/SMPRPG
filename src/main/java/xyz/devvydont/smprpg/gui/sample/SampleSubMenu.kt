package xyz.devvydont.smprpg.gui.sample

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuButtonClickHandler
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class SampleSubMenu(player: Player, parentMenu: MenuBase?, private val toDisplay: Material) :
    MenuBase(player, 3, parentMenu) {
    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(ComponentUtils.create("Sample Sub Menu", NamedTextColor.BLACK))

        this.setBorderEdge()
        this.setSlot(13, toDisplay)
        this.setButton(22, BUTTON_BACK) { e: InventoryClickEvent -> this.openParentMenu() }
    }
}
