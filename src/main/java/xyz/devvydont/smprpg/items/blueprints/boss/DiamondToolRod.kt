package xyz.devvydont.smprpg.items.blueprints.boss

import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.services.ItemService

class DiamondToolRod(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type) {
    override val itemClassification: ItemClassification get() = ItemClassification.MATERIAL
}
