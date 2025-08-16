package xyz.devvydont.smprpg.commands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.extensions.getArg
import xyz.devvydont.smprpg.gui.player.InterfaceStats
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class CommandStatistics(val name: String) : ICommand {

    /**
     * Get the root of the command builder.
     */
    override fun getRoot(): LiteralCommandNode<CommandSourceStack> {

        return Commands.literal(name)
            .executes { ctx ->
                val executor = ctx.source.executor
                if (executor is Player)
                    execute(executor, executor)
                Command.SINGLE_SUCCESS
            }
            .then(Commands.argument("entity", ArgumentTypes.entity())
                .executes { ctx ->

                    val executor = ctx.source.executor
                    if (executor !is Player) {
                        ctx.source.sender.sendMessage(ComponentUtils.error("Only players can execute this command!"))
                        return@executes Command.SINGLE_SUCCESS
                    }

                    val target = ctx.getArg<EntitySelectorArgumentResolver>("entity").resolve(ctx.source).firstOrNull()
                    if (target !is LivingEntity) {
                        ctx.source.sender.sendMessage(ComponentUtils.error("Your target entity must be a living entity!"))
                        return@executes Command.SINGLE_SUCCESS
                    }

                    execute(executor, target)
                    Command.SINGLE_SUCCESS
                })
            .build()

    }

    private fun execute(executor: Player, entity: LivingEntity) {
        InterfaceStats(executor, entity).openMenu()
    }
}