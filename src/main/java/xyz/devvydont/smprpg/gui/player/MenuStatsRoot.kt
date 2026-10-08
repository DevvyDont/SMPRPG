package xyz.devvydont.smprpg.gui.player

import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.attribute.AttributeCategory
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import xyz.devvydont.smprpg.util.formatting.TooltipStyle
import java.text.DecimalFormat
import java.util.function.Consumer

class MenuStatsRoot(player: Player, private val target: LivingEntity, parentMenu: MenuBase?) :
    MenuBase(player, 3, parentMenu) {
    private val df = DecimalFormat("#.##")

    init {
        render()
    }

    private fun findNextEmpty(start: Int): Int {
        val current = start + 1

        if (current >= this.inventorySize) return -1

        if (this.getItem(current) == null) return current
        return findNextEmpty(current)
    }

    fun render() {
        this.setBorderBottom()
        this.setBackButton(22)

        var index = 10
        for (category in AttributeCategory.entries) {
            this.setButton(
                index,
                generateItemDisplay(category)
            ) { _: InventoryClickEvent -> handleClick(category) }
            index++
        }

        this.setSlot(4, this.help)
    }

    private fun generateItemDisplay(category: AttributeCategory): ItemStack {
        val item = when (category) {
            AttributeCategory.COMBAT -> ItemService.generate(Material.IRON_SWORD)
            AttributeCategory.SURVIVABILITY -> ItemService.generate(Material.SHIELD)
            AttributeCategory.MOVEMENT -> ItemService.generate(Material.LEATHER_BOOTS)
            AttributeCategory.FORAGING -> ItemService.generate(Material.IRON_PICKAXE)
            AttributeCategory.FISHING -> ItemService.generate(CustomItemType.IRON_ROD)
            AttributeCategory.PROFICIENCY -> ItemService.generate(Material.EXPERIENCE_BOTTLE)
            AttributeCategory.SPECIAL -> ItemService.generate(Material.NETHER_STAR)
        }

        item.editMeta(Consumer editMeta@{ meta: ItemMeta ->
            val displayName = ComponentUtils.create(category.DisplayName, NamedTextColor.GOLD)
            meta.displayName(displayName.decoration(TextDecoration.ITALIC, false))
            val lore = ArrayList<Component?>()
            lore.add(ComponentUtils.create(category.Description))
            lore.add(ComponentUtils.EMPTY)

            for (wrapper in AttributeWrapper.entries) {
                if (wrapper.Category == category) {
                    val attributeInstance = instance.getAttribute<LivingEntity>(this.target, wrapper)
                    if (attributeInstance != null) {
                        lore.add(
                            ComponentUtils.merge(
                                ComponentUtils.create("${wrapper.DisplayName}: "),
                                ComponentUtils.create(df.format(attributeInstance.getValue()), NamedTextColor.GREEN)
                            )
                        )
                    }
                }
            }
            meta.lore(ComponentUtils.cleanItalics(lore))
        })

        return item
    }

    private val help: ItemStack
        get() {
            val paper = ItemStack(Material.PAPER)
            paper.setData(DataComponentTypes.TOOLTIP_STYLE, TooltipStyle.INFO.key)
            val meta = paper.itemMeta

            meta.displayName(
                ComponentUtils.create("Attribute Guide", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
            )
            val lore: MutableList<Component?> =
                ArrayList()
            lore.add(ComponentUtils.EMPTY)

            lore.addAll(
                listOf(
                    ComponentUtils.merge(
                        ComponentUtils.create("Attributes", NamedTextColor.GOLD),
                        ComponentUtils.create(" are the foundation of your")
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create("character's "),
                        ComponentUtils.create(Symbols.POWER + "power", NamedTextColor.YELLOW),
                        ComponentUtils.create(". You can modify your")
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create("attributes", NamedTextColor.GOLD),
                        ComponentUtils.create(" in many ways, including but not limited to:")
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create(Symbols.POINT + " Equipping "),
                        ComponentUtils.create("armor", NamedTextColor.WHITE)
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create(Symbols.POINT + " Holding "),
                        ComponentUtils.create("tools/equipment", NamedTextColor.BLUE)
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create(Symbols.POINT + " Leveling up "),
                        ComponentUtils.create("skills", NamedTextColor.AQUA)
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create(Symbols.POINT + " utilizing equipment "),
                        ComponentUtils.create("reforges", NamedTextColor.GOLD)
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create(Symbols.POINT + " augmenting equipment with "),
                        ComponentUtils.create("enchantments", NamedTextColor.LIGHT_PURPLE)
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create(Symbols.POINT + " Summoning "),
                        ComponentUtils.create("pets", NamedTextColor.GREEN)
                    ),
                    ComponentUtils.EMPTY,
                    ComponentUtils.merge(
                        ComponentUtils.create("The individual effects that "),
                        ComponentUtils.create("attributes", NamedTextColor.GOLD),
                        ComponentUtils.create(" provide can")
                    ),
                    ComponentUtils.merge(ComponentUtils.create("be found when hovering over items in your inventory.")),
                    ComponentUtils.merge(
                        ComponentUtils.create("The way that attribute "),
                        ComponentUtils.create("modifiers", NamedTextColor.AQUA),
                        ComponentUtils.create(" display and apply")
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create("can appear misleading and/or confusing due to the nature of how "),
                        ComponentUtils.create("modifiers", NamedTextColor.AQUA)
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create("work. "),
                        ComponentUtils.create("There are 3 types of modifiers that are applied in the following order:")
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create(
                            Symbols.RIGHT_ARROW + " Additive:",
                            NamedTextColor.GREEN
                        ),
                        ComponentUtils.create(" adds to your "),
                        ComponentUtils.create("base value", NamedTextColor.BLUE),
                        ComponentUtils.create(" (ex. +15)", NamedTextColor.DARK_GRAY)
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create(Symbols.RIGHT_ARROW + " Scalar:", NamedTextColor.GREEN),
                        ComponentUtils.create(" applies a single additive multiplier to base value + additive modifiers"),
                        ComponentUtils.create(" (ex. +25%)", NamedTextColor.DARK_GRAY)
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create(
                            Symbols.RIGHT_ARROW + " Multiplicative:",
                            NamedTextColor.GREEN
                        ),
                        ComponentUtils.create(" multiplies the final value after all other modifiers"),
                        ComponentUtils.create(" (ex. x2)", NamedTextColor.DARK_GRAY)
                    ),
                    ComponentUtils.EMPTY,
                    ComponentUtils.merge(
                        ComponentUtils.create("The most important distinction is that "),
                        ComponentUtils.create("scalar modifiers", NamedTextColor.GREEN),
                        ComponentUtils.create(" stack "),
                        ComponentUtils.create("additively", NamedTextColor.GREEN)
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create("while "),
                        ComponentUtils.create("multiplicative modifiers", NamedTextColor.GREEN),
                        ComponentUtils.create(" stack "),
                        ComponentUtils.create("multiplicatively!", NamedTextColor.LIGHT_PURPLE)
                    ),
                    ComponentUtils.EMPTY,
                    ComponentUtils.create(
                        "For example, if all 4 of your armor pieces have '+25% scalar modifiers'",
                        NamedTextColor.DARK_GRAY
                    ),
                    ComponentUtils.create(
                        "then you will have a net +100% (or x2) of that specific attribute!",
                        NamedTextColor.DARK_GRAY
                    ),
                    ComponentUtils.create(
                        "If they were instead '+25% multiplicative modifiers', you would then",
                        NamedTextColor.DARK_GRAY
                    ),
                    ComponentUtils.create(
                        "net an increase by about +144% (or ~2.44x) instead.",
                        NamedTextColor.DARK_GRAY
                    ),
                    ComponentUtils.create(
                        "The mathematical difference is due to the nature of what results from:",
                        NamedTextColor.DARK_GRAY
                    ),
                    ComponentUtils.create(
                        "value * (1 + .25 + .25 + ...)) vs. (value * 1.25 * 1.25 * ...)",
                        NamedTextColor.DARK_GRAY
                    )

                )
            )

            meta.lore(ComponentUtils.cleanItalics(lore))

            paper.setItemMeta(meta)
            return paper
        }

    private fun handleClick(category: AttributeCategory?) {
        openSubMenu(SubmenuStatOverview(player, target, category!!, this))
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(ComponentUtils.merge(
            ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.STAT_MAIN_MENU, NamedTextColor.WHITE),
            ComponentUtils.create(
                Symbols.OVERLAY_BG_OFFSET_STANDARD + "Statistics",
                Symbols.INVENTORY_TITLE_COLOR
            )
        ))
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
    }

}
