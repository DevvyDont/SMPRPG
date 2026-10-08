package xyz.devvydont.smprpg.market

import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitRunnable
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.market.auction.AuctionManager
import xyz.devvydont.smprpg.market.bazaar.BazaarManager
import xyz.devvydont.smprpg.market.storage.MarketDataStore
import xyz.devvydont.smprpg.services.IService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.time.TickTime

class MarketService : IService, Listener {

    lateinit var auctionManager: AuctionManager
        private set

    lateinit var bazaarManager: BazaarManager
        private set

    private lateinit var dataStore: MarketDataStore
    private var autoSaveTask: BukkitRunnable? = null
    private var expiryCheckTask: BukkitRunnable? = null

    override fun setup() {
        val plugin = SMPRPG.plugin

        dataStore = MarketDataStore(plugin.dataFolder)
        dataStore.load()

        auctionManager = AuctionManager(dataStore)
        bazaarManager = BazaarManager(dataStore)
        bazaarManager.initializeDefaults()

        autoSaveTask = object : BukkitRunnable() {
            override fun run() {
                bazaarManager.persistStock()
                dataStore.save()
            }
        }
        autoSaveTask!!.runTaskTimerAsynchronously(
            plugin,
            TickTime.minutes(MarketConstants.AUTO_SAVE_INTERVAL_MINUTES),
            TickTime.minutes(MarketConstants.AUTO_SAVE_INTERVAL_MINUTES)
        )

        expiryCheckTask = object : BukkitRunnable() {
            override fun run() {
                auctionManager.processExpiredAuctions()
            }
        }
        expiryCheckTask!!.runTaskTimer(
            plugin,
            TickTime.minutes(MarketConstants.AUCTION_EXPIRY_CHECK_INTERVAL_MINUTES),
            TickTime.minutes(MarketConstants.AUCTION_EXPIRY_CHECK_INTERVAL_MINUTES)
        )

        plugin.logger.info("Market service initialized")
    }

    override fun cleanup() {
        autoSaveTask?.cancel()
        expiryCheckTask?.cancel()
        bazaarManager.persistStock()
        dataStore.save()
        SMPRPG.plugin.logger.info("Market service cleaned up")
    }

    // ── Admin enable/disable toggles ────────────────────────────────────

    var bazaarEnabled: Boolean
        get() = dataStore.marketSettings.bazaarEnabled
        set(value) {
            dataStore.marketSettings.bazaarEnabled = value
            dataStore.saveSettings()
        }

    var auctionEnabled: Boolean
        get() = dataStore.marketSettings.auctionEnabled
        set(value) {
            dataStore.marketSettings.auctionEnabled = value
            dataStore.saveSettings()
        }

    /** True if the player may use the bazaar (it is enabled, or they have the bypass permission). */
    fun canUseBazaar(player: Player): Boolean = bazaarEnabled || canBypass(player)

    /** True if the player may use the auction house (it is enabled, or they have the bypass permission). */
    fun canUseAuction(player: Player): Boolean = auctionEnabled || canBypass(player)

    /**
     * Guards a bazaar menu open: returns true if allowed, otherwise notifies the player and
     * returns false so the caller can abort opening the menu.
     */
    fun tryOpenBazaar(player: Player): Boolean = guard(canUseBazaar(player), player)

    /**
     * Guards an auction house menu open: returns true if allowed, otherwise notifies the player and
     * returns false so the caller can abort opening the menu.
     */
    fun tryOpenAuction(player: Player): Boolean = guard(canUseAuction(player), player)

    private fun canBypass(player: Player): Boolean =
        player.isOp || player.hasPermission(MarketConstants.MARKET_BYPASS_PERMISSION)

    private fun guard(allowed: Boolean, player: Player): Boolean {
        if (!allowed) player.sendMessage(ComponentUtils.error(MarketConstants.MARKET_DISABLED_MESSAGE))
        return allowed
    }

    @EventHandler
    @Suppress("unused")
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val playerUUID = player.uniqueId.toString()

        val cmd = "/ah claim"
        val cmdHoverTooltip = ComponentUtils.merge(
            ComponentUtils.create("Click to run "),
            ComponentUtils.create(cmd, NamedTextColor.GREEN),
            ComponentUtils.create("!"),
        )

        if (auctionManager.hasUnclaimedItems(playerUUID)) {
            val msg = ComponentUtils.merge(
                ComponentUtils.create("You have unclaimed auction items! Use ", NamedTextColor.GOLD),
                ComponentUtils.create(cmd, NamedTextColor.GREEN)
                    .clickEvent(ClickEvent.runCommand(cmd))
                    .hoverEvent(cmdHoverTooltip),
                ComponentUtils.create(" to collect them.", NamedTextColor.GOLD)
            )
            player.sendMessage(ComponentUtils.alert(msg, NamedTextColor.GREEN))
            player.playSound(player.location, Sound.ENTITY_VILLAGER_YES, 1f, 1f)
        }
    }
}
