package xyz.devvydont.smprpg

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.datapack.DatapackRegistrar
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler
import io.papermc.paper.plugin.lifecycle.event.registrar.RegistrarEvent
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.commands.ICommandBase
import xyz.devvydont.smprpg.commands.LegacyCommand
import xyz.devvydont.smprpg.market.MarketService
import xyz.devvydont.smprpg.market.commands.CommandAuctionHouse
import xyz.devvydont.smprpg.market.commands.CommandBazaar
import xyz.devvydont.smprpg.market.commands.CommandMarket
import xyz.devvydont.smprpg.market.gui.auction.MenuAuctionBrowser
import xyz.devvydont.smprpg.market.gui.bazaar.MenuBazaarBrowser
import xyz.devvydont.smprpg.commands.admin.CommandAttribute
import xyz.devvydont.smprpg.commands.admin.CommandEcoAdmin
import xyz.devvydont.smprpg.commands.admin.CommandRecipes
import xyz.devvydont.smprpg.commands.admin.CommandSimulateFishing
import xyz.devvydont.smprpg.commands.admin.CommandSteal
import xyz.devvydont.smprpg.commands.enchantments.CommandEnchantments
import xyz.devvydont.smprpg.commands.entity.CommandSummon
import xyz.devvydont.smprpg.commands.inventory.CommandPeek
import xyz.devvydont.smprpg.commands.items.CommandEnchant
import xyz.devvydont.smprpg.commands.items.CommandGiveItem
import xyz.devvydont.smprpg.commands.items.CommandReforge
import xyz.devvydont.smprpg.commands.items.CommandSearchItem
import xyz.devvydont.smprpg.commands.player.*
import xyz.devvydont.smprpg.fishing.gui.LootTypeChancesMenu
import xyz.devvydont.smprpg.fishing.gui.MenuSlayer
import xyz.devvydont.smprpg.gui.MainMenu
import xyz.devvydont.smprpg.gui.MenuReforgeBrowser
import xyz.devvydont.smprpg.gui.economy.MenuDeposit
import xyz.devvydont.smprpg.gui.economy.MenuWithdraw
import xyz.devvydont.smprpg.gui.items.MenuTrashItems
import xyz.devvydont.smprpg.gui.player.MenuDifficultyChooser
import xyz.devvydont.smprpg.gui.player.MenuPlayerSettings
import xyz.devvydont.smprpg.services.EnchantmentService
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException


@Suppress("unused")
class SMPRPGBootstrapper : PluginBootstrap {
    private fun bootstrapCommands(context: BootstrapContext) {
        val commandsToRegister: Array<ICommandBase> = arrayOf(

            // Old commands to be moved to the new API.
            CommandSimulateFishing("simulatefishing"),
            CommandGiveItem("give"),
            CommandSearchItem("search"),
            CommandSummon("summon"),
            CommandPeek("peek"),

            // New commands that use the new API.
            CommandEnchant("enchant"),  // Override for the vanilla enchant command.
            CommandSteal(),
            CommandAttribute(),
            CommandEcoAdmin(),
            CommandRecipes(),
            CommandSkill(),
            CommandBalance("balance"),
            CommandBalance("bal"),  // Effectively functions as an alias. We can register the same command multiple times!
            CommandStatistics("statistics"),
            CommandStatistics("stats"),
            CommandWardrobe("wardrobe"),
            CommandWardrobe("sets"),
            CommandWardrobe("wd"),
            CommandWhatAmIHolding("whatamiholding"),
            CommandWhatAmIHolding("waih"),
            CommandBalanceTop("balancetop"),
            CommandBalanceTop("topbalance"),
            CommandBalanceTop("baltop"),
            CommandReforge(),
            ICommand.SimplePlayerCommand("menu", { player -> MainMenu(player).openMenu()}),
            ICommand.SimplePlayerCommand("difficulty", { player -> MenuDifficultyChooser(player).openMenu()}),
            ICommand.SimplePlayerCommand("settings", { player -> MenuPlayerSettings(player).openMenu()}),
            ICommand.SimplePlayerCommand("fishing", { player -> LootTypeChancesMenu(player).openMenu()}),
            ICommand.SimplePlayerCommand("deposit", { player -> MenuDeposit(player).openMenu()}),
            ICommand.SimplePlayerCommand("sell", { player -> MenuDeposit(player).openMenu()}),
            ICommand.SimplePlayerCommand("withdrawal", { player -> MenuWithdraw(player).openMenu()}),
            ICommand.SimplePlayerCommand("trash", { player -> MenuTrashItems(player).openMenu()}),
            CommandEnchantments("enchantments"),
            ICommand.SimplePlayerCommand("reforges", { player -> MenuReforgeBrowser(player).openMenu()}),
            CommandMarket(),
            CommandAuctionHouse(),
            ICommand.SimplePlayerCommand("auctionhouse", { player -> if (SMPRPG.getService(MarketService::class.java).tryOpenAuction(player)) MenuAuctionBrowser(player).openMenu()}),
            CommandBazaar(),
            ICommand.SimplePlayerCommand("bazaar", { player -> if (SMPRPG.getService(MarketService::class.java).tryOpenBazaar(player)) MenuBazaarBrowser(player).openMenu()}),
            ICommand.SimplePlayerCommand("slayer", { player -> MenuSlayer(player).openMenu()}),
        )

        val manager: LifecycleEventManager<BootstrapContext> = context.lifecycleManager
        manager.registerEventHandler<ReloadableRegistrarEvent<Commands>>(
            LifecycleEvents.COMMANDS,
            LifecycleEventHandler { event: ReloadableRegistrarEvent<Commands> ->
                val commands = event.registrar()
                for (command in commandsToRegister) {

                    if (command is LegacyCommand)
                        commands.register(
                        command.name,
                        command.description,
                        command.aliases,
                        command
                        )

                    if (command is ICommand)
                        commands.register(command.getRoot())
                }
            })
    }

    private fun bootstrapEnchantments(context: BootstrapContext) {
        for (enchantment in EnchantmentService.CUSTOM_ENCHANTMENTS)
            enchantment.bootstrap(context)
    }

    private fun bootstrapDatapack(context: BootstrapContext) {
        context.lifecycleManager.registerEventHandler(
            LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                LifecycleEventHandler { event: RegistrarEvent<DatapackRegistrar> ->
                    try {
                        // Retrieve the URI of the datapack folder.
                        val uri: URI = this.javaClass.getResource("/smprpg-data")!!.toURI()
                        // Discover the pack. The ID is set to "provided", which indicates to
                        // a server owner that your plugin includes this data pack.
                        event.registrar().discoverPack(uri, "provided")
                    } catch (e: URISyntaxException) {
                        SMPRPG.plugin.logger.severe("unable to load the smprpg-data builtin datapack. check console for details.")
                        e.printStackTrace()
                    } catch (e: IOException) {
                        SMPRPG.plugin.logger.severe("unable to load the smprpg-data builtin datapack. check console for details.")
                        e.printStackTrace()
                    }
                }
            ))
    }

    override fun bootstrap(bootstrapContext: BootstrapContext) {
        bootstrapCommands(bootstrapContext)
        bootstrapDatapack(bootstrapContext)  // We MUST bootstrap datapack first to have access to tags on enchantment definitions.
        bootstrapEnchantments(bootstrapContext)
    }


}
