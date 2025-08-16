package xyz.devvydont.smprpg.commands.admin

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import org.bukkit.Sound
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.extensions.getArg
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * A simple command that allows someone with admin privileges to "steal" a player's inventory and ender chest.
 * This is more of a silly command that can be used in the event of someone needing their items taken from them.
 */
class CommandSteal : ICommand {

    override fun getRoot() : LiteralCommandNode<CommandSourceStack> {
        val root = Commands.literal("steal")
            .requires { ctx -> ctx.sender.hasPermission("smprpg.commands.steal") }
            .then(
                Commands.argument("player", ArgumentTypes.player())
                    .executes(this::executeSteal)
            )
        return root.build()
    }

    /**
     * Executes the steal logic when we know who we want to steal from.
     */
    private fun executeSteal(ctx: CommandContext<CommandSourceStack>): Int {

        val victim = ctx.getArg<PlayerSelectorArgumentResolver>("player")
            .resolve(ctx.source)
            .firstOrNull()
        if (victim == null) {
            ctx.source.sender.sendMessage(ComponentUtils.error("Could not find that player!"))
            return Command.SINGLE_SUCCESS
        }

        // The destination is the player (if it was a player...) that is receiving the items.
        val thief = ctx.source.executor as? Player
        if (victim == thief) {
            ctx.source.sender.sendMessage(ComponentUtils.error("You cannot steal your own inventory!"))
            return Command.SINGLE_SUCCESS
        }

        if (thief != null) {
            thief.inventory.clear()
            thief.enderChest.clear()
        }

        val invContents = victim.inventory.contents
        val enderContents = victim.enderChest.contents
        victim.inventory.clear()
        victim.enderChest.clear()

        if (thief != null) {
            thief.inventory.contents = invContents
            thief.enderChest.contents = enderContents
            thief.playSound(thief.location, Sound.BLOCK_ENDER_CHEST_CLOSE, 1f, 1f)
        }

        thief?.sendMessage(ComponentUtils.success("Stole ${victim.name}'s inventory and ender chest!"))
        victim.sendMessage(ComponentUtils.error("${thief?.name ?: "Someone"} stole your inventory!"))
        return Command.SINGLE_SUCCESS
    }

}