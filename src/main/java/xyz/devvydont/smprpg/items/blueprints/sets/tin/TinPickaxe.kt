package xyz.devvydont.smprpg.items.blueprints.sets.tin

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.Tool
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys
import net.kyori.adventure.util.TriState
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemPickaxe
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.items.ToolGlobals

class TinPickaxe(itemService: ItemService, type: CustomItemType) : TinAttributeItem(itemService, type), IBreakableEquipment {

    override val itemClassification: ItemClassification get() = ItemClassification.PICKAXE
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.MINING, toolStats.skillReqLevel))

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?>? {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.MINING_POWER, toolStats.miningPower.toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, ItemPickaxe.getPickaxeDamage(Material.WOODEN_PICKAXE)),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, ItemPickaxe.PICKAXE_ATTACK_SPEED_DEBUFF),
            AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, toolStats.speed.toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.MINING_FORTUNE, toolStats.fortune.toDouble())
        )
    }

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        itemStack.setData<Tool?>(DataComponentTypes.TOOL, TOOL_COMP)
    }

    companion object {
        val TOOL_COMP: Tool = Tool.tool()
            .addRule(
                Tool.rule(
                    ToolGlobals.blockRegistry.getTag(BlockTypeTagKeys.MINEABLE_PICKAXE),
                    7.0f,
                    TriState.TRUE
                )
            ).build()
    }
}
