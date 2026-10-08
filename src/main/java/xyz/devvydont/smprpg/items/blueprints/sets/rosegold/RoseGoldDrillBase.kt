package xyz.devvydont.smprpg.items.blueprints.sets.rosegold

import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.interfaces.IModularToolComponent
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.items.ToolStats

class RoseGoldDrillBase(itemService: ItemService, type: CustomItemType) : RoseGoldAttributeItem(itemService, type),
    IModularToolComponent {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.MINING, toolStats.skillReqLevel))

    override fun getAttributes(): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.MINING_FORTUNE, ToolStats.ROSE_GOLD.fortune * 1.5, ATTR_KEY),
            AdditiveAttributeEntry(AttributeWrapper.MINING_POWER, -1.0, ATTR_KEY)
        )
    }

    override fun getAttrKey(): String {
        return ATTR_KEY
    }

    companion object {
        const val ATTR_KEY: String = "rose_gold_drill_base"
    }
}
