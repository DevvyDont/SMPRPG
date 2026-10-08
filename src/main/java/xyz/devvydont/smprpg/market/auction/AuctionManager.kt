package xyz.devvydont.smprpg.market.auction

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.market.MarketConstants
import xyz.devvydont.smprpg.market.MarketService
import xyz.devvydont.smprpg.market.storage.MarketDataStore
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import kotlin.math.roundToLong

/**
 * Core auction logic: create, bid, buy, expire, claim.
 */
class AuctionManager(private val dataStore: MarketDataStore) {

    private val economy get() = SMPRPG.getService(EconomyService::class.java)

    /**
     * Returns the disabled error message if the auction house is off and [player] cannot bypass it,
     * or null if the player is allowed to trade. Claiming items is intentionally never gated so
     * players can always retrieve what they already own.
     */
    private fun disabledMessageFor(player: Player): String? =
        if (SMPRPG.getService(MarketService::class.java).canUseAuction(player)) null
        else MarketConstants.MARKET_DISABLED_MESSAGE

    fun getActiveAuctions(): List<Auction> {
        return dataStore.auctionData.auctions.filter { it.status == AuctionStatus.ACTIVE && !it.isExpired() }
    }

    fun getActiveAuctionsByPlayer(playerUUID: String): List<Auction> {
        return getActiveAuctions().filter { it.sellerUUID == playerUUID }
    }

    fun getAuctionsByCategory(category: AuctionCategory): List<Auction> {
        return getActiveAuctions().filter { it.category == category }
    }

    fun searchAuctions(query: String): List<Auction> {
        val lowerQuery = query.lowercase().replace(" ", "").replace("_", "")
        return getActiveAuctions().filter {
            it.itemDisplayName.lowercase().replace(" ", "").contains(lowerQuery)
        }
    }

    fun getAuctionById(id: String): Auction? {
        return dataStore.auctionData.auctions.find { it.id == id }
    }

    fun getPlayerData(playerUUID: String): PlayerAuctionData {
        return dataStore.auctionData.playerData.getOrPut(playerUUID) { PlayerAuctionData() }
    }

    /**
     * Create a new auction listing.
     * Returns null if the listing was successful, or an error message string.
     */
    fun createAuction(
        seller: Player,
        item: ItemStack,
        type: AuctionType,
        startingPrice: Long,
        durationMs: Long
    ): String? {
        disabledMessageFor(seller)?.let { return it }
        val playerUUID = seller.uniqueId.toString()
        val activeCount = getActiveAuctionsByPlayer(playerUUID).size
        if (activeCount >= MarketConstants.AUCTION_MAX_ACTIVE_PER_PLAYER) {
            return "You have reached the maximum of ${MarketConstants.AUCTION_MAX_ACTIVE_PER_PLAYER} active auctions!"
        }

        if (startingPrice < MarketConstants.AUCTION_MIN_PRICE) {
            return "Minimum starting price is ${EconomyService.formatMoney(MarketConstants.AUCTION_MIN_PRICE)}!"
        }

        val listingFee = (startingPrice * MarketConstants.AUCTION_LISTING_FEE_PERCENT).roundToLong().coerceAtLeast(1)
        if (economy.getMoney(seller) < listingFee) {
            return "You need ${EconomyService.formatMoney(listingFee)} to pay the listing fee!"
        }

        val blueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(item)
        val classification = blueprint.itemClassification
        val category = AuctionCategory.fromClassification(classification)

        val displayName = blueprint.getItemName(item)
        val itemKey = if (blueprint is CustomItemBlueprint) blueprint.customItemType.key else item.type.name.lowercase()

        val auction = Auction(
            sellerUUID = playerUUID,
            sellerName = seller.name,
            itemData = Auction.serializeItem(item),
            itemDisplayName = displayName,
            itemKey = itemKey,
            category = category,
            type = type,
            startingPrice = startingPrice,
            currentBid = startingPrice,
            expiresAt = System.currentTimeMillis() + durationMs
        )

        economy.takeMoney(seller, listingFee.toDouble())
        seller.sendMessage(
            ComponentUtils.success(
                ComponentUtils.merge(
                    ComponentUtils.create("Auction listed! Listing fee: "),
                    ComponentUtils.create(EconomyService.formatMoney(listingFee), NamedTextColor.GOLD)
                )
            )
        )

        dataStore.auctionData.auctions.add(auction)
        dataStore.saveAuctions()
        return null
    }

    /**
     * Place a bid on an auction.
     * Returns null on success, or an error message string.
     */
    fun placeBid(bidder: Player, auction: Auction, bidAmount: Long): String? {
        disabledMessageFor(bidder)?.let { return it }
        if (auction.status != AuctionStatus.ACTIVE) {
            return "This auction is no longer active!"
        }

        if (auction.isExpired()) {
            return "This auction has expired!"
        }

        if (bidder.uniqueId.toString() == auction.sellerUUID) {
            return "You cannot bid on your own auction!"
        }

        if (auction.hasBids() && bidder.uniqueId.toString() == auction.highestBidderUUID) {
            return "You are already the highest bidder!"
        }

        val minBid = if (auction.hasBids()) {
            auction.currentBid + (auction.currentBid * MarketConstants.AUCTION_MIN_BID_INCREMENT).roundToLong().coerceAtLeast(1)
        } else {
            auction.startingPrice
        }

        if (bidAmount < minBid) {
            return "Minimum bid is ${EconomyService.formatMoney(minBid)}!"
        }

        if (economy.getMoney(bidder) < bidAmount) {
            return "You don't have enough coins!"
        }

        // Refund previous bidder
        if (auction.hasBids()) {
            val previousBidder = Bukkit.getOfflinePlayer(java.util.UUID.fromString(auction.highestBidderUUID!!))
            economy.addMoney(previousBidder, auction.currentBid.toDouble())

            val onlinePrevious = previousBidder.player
            if (onlinePrevious != null) {
                onlinePrevious.sendMessage(
                    ComponentUtils.alert(
                        ComponentUtils.merge(
                            ComponentUtils.create("You have been outbid on "),
                            ComponentUtils.create(auction.itemDisplayName, NamedTextColor.AQUA),
                            ComponentUtils.create("! "),
                            ComponentUtils.create(EconomyService.formatMoney(auction.currentBid), NamedTextColor.GOLD),
                            ComponentUtils.create(" has been refunded.")
                        )
                    )
                )
            }
        }

        economy.takeMoney(bidder, bidAmount.toDouble())
        auction.currentBid = bidAmount
        auction.highestBidderUUID = bidder.uniqueId.toString()
        auction.highestBidderName = bidder.name

        bidder.sendMessage(
            ComponentUtils.success(
                ComponentUtils.merge(
                    ComponentUtils.create("Bid of "),
                    ComponentUtils.create(EconomyService.formatMoney(bidAmount), NamedTextColor.GOLD),
                    ComponentUtils.create(" placed on "),
                    ComponentUtils.create(auction.itemDisplayName, NamedTextColor.AQUA)
                )
            )
        )

        // Notify the seller that someone bid on their auction
        val seller = Bukkit.getPlayer(java.util.UUID.fromString(auction.sellerUUID))
        if (seller != null) {
            seller.sendMessage(
                ComponentUtils.alert(
                    ComponentUtils.merge(
                        ComponentUtils.create(bidder.name, NamedTextColor.WHITE),
                        ComponentUtils.create(" placed a bid of "),
                        ComponentUtils.create(EconomyService.formatMoney(bidAmount), NamedTextColor.GOLD),
                        ComponentUtils.create(" on your "),
                        ComponentUtils.create(auction.itemDisplayName, NamedTextColor.AQUA),
                        ComponentUtils.create("!")
                    )
                )
            )
        }

        dataStore.saveAuctions()
        return null
    }

    /**
     * Execute a Buy It Now purchase.
     * Returns null on success, or an error message string.
     */
    fun buyItNow(buyer: Player, auction: Auction): String? {
        disabledMessageFor(buyer)?.let { return it }
        if (auction.type != AuctionType.BUY_IT_NOW) {
            return "This is not a Buy It Now auction!"
        }

        if (auction.status != AuctionStatus.ACTIVE) {
            return "This auction is no longer active!"
        }

        if (buyer.uniqueId.toString() == auction.sellerUUID) {
            return "You cannot buy your own auction!"
        }

        val price = auction.startingPrice
        if (economy.getMoney(buyer) < price) {
            return "You don't have enough coins!"
        }

        economy.takeMoney(buyer, price.toDouble())
        buyer.inventory.addItem(auction.getItemStack()).values.forEach { overflow ->
            buyer.world.dropItemNaturally(buyer.eyeLocation, overflow)
        }

        buyer.sendMessage(
            ComponentUtils.success(
                ComponentUtils.merge(
                    ComponentUtils.create("Purchased "),
                    ComponentUtils.create(auction.itemDisplayName, NamedTextColor.AQUA),
                    ComponentUtils.create(" for "),
                    ComponentUtils.create(EconomyService.formatMoney(price), NamedTextColor.GOLD)
                )
            )
        )

        val tax = (price * MarketConstants.AUCTION_SALE_TAX_PERCENT).roundToLong()
        val payout = price - tax
        val sellerData = getPlayerData(auction.sellerUUID)
        sellerData.addCoins(payout)

        val onlineSeller = Bukkit.getPlayer(java.util.UUID.fromString(auction.sellerUUID))
        if (onlineSeller != null) {
            onlineSeller.sendMessage(
                ComponentUtils.success(
                    ComponentUtils.merge(
                        ComponentUtils.create("Your "),
                        ComponentUtils.create(auction.itemDisplayName, NamedTextColor.AQUA),
                        ComponentUtils.create(" sold for "),
                        ComponentUtils.create(EconomyService.formatMoney(payout), NamedTextColor.GOLD),
                        ComponentUtils.create("! Use "),
                        ComponentUtils.create("/ah claim", NamedTextColor.GREEN),
                        ComponentUtils.create(" to collect.")
                    )
                )
            )
        }

        auction.status = AuctionStatus.COMPLETED
        dataStore.saveAuctions()
        return null
    }

    /**
     * Cancel an auction. Only the seller can cancel.
     */
    fun cancelAuction(seller: Player, auction: Auction): String? {
        if (seller.uniqueId.toString() != auction.sellerUUID) {
            return "You can only cancel your own auctions!"
        }

        if (auction.status != AuctionStatus.ACTIVE) {
            return "This auction is no longer active!"
        }

        if (auction.hasBids()) {
            return "You cannot cancel an auction that has bids!"
        }

        val sellerData = getPlayerData(auction.sellerUUID)
        sellerData.addItem(auction.itemData)
        auction.status = AuctionStatus.CANCELLED

        seller.sendMessage(
            ComponentUtils.success(
                ComponentUtils.merge(
                    ComponentUtils.create("Auction cancelled. Claim your "),
                    ComponentUtils.create(auction.itemDisplayName, NamedTextColor.AQUA),
                    ComponentUtils.create(" from "),
                    ComponentUtils.create("/ah claim", NamedTextColor.GREEN)
                )
            )
        )

        dataStore.saveAuctions()
        return null
    }

    /**
     * Process expired auctions. Should be called periodically.
     */
    fun processExpiredAuctions() {
        val now = System.currentTimeMillis()
        for (auction in dataStore.auctionData.auctions) {
            if (auction.status != AuctionStatus.ACTIVE) continue
            if (now < auction.expiresAt) continue

            if (auction.hasBids()) {
                // Auction completed with bids
                val tax = (auction.currentBid * MarketConstants.AUCTION_SALE_TAX_PERCENT).roundToLong()
                val payout = auction.currentBid - tax

                val sellerData = getPlayerData(auction.sellerUUID)
                sellerData.addCoins(payout)

                val bidderData = getPlayerData(auction.highestBidderUUID!!)
                bidderData.addItem(auction.itemData)

                auction.status = AuctionStatus.COMPLETED

                notifyPlayer(auction.sellerUUID,
                    ComponentUtils.success(
                        ComponentUtils.merge(
                            ComponentUtils.create("Your auction for "),
                            ComponentUtils.create(auction.itemDisplayName, NamedTextColor.AQUA),
                            ComponentUtils.create(" sold for "),
                            ComponentUtils.create(EconomyService.formatMoney(payout), NamedTextColor.GOLD),
                            ComponentUtils.create("! Use "),
                            ComponentUtils.create("/ah claim", NamedTextColor.GREEN),
                            ComponentUtils.create(" to collect.")
                        )
                    )
                )
                notifyPlayer(auction.highestBidderUUID!!,
                    ComponentUtils.success(
                        ComponentUtils.merge(
                            ComponentUtils.create("You won the auction for "),
                            ComponentUtils.create(auction.itemDisplayName, NamedTextColor.AQUA),
                            ComponentUtils.create("! Use "),
                            ComponentUtils.create("/ah claim", NamedTextColor.GREEN),
                            ComponentUtils.create(" to collect.")
                        )
                    )
                )
            } else {
                // No bids - return item to seller
                val sellerData = getPlayerData(auction.sellerUUID)
                sellerData.addItem(auction.itemData)
                auction.status = AuctionStatus.EXPIRED_UNCLAIMED

                notifyPlayer(auction.sellerUUID,
                    ComponentUtils.alert(
                        ComponentUtils.merge(
                            ComponentUtils.create("Your auction for "),
                            ComponentUtils.create(auction.itemDisplayName, NamedTextColor.AQUA),
                            ComponentUtils.create(" expired with no bids. Use "),
                            ComponentUtils.create("/ah claim", NamedTextColor.GREEN),
                            ComponentUtils.create(" to reclaim it.")
                        )
                    )
                )
            }
        }

        dataStore.saveAuctions()
    }

    /**
     * Claim all unclaimed items and coins for a player.
     * Returns the number of items claimed and coins received.
     */
    fun claimItems(player: Player): Pair<Int, Long> {
        val data = getPlayerData(player.uniqueId.toString())
        var itemsClaimed = 0
        val coinsClaimed = data.unclaimedCoins

        if (coinsClaimed > 0) {
            economy.addMoney(player, coinsClaimed.toDouble())
            data.unclaimedCoins = 0
        }

        val iterator = data.unclaimedItems.iterator()
        while (iterator.hasNext()) {
            val itemData = iterator.next()
            try {
                val bytes = java.util.Base64.getDecoder().decode(itemData)
                val item = ItemStack.deserializeBytes(bytes)
                player.inventory.addItem(item).values.forEach { overflow ->
                    player.world.dropItemNaturally(player.eyeLocation, overflow)
                }
                iterator.remove()
                itemsClaimed++
            } catch (e: Exception) {
                SMPRPG.plugin.logger.warning("Failed to deserialize auction item for ${player.name}: ${e.message}")
            }
        }

        if (itemsClaimed > 0 || coinsClaimed > 0) {
            dataStore.saveAuctions()
        }

        return Pair(itemsClaimed, coinsClaimed)
    }

    fun hasUnclaimedItems(playerUUID: String): Boolean {
        return getPlayerData(playerUUID).hasUnclaimed()
    }

    private fun notifyPlayer(playerUUID: String, message: net.kyori.adventure.text.Component) {
        val player = Bukkit.getPlayer(java.util.UUID.fromString(playerUUID))
        player?.sendMessage(message)
    }
}
