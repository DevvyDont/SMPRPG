package xyz.devvydont.smprpg.commands.items

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.extensions.getArg
import xyz.devvydont.smprpg.extensions.queryEnum
import xyz.devvydont.smprpg.gui.items.MenuReforge
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.concurrent.CompletableFuture


const val SHORTCUT_PERMISSION = "smprpg.command.reforge"
const val ADMIN_PERMISSION = "smprpg.command.reforge.admin"

class CommandReforge : ICommand {

    /**
     * Get the root of the command builder.
     */
    override fun getRoot(): LiteralCommandNode<CommandSourceStack> {

        // Keep in mind, this command has 2 functionalities. A shortcut to the NPC reforge menu,
        // and an admin reforge application method. Don't forget to check separate permissions for both.
        return Commands.literal("reforge")
            .requires { ctx -> ctx.sender.hasPermission(SHORTCUT_PERMISSION) || ctx.sender.isOp }
            .executes { ctx ->
                val target = ctx.source.executor
                if (target !is Player) {
                    ctx.source.sender.sendMessage(ComponentUtils.error("The target must be a player!"))
                    return@executes Command.SINGLE_SUCCESS
                }
                MenuReforge(target).openMenu()
                Command.SINGLE_SUCCESS
            }
            .then(Commands.argument("type", StringArgumentType.string())
                .requires { ctx -> ctx.sender.hasPermission(ADMIN_PERMISSION) || ctx.sender.isOp }
                .suggests(this::getReforgeSuggestions)
                .executes { ctx ->
                    val target = ctx.source.executor
                    if (target !is LivingEntity) {
                        ctx.source.sender.sendMessage(ComponentUtils.error("The target is not a living entity!"))
                        return@executes Command.SINGLE_SUCCESS
                    }
                    val equipment = target.equipment
                    if (equipment == null) {
                        ctx.source.sender.sendMessage(ComponentUtils.error("The target cannot hold things!"))
                        return@executes Command.SINGLE_SUCCESS
                    }
                    val item: ItemStack = equipment.itemInMainHand
                    if (item.type == Material.AIR) {
                        ctx.source.sender.sendMessage(ComponentUtils.error("The target is not holding anything!"))
                        return@executes Command.SINGLE_SUCCESS
                    }

                    val reforgeType = queryEnum<ReforgeType>(ctx.getArg<String>("type"))
                    if (reforgeType == null) {
                        ctx.source.sender.sendMessage(ComponentUtils.error("The provided reforge was invalid!"))
                        return@executes Command.SINGLE_SUCCESS
                    }

                    val reforge: ReforgeBase? = SMPRPG.getService(ItemService::class.java).getReforge(reforgeType)
                    if (reforge == null) {
                        ctx.source.sender.sendMessage(ComponentUtils.error("The provided reforge was not registered to the server!"))
                        return@executes Command.SINGLE_SUCCESS
                    }
                    reforge.apply(item)
                    ctx.source.sender.sendMessage(ComponentUtils.success("Applied the " + reforgeType.name + " reforge!"))
                    Command.SINGLE_SUCCESS
                })
            .build()
    }

    /**
     * Build the suggestions when filling out the reforge parameter.
     */
    private fun getReforgeSuggestions(ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        for (reforge in ReforgeType.entries)
            builder.suggest(reforge.name.lowercase())
        return builder.buildFuture()
    }
}