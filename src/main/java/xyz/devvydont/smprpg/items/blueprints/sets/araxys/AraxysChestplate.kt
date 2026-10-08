package xyz.devvydont.smprpg.items.blueprints.sets.araxys

import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.services.ItemService

class AraxysChestplate(itemService: ItemService, type: CustomItemType) : AraxysArmorSet(itemService, type) {

    override val itemClassification: ItemClassification get() = ItemClassification.CHESTPLATE

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.CHEST }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, DEFENSE.toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.HEALTH, HEALTH.toDouble()),
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, STRENGTH),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 40.0)
        )
    }

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 8 }

    companion object {
        const val DEFENSE: Int = 390
        const val HEALTH: Int = 155
        const val STRENGTH: Double = .75
        const val CRIT: Int = 40
    }
}
