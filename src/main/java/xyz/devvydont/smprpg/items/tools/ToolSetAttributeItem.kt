package xyz.devvydont.smprpg.items.tools

import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.items.ToolStats

abstract class ToolSetAttributeItem(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type), IRepairable {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(getCraftingMaterial())

    abstract val toolStats: ToolStats

    override fun getAttributeModifiers(item: ItemStack?): Collection<AttributeEntry?>? {
        return listOf()
    }

    open fun getCraftingMaterial(): ItemStack {
        throw NotImplementedError("Crafting material should be defined by child class. Use null if there should not be a recipe.")
    }

    override fun getPowerRating(): Int {
        return toolStats.power
    }

    open fun getMaxDurability(): Int {
        return toolStats.durability
    }

    override fun getActiveSlot(): EquipmentSlotGroup? {
        return EquipmentSlotGroup.MAINHAND
    }

}