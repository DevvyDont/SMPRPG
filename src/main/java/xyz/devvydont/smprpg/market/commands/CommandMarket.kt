package xyz.devvydont.smprpg.market.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.format.NamedTextColor
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.market.MarketService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Admin command to enable or disable the bazaar and auction house independently.
 * While a market is disabled, regular players cannot open or transact in it; players with the
 * bypass permission (or operators) are unaffected. The state persists across restarts.
 *
 * Usage:
 *   /market                       — show current status
 *   /market bazaar enable|disable
 *   /market auction enable|disable
 */
class CommandMarket : ICommand {

    private enum class Market(val displayName: String) {
        BAZAAR("Bazaar"),
        AUCTION("Auction House")
    }

    override fun getRoot(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("market")
            .requires { ctx -> ctx.sender.hasPermission(ADMIN_PERMISSION) || ctx.sender.isOp }
            .executes { ctx ->
                sendStatus(ctx.source)
                Command.SINGLE_SUCCESS
            }
            .then(buildToggle(Market.BAZAAR))
            .then(buildToggle(Market.AUCTION))
            .build()
    }

    private fun buildToggle(market: Market): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal(market.name.lowercase())
            .then(
                Commands.literal("enable").executes { ctx ->
                    setEnabled(ctx.source, market, true)
                    Command.SINGLE_SUCCESS
                }
            )
            .then(
                Commands.literal("disable").executes { ctx ->
                    setEnabled(ctx.source, market, false)
                    Command.SINGLE_SUCCESS
                }
            )
            .build()
    }

    private fun setEnabled(source: CommandSourceStack, market: Market, enabled: Boolean) {
        val service = SMPRPG.getService(MarketService::class.java)
        when (market) {
            Market.BAZAAR -> service.bazaarEnabled = enabled
            Market.AUCTION -> service.auctionEnabled = enabled
        }

        val state = if (enabled) "enabled" else "disabled"
        source.sender.sendMessage(
            ComponentUtils.success(
                ComponentUtils.merge(
                    ComponentUtils.create("${market.displayName} is now "),
                    ComponentUtils.create(state, if (enabled) NamedTextColor.GREEN else NamedTextColor.RED),
                    ComponentUtils.create(".")
                )
            )
        )
    }

    private fun sendStatus(source: CommandSourceStack) {
        val service = SMPRPG.getService(MarketService::class.java)
        source.sender.sendMessage(
            ComponentUtils.merge(
                statusLine(Market.BAZAAR, service.bazaarEnabled),
                ComponentUtils.create("\n"),
                statusLine(Market.AUCTION, service.auctionEnabled)
            )
        )
    }

    private fun statusLine(market: Market, enabled: Boolean) = ComponentUtils.merge(
        ComponentUtils.create("${market.displayName}: ", NamedTextColor.GRAY),
        ComponentUtils.create(
            if (enabled) "ENABLED" else "DISABLED",
            if (enabled) NamedTextColor.GREEN else NamedTextColor.RED
        )
    )

    companion object {
        private const val ADMIN_PERMISSION = "smprpg.command.market"
    }
}
