package xyz.devvydont.smprpg.fishing.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.gui.InterfaceUtil.getNamedItemWithDescription
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.slayer.MenuSlayerQuest
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.SlayerService
import xyz.devvydont.smprpg.slayer.quest.SlayerType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols

/**
 * A menu that allows a player to view their current "fishing context", so they know what they are rolling for.
 */
class MenuSlayer : MenuBase {
    constructor(player: Player) : super(player, 3) {
        render()
    }

    constructor(player: Player, parentMenu: MenuBase?) : super(player, 3, parentMenu) {
        render()
    }

    /**
     * Render the menu.
     */
    fun render() {
        this.sounds.setMenuOpen(Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 2.0f)
        this.sounds.setMenuOpenSub(Sound.ENTITY_ENDER_DRAGON_HURT, 1f, 1.0f)
        this.sounds.setMenuClose(Sound.ENTITY_ENDER_DRAGON_HURT, 1f, 0.5f)
        this.setBorderFull()

        generateAutoslayerButton()

        this.setBackButton(22)
        // Place buttons for each slayer

        // Dummy out some slots for NYI slayers
        for (i in SLAYERS_START..<SLAYERS_START + NUM_SLAYERS) {
            this.setButton(
                i, getNamedItemWithDescription(
                    Material.COAL_BLOCK,
                    ComponentUtils.create("Future Slayer Boss", NamedTextColor.DARK_PURPLE),
                    ComponentUtils.EMPTY,
                    ComponentUtils.merge(
                        ComponentUtils.create("This boss has not been "),
                        ComponentUtils.create("developed", NamedTextColor.YELLOW)
                    ),
                    ComponentUtils.create("yet, come back later!")
                ), { e: InventoryClickEvent? ->
                    playInvalidAnimation()
                })
        }

        // Shambling Abomination
        this.setButton(
            SLAYERS_START, getNamedItemWithDescription(
                CustomItemType.NECROTIC_FLESH,
                ComponentUtils.create("Shambling Abomination", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Resurrected via rituals"),
                ComponentUtils.merge(
                    ComponentUtils.create("from the "),
                    ComponentUtils.create("Necronomicon", NamedTextColor.DARK_RED),
                    ComponentUtils.create(","),
                ),
                ComponentUtils.create("this affront to the living preys"),
                ComponentUtils.create("on those who dare awaken it.")
            ), { e: InventoryClickEvent? ->
                MenuSlayerQuest(this.player, this, SlayerType.SHAMBLING_ABOMINATION).openMenu()
            })

        // Illager Warlock
        this.setButton(
            SLAYERS_START + 2, getNamedItemWithDescription(
                CustomItemType.SPELL_POWDER,
                ComponentUtils.create("Illager Warlock", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Once just a mere wizard, this"),
                ComponentUtils.merge(
                    ComponentUtils.create("Illager has mastered "),
                    ComponentUtils.create("the arcane arts", NamedTextColor.AQUA),
                    ComponentUtils.create(","),
                ),
                ComponentUtils.create("and has nigh-complete control"),
                ComponentUtils.create("of the world around them.")
            ), { e: InventoryClickEvent? ->
                MenuSlayerQuest(this.player, this, SlayerType.ILLAGER_WARLOCK).openMenu()
            })
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.SLAYER_MAIN_MENU, NamedTextColor.WHITE),
                ComponentUtils.create(
                    Symbols.OFFSET_NEG_128 + Symbols.OFFSET_NEG_32 + Symbols.OFFSET_NEG_2 + "Slayer",
                    Symbols.INVENTORY_TITLE_COLOR
                )
            )
        )
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.setCancelled(true)
    }

    fun generateAutoslayerButton() {
        val autoslayerSetting = player.persistentDataContainer.getOrDefault(SlayerService.AUTOSLAYER_PDC_KEY, PersistentDataType.BOOLEAN, false)
        val itemType = if (autoslayerSetting) Material.LIME_DYE else Material.GRAY_DYE
        this.setButton(
            18, getNamedItemWithDescription(
                itemType,
                ComponentUtils.create("Auto-Slayer", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Automatically starts another"),
                ComponentUtils.create("Slayer Quest when you complete"),
                ComponentUtils.create("your current quest."),
                ComponentUtils.EMPTY,
                ComponentUtils.merge(
                    ComponentUtils.create("Currently toggled: "),
                    ComponentUtils.create(if (autoslayerSetting) "ON" else "OFF", if (autoslayerSetting) NamedTextColor.GREEN else NamedTextColor.RED),
                )
            ), { e: InventoryClickEvent? ->
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, SoundCategory.UI, 1f, 2f)
                player.persistentDataContainer.set<Byte, Boolean>(SlayerService.AUTOSLAYER_PDC_KEY, PersistentDataType.BOOLEAN, !autoslayerSetting)
                this.generateAutoslayerButton()
            })
    }


    companion object {
        // GUI positions.
        const val SLAYERS_START: Int = 10
        const val NUM_SLAYERS = 7
    }
}