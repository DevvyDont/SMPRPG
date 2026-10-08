package xyz.devvydont.smprpg.items.blueprints.sets.forsaken

import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.items.blueprints.sets.reaver.ReaverArmorSet
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.items.interfaces.ISkillRequirement
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

abstract class ForsakenArmorSet(itemService: ItemService, type: CustomItemType) : ReaverArmorSet(itemService, type),
    IRepairable, ISkillRequirement {

    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf<ItemStack>(itemService.getCustomItem(CRAFTING_COMPONENT))
    override val skillRequirements: MutableMap<SkillType, Int> = mutableMapOf(Pair(SkillType.COMBAT, 25))

    override fun getWitherResistance(): Int { return 50 }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, getDefense().toDouble()),
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, getStrength()),
            AdditiveAttributeEntry(AttributeWrapper.KNOCKBACK_RESISTANCE, .25),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 20.0)
        )
    }

    abstract override fun getDefense(): Int
    abstract override fun getHealth(): Int
    abstract override fun getStrength(): Double

    override fun getPowerRating(): Int {
        return POWER
    }

    override fun getMaxDurability(): Int {
        return 40000
    }

    companion object {
        const val POWER: Int = 30
        const val DURABILITY: Int = 40000
        @JvmField
        val CRAFTING_COMPONENT: CustomItemType = CustomItemType.PREMIUM_NETHER_STAR
    }
}
