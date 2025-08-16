package xyz.devvydont.smprpg.gui

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ResolvableProfile
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.apache.commons.lang3.StringUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.fishing.gui.LootTypeChancesMenu
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuButtonClickHandler
import xyz.devvydont.smprpg.gui.enchantments.MagicMenu
import xyz.devvydont.smprpg.gui.player.InterfaceStats
import xyz.devvydont.smprpg.gui.player.MenuDifficultyChooser
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.skills.SkillGlobals.getExperienceForLevel
import xyz.devvydont.smprpg.skills.SkillInstance
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.text.DecimalFormat

/**
 * A simple menu that contains buttons to open other menus!
 */
class MainMenu(player: Player) : MenuBase(player, ROWS) {
    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
        this.playInvalidAnimation()
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(Component.text("Main Menu"))
        render()
    }

    fun render() {
        this.setBorderFull()
        this.setBackButton()

        this.setButton(
            PLAYER_MENU,
            this.playerDisplay
        ) { e: InventoryClickEvent ->
            this.openSubMenu(
                InterfaceStats(
                    this.player,
                    this.player,
                    this
                )
            )
        }
        this.setButton(
            COMBAT_INDEX,
            this.combatDisplay
        ) { e: InventoryClickEvent -> this.playInvalidAnimation() }
        this.setButton(
            MINING_INDEX,
            this.miningDisplay
        ) { e: InventoryClickEvent -> this.playInvalidAnimation() }
        this.setButton(
            FISHING_INDEX,
            this.fishingDisplay
        ) { e: InventoryClickEvent ->
            this.openSubMenu(
                LootTypeChancesMenu(
                    this.player,
                    this
                )
            )
        }
        this.setButton(
            FARMING_INDEX,
            this.farmingDisplay
        ) { e: InventoryClickEvent -> this.playInvalidAnimation() }
        this.setButton(
            WOODCUTTING_INDEX,
            this.woodcuttingDisplay
        ) { e: InventoryClickEvent -> this.playInvalidAnimation() }
        this.setButton(
            MAGIC_INDEX,
            this.magicDisplay
        ) { e: InventoryClickEvent -> this.openSubMenu(MagicMenu(this.player, this)) }

        this.setButton(
            DIFFICULTY_INDEX,
            this.difficultyDisplay
        ) { e: InventoryClickEvent ->
            this.openSubMenu(
                MenuDifficultyChooser(
                    this.player,
                    this
                )
            )
        }
    }

    private val difficultyDisplay: ItemStack
        get() {
            val item = InterfaceUtil.getNamedItemWithDescription(
                Material.NETHER_STAR,
                ComponentUtils.create("Difficulty", NamedTextColor.GREEN),
                ComponentUtils.create("Check and/or lower your difficulty!"),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Click to view difficulty options!", NamedTextColor.YELLOW)
            )
            item.setData<ResolvableProfile?>(
                DataComponentTypes.PROFILE,
                ResolvableProfile.resolvableProfile(this.player.playerProfile)
            )
            return item
        }

    private val playerDisplay: ItemStack
        get() {
            val item = InterfaceUtil.getNamedItemWithDescription(
                Material.PLAYER_HEAD,
                ComponentUtils.create("Your Profile", NamedTextColor.GOLD),
                ComponentUtils.create("View your statistics and various information"),
                ComponentUtils.merge(
                    ComponentUtils.create("You can check other players by using "),
                    ComponentUtils.create("/stats", NamedTextColor.GREEN)
                ),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Click to view information about you!", NamedTextColor.YELLOW)
            )
            item.setData<ResolvableProfile?>(
                DataComponentTypes.PROFILE,
                ResolvableProfile.resolvableProfile(this.player.playerProfile)
            )
            return item
        }

    private val combatDisplay: ItemStack
        get() {
            val lore =
                ArrayList<Component>()
            lore.add(ComponentUtils.create("View information about various"))
            lore.add(ComponentUtils.create("creatures and their drops!"))
            lore.add(ComponentUtils.EMPTY)
            lore.addAll(
                formatSkill(
                    SMPRPG.getService(EntityService::class.java).getPlayerInstance(this.player)
                        .combatSkill
                )
            )
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Click to view information about combat!", NamedTextColor.YELLOW))
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Will be implemented at a later date B)", NamedTextColor.RED))
            return InterfaceUtil.getNamedItemWithDescription(
                Material.DIAMOND_SWORD,
                ComponentUtils.create("Combat", NamedTextColor.GOLD),
                lore
            )
        }

    private val miningDisplay: ItemStack
        get() {
            val lore =
                ArrayList<Component>()
            lore.add(ComponentUtils.create("View information about various"))
            lore.add(ComponentUtils.create("ores and materials in the world!"))
            lore.add(ComponentUtils.EMPTY)
            lore.addAll(
                formatSkill(
                    SMPRPG.getService(EntityService::class.java).getPlayerInstance(this.player)
                        .miningSkill
                )
            )
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Click to view information about mining!", NamedTextColor.YELLOW))
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Will be implemented at a later date B)", NamedTextColor.RED))
            return InterfaceUtil.getNamedItemWithDescription(
                Material.IRON_PICKAXE,
                ComponentUtils.create("Mining", NamedTextColor.GOLD),
                lore
            )
        }

    private val fishingDisplay: ItemStack
        get() {
            val lore =
                ArrayList<Component>()
            lore.add(ComponentUtils.create("Check to see what you are allowed to"))
            lore.add(ComponentUtils.create("catch and what you have caught so far!"))
            lore.add(ComponentUtils.EMPTY)
            lore.addAll(
                formatSkill(
                    SMPRPG.getService(EntityService::class.java).getPlayerInstance(this.player)
                        .fishingSkill
                )
            )
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Click to view information about fishing!", NamedTextColor.YELLOW))
            return InterfaceUtil.getNamedItemWithDescription(
                Material.COD,
                ComponentUtils.create("Fishing", NamedTextColor.GOLD),
                lore
            )
        }

    private val farmingDisplay: ItemStack
        get() {
            val lore =
                ArrayList<Component>()
            lore.add(ComponentUtils.create("View information relating to"))
            lore.add(ComponentUtils.create("growing and maintaining crops!"))
            lore.add(ComponentUtils.EMPTY)
            lore.addAll(
                formatSkill(
                    SMPRPG.getService(EntityService::class.java).getPlayerInstance(this.player)
                        .farmingSkill
                )
            )
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Click to view information about farming!", NamedTextColor.YELLOW))
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Will be implemented at a later date B)", NamedTextColor.RED))
            return InterfaceUtil.getNamedItemWithDescription(
                Material.WHEAT,
                ComponentUtils.create("Farming", NamedTextColor.GOLD),
                lore
            )
        }

    private val woodcuttingDisplay: ItemStack
        get() {
            val lore =
                ArrayList<Component>()
            lore.add(ComponentUtils.create("View information relating to"))
            lore.add(ComponentUtils.create("chopping down trees!"))
            lore.add(ComponentUtils.EMPTY)
            lore.addAll(
                formatSkill(
                    SMPRPG.getService(EntityService::class.java).getPlayerInstance(this.player)
                        .woodcuttingSkill
                )
            )
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Click to view information about woodcutting!", NamedTextColor.YELLOW))
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Will be implemented at a later date B)", NamedTextColor.RED))
            return InterfaceUtil.getNamedItemWithDescription(
                Material.DARK_OAK_SAPLING,
                ComponentUtils.create("Woodcutting", NamedTextColor.GOLD),
                lore
            )
        }

    private val magicDisplay: ItemStack
        get() {
            val lore =
                ArrayList<Component>()
            lore.add(ComponentUtils.create("View all the ways to magically"))
            lore.add(ComponentUtils.create("increase your potential!"))
            lore.add(ComponentUtils.EMPTY)
            lore.addAll(
                formatSkill(
                    SMPRPG.getService(EntityService::class.java).getPlayerInstance(this.player)
                        .magicSkill
                )
            )
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Click to view information about magic!", NamedTextColor.YELLOW))
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Will be polished at a later date B)", NamedTextColor.RED))
            return InterfaceUtil.getNamedItemWithDescription(
                Material.ENCHANTING_TABLE,
                ComponentUtils.create("Magic", NamedTextColor.GOLD),
                lore
            )
        }

    private fun formatSkill(skill: SkillInstance): List<Component> {
        val rightBound = getExperienceForLevel(skill.nextLevel)
        val progress = skill.experienceProgress
        val percentage = progress.toDouble() / rightBound
        val greenBars = (percentage * BAR_NUM_CHARS).toInt()
        val grayBars: Int = BAR_NUM_CHARS - greenBars
        val bar = ComponentUtils.merge(
            ComponentUtils.create(StringUtils.repeat(BAR_CHARACTER, greenBars), NamedTextColor.GREEN),
            ComponentUtils.create(StringUtils.repeat(BAR_CHARACTER, grayBars), NamedTextColor.DARK_GRAY)
        ).decoration(TextDecoration.BOLD, true)

        val df = DecimalFormat("#,###")

        return listOf(
            ComponentUtils.merge(
                ComponentUtils.create("You are currently: "),
                ComponentUtils.create(skill.type.displayName + " " + skill.level, NamedTextColor.AQUA)
            ),
            ComponentUtils.merge(
                ComponentUtils.create(df.format(progress.toLong()) + "XP ", NamedTextColor.GRAY),
                bar,
                ComponentUtils.create(" " + df.format(rightBound.toLong()) + "XP", NamedTextColor.GRAY)
            )
        )
    }

    companion object {
        private const val ROWS = 5

        private const val BAR_CHARACTER = '▏'
        private const val BAR_NUM_CHARS = 100

        /*
    Indexes of buttons.
     */
        private const val PLAYER_MENU = 13

        private const val COMBAT_INDEX = 19
        private const val MINING_INDEX = 20
        private const val FISHING_INDEX = 21
        private const val MAGIC_INDEX = 23
        private const val WOODCUTTING_INDEX = 24
        private const val FARMING_INDEX = 25

        private const val DIFFICULTY_INDEX = 41
        private const val SETTINGS_INDEX = 42
    }
}
