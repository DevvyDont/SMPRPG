package xyz.devvydont.smprpg.gui.player

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.gui.InterfaceUtil
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * The root settings menu. Acts as a hub that branches into categorized setting submenus. Currently the only
 * category is HUD options, but this is the place to add further categories in the future.
 */
class MenuPlayerSettings : MenuBase {

    constructor(player: Player) : super(player, ROWS)
    constructor(player: Player, parent: MenuBase?) : super(player, ROWS, parent)

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(ComponentUtils.create("Settings", NamedTextColor.DARK_GRAY))
        render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
        this.playInvalidAnimation()
    }

    private fun render() {
        this.setBorderFull()
        this.setBackButton()

        this.setButton(HUD_OPTIONS_SLOT, hudOptionsDisplay) {
            this.openSubMenu(MenuHudSettings(this.player, this))
        }
    }

    private val hudOptionsDisplay: ItemStack
        get() = InterfaceUtil.getNamedItemWithDescription(
            Material.GLOWSTONE_DUST,
            ComponentUtils.create("HUD Options", NamedTextColor.GOLD),
            ComponentUtils.create("Customize what shows on your"),
            ComponentUtils.create("action bar and experience bar."),
            ComponentUtils.EMPTY,
            ComponentUtils.create("Click to configure your HUD!", NamedTextColor.YELLOW)
        )

    companion object {
        private const val ROWS = 3
        private const val HUD_OPTIONS_SLOT = 13
    }
}
