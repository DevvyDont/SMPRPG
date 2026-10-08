package xyz.devvydont.smprpg.items.blueprints.sets.palladium

import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemArmor
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

class PalladiumHelmet(itemService: ItemService, type: CustomItemType) : PalladiumArmorSet(itemService, type),
    IBreakableEquipment, IModelOverridden {

    override val itemClassification: ItemClassification get() = ItemClassification.HELMET
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.COMBAT, toolStats.skillReqLevel))

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(
                AttributeWrapper.DEFENSE,
                ItemArmor.getDefenseFromItemType(customItemType).toDouble()
            ),
            AdditiveAttributeEntry(AttributeWrapper.ARCANE_RATING, 2.0)
        )
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.HEAD
    }

    override fun getMaxDurability(): Int {
        return armorDurabilityUnit * 5
    }
}
