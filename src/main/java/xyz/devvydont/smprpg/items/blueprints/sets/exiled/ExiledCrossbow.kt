package xyz.devvydont.smprpg.items.blueprints.sets.exiled

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemAxe.Companion.getAxeDamage
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.services.ItemService
import java.util.List

class ExiledCrossbow(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment {

    override val itemClassification: ItemClassification get() = ItemClassification.CROSSBOW

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf<AttributeEntry?>(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 100.0)
        )
    }

    override fun getPowerRating(): Int { return 25 }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.HAND }

    override fun getMaxDurability(): Int { return 1024 }
}
