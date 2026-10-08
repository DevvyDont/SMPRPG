package xyz.devvydont.smprpg.commands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.extensions.getArg
import xyz.devvydont.smprpg.gui.player.InterfaceWardrobe

class CommandWardrobe(val name: String) : ICommand {

    /**
     * Get the root of the command builder.
     */
    override fun getRoot(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal(name)
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes { ctx ->
                    val player = ctx.getArg<PlayerSelectorArgumentResolver>("player").resolve(ctx.source).firstOrNull()
                    val executor = ctx.source.executor
                    if (player != null && executor is Player)
                        InterfaceWardrobe(null, executor, player).openMenu()
                    Command.SINGLE_SUCCESS
                })
            .executes{ ctx ->
                val player = ctx.source.sender
                if (player is Player)
                    InterfaceWardrobe(null, player, player).openMenu()
                Command.SINGLE_SUCCESS
            }
            .build()
    }

}