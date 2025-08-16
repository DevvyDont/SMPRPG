package xyz.devvydont.smprpg.commands.admin

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.extensions.getArg
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Admin command to interact with the economy of the server.
 * In most cases, the economy plugin will provide this for you, but this command can
 * be used as a fallback just in case.
 */
class CommandEcoAdmin : ICommand {

    /**
     * Get the root of the command builder.
     */
    override fun getRoot(): LiteralCommandNode<CommandSourceStack> {

        fun playerValue(exec: Command<CommandSourceStack>) =
            Commands.argument("player", ArgumentTypes.players()) // singular: one player name
                .then(
                    Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1e21))
                        .suggests { _, b ->
                            b.suggest("0")
                                .suggest("1000")
                                .suggest("50000")
                                .suggest("250000")
                                .suggest("1000000")
                                .buildFuture()
                        }
                        .executes(exec) // attach to the deepest node
                )

        return Commands.literal("economy")
            .requires { it.sender.hasPermission("smprpg.command.eco") }
            .then(
                Commands.literal("add")
                    .then(playerValue(this::executeAdd))
            )
            .then(
                Commands.literal("set")
                    .then(playerValue(this::executeSet))
            )
            .then(
                Commands.literal("take")
                    .then(playerValue(this::executeRemove))
            )
            .build()
    }

    private fun executeAdd(ctx: CommandContext<CommandSourceStack>): Int {
        val eco = SMPRPG.getService(EconomyService::class.java)
        val amount = ctx.getArg<Double>("value")
        var success = 0
        for (player in ctx.getArg<PlayerSelectorArgumentResolver>("player").resolve(ctx.source))
            if (eco.addMoney(player, amount))
                success += 1

        ctx.source.sender.sendMessage(ComponentUtils.success("$success players have had ${EconomyService.formatMoney(amount.toLong())} added to their balance!"))
        return Command.SINGLE_SUCCESS
    }

    private fun executeSet(ctx: CommandContext<CommandSourceStack>): Int {
        val eco = SMPRPG.getService(EconomyService::class.java)
        val amount = ctx.getArg<Double>("value")
        var success = 0
        for (player in ctx.getArg<PlayerSelectorArgumentResolver>("player").resolve(ctx.source))
            if (eco.setMoney(player, amount))
                success += 1

        ctx.source.sender.sendMessage(ComponentUtils.success("$success players have had their balance set to ${EconomyService.formatMoney(amount.toLong())}!"))
        return Command.SINGLE_SUCCESS
    }

    private fun executeRemove(ctx: CommandContext<CommandSourceStack>): Int {
        val eco = SMPRPG.getService(EconomyService::class.java)
        val amount = ctx.getArg<Double>("value")
        var success = 0
        for (player in ctx.getArg<PlayerSelectorArgumentResolver>("player").resolve(ctx.source))
            if (eco.takeMoney(player, amount))
                success += 1

        ctx.source.sender.sendMessage(ComponentUtils.success("$success players have had ${EconomyService.formatMoney(amount.toLong())} taken from their balance!"))
        return Command.SINGLE_SUCCESS
    }


}