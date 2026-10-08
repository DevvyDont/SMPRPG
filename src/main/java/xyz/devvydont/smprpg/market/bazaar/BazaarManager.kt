package xyz.devvydont.smprpg.market.bazaar

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.base.VanillaItemBlueprint
import xyz.devvydont.smprpg.items.blueprints.resources.VanillaResource
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.market.MarketConstants
import xyz.devvydont.smprpg.market.MarketService
import xyz.devvydont.smprpg.market.storage.MarketDataStore
import xyz.devvydont.smprpg.recipe.CompressionGraph
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.crafting.CompressionRecipeMember
import xyz.devvydont.smprpg.util.crafting.MaterialWrapper
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import kotlin.math.min
import kotlin.math.roundToLong

/**
 * Represents a single item entry in a bulk sell-all transaction.
 * For compressed items, [tier] and [compressionAmount] describe the compression tier being sold.
 * Stock adjustments are always at the base item level (quantity * compressionAmount).
 */
data class BazaarSellEntry(
    val bazaarItem: BazaarItem,
    val displayName: String,
    val quantity: Int,
    val payout: Long,
    val tier: CompressionRecipeMember? = null,
    val compressionAmount: Int = 1
)

/**
 * Represents a resolved bazaar item: either a custom plugin item or a vanilla Minecraft material.
 */
private sealed class ResolvedItem {
    data class Custom(val type: CustomItemType) : ResolvedItem()
    data class Vanilla(val material: Material) : ResolvedItem()
}

private fun resolveItem(key: String): ResolvedItem? {
    val customType = CustomItemType.entries.find { it.getKey() == key }
    if (customType != null) return ResolvedItem.Custom(customType)
    return try {
        ResolvedItem.Vanilla(Material.valueOf(key.uppercase()))
    } catch (_: IllegalArgumentException) {
        null
    }
}

/**
 * Core bazaar logic: buy, sell, price queries.
 * Items are auto-generated from CustomItemType entries with Worth > 0 and SellableResource handler.
 */
class BazaarManager(private val dataStore: MarketDataStore) {

    private val economy get() = SMPRPG.getService(EconomyService::class.java)
    private var compressionFlowCache: Map<String, List<CompressionRecipeMember>>? = null
    private var categoryTreeCache: List<BazaarCategoryNode>? = null

    /**
     * Runtime source of truth: structure merged with current stock. Rebuilt from the data store on
     * startup and on [reloadStructure]. Stock mutations happen here and are flushed via [persistStock].
     */
    private val items = mutableMapOf<String, BazaarItem>()

    /**
     * Returns the disabled error message if the bazaar is off and [player] cannot bypass it,
     * or null if the player is allowed to trade. Used to gate every transaction entry point.
     */
    private fun disabledMessageFor(player: Player): String? =
        if (SMPRPG.getService(MarketService::class.java).canUseBazaar(player)) null
        else MarketConstants.MARKET_DISABLED_MESSAGE

    fun findCompressionFlow(bazaarKey: String): List<CompressionRecipeMember>? {
        val cache = compressionFlowCache ?: buildCompressionFlowCache().also { compressionFlowCache = it }
        return cache[bazaarKey]
    }

    private fun buildCompressionFlowCache(): Map<String, List<CompressionRecipeMember>> {
        val itemService = SMPRPG.getService(ItemService::class.java)
        val result = mutableMapOf<String, List<CompressionRecipeMember>>()

        // Walk each compression chain root forward through the unified registry's compression graph.
        for (rootId in CompressionGraph.roots()) {
            val flow = CompressionGraph.flowFromRoot(rootId).mapNotNull { (id, inputAmount) ->
                wrapperOf(id, itemService)?.let { CompressionRecipeMember(it, inputAmount) }
            }
            if (flow.isEmpty()) continue
            result[flow.first().material.key()] = flow
        }
        return result
    }

    /** Convert a `namespace:path` identifier into the [MaterialWrapper] the bazaar flow members use. */
    private fun wrapperOf(id: String, itemService: ItemService): MaterialWrapper? {
        val idx = id.indexOf(':')
        if (idx < 0) return null
        val namespace = id.substring(0, idx)
        val path = id.substring(idx + 1)
        return when (namespace) {
            "minecraft" -> Material.matchMaterial("minecraft:$path")?.let { MaterialWrapper(it) }
            "smprpg" -> itemService.getItemTypeFromKey(path)?.let { MaterialWrapper(it) }
            else -> null
        }
    }

    // ── Category classification (private bootstrap helper) ──────────────

    private fun classifyItem(name: String): String {
        val lower = name.lowercase()
        for ((path, keywords) in SUBCATEGORY_KEYWORDS) {
            if (keywords.any { lower.contains(it) }) return path
        }
        for ((path, keywords) in CATEGORY_KEYWORDS) {
            if (keywords.any { lower.contains(it) }) return path
        }
        return DEFAULT_CATEGORY
    }

    // ── Initialization & runtime state ──────────────────────────────────

    /**
     * Generates default structure from [CustomItemType] entries if no structure exists yet,
     * then builds the runtime item map. Safe to call on every startup.
     */
    fun initializeDefaults() {
        if (dataStore.bazaarStructure.items.isEmpty()) {
            generateDefaults()
            dataStore.saveBazaarStructure()
            SMPRPG.plugin.logger.info("Initialized ${dataStore.bazaarStructure.items.size} default bazaar items")
        }

        buildRuntimeItems()
    }

    /**
     * Populates the structure with every tradeable commodity: custom items whose blueprint is
     * [ISellable] with a commodity classification and a positive worth, plus vanilla materials in
     * [VanillaResource]'s worth map (excluding cosmetic color/shape variants). Prices derive from
     * worth via the configured multipliers; stock uses value-inverse tiers.
     */
    private fun generateDefaults() {
        val itemService = SMPRPG.getService(ItemService::class.java)
        val structures = dataStore.bazaarStructure.items

        for (type in CustomItemType.entries) {
            val blueprint = itemService.getBlueprint(type)
            if (blueprint.itemClassification !in COMMODITY_CLASSIFICATIONS) continue
            if (blueprint !is ISellable) continue
            val worth = sampleWorth(type, blueprint).toLong()
            if (worth <= 0) continue
            val key = type.getKey()
            structures[key] = buildDefaultStructure(key, type.ItemName, classifyItem(type.name), worth)
        }

        for ((material, worth) in VanillaResource.getMaterialWorthMap()) {
            if (worth <= 0 || isExcludedVanillaMaterial(material)) continue
            val key = material.name.lowercase()
            if (structures.containsKey(key)) continue
            structures[key] = buildDefaultStructure(key, vanillaDisplayName(material), classifyItem(key), worth.toLong())
        }
    }

    /** Worth of a single unit of a custom item, resolved via its blueprint (0 if it can't be generated). */
    private fun sampleWorth(type: CustomItemType, sellable: ISellable): Int {
        return try {
            val sample = ItemService.generate(type)
            sample.amount = 1
            sellable.getWorth(sample)
        } catch (_: IllegalArgumentException) {
            0
        }
    }

    private fun buildDefaultStructure(key: String, displayName: String, category: String, worth: Long): BazaarItemStructure {
        val minPrice = (worth * MarketConstants.BAZAAR_MIN_PRICE_MULTIPLIER).roundToLong().coerceAtLeast(1)
        val maxPrice = (worth * MarketConstants.BAZAAR_MAX_PRICE_MULTIPLIER).roundToLong().coerceAtLeast(minPrice + 1)
        return BazaarItemStructure(key, displayName, category, minPrice, maxPrice, stockTierFor(worth))
    }

    private fun isExcludedVanillaMaterial(material: Material): Boolean {
        val name = material.name
        if (name in VANILLA_EXCLUDED_MATERIALS) return true
        return VANILLA_EXCLUDED_SUFFIXES.any { name.endsWith(it) }
    }

    private fun vanillaDisplayName(material: Material): String =
        material.name.split("_").joinToString(" ") { it.lowercase().replaceFirstChar(Char::uppercase) }

    /**
     * Rebuilds the runtime item map by merging structure with persisted stock. Structure entries
     * without a stock entry are seeded at the default starting ratio. Seeded stock is flushed back
     * to the data store so the stock file stays in sync with the structure.
     */
    private fun buildRuntimeItems() {
        items.clear()
        for ((key, structure) in dataStore.bazaarStructure.items) {
            val stock = dataStore.bazaarData.stock[key]
                ?: (structure.maxStock * MarketConstants.BAZAAR_DEFAULT_STARTING_STOCK_RATIO).toInt()
            items[key] = BazaarItem(structure, stock)
        }
        persistStock()
    }

    /** Copies runtime stock levels back into the data store, dropping stale keys. */
    fun persistStock() {
        dataStore.bazaarData.stock.keys.retainAll(items.keys)
        for ((key, item) in items) {
            dataStore.bazaarData.stock[key] = item.currentStock
        }
    }

    /**
     * Re-reads the structure and stock files from disk, rebuilds the runtime map (preserving live
     * stock by flushing it first), and invalidates derived caches. Backs the `/bz reload` command.
     * Returns the number of items loaded.
     */
    fun reloadStructure(): Int {
        persistStock()
        dataStore.saveBazaarData()
        dataStore.load()
        buildRuntimeItems()
        invalidateCategoryTree()
        compressionFlowCache = null
        return items.size
    }

    // ── Category tree ───────────────────────────────────────────────────

    fun getRootCategories(): List<BazaarCategoryNode> {
        return categoryTreeCache ?: BazaarCategoryNode.buildTree(getAllItems(), CATEGORY_DISPLAY_ORDER)
            .also { categoryTreeCache = it }
    }

    fun getItemsByPath(path: String): List<BazaarItem> {
        return items.values.filter { it.category == path }
    }

    fun invalidateCategoryTree() {
        categoryTreeCache = null
    }

    // ── Item queries ────────────────────────────────────────────────────

    fun getAllItems(): List<BazaarItem> {
        return items.values.toList()
    }

    fun getItem(key: String): BazaarItem? {
        return items[key]
    }

    fun searchItems(query: String): List<BazaarItem> {
        val lowerQuery = query.lowercase().replace(" ", "").replace("_", "")
        return getAllItems().filter {
            it.displayName.lowercase().replace(" ", "").contains(lowerQuery)
        }
    }

    // ── Buy / sell ──────────────────────────────────────────────────────

    /**
     * Buy items from the bazaar.
     * Returns null on success, or an error message string.
     */
    fun buyItems(buyer: Player, bazaarItem: BazaarItem, quantity: Int): String? {
        disabledMessageFor(buyer)?.let { return it }
        if (quantity <= 0 || quantity > MarketConstants.BAZAAR_MAX_TRANSACTION_QUANTITY) {
            return "Invalid quantity! (1-${MarketConstants.BAZAAR_MAX_TRANSACTION_QUANTITY})"
        }

        val availableStock = if (bazaarItem.canGoOutOfStock) bazaarItem.currentStock else quantity
        val actualQuantity = quantity.coerceAtMost(availableStock)

        if (actualQuantity <= 0) {
            return "This item is out of stock!"
        }

        val totalCost = BazaarPricingEngine.calculateBulkBuyCost(bazaarItem, actualQuantity)
        if (economy.getMoney(buyer) < totalCost) {
            return "You need ${EconomyService.formatMoney(totalCost)} to buy $actualQuantity!"
        }

        val resolved = resolveItem(bazaarItem.key) ?: return "Item type not found!"

        economy.takeMoney(buyer, totalCost.toDouble())
        bazaarItem.currentStock = if (bazaarItem.canGoOutOfStock) {
            (bazaarItem.currentStock - actualQuantity).coerceAtLeast(0)
        } else {
            bazaarItem.currentStock - actualQuantity
        }

        when (resolved) {
            is ResolvedItem.Custom -> giveItems(buyer, resolved.type, actualQuantity)
            is ResolvedItem.Vanilla -> giveVanillaItems(buyer, resolved.material, actualQuantity)
        }

        buyer.sendMessage(
            ComponentUtils.success(
                ComponentUtils.merge(
                    ComponentUtils.create("Bought ${actualQuantity}x "),
                    ComponentUtils.create(bazaarItem.displayName, NamedTextColor.AQUA),
                    ComponentUtils.create(" for "),
                    ComponentUtils.create(EconomyService.formatMoney(totalCost), NamedTextColor.GOLD)
                )
            )
        )

        invalidateCategoryTree()
        persistStock()
        dataStore.saveBazaarData()
        return null
    }

    /**
     * Sell items to the bazaar.
     * Returns null on success, or an error message string.
     */
    fun sellItems(seller: Player, bazaarItem: BazaarItem, quantity: Int): String? {
        disabledMessageFor(seller)?.let { return it }
        if (quantity <= 0 || quantity > MarketConstants.BAZAAR_MAX_TRANSACTION_QUANTITY) {
            return "Invalid quantity! (1-${MarketConstants.BAZAAR_MAX_TRANSACTION_QUANTITY})"
        }

        val resolved = resolveItem(bazaarItem.key) ?: return "Item type not found!"

        val available = when (resolved) {
            is ResolvedItem.Custom -> countPlayerItems(seller, resolved.type)
            is ResolvedItem.Vanilla -> countVanillaItems(seller, resolved.material)
        }
        if (available < quantity) {
            return "You only have ${available}x ${bazaarItem.displayName}!"
        }

        val totalPayout = BazaarPricingEngine.calculateBulkSellPayout(bazaarItem, quantity)
        when (resolved) {
            is ResolvedItem.Custom -> removeItems(seller, resolved.type, quantity)
            is ResolvedItem.Vanilla -> removeVanillaItems(seller, resolved.material, quantity)
        }

        bazaarItem.currentStock = if (bazaarItem.canGoOutOfStock) {
            (bazaarItem.currentStock + quantity).coerceAtMost(bazaarItem.maxStock)
        } else {
            bazaarItem.currentStock + quantity
        }
        economy.addMoney(seller, totalPayout.toDouble())

        seller.sendMessage(
            ComponentUtils.success(
                ComponentUtils.merge(
                    ComponentUtils.create("Sold ${quantity}x "),
                    ComponentUtils.create(bazaarItem.displayName, NamedTextColor.AQUA),
                    ComponentUtils.create(" for "),
                    ComponentUtils.create(EconomyService.formatMoney(totalPayout), NamedTextColor.GOLD)
                )
            )
        )

        invalidateCategoryTree()
        persistStock()
        dataStore.saveBazaarData()
        return null
    }

    /**
     * Buy compressed-tier items from the bazaar.
     * Stock is deducted at the base item level (quantity * compressionAmount).
     * Returns null on success, or an error message string.
     */
    fun buyCompressedItems(
        buyer: Player,
        bazaarItem: BazaarItem,
        tier: CompressionRecipeMember,
        compressionAmount: Int,
        quantity: Int
    ): String? {
        disabledMessageFor(buyer)?.let { return it }
        val baseQuantity = quantity * compressionAmount
        if (baseQuantity <= 0) return "Invalid quantity!"

        val availableStock = if (bazaarItem.canGoOutOfStock) bazaarItem.currentStock else baseQuantity
        if (availableStock < baseQuantity) return "Not enough stock!"

        val totalCost = BazaarPricingEngine.calculateBulkBuyCost(bazaarItem, baseQuantity)
        if (economy.getMoney(buyer) < totalCost) {
            return "You need ${EconomyService.formatMoney(totalCost)} to buy $quantity!"
        }

        economy.takeMoney(buyer, totalCost.toDouble())
        bazaarItem.currentStock = if (bazaarItem.canGoOutOfStock) {
            (bazaarItem.currentStock - baseQuantity).coerceAtLeast(0)
        } else {
            bazaarItem.currentStock - baseQuantity
        }
        giveCompressedItems(buyer, tier.material, quantity)

        buyer.sendMessage(
            ComponentUtils.success(
                ComponentUtils.merge(
                    ComponentUtils.create("Bought ${quantity}x "),
                    ComponentUtils.create(tier.material.name(), NamedTextColor.AQUA),
                    ComponentUtils.create(" for "),
                    ComponentUtils.create(EconomyService.formatMoney(totalCost), NamedTextColor.GOLD)
                )
            )
        )

        invalidateCategoryTree()
        persistStock()
        dataStore.saveBazaarData()
        return null
    }

    /**
     * Sell compressed-tier items to the bazaar.
     * Stock is added at the base item level (quantity * compressionAmount).
     * Returns null on success, or an error message string.
     */
    fun sellCompressedItems(
        seller: Player,
        bazaarItem: BazaarItem,
        tier: CompressionRecipeMember,
        compressionAmount: Int,
        quantity: Int
    ): String? {
        disabledMessageFor(seller)?.let { return it }
        val baseQuantity = quantity * compressionAmount
        if (baseQuantity <= 0) return "Invalid quantity!"

        val available = countCompressedItems(seller, tier.material)
        if (available < quantity) {
            return "You only have ${available}x ${tier.material.name()}!"
        }

        val totalPayout = BazaarPricingEngine.calculateBulkSellPayout(bazaarItem, baseQuantity)
        removeCompressedItems(seller, tier.material, quantity)
        bazaarItem.currentStock = if (bazaarItem.canGoOutOfStock) {
            (bazaarItem.currentStock + baseQuantity).coerceAtMost(bazaarItem.maxStock)
        } else {
            bazaarItem.currentStock + baseQuantity
        }
        economy.addMoney(seller, totalPayout.toDouble())

        seller.sendMessage(
            ComponentUtils.success(
                ComponentUtils.merge(
                    ComponentUtils.create("Sold ${quantity}x "),
                    ComponentUtils.create(tier.material.name(), NamedTextColor.AQUA),
                    ComponentUtils.create(" for "),
                    ComponentUtils.create(EconomyService.formatMoney(totalPayout), NamedTextColor.GOLD)
                )
            )
        )

        invalidateCategoryTree()
        persistStock()
        dataStore.saveBazaarData()
        return null
    }

    // ── Sell-all ────────────────────────────────────────────────────────

    /**
     * Calculates what the player could sell across all bazaar items in their inventory.
     * Includes both base items and compressed tiers.
     * Returns only entries where the player has at least 1 of that item.
     */
    fun calculateSellAll(player: Player): List<BazaarSellEntry> {
        val entries = mutableListOf<BazaarSellEntry>()
        for (bazaarItem in getAllItems()) {
            val resolved = resolveItem(bazaarItem.key) ?: continue
            val baseAvailable = when (resolved) {
                is ResolvedItem.Custom -> countPlayerItems(player, resolved.type)
                is ResolvedItem.Vanilla -> countVanillaItems(player, resolved.material)
            }
            if (baseAvailable > 0) {
                val quantity = min(baseAvailable, MarketConstants.BAZAAR_MAX_TRANSACTION_QUANTITY)
                val payout = BazaarPricingEngine.calculateBulkSellPayout(bazaarItem, quantity)
                entries.add(BazaarSellEntry(bazaarItem, bazaarItem.displayName, quantity, payout))
            }

            val flow = findCompressionFlow(bazaarItem.key) ?: continue
            for (tierIndex in 1 until flow.size) {
                val tier = flow[tierIndex]
                val compAmt = calculateCompressionAmount(flow, tierIndex)
                val available = countCompressedItems(player, tier.material)
                if (available <= 0) continue

                val quantity = min(available, MarketConstants.BAZAAR_MAX_TRANSACTION_QUANTITY)
                val baseQuantity = quantity * compAmt
                val payout = BazaarPricingEngine.calculateBulkSellPayout(bazaarItem, baseQuantity)
                entries.add(BazaarSellEntry(bazaarItem, tier.material.name(), quantity, payout, tier, compAmt))
            }
        }
        return entries
    }

    /**
     * Executes a sell-all transaction for the given entries.
     * Re-validates quantities before removing items.
     * Returns null on success, or an error message string.
     */
    fun executeSellAll(player: Player, entries: List<BazaarSellEntry>): String? {
        disabledMessageFor(player)?.let { return it }
        if (entries.isEmpty()) return "Nothing to sell!"

        var totalPayout = 0L
        var totalItemsSold = 0

        for (entry in entries) {
            val isCompressed = entry.tier != null

            val available = if (isCompressed) {
                countCompressedItems(player, entry.tier!!.material)
            } else {
                val resolved = resolveItem(entry.bazaarItem.key) ?: continue
                when (resolved) {
                    is ResolvedItem.Custom -> countPlayerItems(player, resolved.type)
                    is ResolvedItem.Vanilla -> countVanillaItems(player, resolved.material)
                }
            }
            val actualQuantity = min(available, entry.quantity)
            if (actualQuantity <= 0) continue

            val baseQuantity = actualQuantity * entry.compressionAmount
            val payout = BazaarPricingEngine.calculateBulkSellPayout(entry.bazaarItem, baseQuantity)

            if (isCompressed) {
                removeCompressedItems(player, entry.tier!!.material, actualQuantity)
            } else {
                val resolved = resolveItem(entry.bazaarItem.key) ?: continue
                when (resolved) {
                    is ResolvedItem.Custom -> removeItems(player, resolved.type, actualQuantity)
                    is ResolvedItem.Vanilla -> removeVanillaItems(player, resolved.material, actualQuantity)
                }
            }

            entry.bazaarItem.currentStock = if (entry.bazaarItem.canGoOutOfStock) {
                (entry.bazaarItem.currentStock + baseQuantity).coerceAtMost(entry.bazaarItem.maxStock)
            } else {
                entry.bazaarItem.currentStock + baseQuantity
            }
            totalPayout += payout
            totalItemsSold += actualQuantity
        }

        if (totalItemsSold == 0) return "No items could be sold!"

        economy.addMoney(player, totalPayout.toDouble())
        player.sendMessage(
            ComponentUtils.success(
                ComponentUtils.merge(
                    ComponentUtils.create("Sold "),
                    ComponentUtils.create("$totalItemsSold items", NamedTextColor.AQUA),
                    ComponentUtils.create(" for "),
                    ComponentUtils.create(EconomyService.formatMoney(totalPayout), NamedTextColor.GOLD)
                )
            )
        )

        invalidateCategoryTree()
        persistStock()
        dataStore.saveBazaarData()
        return null
    }

    // ── Inventory helpers ───────────────────────────────────────────────

    fun countCompressedItems(player: Player, tierMaterial: MaterialWrapper): Int {
        return if (tierMaterial.isCustom) {
            countPlayerItems(player, tierMaterial.custom)
        } else {
            countVanillaItems(player, tierMaterial.vanilla)
        }
    }

    private fun removeCompressedItems(player: Player, tierMaterial: MaterialWrapper, quantity: Int) {
        if (tierMaterial.isCustom) {
            removeItems(player, tierMaterial.custom, quantity)
        } else {
            removeVanillaItems(player, tierMaterial.vanilla, quantity)
        }
    }

    private fun giveCompressedItems(player: Player, tierMaterial: MaterialWrapper, quantity: Int) {
        if (tierMaterial.isCustom) {
            giveItems(player, tierMaterial.custom, quantity)
        } else {
            giveVanillaItems(player, tierMaterial.vanilla, quantity)
        }
    }

    private fun countPlayerItems(player: Player, type: CustomItemType): Int {
        val itemService = SMPRPG.getService(ItemService::class.java)
        var count = 0
        for (item in player.inventory.contents) {
            if (item == null) continue
            val blueprint = itemService.getBlueprint(item)
            if (blueprint is CustomItemBlueprint && blueprint.customItemType == type) {
                count += item.amount
            }
        }
        return count
    }

    private fun removeItems(player: Player, type: CustomItemType, quantity: Int) {
        val itemService = SMPRPG.getService(ItemService::class.java)
        var remaining = quantity
        for (slot in 0 until player.inventory.size) {
            if (remaining <= 0) break
            val item = player.inventory.getItem(slot) ?: continue
            val blueprint = itemService.getBlueprint(item)
            if (blueprint !is CustomItemBlueprint || blueprint.customItemType != type) continue

            if (item.amount <= remaining) {
                remaining -= item.amount
                player.inventory.setItem(slot, null)
            } else {
                item.amount -= remaining
                remaining = 0
            }
        }
    }

    private fun giveItems(player: Player, type: CustomItemType, quantity: Int) {
        val maxStackSize = type.DisplayMaterial.maxStackSize
        var remaining = quantity
        while (remaining > 0) {
            val batchSize = remaining.coerceAtMost(maxStackSize)
            val item = ItemService.generate(type)
            item.amount = batchSize
            player.inventory.addItem(item).values.forEach { overflow ->
                player.world.dropItemNaturally(player.eyeLocation, overflow)
            }
            remaining -= batchSize
        }
    }

    private fun countVanillaItems(player: Player, material: Material): Int {
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

    private fun removeVanillaItems(player: Player, material: Material, quantity: Int) {
        val itemService = SMPRPG.getService(ItemService::class.java)
        var remaining = quantity
        for (slot in 0 until player.inventory.size) {
            if (remaining <= 0) break
            val item = player.inventory.getItem(slot) ?: continue
            if (item.type != material) continue
            val blueprint = itemService.getBlueprint(item)
            if (blueprint !is VanillaItemBlueprint) continue

            if (item.amount <= remaining) {
                remaining -= item.amount
                player.inventory.setItem(slot, null)
            } else {
                item.amount -= remaining
                remaining = 0
            }
        }
    }

    private fun giveVanillaItems(player: Player, material: Material, quantity: Int) {
        val maxStackSize = material.maxStackSize
        var remaining = quantity
        while (remaining > 0) {
            val batchSize = remaining.coerceAtMost(maxStackSize)
            val item = ItemService.generate(material)
            item.amount = batchSize
            player.inventory.addItem(item).values.forEach { overflow ->
                player.world.dropItemNaturally(player.eyeLocation, overflow)
            }
            remaining -= batchSize
        }
    }

    companion object {

        private const val DEFAULT_CATEGORY = "Miscellaneous"

        /** Item classifications that represent fungible, stackable commodities (vs. gear). */
        private val COMMODITY_CLASSIFICATIONS = setOf(
            ItemClassification.MATERIAL,
            ItemClassification.ITEM,
            ItemClassification.BLOCK,
            ItemClassification.CONSUMABLE,
        )

        /** Vanilla materials skipped during default generation (utility items, not commodities). */
        private val VANILLA_EXCLUDED_MATERIALS = setOf("NAME_TAG", "SADDLE")

        /**
         * Vanilla material name suffixes skipped during default generation: cosmetic color matrices
         * (wool/terracotta/glass/carpet/dye/concrete) and crafted shape variants (slab/stairs/wall).
         */
        private val VANILLA_EXCLUDED_SUFFIXES = listOf(
            "_WOOL", "_TERRACOTTA", "_STAINED_GLASS", "_STAINED_GLASS_PANE", "_CARPET",
            "_DYE", "_CONCRETE", "_CONCRETE_POWDER", "_SLAB", "_STAIRS", "_WALL", "_FENCE",
        )

        private val CATEGORY_DISPLAY_ORDER = listOf(
            "Mining", "Combat", "Farming", "Fishing", "Foraging", "Crafting", "Magic",
            "Bosses", "Consumables", "Augmenting", "Decorative", "Miscellaneous"
        )

        // Checked in order; first keyword substring match wins. More specific paths come first.
        private val SUBCATEGORY_KEYWORDS = mapOf(
            "Mining/Alloys" to listOf("ingot", "nugget", "iron_block", "gold_block", "copper_block", "netherite_block", "tridentite"),
            "Mining/Ores" to listOf("raw", "ore", "ancient_debris"),
            "Mining/Gems" to listOf("diamond", "emerald", "lapis", "amethyst", "quartz", "onyx"),
            "Mining/Dusts" to listOf("coal", "charcoal", "redstone", "glowstone", "sulfur"),
            "Combat/Nether" to listOf("blaze", "cinderite", "magma_cream", "nether_star", "wither"),
            "Combat/End" to listOf("ender", "echo", "shulker"),
            "Combat/Overworld" to listOf("bone", "rotten", "gunpowder", "string", "spider", "slime", "feather", "phantom", "arrow", "necrotic"),
            "Crafting/Rare" to listOf("singularity", "hexed"),
            "Crafting/Components" to listOf("barnacle", "tendril", "resin", "erratic", "minnow", "dissipating", "midnight_hide"),
            "Mining/Stones/Nether" to listOf("netherrack", "nether_brick", "basalt", "soul_sand", "soul_soil", "blackstone"),
            "Mining/Stones/Building" to listOf("end_stone", "purpur", "obsidian", "prismarine", "magma_block", "clay", "glass", "terracotta"),
            "Mining/Stones/Generic" to listOf("stone", "cobble", "deepslate", "granite", "diorite", "andesite", "tuff", "calcite", "sand", "dirt", "gravel", "flint", "brick", "dripstone"),
        )

        private val CATEGORY_KEYWORDS = mapOf(
            "Farming" to listOf("seed", "sapling", "mushroom", "wheat", "bread", "hay", "carrot", "potato", "beetroot", "sugar", "cane", "pumpkin", "melon", "cactus", "kelp", "wart", "cocoa", "cookie", "beef", "mutton", "chicken", "porkchop", "egg", "leather", "stick"),
            "Fishing" to listOf("fish", "cod", "salmon", "tropical", "ink_sac", "lily", "nautilus", "shark", "filament", "fiber", "holomoku", "heart_of_the_sea", "turtle", "tripwire"),
            "Foraging" to listOf("log", "wood", "plank", "leaves", "stem", "hyphae", "bamboo"),
            "Magic" to listOf("displacement", "warp"),
            "Bosses" to listOf("dragon", "summoning", "heart_of_the_void", "pluto", "jupiter", "smoldering", "draconic", "viscera", "warlock", "spellbound", "brains", "amalgamation", "necroplasm"),
        )

        /** Value-inverse maxStock tiers tuned for a 10-40 player server. */
        fun stockTierFor(worth: Long): Int = when {
            worth <= 5 -> 50_000
            worth <= 25 -> 20_000
            worth <= 150 -> 8_000
            worth <= 1_000 -> 3_000
            worth <= 10_000 -> 1_000
            worth <= 100_000 -> 400
            else -> 200
        }

        fun calculateCompressionAmount(flow: List<CompressionRecipeMember>, tierIndex: Int): Int {
            var amount = 1
            for (i in 0 until tierIndex) {
                amount *= flow[i].amount
            }
            return amount
        }
    }
}
