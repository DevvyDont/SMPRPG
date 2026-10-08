package xyz.devvydont.smprpg.gui

import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import xyz.devvydont.smprpg.util.formatting.TooltipStyle
import kotlin.math.max

/**
 * Renders all the reforges in the game for people to browse. The list is paginated and can be narrowed with the
 * category ([CategoryFilter]) and "rollable only" filters at the bottom of the menu.
 */
class MenuReforgeBrowser : MenuBase {
    constructor(player: Player) : super(player, ROWS)

    constructor(player: Player, parent: MenuBase?) : super(player, ROWS, parent)

    private var page = 0
    private var categoryFilter = CategoryFilter.ALL
    private var rollableOnly = false
    private var displayRarity = ItemRarity.RARE

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        super.handleInventoryOpened(event)
        event.titleOverride(Component.text("Reforges"))
        this.render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        super.handleInventoryClicked(event)
        event.isCancelled = true
    }

    /**
     * The list of reforges to display given the current filter state. The [ReforgeType.ERROR] placeholder is never
     * shown to players.
     */
    private fun visibleReforges(): List<ReforgeType> =
        ReforgeType.entries.filter { type ->
            type != ReforgeType.ERROR &&
                    categoryFilter.matches(type) &&
                    (!rollableOnly || type.isRollable)
        }

    fun generateReforgeButton(type: ReforgeType): ItemStack {
        val button: ItemStack = type.displayItem
        val meta = button.itemMeta
        meta.displayName(ComponentUtils.create(type.display(), NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
        button.setItemMeta(meta)
        button.setData(DataComponentTypes.TOOLTIP_STYLE, TooltipStyle.INFO.key)
        val reforge = SMPRPG.getService(ItemService::class.java).getReforge(type)
        if (reforge == null) return button

        val rarity = displayRarity
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
        lore.addAll(reforge.formatAttributeModifiersWithRarity(rarity))
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

    fun render() {
        this.clear()
        this.setBorderEdge()

        val reforges = visibleReforges()
        val pageCount = max(1, (reforges.size + PAGE_SIZE - 1) / PAGE_SIZE)
        page = page.coerceIn(0, pageCount - 1)

        // Place the reforges for the current page into the content area.
        val offset = page * PAGE_SIZE
        for ((index, slot) in CONTENT_SLOTS.withIndex()) {
            val reforgeIndex = offset + index
            if (reforgeIndex >= reforges.size) break

            val reforgeType = reforges[reforgeIndex]
            setButton(slot, generateReforgeButton(reforgeType)) { _: InventoryClickEvent -> }
        }

        renderControls(pageCount)
    }

    /**
     * Lays out the bottom-row controls: pagination arrows, both filters, and the back button.
     */
    private fun renderControls(pageCount: Int) {
        this.setBackButton(SLOT_BACK)

        val previous = BUTTON_PAGE_PREVIOUS.clone()
        previous.editMeta { it.itemName(ComponentUtils.create("Previous Page (${page + 1}/$pageCount)", NamedTextColor.GOLD)) }
        setButton(SLOT_PREVIOUS_PAGE, previous) { _: InventoryClickEvent ->
            page = (page - 1 + pageCount) % pageCount
            render()
            this.sounds.playPagePrevious()
        }

        val next = BUTTON_PAGE_NEXT.clone()
        next.editMeta { it.itemName(ComponentUtils.create("Next Page (${page + 1}/$pageCount)", NamedTextColor.GOLD)) }
        setButton(SLOT_NEXT_PAGE, next) { _: InventoryClickEvent ->
            page = (page + 1) % pageCount
            render()
            this.sounds.playPageNext()
        }

        setButton(SLOT_CATEGORY_FILTER, createCategoryFilterButton()) { _: InventoryClickEvent ->
            categoryFilter = categoryFilter.next()
            page = 0
            render()
            this.playSound(Sound.UI_BUTTON_CLICK)
        }

        setButton(SLOT_ROLLABLE_FILTER, createRollableFilterButton()) { _: InventoryClickEvent ->
            rollableOnly = !rollableOnly
            page = 0
            render()
            this.playSound(Sound.UI_BUTTON_CLICK)
        }

        setButton(SLOT_RARITY, createRarityButton()) { event: InventoryClickEvent ->
            val rarities = ItemRarity.entries
            val step = if (event.isRightClick) -1 else 1
            displayRarity = rarities[(displayRarity.ordinal + step + rarities.size) % rarities.size]
            render()
            this.playSound(Sound.UI_BUTTON_CLICK)
        }
    }

    private fun createCategoryFilterButton(): ItemStack {
        val display = createNamedItem(
            categoryFilter.icon,
            ComponentUtils.create("Filter: ", NamedTextColor.GOLD)
                .append(ComponentUtils.create(categoryFilter.displayName, NamedTextColor.YELLOW))
        )
        val lore = ArrayList<Component>()
        lore.add(ComponentUtils.EMPTY)
        for (option in CategoryFilter.entries) {
            val color = if (option == categoryFilter) NamedTextColor.GREEN else NamedTextColor.DARK_GRAY
            val prefix = if (option == categoryFilter) "▶ " else "  "
            lore.add(ComponentUtils.create(prefix + option.displayName, color))
        }
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Click to cycle which reforges are shown", NamedTextColor.YELLOW))
        display.editMeta { it.lore(ComponentUtils.cleanItalics(lore)) }
        return display
    }

    private fun createRollableFilterButton(): ItemStack {
        val state = if (rollableOnly) "On" else "Off"
        val display = createNamedItem(
            Material.ANVIL,
            ComponentUtils.create("Show Rollable Only: ", NamedTextColor.GOLD)
                .append(ComponentUtils.create(state, if (rollableOnly) NamedTextColor.GREEN else NamedTextColor.RED))
        )
        display.editMeta { meta ->
            meta.lore(
                ComponentUtils.cleanItalics(
                    listOf(
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("When on, only reforges that can be", NamedTextColor.GRAY),
                        ComponentUtils.create("randomly rolled at an anvil are shown", NamedTextColor.GRAY),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Click to toggle", NamedTextColor.YELLOW)
                    )
                )
            )
            meta.setEnchantmentGlintOverride(rollableOnly)
        }
        return display
    }

    private fun createRarityButton(): ItemStack {
        val display = createNamedItem(
            Material.NAME_TAG,
            ComponentUtils.create("Showing Stats As: ", NamedTextColor.GOLD)
                .append(ComponentUtils.create(displayRarity.name, displayRarity.color))
        )
        val lore = ArrayList<Component>()
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Reforge stats scale with the rarity of", NamedTextColor.GRAY))
        lore.add(ComponentUtils.create("the item they are applied to.", NamedTextColor.GRAY))
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Left click for next rarity", NamedTextColor.YELLOW))
        lore.add(ComponentUtils.create("Right click for previous rarity", NamedTextColor.YELLOW))
        display.editMeta { it.lore(ComponentUtils.cleanItalics(lore)) }
        return display
    }

    /**
     * The set of high level equipment categories a reforge can be narrowed by. A reforge matches a category if any of
     * its allowed item classifications fall within that category. [ALL] matches everything.
     */
    private enum class CategoryFilter(
        val displayName: String,
        val icon: Material,
        private val classifications: Set<ItemClassification>
    ) {
        ALL("All Reforges", Material.NETHER_STAR, emptySet()),
        ARMOR(
            "Armor", Material.DIAMOND_CHESTPLATE,
            setOf(ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS)
        ),
        MELEE(
            "Melee Weapons", Material.DIAMOND_SWORD,
            setOf(
                ItemClassification.SWORD, ItemClassification.SPEAR, ItemClassification.STAFF, ItemClassification.TRIDENT,
                ItemClassification.MACE, ItemClassification.AXE, ItemClassification.WEAPON
            )
        ),
        RANGED(
            "Ranged Weapons", Material.BOW,
            setOf(ItemClassification.BOW, ItemClassification.SHORTBOW, ItemClassification.CROSSBOW)
        ),
        TOOLS(
            "Tools", Material.DIAMOND_PICKAXE,
            setOf(
                ItemClassification.TOOL, ItemClassification.PICKAXE, ItemClassification.DRILL, ItemClassification.SHOVEL,
                ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.AXE
            )
        ),
        FISHING("Fishing Rods", Material.FISHING_ROD, setOf(ItemClassification.ROD)),
        TOMES("Tomes", Material.ENCHANTED_BOOK, setOf(ItemClassification.TOME)),
        CHARMS("Charms", Material.AMETHYST_SHARD, setOf(ItemClassification.CHARM));

        fun next(): CategoryFilter = entries[(ordinal + 1) % entries.size]

        fun matches(type: ReforgeType): Boolean {
            if (classifications.isEmpty()) return true
            return type.allowedItems.any { it in classifications }
        }
    }

    companion object {
        const val ROWS: Int = 6

        // The bottom-row control slots.
        private const val SLOT_PREVIOUS_PAGE = 45
        private const val SLOT_CATEGORY_FILTER = 47
        private const val SLOT_ROLLABLE_FILTER = 48
        private const val SLOT_BACK = 49
        private const val SLOT_RARITY = 50
        private const val SLOT_NEXT_PAGE = 53

        // The inner slots (everything inside the edge border) used to display reforges.
        private val CONTENT_SLOTS: List<Int> = buildList {
            for (row in 1..ROWS - 2)
                for (col in 1..7)
                    add(row * 9 + col)
        }
        private val PAGE_SIZE = CONTENT_SLOTS.size
    }
}
