package xyz.devvydont.smprpg.items.blueprints.craftengine

import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService

open class CraftEngineCompressibleBlueprint(itemService: ItemService, type: CustomItemType) : CraftEngineBlueprint(itemService, type),
    IModelOverridden, ISellable {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM
}