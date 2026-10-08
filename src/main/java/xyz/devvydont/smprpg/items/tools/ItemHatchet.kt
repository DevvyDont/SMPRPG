package xyz.devvydont.smprpg.items.tools

import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IDamageFromCrops
import xyz.devvydont.smprpg.services.ItemService

open class ItemHatchet(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, IDamageFromCrops {

    override val itemClassification: ItemClassification get() = ItemClassification.HATCHET

    open val hatchetMiningPower: Double get() = 0.0
    open val hatchetDamage: Double get() = 1.0
    open val hatchetFortune: Double get() = 1.0
    open val hatchetSpeed: Double get() = 1.0

    override fun getPowerRating(): Int { return 0 }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.MINING_POWER, this.hatchetMiningPower),
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, this.hatchetDamage),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, HATCHET_ATTACK_SPEED_DEBUFF),
            AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, this.hatchetSpeed),
            AdditiveAttributeEntry(AttributeWrapper.WOODCUTTING_FORTUNE, this.hatchetFortune),
            AdditiveAttributeEntry(AttributeWrapper.FARMING_FORTUNE, this.hatchetFortune)
        )
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.MAINHAND
    }

    override fun getMaxDurability(): Int {
        return 50000
    }

    companion object {
        var HATCHET_ATTACK_SPEED_DEBUFF: Double = -0.8
    }
}
