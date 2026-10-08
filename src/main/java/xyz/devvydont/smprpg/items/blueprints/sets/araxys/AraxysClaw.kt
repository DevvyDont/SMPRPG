package xyz.devvydont.smprpg.items.blueprints.sets.araxys

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.blueprints.sets.araxys.AraxysArmorSet.Companion.ARMOR_DURABILITY_UNIT
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword.Companion.getSwordDamage
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.services.ItemService

class AraxysClaw(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment {

    override val itemClassification: ItemClassification get() = ItemClassification.WEAPON

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getSwordDamage(Material.NETHERITE_SWORD) * 2),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -.25)
        )
    }

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 8 }

    override fun getPowerRating(): Int {
        return AraxysArmorSet.Companion.POWER
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.MAINHAND
    }
}
