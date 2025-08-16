package xyz.devvydont.smprpg.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.literal
import org.bukkit.entity.Player

/**
 * a simple command to be registered to the server during the bootstrapping phase.
 * Eventually, we will phase out the normal command logic and fully use the new [io.papermc.paper.command.brigadier.Commands]
 * package since it is more feature packed. Once we make the change, this interface should contain one method, and that
 * is the [com.mojang.brigadier.tree.LiteralCommandNode] getter.
 */
interface ICommandBase {
}

/**
 * Utilizes the Brigadier "tree" style command building system.
 * Start building a command and its arguments using the [io.papermc.paper.command.brigadier.Commands.literal] method.
 */
interface ICommand : ICommandBase {

    /**
     * Get the root of the command builder.
     */
    fun getRoot() : LiteralCommandNode<CommandSourceStack>

    /**
     * A shortcut to utilize for simple commands that perform a small action on a player.
     * There will come times when you want a command to perform a simple action, such as open up a menu.
     * You can use this class to bypass the type checking for the sender/executor class, and only run some
     * action on an executor if they are a player.
     * For example:
     * command = SimplePlayerCommand("test", p -> TestMenu().open(p))
     * Optionally, you can also provide a permission to execute the command. A null value will assume that a player is
     * always allowed to execute the command.
     */
    class SimplePlayerCommand(private val name: String, private val action: (Player) -> Unit, private val permission: String? = null) : ICommand {
        override fun getRoot(): LiteralCommandNode<CommandSourceStack> {
            return literal(name)
                .requires { ctx -> permission == null || ctx.sender.hasPermission(permission) || ctx.sender.isOp }
                .executes { ctx ->
                    val sender = ctx.source.executor
                    if (sender is Player)
                        action(sender)
                    return@executes Command.SINGLE_SUCCESS
                }
                .build()
        }
    }

}