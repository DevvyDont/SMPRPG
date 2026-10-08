package xyz.devvydont.smprpg.items.blueprints.sets.adamantium

import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.interfaces.IModularToolComponent
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

class AdamantiumDrillHead(itemService: ItemService, type: CustomItemType) : AdamantiumAttributeItem(itemService, type),
    IModularToolComponent {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.MINING, toolStats.skillReqLevel))

    override fun getAttributes(): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, 1650.0, ATTR_KEY),
            AdditiveAttributeEntry(AttributeWrapper.MINING_POWER, 5.0, ATTR_KEY)
        )
    }

    override fun getAttrKey(): String {
        return ATTR_KEY
    }

    companion object {
        const val ATTR_KEY: String = "adamantium_drill_head"
    }
}
