package xyz.devvydont.smprpg.market.gui.bazaar

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.market.bazaar.BazaarDemandDisplay
import xyz.devvydont.smprpg.market.bazaar.BazaarItem
import xyz.devvydont.smprpg.market.bazaar.BazaarManager
import xyz.devvydont.smprpg.market.bazaar.BazaarPricingEngine
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.util.crafting.CompressionRecipeMember
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Tier selector menu for compressable bazaar items.
 * Shows one button per compression tier, allowing players to buy/sell at any tier.
 */
class MenuBazaarCompressionView(
    player: Player,
    private val bazaarItem: BazaarItem,
    private val compressionFlow: List<CompressionRecipeMember>,
    parentMenu: MenuBase? = null
) : MenuBase(player, ROWS, parentMenu) {

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(Component.text("Bazaar - ${bazaarItem.displayName} Tiers"))
        render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    private fun render() {
        setBorderFull()
        setBackButton()

        val tierCount = compressionFlow.size
        val startSlot = TIER_ROW_START + (MAX_TIER_SLOTS - tierCount).coerceAtLeast(0) / 2

        for (tierIndex in 0 until tierCount) {
            val tier = compressionFlow[tierIndex]
            val compAmt = BazaarManager.calculateCompressionAmount(compressionFlow, tierIndex)
            val buyPrice = BazaarPricingEngine.calculateBulkBuyCost(bazaarItem, compAmt)
            val sellPrice = BazaarPricingEngine.calculateBulkSellPayout(bazaarItem, compAmt)
            val tierName = tier.material.name()

            val lore = mutableListOf<Component>()
            if (tierIndex > 0) {
                lore.add(ComponentUtils.merge(
                    ComponentUtils.create("= ${compAmt}x ", NamedTextColor.DARK_GRAY),
                    ComponentUtils.create(bazaarItem.displayName, NamedTextColor.GRAY)
                ))
            }
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.merge(
                ComponentUtils.create("Buy Price: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(buyPrice), NamedTextColor.GOLD)
            ))
            lore.add(ComponentUtils.merge(
                ComponentUtils.create("Sell Price: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(sellPrice), NamedTextColor.GOLD)
            ))
            lore.add(ComponentUtils.EMPTY)
            if (tierIndex > 0) {
                lore.addAll(BazaarDemandDisplay.buildCompressedDemandLore(player, bazaarItem, compAmt))
            } else {
                lore.addAll(BazaarDemandDisplay.buildDemandLore(player, bazaarItem))
            }
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Click to buy/sell!", NamedTextColor.YELLOW))

            val tierItemType = if (tier.material.isCustom) tier.material.custom else null
            val tierBazaarKey = tier.material.key()
            val icon = MenuBazaarCategory.createBazaarDisplayItem(tierItemType, tierName, lore, bazaarKey = tierBazaarKey)

            val slot = startSlot + tierIndex
            val capturedTierIndex = tierIndex
            setButton(slot, icon) { _: InventoryClickEvent ->
                openSubMenu(MenuBazaarItemView(player, bazaarItem, this, compressionFlow, capturedTierIndex))
            }
        }

        // Base item info in row 2
        val baseItemType = if (compressionFlow.first().material.isCustom) compressionFlow.first().material.custom else null
        val infoLore = mutableListOf(
            ComponentUtils.EMPTY,
            ComponentUtils.merge(
                ComponentUtils.create("Unit Buy: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(BazaarPricingEngine.calculateBuyPrice(bazaarItem)), NamedTextColor.GOLD)
            ),
            ComponentUtils.merge(
                ComponentUtils.create("Unit Sell: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(BazaarPricingEngine.calculateSellPrice(bazaarItem)), NamedTextColor.GOLD)
            ),
            ComponentUtils.EMPTY,
        )
        infoLore.addAll(BazaarDemandDisplay.buildDemandLore(player, bazaarItem))

        val infoItem = MenuBazaarCategory.createBazaarDisplayItem(
            baseItemType, bazaarItem.displayName, infoLore, bazaarKey = bazaarItem.key
        )
        setSlot(INFO_SLOT, infoItem)
    }

    companion object {
        private const val ROWS = 4
        private const val MAX_TIER_SLOTS = 7
        private const val TIER_ROW_START = 10 // row 1, slot 1 (inside border)
        private const val INFO_SLOT = 22 // row 2, center
    }
}
