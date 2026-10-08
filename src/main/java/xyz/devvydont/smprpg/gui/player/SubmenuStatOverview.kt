package xyz.devvydont.smprpg.gui.player

import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeCategory
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService.Companion.calculateEffectiveHealth
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService.Companion.calculateResistancePercentage
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.BreakingPowerFormatting
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import xyz.devvydont.smprpg.util.formatting.TooltipStyle
import java.text.DecimalFormat
import java.util.*
import java.util.function.Consumer

class SubmenuStatOverview(player: Player, private val target: LivingEntity, val category : AttributeCategory, parentMenu: MenuBase?) :
    MenuBase(player, 6, parentMenu) {
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
        this.setBorderEdge()
        this.setBackButton(49)

        var index = -1
        for (attribute in AttributeWrapper.entries) {
            if (attribute.Category == category) {
                val attributeInstance = instance.getAttribute<LivingEntity>(this.target, attribute) ?: continue

                index = findNextEmpty(index)
                if (index == -1) return

                this.setButton(
                    index,
                    generateItemDisplay(attribute)
                ) { _: InventoryClickEvent -> handleClick(attribute) }
            }
        }

        this.setSlot(45, this.help)
    }

    private fun generateItemDisplay(attribute: AttributeWrapper): ItemStack {
        val item = resolveAttributeDisplay(attribute)
        val attributeInstance = instance.getAttribute<LivingEntity>(this.target, attribute)

        item.editMeta(Consumer editMeta@{ meta: ItemMeta ->
            val displayName = ComponentUtils.merge(
                ComponentUtils.create(attribute.DisplayName, NamedTextColor.GOLD), ComponentUtils.SPACE,
                ComponentUtils.create("(" + attribute.Category.DisplayName + ")", NamedTextColor.DARK_GRAY)
            )
            meta.displayName(displayName.decoration(TextDecoration.ITALIC, false))

            if (attributeInstance == null) return@editMeta

            val lore = ArrayList<Component?>()
            lore.add(ComponentUtils.EMPTY)
            lore.add(attribute.Description)
            lore.add(ComponentUtils.EMPTY)
            lore.add(
                ComponentUtils.merge(
                    ComponentUtils.create("Base: "),
                    ComponentUtils.create(df.format(attributeInstance.baseValue), NamedTextColor.GREEN)
                )
            )

            if (!attributeInstance.modifiers.isEmpty()) {
                lore.add(ComponentUtils.EMPTY)
                lore.add(ComponentUtils.create("Active Modifiers:", NamedTextColor.YELLOW))
            }
            val modifiers = sortModifiers(attributeInstance.modifiers)
            for (modifier in modifiers) {
                val sanitizedName = getSanitizedName(modifier.name)
                lore.add(
                    ComponentUtils.merge(
                        sanitizedName, ComponentUtils.SPACE,
                        resolveOperation(modifier.operation, modifier.amount), ComponentUtils.SPACE,
                        ComponentUtils.create(
                            "(" + modifier.slotGroup.toString().lowercase(Locale.getDefault()) + ")",
                            NamedTextColor.DARK_GRAY
                        )
                    )
                )
            }
            lore.add(ComponentUtils.EMPTY)
            val finalValue = attributeInstance.getValue()
            // Breaking Power is colored by its tier so it matches how it's shown in item lore and popups.
            val finalColor = if (attribute == AttributeWrapper.MINING_POWER)
                BreakingPowerFormatting.color(finalValue)
            else
                NamedTextColor.GREEN
            lore.add(
                ComponentUtils.merge(
                    ComponentUtils.create("Final: "),
                    ComponentUtils.create(df.format(finalValue), finalColor)
                )
            )

            // Append Defense/EHP if def stat
            if (attribute == AttributeWrapper.DEFENSE) {
                val hpAttr = instance.getAttribute<LivingEntity>(this.target, AttributeWrapper.HEALTH)
                val hp = hpAttr?.getValue() ?: 0.0
                val def = SMPRPG.getService(EntityService::class.java).getEntityInstance(this.target)
                    .defense
                val ehp = calculateEffectiveHealth(hp, def.toDouble())

                lore.add(ComponentUtils.EMPTY)
                lore.add(ComponentUtils.create("Effective Health: ", NamedTextColor.YELLOW))
                lore.add(
                    ComponentUtils.merge(
                        ComponentUtils.create(String.format("%d ", ehp.toInt()), NamedTextColor.GREEN),
                        ComponentUtils.create(
                            String.format(
                                "EHP=%dHP/%.2fDEF%%",
                                hp.toInt(),
                                calculateResistancePercentage(def.toDouble()) * 100
                            ), NamedTextColor.DARK_GRAY
                        )
                    )
                )
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
                ArrayList<Component?>()
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
                        ComponentUtils.create("armor", NamedTextColor.BLACK)
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

    private fun handleClick(attribute: AttributeWrapper?) {
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(ComponentUtils.merge(
            ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.STAT_SUB_MENU, NamedTextColor.WHITE),
            ComponentUtils.create(
                Symbols.OVERLAY_BG_OFFSET_STANDARD + "${category.DisplayName} Stats",
                Symbols.INVENTORY_TITLE_COLOR
            )
        ))
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    private fun resolveAttributeDisplay(attribute: AttributeWrapper): ItemStack {
        return when (attribute) {
            AttributeWrapper.LUCK -> ItemStack.of(Material.EMERALD)
            AttributeWrapper.ARMOR -> ItemStack.of(Material.NETHERITE_CHESTPLATE)
            AttributeWrapper.STRENGTH -> ItemStack.of(Material.DIAMOND_SWORD)
            AttributeWrapper.MINING_POWER -> ItemStack.of(Material.IRON_PICKAXE)
            AttributeWrapper.MINING_SPEED -> ItemStack.of(Material.GOLDEN_PICKAXE)
            AttributeWrapper.SCALE -> ItemStack.of(Material.LADDER)
            AttributeWrapper.HEALTH -> ItemStack.of(Material.APPLE)
            AttributeWrapper.DEFENSE -> ItemStack.of(Material.IRON_CHESTPLATE)
            AttributeWrapper.SAFE_FALL -> ItemStack.of(Material.FEATHER)
            AttributeWrapper.BURNING_TIME -> ItemStack.of(Material.BLAZE_POWDER)
            AttributeWrapper.OXYGEN_BONUS -> ItemStack.of(Material.GLASS_BOTTLE)
            AttributeWrapper.INTELLIGENCE -> ItemStack.of(Material.POTION)
            AttributeWrapper.ARCANE_RATING -> ItemStack.of(Material.FIREWORK_STAR)
            AttributeWrapper.ABSORPTION -> ItemStack.of(Material.GOLDEN_APPLE)
            AttributeWrapper.SNEAKING_SPEED -> ItemStack.of(Material.CHAINMAIL_LEGGINGS)
            AttributeWrapper.ATTACK_SPEED -> ItemStack.of(Material.CLOCK)
            AttributeWrapper.MOVEMENT_SPEED -> ItemStack.of(Material.SUGAR)
            AttributeWrapper.JUMP_HEIGHT -> ItemStack.of(Material.RABBIT_FOOT)
            AttributeWrapper.GRAVITY -> ItemStack.of(Material.WIND_CHARGE)
            AttributeWrapper.SWEEPING -> ItemStack.of(Material.NETHERITE_HOE)
            AttributeWrapper.COMBAT_REACH -> ItemStack.of(Material.SPYGLASS)
            AttributeWrapper.MINING_REACH -> ItemStack.of(Material.BRUSH)
            AttributeWrapper.MINING_FORTUNE -> ItemStack.of(Material.DIAMOND)
            AttributeWrapper.FARMING_FORTUNE -> ItemStack.of(Material.WHEAT)
            AttributeWrapper.CRITTER_CHANCE -> ItemStack.of(Material.ROOTED_DIRT)
            AttributeWrapper.WHEAT_FORTUNE -> ItemStack.of(Material.WHEAT)
            AttributeWrapper.POTATO_FORTUNE -> ItemStack.of(Material.POTATO)
            AttributeWrapper.ONION_FORTUNE -> ItemService.generate(CustomItemType.ONION)
            AttributeWrapper.CARROT_FORTUNE -> ItemService.generate(Material.CARROT)
            AttributeWrapper.MELON_FORTUNE -> ItemService.generate(Material.MELON_SLICE)
            AttributeWrapper.LUMBERING -> ItemStack.of(Material.IRON_AXE)
            AttributeWrapper.WOODCUTTING_FORTUNE -> ItemStack.of(Material.OAK_LOG)
            AttributeWrapper.FISHING_SPEED -> ItemStack.of(Material.PRISMARINE)
            AttributeWrapper.FISHING_RATING -> ItemStack.of(Material.FISHING_ROD)
            AttributeWrapper.FISHING_CREATURE_CHANCE -> ItemStack.of(Material.DROWNED_SPAWN_EGG)
            AttributeWrapper.FISHING_TREASURE_CHANCE -> ItemStack.of(Material.GOLD_BLOCK)
            AttributeWrapper.STEP -> ItemStack.of(Material.QUARTZ_SLAB)
            AttributeWrapper.AIRBORNE_MINING -> ItemStack.of(Material.FEATHER)
            AttributeWrapper.UNDERWATER_MINING -> ItemStack.of(Material.TURTLE_HELMET)
            AttributeWrapper.WATER_MOVEMENT -> ItemStack.of(Material.LEATHER_BOOTS)
            AttributeWrapper.REGENERATION -> ItemStack.of(Material.GLISTERING_MELON_SLICE)
            AttributeWrapper.FALL_DAMAGE_MULTIPLIER -> ItemStack.of(Material.SLIME_BLOCK)
            AttributeWrapper.EXPLOSION_KNOCKBACK_RESISTANCE -> ItemStack.of(Material.GUNPOWDER)
            AttributeWrapper.FOLLOW_RANGE -> ItemStack.of(Material.SPYGLASS)
            AttributeWrapper.FLYING_SPEED -> ItemStack.of(Material.ELYTRA)
            AttributeWrapper.MOVEMENT_EFFICIENCY -> ItemStack.of(Material.SOUL_SAND)
            AttributeWrapper.ATTACK_KNOCKBACK -> ItemStack.of(Material.STICK)
            AttributeWrapper.ZOMBIE_REINFORCEMENTS -> ItemStack.of(Material.ZOMBIE_HEAD)
            AttributeWrapper.KNOCKBACK_RESISTANCE -> ItemStack.of(Material.SHIELD)
            AttributeWrapper.CRITICAL_CHANCE -> ItemStack.of(Material.GOLDEN_AXE)
            AttributeWrapper.CRITICAL_DAMAGE -> ItemStack.of(Material.DIAMOND_AXE)
            AttributeWrapper.PROFICIENCY -> ItemStack.of(Material.EXPERIENCE_BOTTLE)
            AttributeWrapper.COMBAT_PROFICIENCY -> {
                val item = ItemStack.of(Material.PAPER)
                item.setData(DataComponentTypes.ITEM_MODEL, Key.key("smprpg:ui/combat_proficiency"))
                return item
            }
            AttributeWrapper.MINING_PROFICIENCY -> {
                val item = ItemStack.of(Material.PAPER)
                item.setData(DataComponentTypes.ITEM_MODEL, Key.key("smprpg:ui/mining_proficiency"))
                return item
            }
            AttributeWrapper.FISHING_PROFICIENCY -> {
                val item = ItemStack.of(Material.PAPER)
                item.setData(DataComponentTypes.ITEM_MODEL, Key.key("smprpg:ui/fishing_proficiency"))
                return item
            }
            AttributeWrapper.FARMING_PROFICIENCY -> {
                val item = ItemStack.of(Material.PAPER)
                item.setData(DataComponentTypes.ITEM_MODEL, Key.key("smprpg:ui/farming_proficiency"))
                return item
            }
            AttributeWrapper.WOODCUTTING_PROFICIENCY -> {
                val item = ItemStack.of(Material.PAPER)
                item.setData(DataComponentTypes.ITEM_MODEL, Key.key("smprpg:ui/woodcutting_proficiency"))
                return item
            }
            AttributeWrapper.MAGIC_PROFICIENCY -> {
                val item = ItemStack.of(Material.PAPER)
                item.setData(DataComponentTypes.ITEM_MODEL, Key.key("smprpg:ui/magic_proficiency"))
                return item
            }
            AttributeWrapper.SLAYER_PROFICIENCY -> {
                val item = ItemStack.of(Material.PAPER)
                item.setData(DataComponentTypes.ITEM_MODEL, Key.key("smprpg:ui/slayer_proficiency"))
                return item
            }
            else -> ItemStack.of(Material.BARRIER)
        }
    }

    private fun resolveOperation(operation: AttributeModifier.Operation, amount: Double): Component {
        return when (operation) {
            AttributeModifier.Operation.ADD_NUMBER -> ComponentUtils.create(
                String.format(
                    "%s%s",
                    if (amount >= 0) "+" else "-",
                    df.format(amount)
                ), if (amount >= 0) NamedTextColor.GREEN else NamedTextColor.RED
            )

            AttributeModifier.Operation.ADD_SCALAR -> ComponentUtils.create(
                String.format(
                    "%s%s%%",
                    if (amount >= 0) "+" else "-",
                    df.format(amount * 100)
                ), if (amount >= 0) NamedTextColor.GREEN else NamedTextColor.RED
            )

            AttributeModifier.Operation.MULTIPLY_SCALAR_1 -> ComponentUtils.create(
                String.format(
                    "x%s",
                    df.format(amount + 1)
                ), if (amount >= 0) NamedTextColor.GREEN else NamedTextColor.RED
            )
        }
    }

    private fun sortModifiers(modifiers: MutableCollection<AttributeModifier>): MutableCollection<AttributeModifier> {
        val result = ArrayList<AttributeModifier>()
        result.addAll(
            modifiers.stream()
                .filter { m: AttributeModifier? -> m!!.operation == AttributeModifier.Operation.ADD_NUMBER }
                .toList())
        result.addAll(
            modifiers.stream()
                .filter { m: AttributeModifier? -> m!!.operation == AttributeModifier.Operation.ADD_SCALAR }
                .toList())
        result.addAll(
            modifiers.stream()
                .filter { m: AttributeModifier? -> m!!.operation == AttributeModifier.Operation.MULTIPLY_SCALAR_1 }
                .toList())
        return result
    }

    private fun getSanitizedName(name : String) : Component {
        var newName = name.removeSuffix("base")
        newName = newName.removeSuffix("bonus")
        val stringParts = newName.split("_")
        var retName = ""
        for (part in stringParts) {
            retName += part.capitalize()
            if (part != stringParts.last())
                retName += " "
        }
        return ComponentUtils.create(retName, NamedTextColor.WHITE)
    }
}
