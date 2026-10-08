package xyz.devvydont.smprpg.items.blueprints.sets.bone

import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.services.ItemService

class BoneHelmet(itemService: ItemService, type: CustomItemType) : BoneArmorSet(itemService, type) {

    override val itemClassification: ItemClassification get() = ItemClassification.HELMET
    override val defense: Int get() = 35

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 5 }
}
