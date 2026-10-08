package xyz.devvydont.smprpg.items.blueprints.sets.platinum

import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemArmor
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

class PlatinumBoots(itemService: ItemService, type: CustomItemType) : PlatinumArmorSet(itemService, type),
    IBreakableEquipment, IModelOverridden {

    override val itemClassification: ItemClassification get() = ItemClassification.BOOTS
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.COMBAT, toolStats.skillReqLevel))

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(
                AttributeWrapper.DEFENSE,
                ItemArmor.getDefenseFromItemType(customItemType).toDouble()
            ),
            AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, 20.0),
            MultiplicativeAttributeEntry(AttributeWrapper.STRENGTH, .3)
        )
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.FEET
    }

    override fun getMaxDurability(): Int {
        return armorDurabilityUnit * 4
    }
}
