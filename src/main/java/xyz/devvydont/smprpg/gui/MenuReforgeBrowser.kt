package xyz.devvydont.smprpg.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuButtonClickHandler
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
import xyz.devvydont.smprpg.util.formatting.Symbols

/**
 * Renders all the reforges in the game for people to browse.
 */
class MenuReforgeBrowser : MenuBase {
    constructor(player: Player) : super(player, ROWS)

    constructor(player: Player, parent: MenuBase?) : super(player, ROWS, parent)

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        super.handleInventoryOpened(event)
        event.titleOverride(Component.text("Reforges"))
        this.render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        super.handleInventoryClicked(event)
        event.isCancelled = true
    }

    fun generateReforgeButton(type: ReforgeType): ItemStack {
        val button: ItemStack = createNamedItem(
            type.displayMaterial,
            ComponentUtils.create(type.display(), NamedTextColor.GOLD)
        )
        val reforge = SMPRPG.getService(ItemService::class.java).getReforge(type)
        if (reforge == null) return button

        val rarity = ItemRarity.RARE
        val lore: MutableList<Component> = ArrayList()
        lore.add(ComponentUtils.EMPTY)
        val rollable = type.isRollable
        lore.add(
            ComponentUtils.merge(
                ComponentUtils.create("Rollable? "),
                ComponentUtils.create(
                    if (rollable) Symbols.CHECK else Symbols.X,
                    if (rollable) NamedTextColor.GREEN else NamedTextColor.RED
                )
            )
        )
        lore.add(ComponentUtils.EMPTY)
        lore.addAll(reforge.description)
        lore.add(ComponentUtils.EMPTY)
        lore.add(
            ComponentUtils.merge(
                ComponentUtils.create("Showing stats for ", NamedTextColor.GOLD),
                ComponentUtils.create(rarity.name, rarity.color)
            )
        )
        lore.addAll(reforge.formatAttributeModifiersWithRarity(ItemRarity.RARE))
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Valid Equipment:", NamedTextColor.BLUE))
        for (clazz in type.allowedItems) lore.add(
            ComponentUtils.create(
                "- " + MinecraftStringUtils.getTitledString(
                    clazz.name
                )
            )
        )
        button.lore(ComponentUtils.cleanItalics(lore))
        return button
    }

    fun handleButtonClicked(reforgeType: ReforgeType?) {
    }

    fun render() {
        setBorderBottom()

        var reforgeIndex = 0
        // todo paginate if there are too many reforges
        for (slot in 0..<inventorySize) {
            // Already occupied?

            val item = getItem(slot)
            if (item != null && item.type != Material.AIR) continue

            // No more reforges?
            if (reforgeIndex + 1 >= ReforgeType.entries.size) break

            // Render
            val reforgeType = ReforgeType.entries[reforgeIndex + 1]
            setButton(
                slot,
                generateReforgeButton(reforgeType)
            ) { event: InventoryClickEvent -> handleButtonClicked(reforgeType) }
            reforgeIndex++
        }

        // Create a back button
        this.setBackButton((ROWS - 1) * 9 + 4)
    }

    companion object {
        const val ROWS: Int = 6
    }
}
