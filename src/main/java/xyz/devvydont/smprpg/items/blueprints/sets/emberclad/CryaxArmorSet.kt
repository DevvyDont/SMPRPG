package xyz.devvydont.smprpg.items.blueprints.sets.emberclad

import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.trim.TrimMaterial
import org.bukkit.inventory.meta.trim.TrimPattern
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.items.interfaces.ITrimmable
import xyz.devvydont.smprpg.services.ItemService

abstract class CryaxArmorSet(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, ITrimmable, IRepairable {

    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(CustomItemType.ENCHANTED_BLAZE_ROD))

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.ARMOR }

    override fun getPowerRating(): Int { return POWER }

    override fun getTrimMaterial(): TrimMaterial? { return TrimMaterial.COPPER }

    override fun getTrimPattern(): TrimPattern? { return TrimPattern.TIDE }

    companion object {
        const val POWER: Int = 35
        @JvmField
        val INGREDIENT: CustomItemType = CustomItemType.ENCHANTED_BLAZE_ROD
        const val ARMOR_DURABILITY_UNIT = 64
    }
}
