package xyz.devvydont.smprpg.services

import net.kyori.adventure.text.format.NamedTextColor
import net.milkbowl.vault.economy.Economy
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import java.text.DecimalFormat
import kotlin.math.roundToLong

/**
 * Acts as a middleman between performing transactions for player currency
 */
class EconomyService : IService {
    private var economy: Economy? = null

    @Throws(RuntimeException::class)
    override fun setup() {
        // If vault isn't installed, we cannot function correctly.
        val plugin = plugin
        if (plugin.server.pluginManager.getPlugin("Vault") == null) {
            plugin.logger.severe("Vault is not installed. Please install Vault")
            throw RuntimeException("Vault is not installed. Please install Vault")
        }

        // We need to make sure the economy class is valid
        val provider = plugin.server.servicesManager.getRegistration(Economy::class.java)
        if (provider == null) {
            plugin.logger.severe("Failed to detect Economy service, is Vault installed correctly?")
            throw RuntimeException("Failed to detect Economy service. Vault is not installed correctly.")
        }

        this.economy = provider.getProvider()
        plugin.logger.info("Successfully hooked into Vault Economy service")
    }

    override fun cleanup() {
        plugin.logger.info("Cleaning up EconomyService")
    }

    /**
     * Give a player money. This is for generating money into the economy
     * Will always round to the nearest whole number
     * @param player The player to give money to, doesn't need to be online at the moment
     * @param amount The amount of coins to give a player
     * @return boolean, true if successful
     */
    fun addMoney(player: OfflinePlayer, amount: Double): Boolean {
        val response = economy!!.depositPlayer(player, amount.roundToLong().toDouble())
        plugin.logger.finest(
            String.format(
                "Server has paid %s %.0f coins, balance is now %.0f",
                player.name,
                response.amount,
                response.balance
            )
        )
        return response.transactionSuccess()
    }

    /**
     * Take a player's money as a transaction to the server. This is used for various methods of fees and money
     * transfer across the server. Will always round to the nearest whole number
     * @param player The player to take money from, doesn't need to be online at the moment
     * @param amount The amount of coins to take from a player
     * @return boolean, true if successful
     */
    fun takeMoney(player: OfflinePlayer, amount: Double): Boolean {
        val response = economy!!.withdrawPlayer(player, amount.roundToLong().toDouble())
        plugin.logger.info(
            String.format(
                "Server has attempted to take %s's %.0f coins, balance is now %.0f (%s-%s)",
                player.name,
                response.amount,
                response.balance,
                response.type,
                response.errorMessage
            )
        )
        return response.transactionSuccess()
    }

    /**
     * A variation of EconomyService#takeMoney()
     * This version also calls takeMoney(), but also alerts the user in chat that their balance was updated.
     * As a consequence of this, an online player is required so that we can send them a message.
     *
     *
     * This method also assumes that the player already has enough money, and this method may behave unexpectedly if
     * the process of checking for sufficient funds is skipped.
     * @param player The player to take money from.
     * @param cost The amount of money to take from the player.
     */
    fun spendMoney(player: Player, cost: Long) {
        this.takeMoney(player, cost.toDouble())
        player.sendMessage(
            ComponentUtils.merge(
                ComponentUtils.create(formatMoney(cost), NamedTextColor.GOLD),
                ComponentUtils.create(" has been taken from your account. Your balance is now "),
                ComponentUtils.create(formatMoney(getMoney(player)), NamedTextColor.GOLD)
            )
        )
    }

    /**
     * Sets the player's balance.
     * Returns true if the operation was successful, false otherwise.
     */
    fun setMoney(player: Player, amount: Double): Boolean {
        val balance = getMoney(player)
        val success: Boolean = if (balance < amount)
            addMoney(player, amount-balance);
        else
            takeMoney(player, balance-amount);
        return success
    }

    /**
     * See a player's balance, this is represented in "coins" and is always an integer.
     * @param player Player to query balance of
     * @return an int represented rounded balance of a player
     */
    fun getMoney(player: Player?): Long {
        return economy!!.getBalance(player).roundToLong()
    }

    /**
     * See a player's balance, this is represented in "coins" and is always an integer.
     * @param player Player to query balance of
     * @return an int represented rounded balance of a player
     */
    fun getMoney(player: OfflinePlayer?): Long {
        return economy!!.getBalance(player).roundToLong()
    }

    /**
     * Formats a string for display across the plugin, this method specifically some number
     * @param player The player you want to format money for.
     * @return A clean user readable formatted representation of the money amount.
     */
    fun formatMoney(player: OfflinePlayer?): String {
        return formatMoney(getMoney(player))
    }


    companion object {
        /**
         * Formats a string for display across the plugin, this method specifically some number
         * @param amount The amount you want to format.
         * @return A clean user readable formatted representation of the money amount.
         */
        @JvmStatic
        fun formatMoney(amount: Int): String {
            val number = DecimalFormat("#,###,###,###,###").format(amount.toLong())
            return String.format("%s%s", number, Symbols.COIN)
        }

        /**
         * Formats a string for display across the plugin, this method specifically some number
         * @param amount The amount you want to format.
         * @return A clean user readable formatted representation of the money amount.
         */
        @JvmStatic
        fun formatMoney(amount: Long): String {
            val number = DecimalFormat("###,###,###,###,###,###,###").format(amount)
            return String.format("%s%s", number, Symbols.COIN)
        }

        /**
         * Formats a string for display across the plugin, this method specifically some number
         * @param amount The amount you want to format.
         * @return A clean user readable formatted representation of the money amount.
         */
        @JvmStatic
        fun formatMoney(amount: Double): String {
            val number = DecimalFormat("###,###,###,###,###,###,###").format(amount)
            return String.format("%s%s", number, Symbols.COIN)
        }
    }
}
