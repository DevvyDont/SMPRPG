package xyz.devvydont.smprpg.gui.slayer

import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.InterfaceUtil.getNamedItemWithDescription
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.SlayerService
import xyz.devvydont.smprpg.slayer.quest.SlayerClassification
import xyz.devvydont.smprpg.slayer.quest.SlayerQuest
import xyz.devvydont.smprpg.slayer.quest.SlayerType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import xyz.devvydont.smprpg.util.formatting.TooltipStyle

/**
 * A menu that allows a player to view their current "fishing context", so they know what they are rolling for.
 */
class MenuSlayerQuest : MenuBase {
    constructor(player: Player, type : SlayerType) : super(player, 5) {
        slayerType = type
        render()
    }

    constructor(player: Player, parentMenu: MenuBase?, type : SlayerType) : super(player, 5, parentMenu) {
        slayerType = type
        render()
    }

    var slayerType : SlayerType

    /**
     * Render the menu.
     */
    fun render() {
        this.sounds.setMenuClose(Sound.ENTITY_ENDER_DRAGON_HURT, 1f, 2f)
        this.setBorderFull()

        this.setBackButton(40)
        // Place buttons for each slayer tier

        when (slayerType) {
            SlayerType.SHAMBLING_ABOMINATION -> {
                this.setButton(
                    SLAYERS_START, getSlayerInfoButton(
                        CustomItemType.NECROTIC_FLESH,
                        ComponentUtils.create("Shambling Abomination I", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                        ComponentUtils.create("Basic", NamedTextColor.GRAY, TextDecoration.UNDERLINED),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Not-so-shambling Gait", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                        ComponentUtils.create("The Shambling Abomination will"),
                        ComponentUtils.create("chase you down at a relatively"),
                        ComponentUtils.create("quick pace, and strike when close."),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(ComponentUtils.create("Cost: "), ComponentUtils.money(SlayerClassification.SHAMBLING_HORROR_1.cost)),
                        ComponentUtils.merge(ComponentUtils.create("Exp Needed: "), ComponentUtils.create(String.format("%,d", SlayerClassification.SHAMBLING_HORROR_1.xpToSpawn),
                            NamedTextColor.AQUA)),
                        ComponentUtils.merge(
                            ComponentUtils.create("Rewards: "),
                            getSlayerXpComponent(SlayerClassification.SHAMBLING_HORROR_1)
                        )

                    ), { e: InventoryClickEvent? -> startQuest(SlayerClassification.SHAMBLING_HORROR_1)})

                this.setButton(
                    SLAYERS_START + 1, getSlayerInfoButton(
                        CustomItemType.NECROTIC_FLESH,
                        ComponentUtils.create("Shambling Abomination II", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                        ComponentUtils.create("Intermediate", NamedTextColor.GRAY, TextDecoration.UNDERLINED),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Ferocious Frenzy", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                        ComponentUtils.create("The Shambling Abomination will"),
                        ComponentUtils.create("drastically speed up at half health,"),
                        ComponentUtils.merge(
                            ComponentUtils.create("and "),
                            ComponentUtils.create("double", NamedTextColor.RED),
                            ComponentUtils.create(" its attack rate."),
                        ),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(
                            ComponentUtils.create("Shambling Abomination II", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" also inherits")),
                        ComponentUtils.create("all abilities from previous boss tiers."),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(ComponentUtils.create("Cost: "), ComponentUtils.money(SlayerClassification.SHAMBLING_HORROR_2.cost)),
                        ComponentUtils.merge(ComponentUtils.create("Exp Needed: "), ComponentUtils.create(String.format("%,d", SlayerClassification.SHAMBLING_HORROR_2.xpToSpawn),
                            NamedTextColor.AQUA)),
                        ComponentUtils.merge(
                            ComponentUtils.create("Rewards: "),
                            getSlayerXpComponent(SlayerClassification.SHAMBLING_HORROR_2)
                        )

                    ), { e: InventoryClickEvent? -> startQuest(SlayerClassification.SHAMBLING_HORROR_2)})

                this.setButton(
                    SLAYERS_START + 2, getSlayerInfoButton(
                        CustomItemType.NECROTIC_FLESH,
                        ComponentUtils.create("Shambling Abomination III", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                        ComponentUtils.create("Advanced", NamedTextColor.GRAY, TextDecoration.UNDERLINED),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Furious Frenzy", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                        ComponentUtils.merge(
                            ComponentUtils.create("The "),
                            ComponentUtils.create("Ferocious Frenzy", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                            ComponentUtils.create(" ability now also grants"),
                        ),
                        ComponentUtils.merge(
                            ComponentUtils.create("a "),
                            ComponentUtils.create("50%", NamedTextColor.RED),
                            ComponentUtils.create(" increase in strength when activated."),
                        ),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(
                            ComponentUtils.create("Shambling Abomination III", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" also inherits")),
                        ComponentUtils.create("all abilities from previous boss tiers."),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(ComponentUtils.create("Cost: "), ComponentUtils.money(SlayerClassification.SHAMBLING_HORROR_3.cost)),
                        ComponentUtils.merge(ComponentUtils.create("Exp Needed: "), ComponentUtils.create(String.format("%,d", SlayerClassification.SHAMBLING_HORROR_3.xpToSpawn),
                            NamedTextColor.AQUA)),
                        ComponentUtils.merge(
                            ComponentUtils.create("Rewards: "),
                            getSlayerXpComponent(SlayerClassification.SHAMBLING_HORROR_3)
                        )
                    ), { e: InventoryClickEvent? -> startQuest(SlayerClassification.SHAMBLING_HORROR_3)})

                this.setButton(
                    SLAYERS_START + 3, getSlayerInfoButton(
                        CustomItemType.NECROTIC_FLESH,
                        ComponentUtils.create("Shambling Abomination IV", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                        ComponentUtils.create("Expert", NamedTextColor.GRAY, TextDecoration.UNDERLINED),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Spontaneous Human(?) Combustion", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                        ComponentUtils.merge(
                            ComponentUtils.create("The "),
                            ComponentUtils.create("Shambling Horror", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" will implode every"),
                        ),
                        ComponentUtils.merge(
                            ComponentUtils.create("10 seconds", NamedTextColor.RED),
                            ComponentUtils.create(", dealing "),
                            ComponentUtils.create("500", NamedTextColor.RED),
                            ComponentUtils.create(" true damage to players "),
                        ),
                        ComponentUtils.create("that are caught in range."),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(
                            ComponentUtils.create("Shambling Abomination IV", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" also inherits")),
                        ComponentUtils.create("all abilities from previous boss tiers."),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(ComponentUtils.create("Cost: "), ComponentUtils.money(SlayerClassification.SHAMBLING_HORROR_4.cost)),
                        ComponentUtils.merge(ComponentUtils.create("Exp Needed: "), ComponentUtils.create(String.format("%,d", SlayerClassification.SHAMBLING_HORROR_4.xpToSpawn),
                            NamedTextColor.AQUA)),
                        ComponentUtils.merge(
                            ComponentUtils.create("Rewards: "),
                            getSlayerXpComponent(SlayerClassification.SHAMBLING_HORROR_4)
                        )
                    ), { e: InventoryClickEvent? -> startQuest(SlayerClassification.SHAMBLING_HORROR_4)})

                this.setButton(
                    SLAYERS_START + 4, getSlayerInfoButton(
                        CustomItemType.NECROTIC_FLESH,
                        ComponentUtils.create("Shambling Abomination V", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                        ComponentUtils.create("Brutal", NamedTextColor.GRAY, TextDecoration.UNDERLINED),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Syphon", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                        ComponentUtils.merge(
                            ComponentUtils.create("The "),
                            ComponentUtils.create("Shambling Abomination", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" will syphon "),
                            ComponentUtils.create("10%", NamedTextColor.RED),
                            ComponentUtils.create(" of your max health every 3")),
                        ComponentUtils.merge(
                            ComponentUtils.create("seconds, and return "),
                            ComponentUtils.create("1000x", NamedTextColor.RED),
                            ComponentUtils.create(" the syphoned amount as healing to itself."),
                        ),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Ragnarok Rage", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                        ComponentUtils.merge(
                            ComponentUtils.create("Furious Frenzy", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                            ComponentUtils.create(" now triggers at "),
                            ComponentUtils.create("35%", NamedTextColor.RED),
                            ComponentUtils.create(" health,"),
                        ),
                        ComponentUtils.merge(
                            ComponentUtils.create("quickens", NamedTextColor.RED),
                            ComponentUtils.create(" the rate that "),
                            ComponentUtils.create("Spontaneous Human(?) Combustion", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED)),
                        ComponentUtils.merge(
                            ComponentUtils.create("triggers by"),
                            ComponentUtils.create(" 2x", NamedTextColor.RED),
                            ComponentUtils.create(", and increases the true damage dealt to "),
                            ComponentUtils.create("1000", NamedTextColor.RED),
                            ComponentUtils.create(".")
                        ),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(
                            ComponentUtils.create("Shambling Abomination V", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" also inherits")),
                        ComponentUtils.create("all abilities from previous boss tiers."),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(ComponentUtils.create("Cost: "), ComponentUtils.money(SlayerClassification.SHAMBLING_HORROR_5.cost)),
                        ComponentUtils.merge(ComponentUtils.create("Exp Needed: "), ComponentUtils.create(String.format("%,d", SlayerClassification.SHAMBLING_HORROR_5.xpToSpawn),
                            NamedTextColor.AQUA)),
                        ComponentUtils.merge(
                            ComponentUtils.create("Rewards: "),
                            getSlayerXpComponent(SlayerClassification.SHAMBLING_HORROR_5)
                        )
                    ), { e: InventoryClickEvent? -> startQuest(SlayerClassification.SHAMBLING_HORROR_5)})
            }
            SlayerType.PIGLIN_WARLORD -> {
                TODO()
            }
            SlayerType.ILLAGER_WARLOCK -> {
                this.setButton(
                    SLAYERS_START, getSlayerInfoButton(
                        CustomItemType.SPELL_POWDER,
                        ComponentUtils.create("Illager Warlock I", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                        ComponentUtils.create("Basic", NamedTextColor.GRAY, TextDecoration.UNDERLINED),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Spellbook", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                        ComponentUtils.merge(
                            ComponentUtils.create("The "),
                            ComponentUtils.create("Illager Warlock", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" uses their")
                        ),
                        ComponentUtils.merge(
                            ComponentUtils.create("spellbook to cast "),
                            ComponentUtils.create("Teleport", NamedTextColor.LIGHT_PURPLE),
                            ComponentUtils.create(" and "),
                            ComponentUtils.create("Fireball", NamedTextColor.RED),
                            ComponentUtils.create(".")
                        ),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Postmortal", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                        ComponentUtils.merge(
                            ComponentUtils.create("The "),
                            ComponentUtils.create("Illager Warlock", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" will use their")
                        ),
                        ComponentUtils.merge(
                            ComponentUtils.create("Totem of Undying", NamedTextColor.BLUE),
                            ComponentUtils.create(" and revive on first death.")
                        ),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(ComponentUtils.create("Cost: "), ComponentUtils.money(SlayerClassification.ILLAGER_WARLOCK_1.cost)),
                        ComponentUtils.merge(ComponentUtils.create("Exp Needed: "), ComponentUtils.create(String.format("%,d", SlayerClassification.ILLAGER_WARLOCK_1.xpToSpawn),
                            NamedTextColor.AQUA)),
                        ComponentUtils.merge(
                            ComponentUtils.create("Rewards: "),
                            getSlayerXpComponent(SlayerClassification.ILLAGER_WARLOCK_1)
                        )

                    ), { e: InventoryClickEvent? -> startQuest(SlayerClassification.ILLAGER_WARLOCK_1)})

                this.setButton(
                    SLAYERS_START + 1, getSlayerInfoButton(
                        CustomItemType.SPELL_POWDER,
                        ComponentUtils.create("Illager Warlock II", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                        ComponentUtils.create("Intermediate", NamedTextColor.GRAY, TextDecoration.UNDERLINED),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Spellbook", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                        ComponentUtils.merge(
                            ComponentUtils.create("The "),
                            ComponentUtils.create("Illager Warlock", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" expands their")
                        ),
                        ComponentUtils.merge(
                            ComponentUtils.create("spellbook to include "),
                            ComponentUtils.create("Fangs", TextColor.color(252, 240, 194)),
                            ComponentUtils.create(".")
                        ),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Arcane Aura", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                        ComponentUtils.merge(
                            ComponentUtils.create("The "),
                            ComponentUtils.create("Illager Warlock", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" summons an arcane field around")
                        ),
                        ComponentUtils.merge(
                            ComponentUtils.create("them that "),
                            ComponentUtils.create("damages", NamedTextColor.RED),
                            ComponentUtils.create(", "),
                            ComponentUtils.create("drains mana", NamedTextColor.AQUA),
                            ComponentUtils.create(", or"),
                            ComponentUtils.create(" slows players", NamedTextColor.YELLOW),
                        ),
                        ComponentUtils.create("depending on the aura color."),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(ComponentUtils.create("Cost: "), ComponentUtils.money(SlayerClassification.ILLAGER_WARLOCK_2.cost)),
                        ComponentUtils.merge(ComponentUtils.create("Exp Needed: "), ComponentUtils.create(String.format("%,d", SlayerClassification.ILLAGER_WARLOCK_2.xpToSpawn),
                            NamedTextColor.AQUA)),
                        ComponentUtils.merge(
                            ComponentUtils.create("Rewards: "),
                            getSlayerXpComponent(SlayerClassification.ILLAGER_WARLOCK_2)
                        )

                    ), { e: InventoryClickEvent? -> startQuest(SlayerClassification.ILLAGER_WARLOCK_2)})

                this.setButton(
                    SLAYERS_START + 2, getSlayerInfoButton(
                        CustomItemType.SPELL_POWDER,
                        ComponentUtils.create("Illager Warlock III", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                        ComponentUtils.create("Advanced", NamedTextColor.GRAY, TextDecoration.UNDERLINED),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Spellbook", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                        ComponentUtils.merge(
                            ComponentUtils.create("The "),
                            ComponentUtils.create("Illager Warlock", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" expands their")
                        ),
                        ComponentUtils.merge(
                            ComponentUtils.create("spellbook to include "),
                            ComponentUtils.create("Repel", NamedTextColor.BLUE),
                            ComponentUtils.create(" and "),
                            ComponentUtils.create("Rapid Snap", NamedTextColor.DARK_AQUA),
                            ComponentUtils.create("."),
                        ),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(
                            ComponentUtils.create("Illager Warlock III", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" also inherits")),
                        ComponentUtils.create("all abilities from previous boss tiers."),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(ComponentUtils.create("Cost: "), ComponentUtils.money(SlayerClassification.ILLAGER_WARLOCK_3.cost)),
                        ComponentUtils.merge(ComponentUtils.create("Exp Needed: "), ComponentUtils.create(String.format("%,d", SlayerClassification.ILLAGER_WARLOCK_3.xpToSpawn),
                            NamedTextColor.AQUA)),
                        ComponentUtils.merge(
                            ComponentUtils.create("Rewards: "),
                            getSlayerXpComponent(SlayerClassification.ILLAGER_WARLOCK_3)
                        )
                    ), { e: InventoryClickEvent? -> startQuest(SlayerClassification.ILLAGER_WARLOCK_3)})

                this.setButton(
                    SLAYERS_START + 3, getSlayerInfoButton(
                        CustomItemType.SPELL_POWDER,
                        ComponentUtils.create("Illager Warlock IV", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                        ComponentUtils.create("Expert", NamedTextColor.GRAY, TextDecoration.UNDERLINED),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Spellbook", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                        ComponentUtils.merge(
                            ComponentUtils.create("The "),
                            ComponentUtils.create("Illager Warlock", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" expands their spellbook")
                        ),
                        ComponentUtils.merge(
                            ComponentUtils.create("to include "),
                            ComponentUtils.create("Summon Vex", TextColor.color(181, 209, 237)),
                            ComponentUtils.create(" and improves "),
                            ComponentUtils.create("Rapid Snap", NamedTextColor.DARK_AQUA),
                            ComponentUtils.create("."),
                        ),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(
                            ComponentUtils.create("Illager Warlock IV", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" also inherits")),
                        ComponentUtils.create("all abilities from previous boss tiers."),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(ComponentUtils.create("Cost: "), ComponentUtils.money(SlayerClassification.ILLAGER_WARLOCK_4.cost)),
                        ComponentUtils.merge(ComponentUtils.create("Exp Needed: "), ComponentUtils.create(String.format("%,d", SlayerClassification.ILLAGER_WARLOCK_4.xpToSpawn),
                            NamedTextColor.AQUA)),
                        ComponentUtils.merge(
                            ComponentUtils.create("Rewards: "),
                            getSlayerXpComponent(SlayerClassification.ILLAGER_WARLOCK_4)
                        )
                    ), { e: InventoryClickEvent? -> startQuest(SlayerClassification.ILLAGER_WARLOCK_4)})

                this.setButton(
                    SLAYERS_START + 4, getSlayerInfoButton(
                        CustomItemType.SPELL_POWDER,
                        ComponentUtils.create("Illager Warlock V", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                        ComponentUtils.create("Brutal", NamedTextColor.GRAY, TextDecoration.UNDERLINED),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Arcane Mastery", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED),
                        ComponentUtils.merge(
                            ComponentUtils.create("The "),
                            ComponentUtils.create("Illager Warlock", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" casts their spells")
                        ),
                        ComponentUtils.merge(
                            ComponentUtils.create("significantly faster", NamedTextColor.AQUA),
                            ComponentUtils.create(" than previous tiers."),
                        ),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(
                            ComponentUtils.create("Illager Warlock V", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                            ComponentUtils.create(" also inherits")),
                        ComponentUtils.create("all abilities from previous boss tiers."),
                        ComponentUtils.EMPTY,
                        ComponentUtils.merge(ComponentUtils.create("Cost: "), ComponentUtils.money(SlayerClassification.ILLAGER_WARLOCK_5.cost)),
                        ComponentUtils.merge(ComponentUtils.create("Exp Needed: "), ComponentUtils.create(String.format("%,d", SlayerClassification.ILLAGER_WARLOCK_5.xpToSpawn),
                            NamedTextColor.AQUA)),
                        ComponentUtils.merge(
                            ComponentUtils.create("Rewards: "),
                            getSlayerXpComponent(SlayerClassification.ILLAGER_WARLOCK_5)
                        )
                    ), { e: InventoryClickEvent? -> startQuest(SlayerClassification.ILLAGER_WARLOCK_5)})
            }
        }
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        var bgAsset = ""
        when (slayerType) {
            SlayerType.SHAMBLING_ABOMINATION -> bgAsset = Symbols.SLAYER_SHAMBLING_MENU
            SlayerType.PIGLIN_WARLORD -> bgAsset = Symbols.SLAYER_PIGLIN_MENU
            SlayerType.ILLAGER_WARLOCK -> bgAsset = Symbols.SLAYER_ILLAGER_MENU
        }
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_1 + bgAsset, NamedTextColor.WHITE),
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

    fun getSlayerXpComponent(classification : SlayerClassification) : Component {
        return ComponentUtils.merge(
            ComponentUtils.create(String.format("%,d", classification.slayerXpReward), NamedTextColor.LIGHT_PURPLE),
            ComponentUtils.create(" Slayer Experience", NamedTextColor.LIGHT_PURPLE))
    }

    fun startQuest(slayerInfo : SlayerClassification) {
        val questOwner = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        val quest = SlayerQuest(questOwner, slayerInfo)
        if (!SMPRPG.getService(SlayerService::class.java).canStartQuest(quest)) {
            player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
            quest.cleanup()
            return
        }

        openSubMenu(
            MenuSlayerQuestConfirmDialog(
                player, this, slayerInfo
            )
        )
    }

    fun getSlayerInfoButton(itemType : CustomItemType, name : Component, vararg lines: Component) : ItemStack {
        val item = getNamedItemWithDescription(itemType, name, listOf(*lines))
        item.setData(DataComponentTypes.TOOLTIP_STYLE, TooltipStyle.INFO.key)
        return item
    }

    companion object {
        // GUI positions.
        const val SLAYERS_START: Int = 11
        const val SLAYER_TIERS = 5
    }
}