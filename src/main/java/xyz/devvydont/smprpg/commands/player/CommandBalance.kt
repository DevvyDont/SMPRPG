package xyz.devvydont.smprpg.commands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.extensions.getArg
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class CommandBalance(val name: String) : ICommand {

    /**
     * Get the root of the command builder.
     */
    override fun getRoot(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal(name)
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes { ctx ->
                    val player = ctx.getArg<PlayerSelectorArgumentResolver>("player").resolve(ctx.source).firstOrNull()
                    if (player != null)
                        performBalanceQuery(ctx.source.sender, player)
                    Command.SINGLE_SUCCESS
                })
            .executes{ ctx ->
                if (ctx.source.sender is Player)
                    performBalanceQuery(ctx.source.sender, ctx.source.sender as Player)
                Command.SINGLE_SUCCESS
            }
            .build()
    }

    private fun performBalanceQuery(interested: CommandSender, player: Player) {
        val bal = SMPRPG.getService(EconomyService::class.java).getMoney(player)
        val name = if (interested == player)
            ComponentUtils.merge(ComponentUtils.create("You", NamedTextColor.BLUE), ComponentUtils.create(" have"))
        else
            ComponentUtils.merge(ComponentUtils.create(player.name, NamedTextColor.GREEN), ComponentUtils.create(" has"))

        interested.sendMessage(ComponentUtils.alert(ComponentUtils.merge(
            name,
            ComponentUtils.SPACE,
            ComponentUtils.create("a balance of "),
            ComponentUtils.create(EconomyService.formatMoney(bal), NamedTextColor.GOLD)
        ), NamedTextColor.GOLD))
    }
}