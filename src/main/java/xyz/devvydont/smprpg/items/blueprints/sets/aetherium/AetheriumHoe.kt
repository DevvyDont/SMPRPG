package xyz.devvydont.smprpg.items.blueprints.sets.aetherium

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.Tool
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemHoe
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IDamageFromCrops
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

class AetheriumHoe(itemService: ItemService, type: CustomItemType) : AetheriumAttributeItem(itemService, type),
    IBreakableEquipment, IModelOverridden, IDamageFromCrops {

    override val itemClassification: ItemClassification get() = ItemClassification.HOE
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.FARMING, toolStats.skillReqLevel))

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?>? {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, ItemHoe.getHoeDamage(customItemType)),
            MultiplicativeAttributeEntry(
                AttributeWrapper.ATTACK_SPEED,
                ItemHoe.getHoeAttackSpeedDebuff(customItemType)
            ),
            AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, toolStats.speed.toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.FARMING_FORTUNE, toolStats.fortune.toDouble())
        )
    }

    override fun getActiveSlot(): EquipmentSlotGroup? {
        return EquipmentSlotGroup.MAINHAND
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
