package xyz.devvydont.smprpg.items.blueprints.sets.bone

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.trim.TrimMaterial
import org.bukkit.inventory.meta.trim.TrimPattern
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.items.interfaces.ITrimmable
import xyz.devvydont.smprpg.services.ItemService

abstract class BoneArmorSet(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, ITrimmable, IRepairable {

    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(Material.BONE))

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, this.defense.toDouble()),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -0.03)
        )
    }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.ARMOR }

    abstract val defense: Int

    override fun getTrimMaterial(): TrimMaterial? { return TrimMaterial.IRON }

    override fun getTrimPattern(): TrimPattern? { return TrimPattern.SHAPER }

    override fun getPowerRating(): Int { return 10 }

    companion object {
        const val ARMOR_DURABILITY_UNIT : Int = 14
    }
}
