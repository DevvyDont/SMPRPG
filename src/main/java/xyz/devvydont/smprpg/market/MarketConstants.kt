package xyz.devvydont.smprpg.market

object MarketConstants {

    // --- Auction House ---

    /** Percentage of starting price charged as a listing fee */
    const val AUCTION_LISTING_FEE_PERCENT = 0.02

    /** Percentage of final sale price taken as tax */
    const val AUCTION_SALE_TAX_PERCENT = 0.05

    /** Minimum starting price for an auction */
    const val AUCTION_MIN_PRICE = 10L

    /** Maximum starting price for an auction */
    const val AUCTION_MAX_PRICE = 10_000_000L

    /** Maximum active auctions per player */
    const val AUCTION_MAX_ACTIVE_PER_PLAYER = 15

    private const val MINUTES_MS = 60L * 1000
    private const val HOURS_MS = 60L * MINUTES_MS
    private const val DAYS_MS = 24L * HOURS_MS

    /** Minimum bid increment as a percentage of the current bid */
    const val AUCTION_MIN_BID_INCREMENT = 0.01

    /** Raise percentage options available when placing bids */
    val AUCTION_BID_RAISE_OPTIONS = listOf(
        0.01 to "+1%",
        0.02 to "+2%",
        0.05 to "+5%",
        0.10 to "+10%",
        0.25 to "+25%",
    )

    /** Default index into [AUCTION_BID_RAISE_OPTIONS] (2%) */
    const val AUCTION_DEFAULT_RAISE_INDEX = 1

    /** Days before unclaimed items are voided */
    const val AUCTION_CLAIM_EXPIRY_DAYS = 14

    /** Default duration index (24 Hours) */
    const val AUCTION_DEFAULT_DURATION_INDEX = 8

    /** Auction durations available for selection, in display order */
    val AUCTION_DURATIONS = listOf(
        5L * MINUTES_MS to "5 Minutes",
        10L * MINUTES_MS to "10 Minutes",
         1L * HOURS_MS   to "1 Hour",
         2L * HOURS_MS   to "2 Hours",
         3L * HOURS_MS   to "3 Hours",
         4L * HOURS_MS   to "4 Hours",
         6L * HOURS_MS   to "6 Hours",
         9L * HOURS_MS   to "9 Hours",
        12L * HOURS_MS   to "12 Hours",
        24L * HOURS_MS   to "24 Hours",
         2L * DAYS_MS    to "2 Days",
         3L * DAYS_MS    to "3 Days",
         5L * DAYS_MS    to "5 Days",
         7L * DAYS_MS    to "7 Days",
        14L * DAYS_MS    to "14 Days",
    )

    // --- Bazaar ---

    /** Spread between buy and sell price (sell price = buy price * this) */
    const val BAZAAR_SELL_SPREAD = 0.90

    /** Price multiplier for generating max price from Worth */
    const val BAZAAR_MAX_PRICE_MULTIPLIER = 3.0

    /** Price multiplier for generating min price from Worth */
    const val BAZAAR_MIN_PRICE_MULTIPLIER = 0.5

    /** Default maximum stock for bazaar items */
    const val BAZAAR_DEFAULT_MAX_STOCK = 10000

    /** Default starting stock ratio (percentage of max stock) */
    const val BAZAAR_DEFAULT_STARTING_STOCK_RATIO = 0.5

    /** Maximum quantity a player can buy/sell in a single transaction */
    const val BAZAAR_MAX_TRANSACTION_QUANTITY = 640

    // --- Administration ---

    /** Permission allowing a player to use the markets even while they are disabled. */
    const val MARKET_BYPASS_PERMISSION = "smprpg.market.bypass"

    /** Message shown to players who try to use a market that has been disabled by an admin. */
    const val MARKET_DISABLED_MESSAGE = "This market is currently disabled by an administrator."

    // --- Auto-save ---

    /** Auto-save interval in minutes */
    const val AUTO_SAVE_INTERVAL_MINUTES = 5L

    /** Auction expiry check interval in minutes */
    const val AUCTION_EXPIRY_CHECK_INTERVAL_MINUTES = 1L
}
