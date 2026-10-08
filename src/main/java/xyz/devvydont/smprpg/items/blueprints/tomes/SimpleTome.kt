package xyz.devvydont.smprpg.items.blueprints.tomes

import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.services.ItemService

class SimpleTome(itemService: ItemService, type: CustomItemType) : TomeBlueprint(itemService, type) {

    override val maxSpellSlots: Int get() = 1
    override val cooldownMult: Double get() = 1.1

    override fun updateItemData(itemStack: ItemStack) {
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1)
        super.updateItemData(itemStack)
    }

    override fun getAttributeModifiers(item: ItemStack): MutableCollection<AttributeEntry> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, 100.0),
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 5.0)
        )
    }

    override fun getPowerRating(): Int { return 10 }
    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.HAND }
}
