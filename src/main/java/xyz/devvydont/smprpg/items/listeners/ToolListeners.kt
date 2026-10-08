package xyz.devvydont.smprpg.items.listeners

import io.papermc.paper.datacomponent.DataComponentTypes
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.core.block.property.IntegerProperty
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.checkerframework.checker.index.qual.NonNegative
import xyz.devvydont.smprpg.block.CraftEngineBlockEnums
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.blueprints.farming.ProgressiveHoeBlueprint
import xyz.devvydont.smprpg.items.interfaces.IFueledEquipment
import xyz.devvydont.smprpg.items.interfaces.IFurnaceFuel
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import kotlin.math.max
import net.momirealms.craftengine.core.util.Key as CEKey

class ToolListeners : ToggleableListener() {
    @EventHandler
    fun onBreakBlock(event: BlockBreakEvent) {
        val player = event.getPlayer()

        val itemUsedToBreak = player.getEquipment().getItemInMainHand()
        val itemUsedToBreakBlueprint = blueprint(itemUsedToBreak)

        if (itemUsedToBreakBlueprint is IFueledEquipment) {
            val bp = blueprint(itemUsedToBreak) as IFueledEquipment
            player.getWorld().playSound(
                player.getLocation(),
                (itemUsedToBreakBlueprint as IFueledEquipment).getBreakSound(),
                0.5f,
                1.0f
            )

            // Decrement fuel
            bp.setFuelUsed(itemUsedToBreak, itemUsedToBreak.getData<@NonNegative Int?>(DataComponentTypes.DAMAGE)!! + 1)
        }
    }

    /**
     * Handle incrementing progressive hoe progress.
     */
    @EventHandler
    fun onCropHarvest(event: BlockBreakEvent) {
        val potentialHoe = event.player.inventory.itemInMainHand
        val blueprint = blueprint(potentialHoe)
        if (blueprint is ProgressiveHoeBlueprint) {
            var cropKeyToMatch: CEKey
            when (blueprint.type) {
                CustomItemType.WHEAT_HOE -> cropKeyToMatch = CEKey.of("minecraft:wheat")
                CustomItemType.POTATO_HOE -> cropKeyToMatch = CEKey.of("minecraft:potatoes")
                CustomItemType.ONION_HOE -> cropKeyToMatch = CraftEngineBlockEnums.ONION_PLANT.key
                CustomItemType.CARROT_HOE -> cropKeyToMatch = CEKey.of("minecraft:carrots")
                CustomItemType.MELON_HOE -> cropKeyToMatch = CEKey.of("minecraft:melon")
                else -> return
            }
            val ceBlock = BukkitAdaptor.adapt(event.block)
            if (ceBlock.id() == cropKeyToMatch) {
                // Check if we are at max age.
                if (cropKeyToMatch.namespace == "minecraft") {
                    // Vanilla blocks can just use the Ageable API
                    if (event.block.blockData is Ageable) {
                        val data = event.block.blockData as Ageable
                        if (data.age == data.maximumAge) {
                            ProgressiveHoeBlueprint.incrementCropProgress(potentialHoe, event.player)
                        }
                    }
                    else {
                        ProgressiveHoeBlueprint.incrementCropProgress(potentialHoe, event.player)
                    }
                }
                else {
                    // Custom blocks will reference an external list of maximum ages.
                    val ageProperty = ceBlock.customBlockState()!!.getProperty<Int>("age") as IntegerProperty
                    if (ageProperty.max == ceBlock.customBlockState()!!.get<Int>(ageProperty)) {
                        ProgressiveHoeBlueprint.incrementCropProgress(potentialHoe, event.player)
                    }
                }
            }
        }
    }

    // todo: drill stuff, commented out for now bc it seems to be causing issues
//    @EventHandler(priority = EventPriority.HIGHEST)
//    fun __onAttemptPerformTransmuteUpgradeRecipe(event: PrepareItemCraftEvent) {
//        // If there's a recipe involved, there's nothing to check.
//
//        if (event.getRecipe() != null) return
//
//        var refuelableIndex = -1
//        var totalRefuel = 0
//        var totalItems = 0
//        var i = 0
//        // Get our crafting matrix
//        val matrix = event.getInventory().getMatrix()
//        for (itemStack in matrix) {
//            i++
//            if (itemStack == null) continue
//
//            val bp = blueprint(itemStack)
//            val isEquip = (bp is IFueledEquipment)
//            val isFuel = (bp is IFurnaceFuel) || itemStack.getType().isFuel()
//            if (!isEquip && !isFuel) {
//                // Something in here is not a furnace fuel, or refuelable, abort.
//                return
//            }
//
//            if (isEquip) {
//                if (refuelableIndex != -1) {
//                    // We already have a refuelable found??? Don't be trying to charge two drills on me!
//                    return
//                }
//                refuelableIndex = i - 1
//            } else {
//                if (bp is IFurnaceFuel) totalRefuel += ((bp as IFurnaceFuel).getBurnTime() / 20).toInt() // Convert from ticks to seconds
//                else totalRefuel += itemStack.getType().asItemType()!!.getBurnDuration() / 20
//            }
//            totalItems++
//        }
//        if (totalItems <= 1) {
//            // Kinda hard to refuel if there is just one item in the grid, huh?
//            return
//        }
//        val returnRefuelable = matrix[refuelableIndex]!!.clone()
//        val blueprint = blueprint(returnRefuelable) as IFueledEquipment
//        val fuel = blueprint.getFuelUsed(returnRefuelable)
//        val newFuel = max(0, fuel - totalRefuel)
//        blueprint.setFuelUsed(returnRefuelable, newFuel)
//        blueprint(returnRefuelable).updateItemData(returnRefuelable)
//        event.getInventory().setResult(returnRefuelable)
//    }
}
