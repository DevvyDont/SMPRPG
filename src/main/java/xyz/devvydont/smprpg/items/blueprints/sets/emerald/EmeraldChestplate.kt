package xyz.devvydont.smprpg.items.blueprints.sets.emerald

import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.services.ItemService

class EmeraldChestplate(itemService: ItemService, type: CustomItemType) : EmeraldArmorSet(itemService, type) {

    override val itemClassification: ItemClassification get() = ItemClassification.CHESTPLATE

    override val defense: Double get() = 115.0

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 8 }

}
