package xyz.devvydont.smprpg.util.crafting

import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.blueprints.economy.CustomItemCoin
import xyz.devvydont.smprpg.items.blueprints.resources.scrolls.DynamicEnchantingScroll
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.ItemService
import java.util.*
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

object ItemUtil {
    val COINS: Array<CustomItemType> = arrayOf(
        CustomItemType.COPPER_COIN,
        CustomItemType.SILVER_COIN,
        CustomItemType.GOLD_COIN,
        CustomItemType.PLATINUM_COIN,
        CustomItemType.ENCHANTED_COIN
    )

    /**
     * Given an amount of emeralds, return how many coins we should return
     *
     * @param emeralds
     */
    fun emeraldsToCoins(emeralds: Int): Int {
        return (emeralds + 12).toDouble().pow(2.5).roundToInt()
    }

    /**
     * Given a number of emeralds, return an item stack of coins that is worth
     *
     * @return
     */
    fun determineBestVillagerCurrencyConversion(itemService: ItemService, emeralds: Int): ItemStack {
        val coinTarget = emeraldsToCoins(emeralds)

        // Loop through all the coins types and do math to see which coin we can make have the highest stack size
        // while still capturing all the value of the emeralds.
        for (coinType in COINS) {
            val coin = itemService.getBlueprint(coinType) as CustomItemCoin
            val coinStack = coin.generate()

            // If this coin is unable to capture the full value of the emeralds in 75, skip
            if (coin.getWorth(coinStack) * 75 < coinTarget) continue

            // We have a good coin to use.
            val stackSize = ceil(coinTarget.toDouble() / coin.getWorth(coinStack)).toInt()
            val coinItem = coin.generate()
            coinItem.amount = stackSize
            return coinItem
        }

        // Oh boy we failed to find a suitable coin to satisfy a trade worth 50 million coins.....
        SMPRPG.plugin.logger.severe(
            String.format(
                "Failed to convert trade of %d emeralds to coins. Defaulting to 60m coins!",
                emeralds
            )
        )
        val coin = itemService.getBlueprint(CustomItemType.ENCHANTED_COIN).generate()
        coin.amount = 99
        return coin
    }

    /**
     * Given an amount of coins to drop, determine the best type of coin and stack size to use to estimate the amount
     * of coins desired.
     *
     * @param itemService
     * @param level
     * @return
     */
    fun getOptimalCoinStack(itemService: ItemService, level: Int): ItemStack {
        val typeIndex = level / 10
        val type = COINS[min(max(0, typeIndex), 2)]

        // We have a good coin to use.
        val coinItem = itemService.getBlueprint(type).generate()

        if (type.ordinal >= 2) return coinItem

        // Add some random variation to the drop
        val stackSize = (Math.random() * 4).toInt() + 1
        coinItem.amount = stackSize
        return coinItem
    }

    fun getOptimalCoinStacks(itemService: ItemService, amount: Int): MutableCollection<ItemStack?> {
        val coins = ArrayList<ItemStack?>()

        if (amount <= 0) return coins

        // Define a hard cap so we don't return some whack collection of coins.
        if (amount > CustomItemCoin.getCoinValue(CustomItemType.ENCHANTED_COIN) * 99) {
            coins.add(itemService.getBlueprint(CustomItemType.ENCHANTED_COIN).generate(99))
            return coins
        }

        var remaining = amount
        // Loop through the coin tiers backwards and add as many coins as possible that divide by the amount.
        for (coinType in Arrays.stream<CustomItemType>(COINS).toList().reversed()) {
            val coinWorth = CustomItemCoin.getCoinValue(coinType)
            if (coinWorth <= 0) continue


            // How many times does the worth of the coin divide into the remaining amount?
            val divisibleTimes = remaining / coinWorth
            if (divisibleTimes <= 0) continue

            // Create an item stack for this coin type and subtract the worth from the remaining balance to fulfil.
            coins.add(itemService.getBlueprint(coinType).generate(divisibleTimes))
            remaining -= (divisibleTimes * coinWorth)
        }

        return coins
    }

    /**
     * Given an item stack that is meant to be present in a villager trade, return a replacement for the item
     * if it is needed. If the item is valid, it is simply returned back.
     * Mainly used to convert emeralds into coins.
     *
     * @param itemService
     * @param itemStack
     * @return
     */
    fun checkVillagerItem(itemService: ItemService, itemStack: ItemStack?): ItemStack {
        var item: ItemStack? = itemStack
        // Convert any poisonous potatoes into our SMPRPG items.
        if (itemStack?.type == Material.POISONOUS_POTATO) {
            val itemKey = itemStack.persistentDataContainer
                .getOrDefault(itemService.itemTypeKey, PersistentDataType.STRING, "")
            if (!itemKey.isEmpty()) {
                item = itemService.getCustomItem(itemKey)
                item!!.amount = itemStack.amount
                itemService.ensureItemStackUpdated(item)
            }
        } else if (itemStack?.type == Material.ENCHANTED_BOOK) {
            val enchants = item!!.getData(DataComponentTypes.STORED_ENCHANTMENTS)?.enchantments()
            if (enchants != null) {
                // Grab random enchant from the book. That will become our scroll enchantment
                val customEnch = SMPRPG.getService(EnchantmentService::class.java).getEnchantment(enchants.keys.random())!!
                item = DynamicEnchantingScroll.getScrollWithEnchantment(customEnch)
            }
        }

        // We are only checking for vanilla emeralds, eliminate any other case
        val blueprint = itemService.getBlueprint(item!!)

        // Custom items are fine to stay
        if (blueprint.isCustom) return item

        // If the vanilla item we have is not an emerald, it is fine to stay
        if (item.type != Material.EMERALD) return item

        // This emerald should be converted to coins
        return determineBestVillagerCurrencyConversion(itemService, item.amount)
    }
}
