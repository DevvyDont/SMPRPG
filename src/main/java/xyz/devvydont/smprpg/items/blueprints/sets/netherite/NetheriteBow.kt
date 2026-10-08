package xyz.devvydont.smprpg.items.blueprints.sets.netherite

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword.Companion.getSwordDamage
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword.Companion.getSwordRating
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.items.interfaces.ISkillRequirement
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.items.ToolStats

class NetheriteBow(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, IRepairable, ISkillRequirement {

    override val itemClassification: ItemClassification get() = ItemClassification.BOW
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(Material.NETHERITE_INGOT))
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.COMBAT, ToolStats.NETHERITE.skillReqLevel))

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getSwordDamage(Material.NETHERITE_SWORD))
        )
    }

    override fun getPowerRating(): Int { return getSwordRating(Material.NETHERITE_SWORD) }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.HAND }

    override fun getMaxDurability(): Int { return ToolStats.NETHERITE.durability }

    companion object {
        const val DAMAGE: Double = 80.0
    }
}
