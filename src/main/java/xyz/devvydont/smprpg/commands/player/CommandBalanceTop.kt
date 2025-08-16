package xyz.devvydont.smprpg.commands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.services.ChatService
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.services.EconomyService.Companion.formatMoney
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.*


class CommandBalanceTop(val name: String): ICommand {

    /**
     * Get the root of the command builder.
     */
    override fun getRoot(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal(name)
            .executes { ctx ->
                execute(ctx)
                Command.SINGLE_SUCCESS
            }
            .build()
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>) {
        val commandSender = ctx.source.sender
        commandSender.sendMessage(ComponentUtils.alert(ComponentUtils.create("Querying users...", NamedTextColor.YELLOW),
            NamedTextColor.GREEN))

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            // Create the title UI
            var output = ComponentUtils.merge(
                ComponentUtils.create("------------ "),
                ComponentUtils.create("Top Balances", NamedTextColor.GOLD),
                ComponentUtils.create(" ------------\n\n")
            )

            // Retrieve every player that has ever played on the server
            val allPlayers: MutableMap<UUID, PlayerBalanceEntry> = HashMap<UUID, PlayerBalanceEntry>()
            for (p in Bukkit.getOfflinePlayers())
                allPlayers.put(p.uniqueId, PlayerBalanceEntry(p, SMPRPG.getService(EconomyService::class.java).getMoney(p)))
            for (p in Bukkit.getOnlinePlayers()) allPlayers.put(
                p.uniqueId, PlayerBalanceEntry(p, SMPRPG.getService(EconomyService::class.java).getMoney(p))
            )

            // Construct a sortable list of entries containing player information including their current balance
            val listOfPlayerBalances: MutableList<PlayerBalanceEntry> =
                ArrayList<PlayerBalanceEntry>(allPlayers.values.stream().toList())
            listOfPlayerBalances.sortWith(Comparator { o1: PlayerBalanceEntry, o2: PlayerBalanceEntry ->
                            if (o1.balance == o2.balance)
                                return@Comparator 0
                            if (o1.balance > o2.balance)
                                return@Comparator -1
                            else
                                return@Comparator 1
            })

            // Sum all entries balances for a server total
            var sum = 0.0
            for (entry in listOfPlayerBalances)
                sum += entry.balance

            // Display the total economy
            output = output.append(
                ComponentUtils.merge(
                    ComponentUtils.create("Total Server Economy: ", NamedTextColor.RED, TextDecoration.BOLD),
                    ComponentUtils.create(formatMoney(sum), NamedTextColor.GOLD),
                    ComponentUtils.create("\n\n")
                )
            )

            // Display all the entries
            var rank = 1
            for (entry in listOfPlayerBalances) {
                // Don't show past #10
                if (rank > 10) break

                val name = SMPRPG.getService(ChatService::class.java).getPlayerDisplay(entry.player)
                output = output.append(
                    ComponentUtils.merge(
                        ComponentUtils.create(
                            String.format("#%d: ", rank),
                            NamedTextColor.AQUA,
                            TextDecoration.ITALIC
                        ),
                        name,
                        ComponentUtils.create(" - "),
                        ComponentUtils.create(formatMoney(entry.balance), NamedTextColor.GOLD),
                        ComponentUtils.create("\n")
                    )
                )
                rank++
            }

            // Display the UI
            output = output.append(ComponentUtils.create("\n-------------------------------------"))
            commandSender.sendMessage(output)
        })

    }
}

@JvmRecord
private data class PlayerBalanceEntry(val player: OfflinePlayer, val balance: Long)