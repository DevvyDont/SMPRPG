package xyz.devvydont.smprpg.items.blueprints.sets.platinum

import net.kyori.adventure.key.Key
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISkillRequirement
import xyz.devvydont.smprpg.items.tools.ToolSetAttributeItem
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.items.ToolStats

abstract class PlatinumAttributeItem(itemService: ItemService, type: CustomItemType) : ToolSetAttributeItem(itemService,
    type), ISkillRequirement {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM
    override val toolStats: ToolStats get() = ToolStats.PLATINUM

    override fun getCraftingMaterial(): ItemStack {
        return itemService.getCustomItem(CustomItemType.PLATINUM_INGOT)
    }

    open fun getDisplayKey(): Key {
        return IModelOverridden.ofItemTypeInDirectory(customItemType, "material_sets/platinum")
    }

    open fun getComponentPrefix(): String {
        return "Platinum"
    }

    override fun getActiveSlot(): EquipmentSlotGroup? {
        return EquipmentSlotGroup.MAINHAND
    }
}