package xyz.devvydont.smprpg.items.blueprints.sets.cultivator

import net.kyori.adventure.key.Key
import org.bukkit.NamespacedKey
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISkillRequirement
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

abstract class CultivatorArmorSet(itemService: ItemService, type: CustomItemType) :
    CustomAttributeItem(itemService, type), IBreakableEquipment, IModelOverridden, ISkillRequirement {

    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.FARMING, 15))

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, getDefense().toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.FARMING_FORTUNE, 25.0),
            AdditiveAttributeEntry(AttributeWrapper.FARMING_PROFICIENCY, 5.0),
            AdditiveAttributeEntry(AttributeWrapper.CRITTER_CHANCE, 35.0)
        )
    }

    fun getDefense(): Int {
        return when (itemClassification) {
            ItemClassification.HELMET -> 35
            ItemClassification.CHESTPLATE -> 60
            ItemClassification.LEGGINGS -> 45
            ItemClassification.BOOTS -> 25
            else -> 0
        }
    }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.ARMOR }

    override fun getPowerRating(): Int { return 10 }

    override fun getMaxDurability(): Int { return 512 }

    override fun getDisplayKey(): Key {
        return IModelOverridden.ofItemTypeInDirectory(customItemType, "armor_sets/cultivator")
    }

    companion object {
        val assetKey = NamespacedKey(SMPRPG.plugin, "cultivator")
    }
}
