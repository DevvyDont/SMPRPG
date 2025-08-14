package xyz.devvydont.smprpg.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import org.bukkit.Sound
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.extensions.getArg
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * A simple command that allows someone with admin privileges to "steal" a player's inventory and ender chest.
 * This is more of a silly command that can be used in the event of someone needing their items taken from them.
 */
class CommandSteal : ICommandAdvanced {

    override fun getRoot() : LiteralCommandNode<CommandSourceStack> {
        val root = Commands.literal("steal")
            .requires { ctx -> ctx.sender.hasPermission("smprpg.commands.steal") }
            .then(
                Commands.argument("player", ArgumentTypes.player())
                    .executes(this::executeSteal)
            )
        return root.build()
    }

    private fun executeSteal(ctx: CommandContext<CommandSourceStack>): Int {
        val playerSelector = ctx.getArg<PlayerSelectorArgumentResolver>("player").resolve(ctx.source)
        if (playerSelector.isEmpty()) {
            ctx.source.sender.sendMessage(ComponentUtils.error("Could not find that player!"))
            return Command.SINGLE_SUCCESS
        }

        var destination: Player? = null
        if (ctx.source.sender is Player)
            destination = ctx.source.sender as Player

        if (destination != null) {
            destination.inventory.clear()
            destination.enderChest.clear()
        }

        val player = playerSelector.first()

        if (player == destination) {
            ctx.source.sender.sendMessage(ComponentUtils.error("You cannot steal your own inventory!"))
            return Command.SINGLE_SUCCESS
        }

        val invContents = player.inventory.contents
        val enderContents = player.enderChest.contents
        player.inventory.clear()
        player.enderChest.clear()

        if (destination != null) {
            destination.inventory.contents = invContents
            destination.enderChest.contents = enderContents
            destination.playSound(destination.location, Sound.BLOCK_ENDER_CHEST_CLOSE, 1f, 1f)
        }

        ctx.source.sender.sendMessage(ComponentUtils.success("Stole ${player.name}'s inventory and ender chest!"))
        player.sendMessage(ComponentUtils.error("${ctx.source.sender.name} stole your inventory!"))
        return Command.SINGLE_SUCCESS
    }

}