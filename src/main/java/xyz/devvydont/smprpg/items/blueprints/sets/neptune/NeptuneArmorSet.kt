package xyz.devvydont.smprpg.items.blueprints.sets.neptune

import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.items.interfaces.ISkillRequirement
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

abstract class NeptuneArmorSet(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, IRepairable, ISkillRequirement {

    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(CustomItemType.JUPITERS_ARTIFACT))
    override val skillRequirements: MutableMap<SkillType, Int> = mutableMapOf(Pair(SkillType.COMBAT, 15))

    override fun wantNerfedSellPrice(): Boolean {
        return false
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.ARMOR
    }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, this.defense.toDouble()),
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, this.strength / 100.0),
            AdditiveAttributeEntry(AttributeWrapper.OXYGEN_BONUS, OXYGEN_BONUS.toDouble()),
            ScalarAttributeEntry(AttributeWrapper.BURNING_TIME, -.1),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 10.0)
        )
    }

    abstract val defense: Int

    abstract val strength: Int

    override fun getMaxDurability(): Int {
        return DURABILITY
    }

    override fun getPowerRating(): Int {
        return POWER_LEVEL
    }

    companion object {
        const val POWER_LEVEL: Int = 20
        const val OXYGEN_BONUS: Int = 20
        const val DURABILITY: Int = 25000
    }
}
