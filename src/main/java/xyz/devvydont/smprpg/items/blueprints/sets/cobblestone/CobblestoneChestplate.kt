package xyz.devvydont.smprpg.items.blueprints.sets.cobblestone

import org.bukkit.inventory.EquipmentSlotGroup
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.services.ItemService

class CobblestoneChestplate(itemService: ItemService, type: CustomItemType) : CobblestoneArmorSet(itemService, type) {

    override val itemClassification: ItemClassification get() = ItemClassification.CHESTPLATE
    override val defense: Int get() = 70

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.CHEST }

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 8 }

}
