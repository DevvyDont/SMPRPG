package xyz.devvydont.smprpg.items.blueprints.sets.emberclad

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.trim.TrimPattern
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.services.ItemService

class CryaxHelmet(itemService: ItemService, type: CustomItemType) : CryaxArmorSet(itemService, type) {

    override val itemClassification: ItemClassification get() = ItemClassification.HELMET

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, 130.0),
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, .25),
            ScalarAttributeEntry(AttributeWrapper.BURNING_TIME, -.25)
        )
    }

    override fun getTrimPattern(): TrimPattern? { return TrimPattern.FLOW }

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 5 }

}
