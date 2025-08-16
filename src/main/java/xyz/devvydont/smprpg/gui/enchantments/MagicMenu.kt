package xyz.devvydont.smprpg.gui.enchantments

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import xyz.devvydont.smprpg.gui.InterfaceUtil.getNamedItemWithDescription
import xyz.devvydont.smprpg.gui.MenuReforgeBrowser
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuButtonClickHandler
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class MagicMenu : MenuBase {
    constructor(player: Player) : super(player, 5)

    constructor(player: Player, parentMenu: MenuBase?) : super(player, 5, parentMenu)

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
        this.playInvalidAnimation()
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(Component.text("Magic Menu"))
        render()
    }

    fun render() {
        this.setBorderFull()
        this.setBackButton()

        this.setButton(
            ENCHANTMENTS_INDEX, getNamedItemWithDescription(
                Material.ENCHANTING_TABLE,
                ComponentUtils.create("Enchantments", NamedTextColor.LIGHT_PURPLE),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Click to view enchantments!", NamedTextColor.YELLOW)
            )
        ) { e: InventoryClickEvent -> EnchantmentMenu(this.player, this).openMenu() }

        this.setButton(
            REFORGES_INDEX, getNamedItemWithDescription(
                Material.ANVIL,
                ComponentUtils.create("Reforges", NamedTextColor.BLUE),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Click to view reforges!", NamedTextColor.YELLOW)
            )
        ) { e: InventoryClickEvent -> MenuReforgeBrowser(this.player, this).openMenu() }

        this.setBackButton()
    }

    companion object {
        const val ENCHANTMENTS_INDEX: Int = 20
        const val REFORGES_INDEX: Int = 24
    }
}
