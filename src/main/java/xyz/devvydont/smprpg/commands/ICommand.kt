package xyz.devvydont.smprpg.commands

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack

/**
 * a simple command to be registered to the server during the bootstrapping phase.
 * Eventually, we will phase out the normal command logic and fully use the new [io.papermc.paper.command.brigadier.Commands]
 * package since it is more feature packed. Once we make the change, this interface should contain one method, and that
 * is the [com.mojang.brigadier.tree.LiteralCommandNode] getter.
 */
interface ICommand {
}

/**
 * Utilizes the Brigadier "tree" style command building system.
 * Start building a command and its arguments using the [io.papermc.paper.command.brigadier.Commands.literal] method.
 */
interface ICommandAdvanced : ICommand {

    /**
     * Get the root of the command builder.
     */
    fun getRoot() : LiteralCommandNode<CommandSourceStack>

}