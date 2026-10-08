package xyz.devvydont.smprpg.items.blueprints.sets.inferno

import net.kyori.adventure.key.Key
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.*
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

abstract class InfernoArmorSet(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, IEquippableAssetOverride, IRepairable, ISkillRequirement {
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(CRAFTING_COMPONENT))
    override val skillRequirements: MutableMap<SkillType, Int> = mutableMapOf(Pair(SkillType.COMBAT, 35))

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.ARMOR
    }

    override fun getAttributeModifiers(item: ItemStack): MutableCollection<AttributeEntry> {
        return mutableListOf<AttributeEntry>(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, this.defense.toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.HEALTH, this.health.toDouble()),
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, this.strength),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 25.0)
        )
    }

    abstract val defense: Int

    abstract val health: Int

    abstract val strength: Double

    override fun getPowerRating(): Int {
        return POWER
    }

    override fun getMaxDurability(): Int {
        return 50000
    }

    override fun getAssetId(): Key {
        return key
    }

    companion object {
        const val POWER: Int = 40
        @JvmField
        var CRAFTING_COMPONENT: CustomItemType = CustomItemType.INFERNO_REMNANT
        private val key = Key.key("inferno")
    }
}
