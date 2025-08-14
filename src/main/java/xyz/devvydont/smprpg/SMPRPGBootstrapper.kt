package xyz.devvydont.smprpg

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import xyz.devvydont.smprpg.commands.CommandSimple
import xyz.devvydont.smprpg.commands.CommandSteal
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.commands.ICommandAdvanced
import xyz.devvydont.smprpg.commands.admin.CommandSimulateFishing
import xyz.devvydont.smprpg.commands.economy.*
import xyz.devvydont.smprpg.commands.enchantments.CommandEnchantments
import xyz.devvydont.smprpg.commands.entity.CommandAttribute
import xyz.devvydont.smprpg.commands.entity.CommandSummon
import xyz.devvydont.smprpg.commands.inventory.CommandPeek
import xyz.devvydont.smprpg.commands.items.CommandGiveItem
import xyz.devvydont.smprpg.commands.items.CommandReforges
import xyz.devvydont.smprpg.commands.items.CommandSearchItem
import xyz.devvydont.smprpg.commands.items.CommandTrashItems
import xyz.devvydont.smprpg.commands.player.*
import xyz.devvydont.smprpg.services.EnchantmentService

@Suppress("unused")
class SMPRPGBootstrapper : PluginBootstrap {
    private fun bootstrapCommands(context: BootstrapContext) {
        val commandsToRegister: Array<ICommand> = arrayOf(

            // Old commands to be moved to the new API.
            CommandMenu("menu"),
            CommandAttribute("attribute"),
            CommandEcoAdmin("eco"),
            CommandBalance("balance"),
            CommandBalanceTop("balancetop"),
            CommandDifficulty("difficulty"),
            CommandDeposit("deposit"),
            CommandFishing("fishing"),
            CommandSimulateFishing("simulatefishing"),
            CommandWithdrawal("withdrawal"),
            CommandGiveItem("give"),
            CommandSearchItem("search"),
            CommandStatistics("statistics"),
            CommandSkill("skill"),
            CommandSummon("summon"),
            CommandReforge("reforge"),
            CommandWhatAmIHolding("whatamiholding"),
            CommandPeek("peek"),
            CommandTrashItems("trash"),
            CommandEnchantments("enchantments"),
            CommandReforges("reforges"),

            // New commands that use the new API.
            CommandSteal()
        )

        val manager: LifecycleEventManager<BootstrapContext> = context.lifecycleManager
        manager.registerEventHandler<ReloadableRegistrarEvent<Commands>>(
            LifecycleEvents.COMMANDS,
            LifecycleEventHandler { event: ReloadableRegistrarEvent<Commands> ->
                val commands = event.registrar()
                for (command in commandsToRegister) {

                    if (command is CommandSimple)
                        commands.register(
                        command.name,
                        command.description,
                        command.aliases,
                        command
                        )

                    if (command is ICommandAdvanced)
                        commands.register(command.getRoot())
                }
            })
    }

    private fun bootstrapEnchantments(context: BootstrapContext) {
        for (enchantment in EnchantmentService.CUSTOM_ENCHANTMENTS)
            enchantment.bootstrap(context)
    }

    override fun bootstrap(bootstrapContext: BootstrapContext) {
        bootstrapCommands(bootstrapContext)
        bootstrapEnchantments(bootstrapContext)
    }
}
