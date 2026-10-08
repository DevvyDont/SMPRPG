package xyz.devvydont.smprpg.commands.admin

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.services.RecipeService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Admin command root for SMPRPG. Currently exposes recipe management.
 *
 * Usage: `/smprpg recipes reload` rebuilds the data-driven recipe registry from the recipe YAML
 * files without a server restart.
 */
class CommandRecipes : ICommand {

    override fun getRoot(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("smprpg")
            .requires { it.sender.hasPermission("smprpg.command.admin") || it.sender.isOp }
            .then(
                Commands.literal("recipes")
                    .then(
                        Commands.literal("reload")
                            .executes(this::executeReload)
                    )
                    .then(
                        Commands.literal("export")
                            .executes(this::executeExport)
                    )
            )
            .build()
    }

    private fun executeReload(ctx: CommandContext<CommandSourceStack>): Int {
        val service = SMPRPG.getService(RecipeService::class.java)
        val sender = ctx.source.sender
        try {
            // The rebuild runs across ticks so it never hangs the server; report when it finishes.
            val started = service.reload { count, failures ->
                when {
                    failures.isEmpty() ->
                        sender.sendMessage(ComponentUtils.success("Reloaded custom recipes ($count loaded)."))
                    // Too many to read in chat — point the admin at the console warnings instead.
                    failures.size >= MAX_LISTED_FAILURES ->
                        sender.sendMessage(ComponentUtils.error("Reloaded custom recipes: $count loaded, ${failures.size} failed to load — check the console for the skipped entries."))
                    // A handful — list each reason inline so the admin can fix them without leaving the game.
                    else -> {
                        sender.sendMessage(ComponentUtils.error("Reloaded custom recipes: $count loaded, ${failures.size} failed to load:"))
                        for (failure in failures)
                            sender.sendMessage(ComponentUtils.error("  • $failure"))
                    }
                }
            }
            if (started)
                sender.sendMessage(ComponentUtils.success("Reloading custom recipes in the background..."))
            else
                sender.sendMessage(ComponentUtils.error("A recipe reload is already in progress."))
        } catch (e: Exception) {
            sender.sendMessage(ComponentUtils.error("Failed to reload recipes: ${e.message}"))
            SMPRPG.plugin.logger.severe("Failed to reload recipes: ${e.message}")
        }
        return Command.SINGLE_SUCCESS
    }

    private fun executeExport(ctx: CommandContext<CommandSourceStack>): Int {
        val service = SMPRPG.getService(RecipeService::class.java)
        try {
            val bundled = service.exportBundledRecipes()
            val compression = service.exportCompressionRecipes()
            val enchanting = service.exportEnchantingRecipes()
            ctx.source.sender.sendMessage(
                ComponentUtils.success("Exported $bundled bundled (overwritten) + $compression compression + $enchanting enchanting recipes to recipes/. Run /smprpg recipes reload to apply.")
            )
        } catch (e: Exception) {
            ctx.source.sender.sendMessage(ComponentUtils.error("Export failed: ${e.message}"))
            SMPRPG.plugin.logger.severe("Recipe export failed: ${e.message}")
        }
        return Command.SINGLE_SUCCESS
    }

    companion object {
        // At or above this many load failures, list none inline (too spammy for chat) and defer to the console.
        private const val MAX_LISTED_FAILURES = 10
    }
}
