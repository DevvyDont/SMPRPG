package xyz.devvydont.smprpg.items.blueprints.sets.cobalt

import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.interfaces.IModularToolComponent
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

class CobaltDrillBase(itemService: ItemService, type: CustomItemType) : CobaltAttributeItem(itemService, type),
    IModularToolComponent {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.MINING, toolStats.skillReqLevel))

    override fun getAttributes(): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.MINING_FORTUNE, toolStats.fortune * 1.5, attrKey)
        )
    }

    override fun getAttrKey(): String {
        return Companion.attrKey
    }

    companion object {
        const val attrKey: String = "cobalt_drill_base"
    }
}
