package xyz.devvydont.smprpg.gui.player

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.player.settings.ExperienceBarFill
import xyz.devvydont.smprpg.entity.player.settings.ExperienceBarNumber
import xyz.devvydont.smprpg.entity.player.settings.HealthDisplayMode
import xyz.devvydont.smprpg.entity.player.settings.PlayerSettings
import xyz.devvydont.smprpg.entity.player.settings.StructureWarningMode
import xyz.devvydont.smprpg.gui.InterfaceUtil
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Lets a player toggle how their HUD behaves: what shows on the action bar, how health is rendered, and what
 * the experience bar represents. Each change is persisted immediately and reflected live.
 */
class MenuHudSettings(player: Player, parent: MenuBase?) : MenuBase(player, ROWS, parent) {

    private val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
    private val settings: PlayerSettings
        get() = leveledPlayer.settings

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(ComponentUtils.create("HUD Options", NamedTextColor.DARK_GRAY))
        render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
        this.playInvalidAnimation()
    }

    private fun render() {
        this.setBorderFull()
        this.setBackButton()

        this.setButton(HEALTH_SLOT, healthDisplay) {
            settings.healthDisplayMode = settings.healthDisplayMode.next()
            persistAndRefresh()
        }

        this.setButton(DEFENSE_SLOT, defenseDisplay) {
            settings.isDefenseInActionBar = !settings.isDefenseInActionBar
            persistAndRefresh()
        }

        this.setButton(MANA_SLOT, manaDisplay) {
            settings.isManaInActionBar = !settings.isManaInActionBar
            persistAndRefresh()
        }

        this.setButton(SKILL_EXPERIENCE_SLOT, skillExperienceDisplay) {
            settings.isSkillExperienceInActionBar = !settings.isSkillExperienceInActionBar
            persistAndRefresh()
        }

        this.setButton(STRUCTURE_WARNING_SLOT, structureWarningDisplay) {
            settings.structureWarningMode = settings.structureWarningMode.next()
            persistAndRefresh()
        }

        this.setButton(EXPERIENCE_NUMBER_SLOT, experienceNumberDisplay) {
            settings.experienceBarNumber = settings.experienceBarNumber.next()
            // The experience bar is only redrawn on certain triggers, so refresh it immediately on change.
            leveledPlayer.updateExperienceBar()
            persistAndRefresh()
        }

        this.setButton(EXPERIENCE_FILL_SLOT, experienceFillDisplay) {
            settings.experienceBarFill = settings.experienceBarFill.next()
            leveledPlayer.updateExperienceBar()
            persistAndRefresh()
        }
    }

    /**
     * Persists the current settings, rebuilds the buttons to reflect the new state, and gives visual feedback.
     */
    private fun persistAndRefresh() {
        settings.save(this.player)
        render()
        this.playSuccessAnimation()
    }

    private val defenseDisplay: ItemStack
        get() = InterfaceUtil.getNamedItemWithDescription(
            Material.SHIELD,
            ComponentUtils.create("Defense in Action Bar", NamedTextColor.GOLD),
            buildLore(
                "Show your defense stat on the action bar.",
                booleanOptionLore(settings.isDefenseInActionBar),
                null,
                "Click to toggle!"
            )
        )

    private val manaDisplay: ItemStack
        get() = InterfaceUtil.getNamedItemWithDescription(
            Material.LAPIS_LAZULI,
            ComponentUtils.create("Mana in Action Bar", NamedTextColor.GOLD),
            buildLore(
                "Show your mana on the action bar.",
                booleanOptionLore(settings.isManaInActionBar),
                null,
                "Click to toggle!"
            )
        )

    private val skillExperienceDisplay: ItemStack
        get() = InterfaceUtil.getNamedItemWithDescription(
            Material.BOOK,
            ComponentUtils.create("Skill Experience Popups", NamedTextColor.GOLD),
            buildLore(
                "Show skill XP gains on the action bar.",
                booleanOptionLore(settings.isSkillExperienceInActionBar),
                null,
                "Click to toggle!"
            )
        )

    private val structureWarningDisplay: ItemStack
        get() = InterfaceUtil.getNamedItemWithDescription(
            Material.BELL,
            ComponentUtils.create("Structure Warning", NamedTextColor.GOLD),
            buildLore(
                "Show the notice while you are inside a structure.",
                optionLore(StructureWarningMode.values(), settings.structureWarningMode) { it.display },
                settings.structureWarningMode.description,
                "Click to switch!"
            )
        )

    private val healthDisplay: ItemStack
        get() = InterfaceUtil.getNamedItemWithDescription(
            Material.GOLDEN_APPLE,
            ComponentUtils.create("Health Display", NamedTextColor.GOLD),
            buildLore(
                "How your health is shown on the action bar.",
                optionLore(HealthDisplayMode.values(), settings.healthDisplayMode) { it.display },
                settings.healthDisplayMode.description,
                "Click to switch!"
            )
        )

    private val experienceNumberDisplay: ItemStack
        get() = InterfaceUtil.getNamedItemWithDescription(
            Material.NAME_TAG,
            ComponentUtils.create("Experience Bar Number", NamedTextColor.GOLD),
            buildLore(
                "The number shown on your experience bar.",
                optionLore(ExperienceBarNumber.values(), settings.experienceBarNumber) { it.display },
                settings.experienceBarNumber.description,
                "Click to switch!"
            )
        )

    private val experienceFillDisplay: ItemStack
        get() = InterfaceUtil.getNamedItemWithDescription(
            Material.EXPERIENCE_BOTTLE,
            ComponentUtils.create("Experience Bar Fill", NamedTextColor.GOLD),
            buildLore(
                "What the green bar itself fills with.",
                optionLore(ExperienceBarFill.values(), settings.experienceBarFill) { it.display },
                settings.experienceBarFill.description,
                "Click to switch!"
            )
        )

    /**
     * Assembles a button's lore: a one-line purpose, the selectable options (selected highlighted), an optional
     * description of the current selection, and the action hint at the bottom.
     */
    private fun buildLore(
        purpose: String,
        options: List<Component>,
        selectedDescription: String?,
        actionHint: String
    ): List<Component> {
        val lore = mutableListOf<Component>()
        lore.add(ComponentUtils.create(purpose))
        lore.add(ComponentUtils.EMPTY)
        lore.addAll(options)
        lore.add(ComponentUtils.EMPTY)
        if (selectedDescription != null) {
            lore.add(ComponentUtils.create(selectedDescription, NamedTextColor.GRAY))
            lore.add(ComponentUtils.EMPTY)
        }
        lore.add(ComponentUtils.create(actionHint, NamedTextColor.YELLOW))
        return lore
    }

    /**
     * Renders every value of an enum as a "> Name" line, with the selected value in green and the rest in dark
     * gray, so a player can see all available choices at a glance.
     */
    private fun <T : Enum<T>> optionLore(values: Array<T>, selected: T, display: (T) -> String): List<Component> {
        return values.map { value ->
            ComponentUtils.create(
                "> " + display(value),
                if (value == selected) NamedTextColor.GREEN else NamedTextColor.DARK_GRAY
            )
        }
    }

    /**
     * The on/off equivalent of [optionLore] for a boolean toggle.
     */
    private fun booleanOptionLore(enabled: Boolean): List<Component> {
        return listOf(
            ComponentUtils.create("> On", if (enabled) NamedTextColor.GREEN else NamedTextColor.DARK_GRAY),
            ComponentUtils.create("> Off", if (enabled) NamedTextColor.DARK_GRAY else NamedTextColor.GREEN)
        )
    }

    companion object {
        private const val ROWS = 4

        // Row 1 (4 buttons, odd columns): survival/combat HUD info.
        private const val HEALTH_SLOT = 10
        private const val DEFENSE_SLOT = 12
        private const val MANA_SLOT = 14
        private const val STRUCTURE_WARNING_SLOT = 16

        // Row 2 (3 buttons, centered): progression / experience related.
        private const val SKILL_EXPERIENCE_SLOT = 20
        private const val EXPERIENCE_NUMBER_SLOT = 22
        private const val EXPERIENCE_FILL_SLOT = 24
    }
}
