package xyz.devvydont.smprpg.items.blueprints.sets.amethyst

import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.meta.trim.TrimPattern
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.services.ItemService

class AmethystLeggings(itemService: ItemService, type: CustomItemType) : AmethystArmorSet(itemService, type) {

    override val itemClassification: ItemClassification get() = ItemClassification.LEGGINGS
    override val defense: Int get() = 10
    override val health: Int get() = 10

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.LEGS
    }

    override fun getTrimPattern(): TrimPattern? {
        return TrimPattern.SILENCE
    }

    override fun getMaxDurability(): Int {
        return armorDurabilityUnit * 7
    }

}
