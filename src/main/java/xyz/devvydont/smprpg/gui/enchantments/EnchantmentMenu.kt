package xyz.devvydont.smprpg.gui.enchantments

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuButtonClickHandler
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import java.util.function.Consumer

/*
 * A menu used to view all the enchantments and their attributes in the game.
 */
class EnchantmentMenu : MenuBase {
    // List of the custom enchantment instances we are trying to display.
    protected var enchantments: MutableList<CustomEnchantment>

    // The page we are currently on. Used so we know how to "offset" the enchantments to display.
    private var page = 0
    private var reverseResults = false

    private var sortMode = EnchantmentSortMode.ALPHABETICAL

    constructor(player: Player) : super(player, ROWS) {
        // Set up the enchantments. These are just a copy of all the enchantments in a list.
        enchantments = ArrayList<CustomEnchantment>()
        enchantments.addAll(SMPRPG.getService(EnchantmentService::class.java).customEnchantments)

        sortMode.sort(enchantments)
    }

    constructor(player: Player, parent: MenuBase?) : super(player, ROWS, parent) {
        // Set up the enchantments. These are just a copy of all the enchantments in a list.
        enchantments = ArrayList<CustomEnchantment>()
        enchantments.addAll(SMPRPG.getService(EnchantmentService::class.java).customEnchantments)
    }


    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(ComponentUtils.create("Enchantments", NamedTextColor.BLACK))
        render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        super.handleInventoryClicked(event)
        event.isCancelled = true
    }

    /**
     * Given a custom enchantment instance, generate an itemstack to show in the GUI to describe it.
     *
     * @param enchantment A custom enchantment instance, the level bound to it does not matter.
     * @return An itemstack used to be a display for the enchantment
     */
    private fun generateEnchantmentButton(enchantment: CustomEnchantment): ItemStack {
        val book =
            createNamedItem(Material.ENCHANTED_BOOK, enchantment.getDisplayName().color(enchantment.enchantColor))

        // Start constructing the lore of the item, this is essentially an in depth description of the enchantment.
        val enchantmentDescription: MutableList<Component?> = ArrayList<Component?>()
        enchantmentDescription.add(ComponentUtils.EMPTY)

        // First the most important part. What does it do?
        enchantmentDescription.add(enchantment.enchantment.displayName(1).color(enchantment.enchantColor))
        enchantmentDescription.add(enchantment.build(1).getDescription())
        // If this enchantment has more than one level, we should also show off what the "maxed" version of this enchant entails
        if (enchantment.getMaxLevel() > 1) {
            enchantmentDescription.add(ComponentUtils.EMPTY)
            enchantmentDescription.add(
                enchantment.enchantment.displayName(enchantment.getMaxLevel()).color(enchantment.enchantColor)
            )
            enchantmentDescription.add(enchantment.build(enchantment.getMaxLevel()).getDescription())
        }

        enchantmentDescription.add(ComponentUtils.EMPTY)

        // Now let's add misc. information about this enchantment to put at the bottom.
        enchantmentDescription.add(
            ComponentUtils.merge(
                ComponentUtils.create("Max Enchantment Level: "),
                ComponentUtils.create(enchantment.getMaxLevel().toString(), NamedTextColor.GREEN)
            )
        )
        enchantmentDescription.add(
            ComponentUtils.merge(
                ComponentUtils.create("Enchantment Rarity Ranking: "),
                ComponentUtils.create(enchantment.getWeight().toString(), NamedTextColor.GREEN),
                ComponentUtils.create(" (Lower = Rarer)", NamedTextColor.DARK_GRAY)
            )
        )
        enchantmentDescription.add(
            ComponentUtils.merge(
                ComponentUtils.create("Applicable Item Type: "),
                ComponentUtils.create(
                    MinecraftStringUtils.getTitledString(
                        enchantment.getItemTypeTag().key().asMinimalString().replace("/", " ")
                    ), NamedTextColor.GOLD
                )
            )
        )

        // Any enchantment conflicts?
        if (!enchantment.conflictingEnchantments.isEmpty) {
            enchantmentDescription.add(ComponentUtils.EMPTY)
            enchantmentDescription.add(ComponentUtils.create("Conflicting Enchantments: "))
            for (conflict in enchantment.conflictingEnchantments.values()) {
                val conflictEnchant =
                    SMPRPG.getService(EnchantmentService::class.java).getEnchantment(conflict)
                val conflictEnchantWrapper = SMPRPG.getService(EnchantmentService::class.java)
                    .getEnchantment(conflictEnchant)
                enchantmentDescription.add(
                    ComponentUtils.merge(
                        ComponentUtils.create("- "), conflictEnchantWrapper!!.getDisplayName().color(
                            conflictEnchantWrapper.enchantColor
                        )
                    )
                )
            }
        }

        enchantmentDescription.add(ComponentUtils.EMPTY)
        val magicLvl = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
            .magicSkill.level
        val isUnlocked = magicLvl >= enchantment.getSkillRequirement()
        enchantmentDescription.add(
            ComponentUtils.merge(
                ComponentUtils.create(
                    "Magic Skill Level Requirement: ",
                    if (isUnlocked) NamedTextColor.GRAY else NamedTextColor.RED
                ),
                ComponentUtils.create(
                    enchantment.getSkillRequirement().toString(),
                    if (isUnlocked) NamedTextColor.LIGHT_PURPLE else NamedTextColor.DARK_RED
                )
            )
        )

        enchantmentDescription.add(ComponentUtils.EMPTY)
        enchantmentDescription.add(ComponentUtils.create("Click to go deeper!", NamedTextColor.YELLOW))
        if (enchantment.getMaxLevel() > 1) {
            enchantmentDescription.add(ComponentUtils.EMPTY)
            for (i in 2..enchantment.getMaxLevel()) {
                val unlocked = if (magicLvl >= enchantment.getSkillRequirementForLevel(i)) ComponentUtils.create(
                    Symbols.CHECK,
                    NamedTextColor.GREEN
                ) else ComponentUtils.create(Symbols.X, NamedTextColor.RED)
                enchantmentDescription.add(
                    ComponentUtils.merge(
                        unlocked,
                        ComponentUtils.SPACE,
                        enchantment.build(i).getDisplayName().append(
                            Component.text(" $i")
                        ).color(NamedTextColor.DARK_GRAY),
                        ComponentUtils.create(
                            ": Magic " + enchantment.getSkillRequirementForLevel(i),
                            NamedTextColor.DARK_GRAY
                        )
                    )
                )
            }
        }

        book.editMeta(Consumer { meta: ItemMeta? ->
            meta!!.lore(ComponentUtils.cleanItalics(enchantmentDescription))
        })
        return book
    }

    /**
     * Generates the button responsible for displaying the current sort mode.
     * @return An ItemStack representing the button used for determining sort behavior for the enchantments.
     */
    private fun generateSortButton(): ItemStack {
        val item = createNamedItem(Material.REPEATER, ComponentUtils.create("Change Sort Mode", NamedTextColor.GOLD))
        val lore: MutableList<Component?> = ArrayList<Component?>()
        lore.add(ComponentUtils.EMPTY)
        lore.add(
            ComponentUtils.merge(
                ComponentUtils.create("Current Sort Mode: "),
                ComponentUtils.create(sortMode.display(), NamedTextColor.GREEN),
                if (reverseResults) ComponentUtils.create(
                    " (REVERSED)",
                    NamedTextColor.DARK_GRAY
                ) else ComponentUtils.EMPTY
            )
        )
        lore.add(ComponentUtils.EMPTY)

        for (mode in EnchantmentSortMode.entries) lore.add(
            ComponentUtils.create(
                "> " + mode.display(),
                if (mode == sortMode) NamedTextColor.GREEN else NamedTextColor.DARK_GRAY
            )
        )

        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Click to cycle through sort modes!"))
        lore.add(ComponentUtils.create("Right click to reverse the sorting method!"))

        item.editMeta(Consumer { meta: ItemMeta? ->
            meta!!.lore(ComponentUtils.cleanItalics(lore))
        })
        return item
    }

    /**
     * Resets this interface to its default state where we are displaying enchantments in default order.
     */
    fun render() {
        this.clear()
        this.setBorderEdge()

        // Using the page, generate an offset within the enchant list to know when to display from. If the offset is
        // too high, just assume we are jumping back to the first page.
        // If it is too low, set the offset to the last page.
        // Can be calculated by multiplying the page by the area of the displayable area
        val area: Int = 7 * (ROWS - 2)
        val lastPage = enchantments.size / area
        var offset = page * area
        if (offset >= enchantments.size) {
            page = 0
            offset = 0
        }
        if (offset < 0) {
            page = lastPage
            offset = page * area
        }

        // Attempt to fill as many slots w/ enchantments that we can
        for (i in 0..<inventorySize) {
            // If we don't have another enchantment to render, abort

            if (offset >= enchantments.size) break

            val enchantment = enchantments[offset]

            // If this slot isn't empty, we shouldn't put something there
            if (this.getItem(i) != null) continue

            this.setButton(
                i,
                generateEnchantmentButton(enchantment)
            ) { e: InventoryClickEvent ->
                playSound(Sound.BLOCK_ENCHANTMENT_TABLE_USE)
                openSubMenu(EnchantmentSubMenu(player, this, enchantment))
            }

            offset++
        }

        // Now set the slots for a next/prev page that increment/decrement the page and re-render
        val displayPage = page + 1
        val displayPageMax = lastPage + 1
        this.setButton(
            (ROWS - 1) * 9,
            createNamedItem(
                Material.ARROW,
                ComponentUtils.create("Previous Page ($displayPage/$displayPageMax)", NamedTextColor.GOLD)
            )
        ) { e: InventoryClickEvent ->
            page--
            render()
            this.sounds.playPagePrevious()
        }

        this.setButton(
            (ROWS - 1) * 9 + 8,
            createNamedItem(
                Material.ARROW,
                ComponentUtils.create("Next Page ($displayPage/$displayPageMax)", NamedTextColor.GOLD)
            )
        ) { e: InventoryClickEvent ->
            page++
            render()
            this.sounds.playPageNext()
        }

        // Sort mode button, when this is clicked the sort mode is changed and the page is re-rendered.
        this.setButton((ROWS - 1) * 9 + 6, generateSortButton()) { e: InventoryClickEvent ->

            // If this was a right click, instead flip the reverse flag instead of cycling the mode.
            if (e.isRightClick) reverseResults = !reverseResults
            else sortMode = sortMode.next()

            // Sort all the enchantments based on the mode and reverse if desired
            enchantments = sortMode.sort(enchantments)
            if (reverseResults)
                enchantments = enchantments.reversed() as MutableList<CustomEnchantment>

            render()
            playSound(Sound.BLOCK_DISPENSER_FAIL)
        }

        // Close button
        this.setBackButton()
    }


    companion object {
        // How many rows this GUI will have.
        private const val ROWS = 6
    }
}
