package xyz.devvydont.smprpg.items.blueprints.sets.aetherutil

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent
import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.listeners.item.ItemDurabilityListener
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.time.TickTime
import kotlin.math.max

class WingsOfIcarus(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, IRepairable, IHeaderDescribable, Listener {

    override val itemClassification: ItemClassification get() = ItemClassification.CHESTPLATE

    // TODO: Replace with Moa feather when moas are added.
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(
        itemService.getCustomItem(
            Material.FEATHER
        )
    )

    override fun getAttributeModifiers(item: ItemStack): MutableCollection<AttributeEntry> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, 20.0),
            AdditiveAttributeEntry(AttributeWrapper.HEALTH, 10.0)
        )
    }

    override fun getPowerRating(): Int {
        return 20
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.CHEST
    }

    override fun getMaxDurability(): Int { return 32 }

    override fun getHeader(itemStack: ItemStack?): List<Component> {
        return mutableListOf(ComponentUtils.merge(
            ComponentUtils.create("Wax wings ", NamedTextColor.GOLD),
            ComponentUtils.create("worthy of a demigod.")
        ),
        ComponentUtils.create("Their pliability allows them to regenerate over time."),
        ComponentUtils.create("Be wary though, for those who fly too close"),
        ComponentUtils.merge(
            ComponentUtils.create("to the sun ", NamedTextColor.RED),
            ComponentUtils.create("may see their hubris challenged.")))
    }

    @EventHandler
    fun onFlyAtSun(event: PlayerMoveEvent) {
        val chestplate = event.player.equipment.chestplate
        if (chestplate.isEmpty) return
        if (itemService.getBlueprint(chestplate) is WingsOfIcarus) {
            if (event.player.isGliding) {
                if ((event.player.world.environment == World.Environment.NETHER) || (event.to.y >= event.player.world.seaLevel + MAX_ABOVE_SEA_LEVEL)) {
                    chestplate.damage(ItemDurabilityListener.INSTA_BREAK_ITEM_AMT, event.player)
                    event.player.sendMessage(
                        ComponentUtils.create(
                            "And so, the wax melts as you plunge to the ground below.",
                            NamedTextColor.LIGHT_PURPLE
                        )
                    )
                }
            }
        }
    }

    @EventHandler
    fun onElytraBoost(event: PlayerElytraBoostEvent) {
        val chestplate = event.player.equipment.chestplate
        if (chestplate == null) return
        if (itemService.getBlueprint(chestplate) is WingsOfIcarus) {
            chestplate.damage(ItemDurabilityListener.INSTA_BREAK_ITEM_AMT, event.player)
            event.player.sendMessage(
                ComponentUtils.create(
                    "In your pursuit of a greed for speed, the flames melt your wings as you plunge to the ground below.",
                    NamedTextColor.DARK_PURPLE
                )
            )
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun regenWings(event: ServerLoadEvent) {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            for (player in Bukkit.getOnlinePlayers()) {
                val chestplate = player.equipment.chestplate
                val bp =itemService.getBlueprint(chestplate)
                if (chestplate != null) {
                    if (bp is WingsOfIcarus) {
                        // Item is broken, don't regen broken items.
                        if (chestplate.getData(DataComponentTypes.DAMAGE) == chestplate.getData(DataComponentTypes.MAX_DAMAGE) as Int - 1)
                            continue

                        val newDamage = max(0, chestplate.getData(DataComponentTypes.DAMAGE) as Int - 1)
                        chestplate.setData(DataComponentTypes.DAMAGE, newDamage)
                        bp.updateItemData(chestplate)
                    }
                }
            }
        }, TickTime.INSTANTANEOUSLY, TickTime.seconds(10))
    }

    companion object {
        val MAX_ABOVE_SEA_LEVEL: Int = 128
    }
}
