package xyz.devvydont.smprpg.items.blueprints.sets.elderflame

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
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.services.ItemService

class ElderflameDagger(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, IRepairable {
    override val itemClassification: ItemClassification get() = ItemClassification.SWORD
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(CustomItemType.DRACONIC_CRYSTAL))

    /**
     * What modifiers themselves will be contained on the item if there are no variables to affect them?
     *
     * @param item The item that is supposed to be holding the modifiers.
     * @return
     */
    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 250.0),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 75.0),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -.4)
        )
    }

    override fun getPowerRating(): Int { return 50 }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.MAINHAND }

    override fun getMaxDurability(): Int { return ElderflameArmorSet.ARMOR_DURABILITY_UNIT * 8 }
}
