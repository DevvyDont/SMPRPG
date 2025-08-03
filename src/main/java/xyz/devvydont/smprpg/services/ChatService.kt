package xyz.devvydont.smprpg.services

import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.milkbowl.vault.chat.Chat
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.util.chat.CustomChatRenderer
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.PlayerChatInformation

class ChatService : IService, Listener {
    private var chat: Chat? = null

    @Throws(RuntimeException::class)
    override fun setup() {
        val plugin = plugin
        plugin.logger.info("Setting up Chat service")

        // If vault isn't installed, we cannot function correctly.
        if (plugin.server.pluginManager.getPlugin("Vault") == null) {
            plugin.logger.severe("Vault is not installed. Please install Vault")
            throw RuntimeException("Vault is not installed. Please install Vault")
        }

        // We need to make sure the economy class is valid
        val provider = plugin.server.servicesManager.getRegistration(Chat::class.java)
        if (provider == null) {
            plugin.logger.severe("Failed to detect Chat service, is Vault installed correctly?")
            throw RuntimeException("Failed to detect Chat service, is Vault installed correctly?")
        }

        this.chat = provider.getProvider()
        plugin.logger.info("Successfully hooked into Vault Chat service")
    }

    override fun cleanup() {
        plugin.logger.info("Cleaning up ChatService")
    }

    private fun determineNameColor(player: OfflinePlayer): TextColor? {
        val difficulty = SMPRPG.getService(DifficultyService::class.java).getDifficulty(player)
        return difficulty.Color
    }

    fun getPlayerDisplay(player: OfflinePlayer): Component {
        val info = getPlayerInfo(player)
        return ComponentUtils.merge(
            Component.text(info.prefix, NamedTextColor.WHITE),
            ComponentUtils.create(player.name, info.nameColor)
        )
    }

    fun getPlayerInfo(player: Player): PlayerChatInformation {
        val prefix = chat!!.getPlayerPrefix(player)
        return PlayerChatInformation(player, prefix, chat!!.getPlayerSuffix(player), determineNameColor(player))
    }

    fun getPlayerInfo(player: OfflinePlayer): PlayerChatInformation {
        val world = Bukkit.getWorlds()[0].name
        val prefix = chat!!.getPlayerPrefix(world, player)
        return PlayerChatInformation(player, prefix, chat!!.getPlayerSuffix(world, player), determineNameColor(player))
    }

    /**
     * Injects the player level into a chat message no matter what chat plugins are doing.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onChat(event: AsyncChatEvent) {
        event.renderer(CHAT_RENDERER)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onJoin(event: PlayerJoinEvent) {
        val name = getPlayerDisplay(event.getPlayer())
        val msg = ComponentUtils.merge(
            name,
            ComponentUtils.create(" has joined the game!", NamedTextColor.YELLOW)
        )
        event.joinMessage(ComponentUtils.alert(msg, NamedTextColor.GREEN))
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onLeave(event: PlayerQuitEvent) {
        val name = getPlayerDisplay(event.getPlayer())
        val msg = ComponentUtils.merge(
            name,
            ComponentUtils.create(" has left the game!", NamedTextColor.YELLOW)
        )
        event.quitMessage(ComponentUtils.alert(msg, NamedTextColor.RED))
    }

    companion object {
        /**
         * The chat formatter. Defines logic for how to display chat messages.
         */
        val CHAT_RENDERER: ChatRenderer = CustomChatRenderer()
    }
}
