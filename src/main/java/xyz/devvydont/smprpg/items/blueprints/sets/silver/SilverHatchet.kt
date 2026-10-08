package xyz.devvydont.smprpg.items.blueprints.sets.silver

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.Tool
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemAxe
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IDamageFromCrops
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.items.ToolGlobals

class SilverHatchet(itemService: ItemService, type: CustomItemType) : SilverAttributeItem(itemService, type),
    IBreakableEquipment, IDamageFromCrops {

    override val itemClassification: ItemClassification get() = ItemClassification.HATCHET
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(
        Pair(SkillType.WOODCUTTING, toolStats.skillReqLevel),
        Pair(SkillType.FARMING, toolStats.skillReqLevel)
    )

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.MINING_POWER, ToolGlobals.SILVER_TOOL_MINING_POWER.toDouble()),
            AdditiveAttributeEntry(
                AttributeWrapper.STRENGTH,
                ItemSword.getSwordDamage(CustomItemType.SILVER_SWORD) - 5
            ),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, ItemAxe.AXE_ATTACK_SPEED_DEBUFF),
            AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, toolStats.speed * 0.8),
            AdditiveAttributeEntry(AttributeWrapper.WOODCUTTING_FORTUNE, toolStats.fortune * 0.8),
            AdditiveAttributeEntry(AttributeWrapper.FARMING_FORTUNE, toolStats.fortune * 0.8)
        )
    }

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        itemStack.setData<Tool?>(DataComponentTypes.TOOL, TOOL_COMP)
    }

    companion object {
        val TOOL_COMP: Tool = Tool.tool()
            .defaultMiningSpeed(0.0001f)
            .build()
    }
}
