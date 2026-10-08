package xyz.devvydont.smprpg.items.blueprints.sets.bedrock

import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.services.ItemService

class BedrockLeggings(itemService: ItemService, type: CustomItemType) : BedrockArmorSet(itemService, type) {

    override val itemClassification: ItemClassification get() = ItemClassification.CHESTPLATE
    override val defense: Int get() = 275

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 7 }
}
