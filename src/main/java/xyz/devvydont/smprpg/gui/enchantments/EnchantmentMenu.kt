package xyz.devvydont.smprpg.gui.enchantments

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentTargetDisplay
import xyz.devvydont.smprpg.gui.base.IRecipeDependentMenu
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.items.blueprints.resources.scrolls.DynamicEnchantingScroll
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import java.util.function.Consumer

/*
 * A menu used to view all the enchantments and their attributes in the game.
 */
class EnchantmentMenu : MenuBase, IRecipeDependentMenu {
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
        var book : ItemStack
        if (enchantment.enchantColor == CustomEnchantment.ARTIFICE_COLOR) {
            book =
                renameItem(DynamicEnchantingScroll.getScrollWithEnchantment(enchantment), ComponentUtils.gradient(PlainTextComponentSerializer.plainText().serialize(enchantment.displayName),
                NamedTextColor.DARK_PURPLE, TextColor.color(255, 0, 0)))
        }
        else {
            book =
                renameItem(DynamicEnchantingScroll.getScrollWithEnchantment(enchantment), enchantment.displayName.color(enchantment.enchantColor))
        }

        // Start constructing the lore of the item, this is essentially an in depth description of the enchantment.
        val enchantmentDescription: MutableList<Component?> = ArrayList<Component?>()
        enchantmentDescription.add(ComponentUtils.EMPTY)

        // First the most important part. What does it do?

        if (enchantment.enchantColor == CustomEnchantment.ARTIFICE_COLOR) {
            enchantmentDescription.add(
                ComponentUtils.gradient(
                    PlainTextComponentSerializer.plainText()
                        .serialize(enchantment.enchantment.displayName(1)),
                    NamedTextColor.DARK_PURPLE,
                    TextColor.color(255, 0, 0)
                ).decorate(TextDecoration.ITALIC)
            )
        }
        else
            enchantmentDescription.add(enchantment.enchantment.displayName(1).color(enchantment.enchantColor))
        if (enchantment.longDescription.isEmpty())
            enchantmentDescription.add(enchantment.build(1).description)
        else
            enchantmentDescription.addAll(enchantment.build(1).longDescription)
        // If this enchantment has more than one level, we should also show off what the "maxed" version of this enchant entails
        if (enchantment.maxLevel > 1) {
            enchantmentDescription.add(ComponentUtils.EMPTY)
            if (enchantment.enchantColor == CustomEnchantment.ARTIFICE_COLOR) {
                enchantmentDescription.add(
                    ComponentUtils.gradient(
                        PlainTextComponentSerializer.plainText()
                            .serialize(enchantment.enchantment.displayName(enchantment.maxLevel)),
                        NamedTextColor.DARK_PURPLE,
                        TextColor.color(255, 0, 0)
                    ).decorate(TextDecoration.ITALIC)
                )
            }
            else {
                enchantmentDescription.add(
                    enchantment.enchantment.displayName(enchantment.maxLevel).color(enchantment.enchantColor)
                )
            }
            if (enchantment.longDescription.isEmpty())
                enchantmentDescription.add(enchantment.build(enchantment.maxLevel).description)
            else
                enchantmentDescription.addAll(enchantment.build(enchantment.maxLevel).longDescription)
        }

        enchantmentDescription.add(ComponentUtils.EMPTY)

        // Now let's add misc. information about this enchantment to put at the bottom.
        enchantmentDescription.add(
            ComponentUtils.merge(
                ComponentUtils.create("Max Enchantment Level: "),
                ComponentUtils.create(enchantment.maxLevel.toString(), NamedTextColor.GREEN)
            )
        )
        enchantmentDescription.add(
            ComponentUtils.merge(
                ComponentUtils.create("Enchantment Rarity Ranking: "),
                ComponentUtils.create(enchantment.weight.toString(), NamedTextColor.GREEN),
                ComponentUtils.create(" (Lower = Rarer)", NamedTextColor.DARK_GRAY)
            )
        )
        enchantmentDescription.add(
            ComponentUtils.merge(
                ComponentUtils.create("Applicable Item Type: "),
                EnchantmentTargetDisplay.getApplicableItemsComponent(enchantment.itemTypeTag)
            )
        )

        // Any enchantment conflicts?

        // Uh, yes
        if (enchantment.key == EnchantmentService.ONE_FOR_ALL.key) {
            enchantmentDescription.add(ComponentUtils.EMPTY)
            enchantmentDescription.add(ComponentUtils.create("Conflicting Enchantments: "))
            enchantmentDescription.add(ComponentUtils.create("LITERALLY EVERYTHING", NamedTextColor.DARK_RED,
                TextDecoration.BOLD))
        }

        if (!enchantment.conflictingEnchantments.isEmpty) {
            enchantmentDescription.add(ComponentUtils.EMPTY)
            enchantmentDescription.add(ComponentUtils.create("Conflicting Enchantments: "))
            for (conflict in enchantment.conflictingEnchantments.values()) {
                val conflictEnchant =
                    SMPRPG.getService(EnchantmentService::class.java).getEnchantment(conflict)
                val conflictEnchantWrapper = SMPRPG.getService(EnchantmentService::class.java)
                    .getEnchantment(conflictEnchant)
                if (conflictEnchantWrapper!!.enchantColor == CustomEnchantment.ARTIFICE_COLOR) {
                    enchantmentDescription.add(
                        ComponentUtils.merge(
                            ComponentUtils.create("- "),
                            ComponentUtils.gradient(PlainTextComponentSerializer.plainText().serialize(conflictEnchantWrapper.displayName),
                            NamedTextColor.DARK_PURPLE, TextColor.color(255, 0, 0)
                            )
                        )
                    )
                }
                else {
                    enchantmentDescription.add(
                        ComponentUtils.merge(
                            ComponentUtils.create("- "), conflictEnchantWrapper.displayName.color(
                                conflictEnchantWrapper.enchantColor
                            )
                        )
                    )
                }
            }
        }

        enchantmentDescription.add(ComponentUtils.EMPTY)
        val magicLvl = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
            .magicSkill.level

        enchantmentDescription.add(ComponentUtils.create("Click to go deeper!", NamedTextColor.YELLOW))
        if (this.player.gameMode == GameMode.CREATIVE)
            enchantmentDescription.add(ComponentUtils.create("Shift + Left click to generate an enchantment scroll!", NamedTextColor.GOLD))
        if (enchantment.maxLevel > 1) {
            enchantmentDescription.add(ComponentUtils.EMPTY)
            for (i in 2..enchantment.maxLevel) {
                val recipe = enchantment.getRecipe(i)
                val unlocked = if (magicLvl >= (recipe?.power ?: 999)) ComponentUtils.create(
                    Symbols.CHECK,
                    NamedTextColor.GREEN
                ) else ComponentUtils.create(Symbols.X, NamedTextColor.RED)
                enchantmentDescription.add(
                    ComponentUtils.merge(
                        unlocked,
                        ComponentUtils.SPACE,
                        enchantment.build(i).displayName.append(
                            Component.text(" $i")
                        ).color(NamedTextColor.DARK_GRAY),
                        ComponentUtils.create(
                            ": Magic " + (recipe?.power ?: "undefined"),
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
                val player = e.whoClicked as Player
                if (player.gameMode == GameMode.CREATIVE) {
                    if (e.isShiftClick) {
                        this.playSound(Sound.ENTITY_ITEM_PICKUP, 1f, .5f)
                        this.playSound(Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 2f)
                        val item = DynamicEnchantingScroll.getScrollWithEnchantment(enchantment)
                        SMPRPG.getService(ItemService::class.java).ensureItemStackUpdated(item)
                        player.inventory.addItem(item)
                        return@setButton
                    }
                }
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
