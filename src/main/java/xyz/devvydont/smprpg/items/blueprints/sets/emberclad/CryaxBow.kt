package xyz.devvydont.smprpg.items.blueprints.sets.emberclad

import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.services.ItemService

class CryaxBow(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment {

    override val itemClassification: ItemClassification get() = ItemClassification.BOW

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 100.0),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 30.0)
        )
    }

    override fun getPowerRating(): Int { return CryaxArmorSet.POWER }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.HAND }

    override fun getMaxDurability(): Int { return CryaxArmorSet.ARMOR_DURABILITY_UNIT * 8 }

}
