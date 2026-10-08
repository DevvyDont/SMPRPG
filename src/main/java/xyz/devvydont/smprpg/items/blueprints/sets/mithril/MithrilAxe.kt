package xyz.devvydont.smprpg.items.blueprints.sets.mithril

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
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

class MithrilAxe(itemService: ItemService, type: CustomItemType) : MithrilAttributeItem(itemService, type),
    IBreakableEquipment {

    override val itemClassification: ItemClassification get() = ItemClassification.AXE
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.WOODCUTTING, toolStats.skillReqLevel))

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.MINING_POWER, toolStats.miningPower.toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, ItemAxe.getAxeDamage(CustomItemType.MITHRIL_AXE)),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, ItemAxe.AXE_ATTACK_SPEED_DEBUFF),
            AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, toolStats.speed.toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.WOODCUTTING_FORTUNE, toolStats.fortune.toDouble())
        )
    }

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        itemStack.setData<Tool?>(DataComponentTypes.TOOL, TOOL_COMP)
    }

    companion object {
        val TOOL_COMP: Tool = Tool.tool()
            .build()
    }
}
