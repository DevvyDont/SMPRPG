package xyz.devvydont.smprpg.items.blueprints.sets.bronze

import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.*
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.items.ToolStats

abstract class BronzeArmorSet(itemService: ItemService, type: CustomItemType) :
    CustomAttributeItem(itemService, type), IEquippableAssetOverride, IRepairable, ISkillRequirement {

    override val repairMaterial : MutableCollection<ItemStack> get() = mutableListOf(getCraftingMaterial())
    val toolStats: ToolStats get() = ToolStats.BRONZE
    val armorDurabilityUnit: Int get() = toolStats.getArmorUnitDurability().toInt()

    override fun getAssetId(): Key { return key }

    override fun getPowerRating(): Int { return toolStats.power }

    open fun getCraftingMaterial(): ItemStack = itemService.getCustomItem(CustomItemType.BRONZE_INGOT)

    open fun getDisplayKey(): Key {
        return IModelOverridden.ofItemType(customItemType)
    }

    companion object {
        private val key = Key.key("bronze")
    }
}
