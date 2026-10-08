package xyz.devvydont.smprpg.items.blueprints.tomes

import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.interfaces.ISkillRequirement
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

class BookOfShadows(itemService: ItemService, type: CustomItemType) : TomeBlueprint(itemService, type),
    ISkillRequirement {

    override val maxSpellSlots: Int get() = 4
    override val cooldownMult: Double get() = 0.9
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(
        Pair(SkillType.COMBAT, 30),
        Pair(SkillType.MAGIC, 40)
    )

    override fun updateItemData(itemStack: ItemStack) {
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1)
        super.updateItemData(itemStack)
    }

    override fun getAttributeModifiers(item: ItemStack): MutableCollection<AttributeEntry> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, 400.0),
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 100.0),
            AdditiveAttributeEntry(AttributeWrapper.ARCANE_RATING, 5.0)
        )
    }

    override fun getPowerRating(): Int { return 10 }
    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.HAND }
    
}
