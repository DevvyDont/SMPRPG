package xyz.devvydont.smprpg.market.gui.bazaar

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.InterfaceUtil
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.market.MarketService
import xyz.devvydont.smprpg.market.bazaar.BazaarCategoryNode
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Subcategory selection grid for a bazaar category node.
 * Shows child nodes as clickable buttons, plus an "Other" button if the parent has direct items.
 */
class MenuBazaarSubcategoryBrowser(
    player: Player,
    private val node: BazaarCategoryNode,
    parentMenu: MenuBase? = null
) : MenuBase(player, ROWS, parentMenu) {

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(Component.text(buildTitle(node.path)))
        render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
        playInvalidAnimation()
    }

    private fun render() {
        setBorderFull()
        setBackButton()

        val bazaarManager = SMPRPG.getService(MarketService::class.java).bazaarManager

        val nonEmptyChildren = node.children.filter { it.allItems.isNotEmpty() }
        val hasDirectItems = node.items.isNotEmpty()
        val totalButtons = nonEmptyChildren.size + if (hasDirectItems) 1 else 0

        val startSlot = ROW_CENTER - totalButtons / 2

        for ((index, child) in nonEmptyChildren.withIndex()) {
            val slot = startSlot + index

            val loreLines = MenuBazaarBrowser.buildItemTooltip(child)
            loreLines.add(ComponentUtils.EMPTY)
            loreLines.add(ComponentUtils.create("Click to browse!", NamedTextColor.YELLOW))

            val item = InterfaceUtil.getNamedItemWithDescription(
                child.icon,
                ComponentUtils.create(child.name, NamedTextColor.GREEN),
                loreLines
            )

            setButton(slot, item) { _: InventoryClickEvent ->
                if (child.hasChildren) {
                    openSubMenu(MenuBazaarSubcategoryBrowser(player, child, this))
                } else {
                    openSubMenu(MenuBazaarCategory(
                        player,
                        buildTitle(child.path),
                        { bazaarManager.getItemsByPath(child.path) },
                        this
                    ))
                }
            }
        }

        if (hasDirectItems) {
            val slot = startSlot + nonEmptyChildren.size

            val loreLines = mutableListOf<Component>()
            loreLines.add(ComponentUtils.create("${node.items.size} items available", NamedTextColor.GRAY))
            loreLines.add(ComponentUtils.EMPTY)
            loreLines.add(ComponentUtils.create("Click to browse!", NamedTextColor.YELLOW))

            val item = InterfaceUtil.getNamedItemWithDescription(
                Material.CHEST,
                ComponentUtils.create("Other", NamedTextColor.GREEN),
                loreLines
            )

            setButton(slot, item) { _: InventoryClickEvent ->
                openSubMenu(MenuBazaarCategory(
                    player,
                    buildTitle(node.path) + " - Other",
                    { bazaarManager.getItemsByPath(node.path) },
                    this
                ))
            }
        }
    }

    companion object {
        private const val ROWS = 4
        private const val ROW_CENTER = 13

        fun buildTitle(path: String): String =
            "Bazaar - " + path.replace("/", " - ")
    }
}
