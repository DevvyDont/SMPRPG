package xyz.devvydont.smprpg.items.blueprints.sets.tin

import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemArmor
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

class TinLeggings(itemService: ItemService, type: CustomItemType) : TinArmorSet(itemService, type), IBreakableEquipment {

    override val itemClassification: ItemClassification get() = ItemClassification.LEGGINGS
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.COMBAT, toolStats.skillReqLevel))

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(
                AttributeWrapper.DEFENSE,
                ItemArmor.getDefenseFromItemType(CustomItemType.TIN_LEGGINGS).toDouble()
            ),
            AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, -5.0),
            ScalarAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, .04)
        )
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.LEGS
    }

    override fun getMaxDurability(): Int {
        return armorDurabilityUnit * 7
    }

}
