package xyz.devvydont.smprpg.items.blueprints.vanilla

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.base.VanillaAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.items.interfaces.ISkillRequirement
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.items.ToolStats

class ItemShears(itemService: ItemService, material: Material) : VanillaAttributeItem(itemService, material),
    IBreakableEquipment, IRepairable, ISkillRequirement {

    override val itemClassification: ItemClassification get() = ItemClassification.SHEARS
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(Material.IRON_INGOT))
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.FARMING, ToolStats.IRON.skillReqLevel))

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.MAINHAND
    }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 5.0),
            AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, 750.0),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -.1)
        )
    }

    override fun getPowerRating(): Int { return 2 }

    override fun getMaxDurability(): Int { return ToolStats.IRON.durability }

}
