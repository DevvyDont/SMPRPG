package xyz.devvydont.smprpg.items.blueprints.equipment

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.fishing.utils.TemperatureReading
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ActionBarService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.items.AbilityUtil

class ThermometerBlueprint(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    Listener, IModelOverridden, IHeaderDescribable, ISellable {

    override val itemClassification: ItemClassification get() = ItemClassification.EQUIPMENT

    override fun getHeader(itemStack: ItemStack?): MutableList<Component?> {
        return mutableListOf(
            AbilityUtil.getAbilityComponent("Measure Temperature"),
            ComponentUtils.create("Use to measure the temperature"),
            ComponentUtils.create("of where you are currently standing!")
        )
    }

    /**
     * Given this item stack, how much should it be able to sell for?
     * Keep in mind that the size of the stack needs to considered as well!
     *
     * @param item The item that can be sold.
     * @return The worth of the item.
     */
    override fun getWorth(item: ItemStack): Int {
        return 10000 * item.amount
    }

    override fun getDisplayKey(): Key? {
        return IModelOverridden.ofMaterial(Material.REDSTONE_TORCH)
    }

    @EventHandler
    private fun onInteract(event: PlayerInteractEvent) {
        if (!event.action.isRightClick) return

        if (event.item == null) return
        if (!isItemOfType(event.item!!)) return

        val rawTemp = TemperatureReading.fromBlock(event.player.location.block)
        val temp = TemperatureReading.fromValue(rawTemp)
        SMPRPG.getService(ActionBarService::class.java).addActionBarComponent(
            event.getPlayer(),
            ActionBarService.ActionBarSource.MISC,
            ComponentUtils.merge(
                ComponentUtils.create("Temp: ", NamedTextColor.GOLD),
                temp.Component,
                ComponentUtils.SPACE,
                ComponentUtils.create(String.format("(%.0f°F)", toFahrenheit(rawTemp)), NamedTextColor.DARK_GRAY),
                if (DEBUG) ComponentUtils.create(" $rawTemp", NamedTextColor.RED) else ComponentUtils.EMPTY
            ),
            3
        )
    }

    companion object {
        /**
         * Affects the number display of the temperature. When on, displays real value as well as the fake converted one.
         */
        private const val DEBUG = false

        // Constants for mapping
        private const val MIN_MC_TEMP = -0.5f
        private const val MAX_MC_TEMP = 2.0f
        private const val MIN_C = -20f // freezing biomes
        private const val MAX_C = 50f // nether heat

        /**
         * Converts Minecraft temperature to Celsius.
         * @param mcTemp The Minecraft temperature value (e.g., from Block#getTemperature()).
         * @return Temperature in degrees Celsius.
         */
        fun toCelsius(mcTemp: Double): Double {
            val ratio: Double = (mcTemp - MIN_MC_TEMP) / (MAX_MC_TEMP - MIN_MC_TEMP)
            return MIN_C + ratio * (MAX_C - MIN_C)
        }

        /**
         * Converts Minecraft temperature to Fahrenheit.
         * @param mcTemp The Minecraft temperature value (e.g., from Block#getTemperature()).
         * @return Temperature in degrees Fahrenheit.
         */
        fun toFahrenheit(mcTemp: Double): Double {
            val celsius: Double = toCelsius(mcTemp)
            return celsius * 9 / 5 + 32
        }
    }
}
