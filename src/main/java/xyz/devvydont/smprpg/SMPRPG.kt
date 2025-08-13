package xyz.devvydont.smprpg

import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import xyz.devvydont.smprpg.services.SpecialEffectService
import xyz.devvydont.smprpg.listeners.block.DimensionPortalLockingListener
import xyz.devvydont.smprpg.listeners.block.MultiBlockBreakListener
import xyz.devvydont.smprpg.listeners.block.TrialChamberVaultFix
import xyz.devvydont.smprpg.listeners.crafting.AnvilEnchantmentCombinationFixListener
import xyz.devvydont.smprpg.listeners.damage.AbsorptionDamageFix
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.listeners.damage.EnvironmentalDamageListener
import xyz.devvydont.smprpg.listeners.entity.HealthScaleListener
import xyz.devvydont.smprpg.listeners.damage.PvPListener
import xyz.devvydont.smprpg.listeners.damage.SlimeRapidAttackFixListener
import xyz.devvydont.smprpg.listeners.entity.HealthRegenerationListener
import xyz.devvydont.smprpg.listeners.entity.StructureEntitySpawnListener
import xyz.devvydont.smprpg.loot.LootListener
import xyz.devvydont.smprpg.services.*
import xyz.devvydont.smprpg.util.animations.AnimationService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

class SMPRPG : JavaPlugin() {
    /**
     * A list of listeners that provide basic game mechanics that can be toggled on and off.
     */
    var generalListeners: MutableList<ToggleableListener> = ArrayList()

    /**
     * A list of services that are meant to provide core plugin functionality between other services.
     */
    var services: MutableList<IService> = ArrayList()

    fun getServices(): MutableCollection<IService> {
        return services
    }

    val listeners: MutableCollection<ToggleableListener>
        get() = generalListeners

    override fun onEnable() {
        INSTANCE = this

        // Instantiate services. As a programmer, if you create a service class in the codebase you should have it
        // instantiated here no matter what to prevent runtime exceptions from nonexistent services.
        // Services are meant to be singleton instances that ALWAYS exist.
        services.add(EconomyService()) // Allows transactions/money to work.
        services.add(ChatService()) // Provides plugin with player display names and chat formatting.
        services.add(EnchantmentService()) // Provides base enchantment functionality.
        services.add(AttributeService()) // Provides custom attribute functionality.
        services.add(EntityDamageCalculatorService()) // Logic for most damage calculations.
        services.add(ItemService()) // Provides custom items and handlers for them.
        services.add(AbilityService()) // Provides just about anything entity related, attributes, stats, etc.
        services.add(EntityService()) // Provides just about anything entity related, attributes, stats, etc.
        services.add(DifficultyService()) // Allows players to tweak their profile experience.
        services.add(SpecialEffectService()) // Implements the "ailments" system.
        services.add(SkillService()) // Logic for skills/skill experience for players.
        services.add(DropsService()) // Implements the "drop protection" mechanic, as well as some other QoL.
        services.add(FishingService()) // Implements fishing rework.
        services.add(RecipeService()) // Implements custom recipes.
        services.add(BlockLootService()) // Implements custom block drop behavior.
        services.add(ActionBarService()) // Broadcasts player information to player action bars.
        services.add(UnstableListenersService()) // Implements some listeners that depend on ProtocolLib.
        services.add(AnimationService()) // Mainly provides GUIs with an easy-to-use animation API.

        // Start all the services. Make sure nothing goes wrong.
        for (service in services) {
            service.setup()
            logger.info(service.javaClass.getSimpleName() + " service successfully enabled!")

            // If this service wants to listen to events, register it.
            // Keep in mind there's two cases here, one where a service wants to be toggleable and another where
            // the events should *always* fire, unless we manually unregister it the intended bukkit way.
            if (service is ToggleableListener) {
                service.start()
                logger.info(service.javaClass.getSimpleName() + " is now listening to events and can be toggled on/off.")
            } else if (service is Listener) {
                server.pluginManager.registerEvents(service, INSTANCE!!)
                logger.info(service.javaClass.getSimpleName() + " is now listening to events.")
            }
        }

        // Initialize the general listeners that aren't core enough to be considered services.
        generalListeners.add(EnvironmentalDamageListener()) // Scales environmental damage to be percentage based.
        generalListeners.add(HealthScaleListener()) // Makes health scale update correctly.
        generalListeners.add(HealthRegenerationListener()) // Scales HP regeneration based on max HP.
        generalListeners.add(AbsorptionDamageFix()) // Makes absorption work correctly.
        generalListeners.add(DimensionPortalLockingListener()) // Implements dimension requirements.
        generalListeners.add(AnvilEnchantmentCombinationFixListener()) // Makes anvil combinations work.
        generalListeners.add(PvPListener()) // Disables PVP in certain contexts.
        generalListeners.add(StructureEntitySpawnListener()) // Allows entities to spawn as the level of the structure they're in.
        generalListeners.add(LootListener()) // Overrides vanilla loot tables by injecting our items into it.
        generalListeners.add(TrialChamberVaultFix()) // Allows trial chambers to work with our custom item system.
        generalListeners.add(SlimeRapidAttackFixListener()) // Fixes the vanilla bug of slimes being able to attack every tick.
        generalListeners.add(MultiBlockBreakListener()) // Fixes the vanilla bug of slimes being able to attack every tick.

        // Uncomment this if you want some debugging events.
//        generalListeners.add(new DebuggingListeners());  // Enables some debugging functionality.

        // Start all of them.
        for (listener in generalListeners) listener.start()
    }

    override fun onDisable() {
        for (service in services) service.cleanup()
        for (listener in generalListeners) listener.stop()
    }

    companion object {
        var INSTANCE: SMPRPG? = null

        /**
         * A global shortcut to retrieve the plugin instance.
         * In Kotlin, you can use the 'plugin' variable anywhere in the codebase.
         * In Java, you can use the 'SMPRPG.getPlugin()' call to retrieve the plugin anywhere in the codebase.
         */
        @JvmStatic
        val plugin: SMPRPG
            get() = INSTANCE!!

        /**
         * Sends a message to all online operators. This can be used as an alert if something somewhat serious happens.
         * @param alert The component to show to operators.
         */
        @JvmStatic
        fun broadcastToOperators(alert: TextComponent) {
            Bukkit.getLogger().warning(alert.content())
            for (player in Bukkit.getOnlinePlayers()) if (player.isOp || player.permissionValue("smprpg.receiveopmessages")
                    .toBooleanOrElse(false)
            ) player.sendMessage(ComponentUtils.alert(ComponentUtils.create("OP MSG", NamedTextColor.DARK_RED), alert))
        }

        /**
         * Sends a message to all online operators. This can be used as an alert if something somewhat serious happens.
         * The player parameter is a way to provide additional context by supplying a player who "caused" this message.
         * @param player The player that caused this interaction.
         * @param alert The component to show to operators.
         */
        @JvmStatic
        fun broadcastToOperatorsCausedBy(player: Player, alert: TextComponent) {
            Bukkit.getLogger().warning(alert.content())
            for (op in Bukkit.getOnlinePlayers()) if (op.isOp || op.permissionValue("smprpg.receiveopmessages")
                    .toBooleanOrElse(false)
            ) {
                op.sendMessage(
                    ComponentUtils.alert(
                        ComponentUtils.create("OP MSG", NamedTextColor.DARK_RED),
                        ComponentUtils.create("(Caused by " + player.name + ") ", NamedTextColor.RED).append(alert)
                    )
                )
            }
        }

        /**
         * The core method for cross plugin module communication.
         * Attempts to find a service that is of type clazz. They are programmatically guaranteed to exist.
         * If a service does not exist, a runtime exception will throw as this is a critical level programmer
         * error, as you should never reference services that don't exist.
         * @param clazz The class of the service you want.
         * @return The [IService] instance. If not found, an application exception throws.
         */
        @JvmStatic
        fun <T : IService?> getService(clazz: Class<T>): T {

            if (INSTANCE == null)
                throw IllegalStateException("Plugin is not initialized yet! You cannot call this!")
            val plugin = INSTANCE!!

            for (service in plugin.getServices())
                if (clazz.isAssignableFrom(service.javaClass))
                    return clazz.cast(service)

            plugin.logger.severe("Service " + clazz.getName() + " not instantiated. Did you forget to create it?")
            throw RuntimeException("Service " + clazz.getName() + " not instantiated?")
        }

        /**
         * Attempts to find a listener that is of type clazz. They are not guaranteed to exist.
         * @param clazz The class of the listener you want.
         * @return The [ToggleableListener] instance. If not found, returns null.
         */
        @JvmStatic
        fun <T : ToggleableListener?> getListener(clazz: Class<T>): T? {
            if (INSTANCE == null)
                throw IllegalStateException("Plugin is not initialized yet! You cannot call this!")
            val plugin = INSTANCE!!

            for (listener in plugin.listeners)
                if (clazz.isAssignableFrom(listener.javaClass))
                    return clazz.cast(listener)
            return null
        }
    }
}
