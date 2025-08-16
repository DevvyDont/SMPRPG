package xyz.devvydont.smprpg.gui.player

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.entity.player.ProfileDifficulty
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.services.DifficultyService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.function.Consumer

class MenuDifficultyChooser : MenuBase {
    private var locked = false

    constructor(player: Player) : super(player, 4) {
        render()
    }

    constructor(player: Player, parent: MenuBase?) : super(player, 4, parent) {
        render()
    }

    /**
     * Locks the player to view this menu requiring them to make a decision.
     */
    fun lock() {
        locked = true
        this.player.isInvulnerable = true
    }

    /**
     * Unlocks the player from the menu, meaning they are allowed to close it and not make a decision.
     */
    fun unlock() {
        locked = false
        this.player.isInvulnerable = false
    }

    fun render() {
        this.setBorderFull()

        // All we need to do is add a couple buttons to set our difficulty and tell the player what it does.
        this.setButton(
            13,
            generateDifficultyButton(ProfileDifficulty.STANDARD)
        ) { e: InventoryClickEvent -> handleChoice(ProfileDifficulty.STANDARD) }
        this.setButton(
            11,
            generateDifficultyButton(ProfileDifficulty.EASY)
        ) { e: InventoryClickEvent -> handleChoice(ProfileDifficulty.EASY) }
        this.setButton(
            15,
            generateDifficultyButton(ProfileDifficulty.HARD)
        ) { e: InventoryClickEvent -> handleChoice(ProfileDifficulty.HARD) }

        this.setBackButton(31)
    }

    private fun handleChoice(difficulty: ProfileDifficulty) {
        // If this is an invalid difficulty, don't do anything.

        if (!isAllowedToSwitchTo(difficulty)) {
            this.playInvalidAnimation()
            return
        }

        if (locked) unlock()

        this.closeMenu()

        if (difficulty == SMPRPG.getService(DifficultyService::class.java)
                .getDifficulty(this.player)
        ) {
            player.sendMessage(ComponentUtils.alert("Already playing on that difficulty. Nothing changed."))
            return
        }

        SMPRPG.getService(DifficultyService::class.java).setDifficulty(this.player, difficulty)
        Bukkit.broadcast(
            ComponentUtils.alert(
                ComponentUtils.create("!", NamedTextColor.GOLD),
                ComponentUtils.merge(
                    ComponentUtils.create(this.player.name, difficulty.Color),
                    ComponentUtils.create(" has chosen the path of the "),
                    ComponentUtils.create(difficulty.Display, difficulty.Color),
                    ComponentUtils.create("!")
                )
            )
        )
    }

    private fun generateDifficultyButton(difficulty: ProfileDifficulty): ItemStack {
        var material = matchMaterialToDifficulty(difficulty)
        if (!isAllowedToSwitchTo(difficulty)) material = Material.RED_DYE

        val item = ItemStack(material)
        val lore = ArrayList<Component?>()

        item.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
        item.editMeta(Consumer { meta: ItemMeta? ->
            meta!!.displayName(
                ComponentUtils.create(
                    difficulty.Display,
                    difficulty.Color
                ).decoration(TextDecoration.ITALIC, false)
            )
        })
        item.editMeta(Consumer { meta: ItemMeta? -> meta!!.setEnchantmentGlintOverride(true) })

        lore.add(ComponentUtils.EMPTY)
        if (!isAllowedToSwitchTo(difficulty)) {
            lore.add(
                ComponentUtils.create(
                    "You cannot switch to this difficulty since you already chose an easier one!",
                    NamedTextColor.RED
                )
            )
            lore.add(ComponentUtils.EMPTY)
        }

        if (SMPRPG.getService(DifficultyService::class.java)
                .getDifficulty(this.player) == difficulty
        ) {
            lore.add(ComponentUtils.create("You are playing on this difficulty!", NamedTextColor.GOLD))
            lore.add(ComponentUtils.EMPTY)
        }

        lore.addAll(matchSummaryToDifficulty(difficulty))
        lore.add(ComponentUtils.EMPTY)
        lore.addAll(matchModifiersToDifficulty(difficulty))

        lore.add(ComponentUtils.EMPTY)
        lore.add(
            ComponentUtils.merge(
                ComponentUtils.create("Click to set your difficulty to "),
                ComponentUtils.create(difficulty.Display, difficulty.Color),
                ComponentUtils.create("!")
            )
        )
        lore.add(ComponentUtils.EMPTY)
        lore.add(
            ComponentUtils.create("Once chosen, you may only lower your difficulty!", NamedTextColor.RED)
                .decoration(TextDecoration.BOLD, true)
        )
        if (this.isAllowedToFreelyChange) lore.add(
            ComponentUtils.create(
                "**You have permission to freely change your difficulty!",
                NamedTextColor.GREEN
            )
        )
        item.lore(ComponentUtils.cleanItalics(lore))
        return item
    }

    private val isAllowedToFreelyChange: Boolean
        /**
         * Checks if the viewer is allowed to freely change difficulties with no restrictions. This is considered an
         * admin action since it could be abused for drop rate manipulation.
         * @return
         */
        get() = this.player.isOp || this.player.permissionValue("smprpg.difficulty.ignorerestrictions")
            .toBooleanOrElse(false)

    private fun isAllowedToSwitchTo(difficulty: ProfileDifficulty): Boolean {
        // Permission override

        if (this.isAllowedToFreelyChange) return true

        val current = SMPRPG.getService(DifficultyService::class.java).getDifficulty(this.player)
        // Hasn't chosen yet
        if (current == ProfileDifficulty.NOT_CHOSEN) return true

        return difficulty.ordinal <= current.ordinal
    }

    private fun matchSummaryToDifficulty(difficulty: ProfileDifficulty): List<Component> {
        return when (difficulty) {
            ProfileDifficulty.EASY -> listOf(
                ComponentUtils.merge(
                    ComponentUtils.create("For players who want")
                ),
                ComponentUtils.merge(
                    ComponentUtils.create("a "),
                    ComponentUtils.create("relaxed and casual experience", difficulty.Color),
                    ComponentUtils.create(" with friends.")
                ),
                ComponentUtils.create("Great for a chill time!")
            )

            ProfileDifficulty.STANDARD -> listOf(
                ComponentUtils.create("Balanced for players who want"),
                ComponentUtils.merge(
                    ComponentUtils.create("the "),
                    ComponentUtils.create("core MMO SMP experience", difficulty.Color),
                    ComponentUtils.create(".")
                ),
                ComponentUtils.create("Not too hard, but not too easy.")
            )

            ProfileDifficulty.HARD -> listOf(
                ComponentUtils.create("A hardcore mode designed for players"),
                ComponentUtils.merge(
                    ComponentUtils.create("who want to "),
                    ComponentUtils.create("truly test themselves", difficulty.Color),
                    ComponentUtils.create(".")
                ),
                ComponentUtils.create("Not for the faint of heart.")
            )

            else -> listOf<Component>(ComponentUtils.create("Unknown summary for difficulty: $difficulty"))
        }
    }

    private fun matchModifiersToDifficulty(difficulty: ProfileDifficulty): List<Component> {
        return when (difficulty) {
            ProfileDifficulty.EASY -> listOf(
                ComponentUtils.create("* -50% Incoming Damage", NamedTextColor.GREEN),
                ComponentUtils.create("* +25% Skill Experience", NamedTextColor.GREEN),
                ComponentUtils.create("* Ignore Dimension Requirements", NamedTextColor.GREEN),
                ComponentUtils.create("* Permanent -50 Luck (Drop rates)", NamedTextColor.RED)
            )

            ProfileDifficulty.STANDARD -> listOf(
            ComponentUtils.create("* No changes to any gameplay systems", NamedTextColor.YELLOW),
            ComponentUtils.create("* The intended and balanced experience!", NamedTextColor.YELLOW)
            )
            ProfileDifficulty.HARD -> listOf(
                ComponentUtils.create("* Permanent +100 Luck (Drop rates)", NamedTextColor.GREEN),
                ComponentUtils.create("* +50% Vanilla Experience from Orbs", NamedTextColor.GREEN),
                ComponentUtils.create("* x2 Incoming Damage", NamedTextColor.RED),
                ComponentUtils.create("* -50% Base Stats", NamedTextColor.RED),
                ComponentUtils.create("* -50% Stats from Skills", NamedTextColor.RED),
                ComponentUtils.create("* -25% Skill Experience", NamedTextColor.RED)
            )
            else -> listOf()
        }
    }

    private fun matchMaterialToDifficulty(difficulty: ProfileDifficulty): Material {
        return when (difficulty) {
            ProfileDifficulty.EASY -> Material.CORNFLOWER
            ProfileDifficulty.STANDARD -> Material.IRON_SWORD
            ProfileDifficulty.HARD -> Material.NETHER_STAR
            else -> Material.BARRIER
        }
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(ComponentUtils.create("Choose Your Adventure!", NamedTextColor.GOLD))
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
        this.playInvalidAnimation()
    }

    override fun handleInventoryClosed(event: InventoryCloseEvent) {
        if (event.player == this.player && this.locked) {
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                openMenu()
                playInvalidAnimation()
            }, 0)
        }
    }
}
