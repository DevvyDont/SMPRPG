package xyz.devvydont.smprpg.items.blueprints.sets.cobblestone

import org.bukkit.inventory.EquipmentSlotGroup
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.services.ItemService

class CobblestoneBoots(itemService: ItemService, type: CustomItemType) : CobblestoneArmorSet(itemService, type) {

    override val itemClassification: ItemClassification get() = ItemClassification.BOOTS
    override val defense: Int get() = 40

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.FEET }

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 4 }

}
