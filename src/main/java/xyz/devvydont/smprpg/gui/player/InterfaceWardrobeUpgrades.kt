package xyz.devvydont.smprpg.gui.player

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.player.UpgradeCategory
import xyz.devvydont.smprpg.entity.player.WardrobeUpgrade
import xyz.devvydont.smprpg.gui.InterfaceUtil
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.services.WardrobeService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols

private const val SLOT_LEVELS = 11
private const val SLOT_COINS = 15
private const val SLOT_TOKENS = 29
private const val SLOT_SPECIAL = 33
private const val SLOT_PROGRESS = 22
private const val MAX_WARDROBE_SLOTS = 28

class InterfaceWardrobeUpgrades(viewer: Player, parentMenu: MenuBase?) : MenuBase(viewer, 6, parentMenu) {

    private val wardrobeService = SMPRPG.getService(WardrobeService::class.java)
    private val economyService = SMPRPG.getService(EconomyService::class.java)

    init {
        render()
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(ComponentUtils.create("Wardrobe Upgrades", NamedTextColor.BLACK))
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    private fun render() {
        this.setBorderFull()
        this.setBackButton()

        val completed = wardrobeService.getCompletedUpgrades(player)

        renderCategoryIcon(UpgradeCategory.LEVEL, SLOT_LEVELS, completed)
        renderCoinCategory(SLOT_COINS, completed)
        renderTokenCategory(SLOT_TOKENS, completed)
        renderCategoryIcon(UpgradeCategory.SPECIAL, SLOT_SPECIAL, completed)
        renderProgressIcon(completed)
    }

    private fun upgradeStatusLine(label: String, isCompleted: Boolean): Component {
        return if (isCompleted) {
            ComponentUtils.merge(
                ComponentUtils.create("${Symbols.CHECK} ", NamedTextColor.GREEN),
                ComponentUtils.create(label, NamedTextColor.GREEN)
            )
        } else {
            ComponentUtils.merge(
                ComponentUtils.create("${Symbols.X} ", NamedTextColor.RED),
                ComponentUtils.create(label, NamedTextColor.GRAY)
            )
        }
    }

    private fun categoryTitle(category: UpgradeCategory, completedCount: Int, totalCount: Int): Component {
        return ComponentUtils.create("${category.displayName} ($completedCount/$totalCount)", category.color)
    }

    private fun categoryLoreHeader(completedCount: Int, totalCount: Int): MutableList<Component> {
        return mutableListOf(
            ComponentUtils.EMPTY,
            ComponentUtils.create("Progress: $completedCount/$totalCount", NamedTextColor.GRAY),
            ComponentUtils.EMPTY
        )
    }

    private fun renderCategoryIcon(category: UpgradeCategory, slot: Int, completed: Set<WardrobeUpgrade>) {
        val upgrades = WardrobeUpgrade.byCategory(category)
        val completedCount = upgrades.count { it in completed }

        val lore = categoryLoreHeader(completedCount, upgrades.size)
        for (upgrade in upgrades) {
            lore.add(upgradeStatusLine(upgrade.displayName, upgrade in completed))
        }

        if (category == UpgradeCategory.LEVEL) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Level up your skills to unlock!", NamedTextColor.DARK_GRAY))
        } else if (category == UpgradeCategory.SPECIAL) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Complete special challenges!", NamedTextColor.DARK_GRAY))
        }

        val title = categoryTitle(category, completedCount, upgrades.size)
        this.setSlot(slot, InterfaceUtil.getNamedItemWithDescription(category.icon, title, lore))
    }

    private fun renderCoinCategory(slot: Int, completed: Set<WardrobeUpgrade>) {
        val category = UpgradeCategory.COIN
        val upgrades = WardrobeUpgrade.byCategory(category)
        val completedCount = upgrades.count { it in completed }

        val lore = categoryLoreHeader(completedCount, upgrades.size)
        for ((index, upgrade) in WardrobeUpgrade.COIN_UPGRADES.withIndex()) {
            val formattedCost = EconomyService.formatMoney(WardrobeUpgrade.COIN_COSTS[index])
            lore.add(upgradeStatusLine("${upgrade.displayName} ($formattedCost)", upgrade in completed))
        }

        val nextUpgrade = wardrobeService.getNextCoinUpgrade(player)
        if (nextUpgrade != null) {
            val costText = EconomyService.formatMoney(nextUpgrade.second)
            val canAfford = economyService.getMoney(player) >= nextUpgrade.second
            lore.add(ComponentUtils.EMPTY)
            if (canAfford) {
                lore.add(ComponentUtils.create("Click to purchase for $costText!", NamedTextColor.YELLOW))
            } else {
                lore.add(ComponentUtils.create("You need $costText to buy the next slot!", NamedTextColor.RED))
            }
        } else {
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("All coin upgrades purchased!", NamedTextColor.GREEN))
        }

        val title = categoryTitle(category, completedCount, upgrades.size)
        this.setButton(slot, InterfaceUtil.getNamedItemWithDescription(category.icon, title, lore)) {
            handleCoinPurchase()
        }
    }

    private fun renderTokenCategory(slot: Int, completed: Set<WardrobeUpgrade>) {
        val category = UpgradeCategory.TOKEN
        val upgrades = WardrobeUpgrade.byCategory(category)
        val completedCount = upgrades.count { it in completed }

        val lore = categoryLoreHeader(completedCount, upgrades.size)
        for (upgrade in WardrobeUpgrade.TOKEN_UPGRADES) {
            lore.add(upgradeStatusLine(upgrade.displayName, upgrade in completed))
        }

        val nextUpgrade = wardrobeService.getNextTokenUpgrade(player)
        if (nextUpgrade != null) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Click to consume a ${nextUpgrade.displayName}!", NamedTextColor.YELLOW))
            lore.add(ComponentUtils.create("Token must be in your inventory.", NamedTextColor.DARK_GRAY))
        } else {
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("All token upgrades redeemed!", NamedTextColor.GREEN))
        }

        val title = categoryTitle(category, completedCount, upgrades.size)
        this.setButton(slot, InterfaceUtil.getNamedItemWithDescription(category.icon, title, lore)) {
            handleTokenConsume()
        }
    }

    private fun renderProgressIcon(completed: Set<WardrobeUpgrade>) {
        val totalSlots = WardrobeUpgrade.DEFAULT_SLOTS + completed.size

        val lore = mutableListOf<Component>()
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Total Wardrobe Slots: $totalSlots/$MAX_WARDROBE_SLOTS", NamedTextColor.GRAY))
        lore.add(ComponentUtils.EMPTY)

        for (category in UpgradeCategory.entries) {
            val upgrades = WardrobeUpgrade.byCategory(category)
            val count = upgrades.count { it in completed }
            lore.add(
                ComponentUtils.merge(
                    ComponentUtils.create("${Symbols.POINT} ", NamedTextColor.DARK_GRAY),
                    ComponentUtils.create("${category.displayName}: ", category.color),
                    ComponentUtils.create("$count/${upgrades.size}", NamedTextColor.WHITE)
                )
            )
        }

        lore.add(ComponentUtils.EMPTY)
        lore.add(
            ComponentUtils.merge(
                ComponentUtils.create("Default Slots: ", NamedTextColor.DARK_GRAY),
                ComponentUtils.create("${WardrobeUpgrade.DEFAULT_SLOTS}", NamedTextColor.WHITE)
            )
        )

        val title = ComponentUtils.create("Wardrobe Progress ($totalSlots/$MAX_WARDROBE_SLOTS)", NamedTextColor.AQUA)
        this.setSlot(SLOT_PROGRESS, InterfaceUtil.getNamedItemWithDescription(
            org.bukkit.Material.PAINTING, title, lore
        ))
    }

    private fun handleCoinPurchase() {
        if (wardrobeService.purchaseCoinUpgrade(player)) {
            this.playSuccessAnimation()
        } else {
            this.playInvalidAnimation()
        }
        render()
    }

    private fun handleTokenConsume() {
        if (wardrobeService.consumeToken(player)) {
            this.playSuccessAnimation()
        } else {
            this.playInvalidAnimation()
        }
        render()
    }
}
