package xyz.devvydont.smprpg.items.blueprints.sets.aetherium

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

abstract class AetheriumAttributeItem(itemService: ItemService, type: CustomItemType) : ToolSetAttributeItem(itemService,
    type), ISkillRequirement {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM
    override val toolStats: ToolStats get() = ToolStats.AETHERIUM

    override fun getCraftingMaterial(): ItemStack {
        return itemService.getCustomItem(CustomItemType.AETHERIUM_INGOT)
    }

    open fun getDisplayKey(): Key {
        return IModelOverridden.ofItemTypeInDirectory(customItemType, "material_sets/aetherium")
    }

    open fun getComponentPrefix(): String {
        return "Aetherium"
    }

    override fun getActiveSlot(): EquipmentSlotGroup? {
        return EquipmentSlotGroup.MAINHAND
    }
}