package xyz.devvydont.smprpg.items.blueprints.sets.inferno

import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry.Companion.additive
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomShortbow
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.items.interfaces.ISkillRequirement
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

class InfernoShortbow(itemService: ItemService, type: CustomItemType) : CustomShortbow(itemService, type),
    ISellable, IBreakableEquipment, IRepairable, ISkillRequirement {

    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(CustomItemType.INFERNO_REMNANT))
    override val skillRequirements: MutableMap<SkillType, Int> = mutableMapOf(Pair(SkillType.COMBAT, 35))

    override fun getAttributeModifiers(item: ItemStack): MutableCollection<AttributeEntry> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 120.0),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -.4),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 75.0),
            additive(AttributeWrapper.CRITICAL_CHANCE, 50.0)
        )
    }

    override fun getPowerRating(): Int {
        return InfernoArmorSet.POWER
    }

    override fun getMaxDurability(): Int {
        return 40000
    }

}
