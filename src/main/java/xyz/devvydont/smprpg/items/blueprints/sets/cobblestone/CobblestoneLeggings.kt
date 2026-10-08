package xyz.devvydont.smprpg.items.blueprints.sets.cobblestone

import org.bukkit.inventory.EquipmentSlotGroup
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.services.ItemService

class CobblestoneLeggings(itemService: ItemService, type: CustomItemType) : CobblestoneArmorSet(itemService, type) {

    override val itemClassification: ItemClassification get() = ItemClassification.HELMET
    override val defense: Int get() = 60

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.HEAD }

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 7 }

}
