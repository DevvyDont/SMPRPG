package xyz.devvydont.smprpg.items.blueprints.sets.bedrock

import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.trim.TrimMaterial
import org.bukkit.inventory.meta.trim.TrimPattern
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.ITrimmable
import xyz.devvydont.smprpg.services.ItemService

abstract class BedrockArmorSet(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, ITrimmable {
    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, this.defense.toDouble()),
            ScalarAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, -.2),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -.25),
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, .25)
        )
    }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.ARMOR }

    abstract val defense: Int

    override fun getPowerRating(): Int { return POWER }

    override fun getTrimMaterial(): TrimMaterial? { return TrimMaterial.NETHERITE }

    override fun getTrimPattern(): TrimPattern? { return TrimPattern.RIB }

    companion object {
        const val POWER: Int = 30
        const val ARMOR_DURABILITY_UNIT: Int = 1_024
    }
}
