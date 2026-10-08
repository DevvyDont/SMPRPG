package xyz.devvydont.smprpg.items.blueprints.sets.wood

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.Tool
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword.Companion.getSwordDamage
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IDamageFromCrops
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.items.tools.ItemHatchet
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.items.ToolGlobals
import xyz.devvydont.smprpg.util.items.ToolStats

class WoodHatchet(itemService: ItemService, type: CustomItemType) : ItemHatchet(itemService, type),
    IBreakableEquipment, IRepairable, IDamageFromCrops {

    override fun getPowerRating(): Int { return ToolStats.WOOD.power }

    override val repairMaterial : MutableCollection<ItemStack> = mutableListOf(itemService.getCustomItem(Material.OAK_PLANKS), itemService.getCustomItem(Material.BIRCH_PLANKS), itemService.getCustomItem(Material.SPRUCE_PLANKS),
        itemService.getCustomItem(Material.JUNGLE_PLANKS), itemService.getCustomItem(Material.DARK_OAK_PLANKS), itemService.getCustomItem(Material.ACACIA_PLANKS),
        itemService.getCustomItem(Material.CRIMSON_PLANKS), itemService.getCustomItem(Material.WARPED_PLANKS), itemService.getCustomItem(Material.MANGROVE_PLANKS),
        itemService.getCustomItem(Material.CHERRY_PLANKS), itemService.getCustomItem(Material.PALE_OAK_PLANKS), itemService.getCustomItem(Material.BAMBOO_PLANKS))

    override val hatchetMiningPower: Double get() = ToolStats.WOOD.miningPower.toDouble()
    override val hatchetDamage: Double get() = getSwordDamage(Material.WOODEN_SWORD) - 5
    override val hatchetFortune: Double get() = ToolStats.WOOD.fortune * 0.8
    override val hatchetSpeed: Double get() = ToolStats.WOOD.speed * 0.8

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        itemStack.setData<Tool?>(DataComponentTypes.TOOL, TOOL_COMP)
    }

    override fun getMaxDurability(): Int {
        return ToolGlobals.WOOD_TOOL_DURABILITY
    }

    companion object {
        val TOOL_COMP: Tool = Tool.tool()
            .defaultMiningSpeed(0.0001f)
            .build()
    }
}
