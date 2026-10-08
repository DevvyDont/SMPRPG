package xyz.devvydont.smprpg.market.gui.bazaar

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.InterfaceUtil
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.base.VanillaItemBlueprint
import xyz.devvydont.smprpg.market.MarketConstants
import xyz.devvydont.smprpg.market.MarketService
import xyz.devvydont.smprpg.market.bazaar.BazaarDemandDisplay
import xyz.devvydont.smprpg.market.bazaar.BazaarItem
import xyz.devvydont.smprpg.market.bazaar.BazaarManager
import xyz.devvydont.smprpg.market.bazaar.BazaarPricingEngine
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.crafting.CompressionRecipeMember
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Buy/sell interface for a single bazaar item.
 * Shows pricing info, stock levels, and buy/sell buttons at various quantities.
 */
class MenuBazaarItemView(
    player: Player,
    private val bazaarItem: BazaarItem,
    parentMenu: MenuBase? = null,
    private val compressionFlow: List<CompressionRecipeMember>? = null,
    private val tierIndex: Int = 0
) : MenuBase(player, ROWS, parentMenu) {

    private val isCompressed get() = compressionFlow != null && tierIndex > 0
    private val compressionAmount get() = compressionFlow?.let { BazaarManager.calculateCompressionAmount(it, tierIndex) } ?: 1
    private val tier get() = compressionFlow?.get(tierIndex)
    private val tierMaterial get() = tier?.material

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        val title = if (isCompressed) "Bazaar - ${tierMaterial?.name()}" else "Bazaar - ${bazaarItem.displayName}"
        event.titleOverride(Component.text(title))
        render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    private fun render() {
        setBorderFull()
        setBackButton()

        val compAmt = compressionAmount
        val displayName = if (isCompressed) tierMaterial!!.name() else bazaarItem.displayName
        val buyPrice = BazaarPricingEngine.calculateBulkBuyCost(bazaarItem, compAmt)
        val sellPrice = BazaarPricingEngine.calculateBulkSellPayout(bazaarItem, compAmt)

        // Info display in center
        val displayItemType = if (isCompressed) {
            tierMaterial?.let { mat ->
                if (mat.isCustom) mat.custom else null
            }
        } else {
            CustomItemType.entries.find { it.getKey() == bazaarItem.key }
        }
        val displayBazaarKey = if (isCompressed) tierMaterial?.key() else bazaarItem.key

        val loreLines = mutableListOf(
            ComponentUtils.EMPTY,
            ComponentUtils.merge(
                ComponentUtils.create("Buy Price: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(buyPrice), NamedTextColor.GOLD)
            ),
            ComponentUtils.merge(
                ComponentUtils.create("Sell Price: ", NamedTextColor.GRAY),
                ComponentUtils.create(EconomyService.formatMoney(sellPrice), NamedTextColor.GOLD)
            ),
            ComponentUtils.EMPTY
        )

        if (isCompressed) {
            loreLines.addAll(BazaarDemandDisplay.buildCompressedDemandLore(player, bazaarItem, compAmt))
            loreLines.add(ComponentUtils.merge(
                ComponentUtils.create("= ${compAmt}x ", NamedTextColor.DARK_GRAY),
                ComponentUtils.create(bazaarItem.displayName, NamedTextColor.GRAY)
            ))
        } else {
            loreLines.addAll(BazaarDemandDisplay.buildDemandLore(player, bazaarItem))
        }

        val infoItem = MenuBazaarCategory.createBazaarDisplayItem(
            displayItemType, displayName, loreLines, bazaarKey = displayBazaarKey
        )
        setSlot(INFO_SLOT, infoItem)

        // Buy buttons
        for ((index, qty) in BUY_QUANTITIES.withIndex()) {
            val totalCost = BazaarPricingEngine.calculateBulkBuyCost(bazaarItem, qty * compAmt)
            val canBuy = !bazaarItem.canGoOutOfStock || bazaarItem.currentStock >= qty * compAmt

            val buyItem = InterfaceUtil.getNamedItemWithDescription(
                if (canBuy) Material.EMERALD else Material.BARRIER,
                ComponentUtils.create("Buy ${qty}x", NamedTextColor.GREEN),
                ComponentUtils.merge(
                    ComponentUtils.create("Cost: ", NamedTextColor.GRAY),
                    ComponentUtils.create(EconomyService.formatMoney(totalCost), NamedTextColor.GOLD)
                ),
                if (!canBuy)
                    ComponentUtils.create("Not enough stock!", NamedTextColor.RED)
                else
                    ComponentUtils.create("Click to buy!", NamedTextColor.YELLOW)
            )
            if (canBuy) buyItem.amount = qty

            setButton(BUY_SLOTS[index], buyItem) { _: InventoryClickEvent ->
                if (!canBuy) {
                    playInvalidAnimation()
                    return@setButton
                }
                val bazaarManager = SMPRPG.getService(MarketService::class.java).bazaarManager
                val error = if (isCompressed) {
                    bazaarManager.buyCompressedItems(player, bazaarItem, tier!!, compAmt, qty)
                } else {
                    bazaarManager.buyItems(player, bazaarItem, qty)
                }
                if (error != null) {
                    player.sendMessage(ComponentUtils.error(error))
                    playInvalidAnimation()
                } else {
                    playSuccessAnimation()
                    render()
                }
            }
        }

        // Sell buttons — count the tier's actual item
        val bazaarManager = SMPRPG.getService(MarketService::class.java).bazaarManager
        val playerCount = if (isCompressed) {
            bazaarManager.countCompressedItems(player, tierMaterial!!)
        } else {
            val itemTypeForCount = CustomItemType.entries.find { it.getKey() == bazaarItem.key }
            val vanillaMaterial = if (itemTypeForCount == null) {
                try { Material.valueOf(bazaarItem.key.uppercase()) } catch (_: IllegalArgumentException) { null }
            } else null
            when {
                itemTypeForCount != null -> countPlayerItems(itemTypeForCount)
                vanillaMaterial != null -> countVanillaItems(vanillaMaterial)
                else -> 0
            }
        }

        for ((index, qty) in SELL_QUANTITIES.withIndex()) {
            val totalPayout = BazaarPricingEngine.calculateBulkSellPayout(bazaarItem, qty * compAmt)
            val canSell = playerCount >= qty

            val sellItem = InterfaceUtil.getNamedItemWithDescription(
                if (canSell) Material.GOLD_INGOT else Material.BARRIER,
                ComponentUtils.create("Sell ${qty}x", NamedTextColor.GOLD),
                ComponentUtils.merge(
                    ComponentUtils.create("Payout: ", NamedTextColor.GRAY),
                    ComponentUtils.create(EconomyService.formatMoney(totalPayout), NamedTextColor.GOLD)
                ),
                ComponentUtils.merge(
                    ComponentUtils.create("You have: ", NamedTextColor.GRAY),
                    ComponentUtils.create("$playerCount", if (canSell) NamedTextColor.GREEN else NamedTextColor.RED)
                ),
                if (!canSell)
                    ComponentUtils.create("Not enough items!", NamedTextColor.RED)
                else
                    ComponentUtils.create("Click to sell!", NamedTextColor.YELLOW)
            )
            if (canSell) sellItem.amount = qty

            setButton(SELL_SLOTS[index], sellItem) { _: InventoryClickEvent ->
                if (!canSell) {
                    playInvalidAnimation()
                    return@setButton
                }
                val bm = SMPRPG.getService(MarketService::class.java).bazaarManager
                val error = if (isCompressed) {
                    bm.sellCompressedItems(player, bazaarItem, tier!!, compAmt, qty)
                } else {
                    bm.sellItems(player, bazaarItem, qty)
                }
                if (error != null) {
                    player.sendMessage(ComponentUtils.error(error))
                    playInvalidAnimation()
                } else {
                    playSuccessAnimation()
                    render()
                }
            }
        }

        // Sell All button
        if (playerCount > 0) {
            val sellAllQty = if (isCompressed) playerCount else playerCount.coerceAtMost(MarketConstants.BAZAAR_MAX_TRANSACTION_QUANTITY)
            val sellAllPayout = BazaarPricingEngine.calculateBulkSellPayout(bazaarItem, sellAllQty * compAmt)
            val sellAllItem = InterfaceUtil.getNamedItemWithDescription(
                Material.HOPPER,
                ComponentUtils.create("Sell All (${sellAllQty}x)", NamedTextColor.GOLD),
                ComponentUtils.merge(
                    ComponentUtils.create("Payout: ", NamedTextColor.GRAY),
                    ComponentUtils.create(EconomyService.formatMoney(sellAllPayout), NamedTextColor.GOLD)
                ),
                ComponentUtils.create("Click to sell all!", NamedTextColor.YELLOW)
            )

            setButton(SELL_ALL_SLOT, sellAllItem) { _: InventoryClickEvent ->
                val bm = SMPRPG.getService(MarketService::class.java).bazaarManager
                val currentCount = if (isCompressed) {
                    bm.countCompressedItems(player, tierMaterial!!)
                } else {
                    val typeForCount = CustomItemType.entries.find { it.getKey() == bazaarItem.key }
                    val vanMat = if (typeForCount == null) {
                        try { Material.valueOf(bazaarItem.key.uppercase()) } catch (_: IllegalArgumentException) { null }
                    } else null
                    when {
                        typeForCount != null -> countPlayerItems(typeForCount)
                        vanMat != null -> countVanillaItems(vanMat)
                        else -> 0
                    }
                }
                if (currentCount <= 0) {
                    playInvalidAnimation()
                    return@setButton
                }
                val sellQty = currentCount.coerceAtMost(MarketConstants.BAZAAR_MAX_TRANSACTION_QUANTITY)
                val error = if (isCompressed) {
                    bm.sellCompressedItems(player, bazaarItem, tier!!, compAmt, sellQty)
                } else {
                    bm.sellItems(player, bazaarItem, sellQty)
                }
                if (error != null) {
                    player.sendMessage(ComponentUtils.error(error))
                    playInvalidAnimation()
                } else {
                    playSuccessAnimation()
                    render()
                }
            }
        }
    }

    private fun countPlayerItems(type: CustomItemType): Int {
        val itemService = SMPRPG.getService(ItemService::class.java)
        var count = 0
        for (item in player.inventory.contents) {
            if (item == null) continue
            val blueprint = itemService.getBlueprint(item)
            if (blueprint is xyz.devvydont.smprpg.items.base.CustomItemBlueprint && blueprint.customItemType == type) {
                count += item.amount
            }
        }
        return count
    }

    private fun countVanillaItems(material: Material): Int {
        val itemService = SMPRPG.getService(ItemService::class.java)
        var count = 0
        for (item in player.inventory.contents) {
            if (item == null || item.type != material) continue
            val blueprint = itemService.getBlueprint(item)
            if (blueprint is VanillaItemBlueprint) {
                count += item.amount
            }
        }
        return count
    }

    companion object {
        private const val ROWS = 5
        private const val INFO_SLOT = 13

        // Row 2 layout: [border] [buy1] [buy16] [buy64] [gap] [sell1] [sell16] [sell64] [border]
        private val BUY_QUANTITIES = intArrayOf(1, 16, 64)
        private val BUY_SLOTS = intArrayOf(19, 20, 21)

        private val SELL_QUANTITIES = intArrayOf(1, 16, 64)
        private val SELL_SLOTS = intArrayOf(23, 24, 25)

        // Sell all: row 3 center
        private const val SELL_ALL_SLOT = 31
    }
}
