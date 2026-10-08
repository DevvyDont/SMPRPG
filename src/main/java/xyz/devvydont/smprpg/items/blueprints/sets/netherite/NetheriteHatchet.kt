package xyz.devvydont.smprpg.items.blueprints.sets.netherite

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.Tool
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemPickaxe.Companion.getPickaxeFortune
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword.Companion.getSwordDamage
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword.Companion.getSwordRating
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IDamageFromCrops
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.items.interfaces.ISkillRequirement
import xyz.devvydont.smprpg.items.tools.ItemHatchet
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.items.ToolGlobals
import xyz.devvydont.smprpg.util.items.ToolStats
import java.util.List

class NetheriteHatchet(itemService: ItemService, type: CustomItemType) : ItemHatchet(itemService, type),
    IBreakableEquipment, IRepairable, ISkillRequirement, IDamageFromCrops {

    override val repairMaterial : MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(Material.NETHERITE_INGOT))
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(
        Pair(SkillType.WOODCUTTING, ToolStats.NETHERITE.skillReqLevel),
        Pair(SkillType.FARMING, ToolStats.NETHERITE.skillReqLevel)
    )

    override val hatchetMiningPower: Double get() = ToolStats.NETHERITE.miningPower.toDouble()
    override val hatchetDamage: Double get() = getSwordDamage(Material.NETHERITE_SWORD) - 25
    override val hatchetFortune: Double get() = getPickaxeFortune(Material.NETHERITE_PICKAXE) * 0.8

    override fun getPowerRating(): Int { return ToolStats.NETHERITE.power }

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        itemStack.setData(DataComponentTypes.TOOL, TOOL_COMP)
    }

    override fun getMaxDurability(): Int { return ToolStats.NETHERITE.durability }

    companion object {
        val TOOL_COMP: Tool = Tool.tool()
            .defaultMiningSpeed(0.0001f)
            .build()
    }
}
