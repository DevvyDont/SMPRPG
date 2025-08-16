package xyz.devvydont.smprpg.commands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.services.ChatService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class CommandWhatAmIHolding(val name: String) : ICommand {

    /**
     * Get the root of the command builder.
     */
    override fun getRoot(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal(name)
            .executes { ctx ->
                val sender = ctx.source.sender
                if (sender !is Player)
                    return@executes Command.SINGLE_SUCCESS

                val item = sender.inventory.itemInMainHand
                if (item.type == Material.AIR) {
                    sender.sendMessage(ComponentUtils.create("Must be holding an item!", NamedTextColor.RED))
                    return@executes Command.SINGLE_SUCCESS
                }

                val name: Component = SMPRPG.getService(ChatService::class.java).getPlayerDisplay(sender)
                val holding: Component = ComponentUtils.create(" is holding ")
                Bukkit.broadcast(name.append(holding).append(item.displayName()))
                Command.SINGLE_SUCCESS
            }
            .build()
    }
}