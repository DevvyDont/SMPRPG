package xyz.devvydont.smprpg.items.blueprints.sets.emberclad

import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.services.ItemService

class CryaxLeggings(itemService: ItemService, type: CustomItemType) : CryaxArmorSet(itemService, type) {

    override val itemClassification: ItemClassification get() = ItemClassification.LEGGINGS

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, 175.0),
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, .3),
            ScalarAttributeEntry(AttributeWrapper.BURNING_TIME, -.25)
        )
    }

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 7 }

}
