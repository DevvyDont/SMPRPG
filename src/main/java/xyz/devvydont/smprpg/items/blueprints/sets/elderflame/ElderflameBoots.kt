package xyz.devvydont.smprpg.items.blueprints.sets.elderflame

import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.services.ItemService

class ElderflameBoots(itemService: ItemService, type: CustomItemType) : ElderflameArmorSet(itemService, type) {

    override val itemClassification: ItemClassification get() = ItemClassification.BOOTS

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, 155.0),
            AdditiveAttributeEntry(AttributeWrapper.HEALTH, 35.0),
            AdditiveAttributeEntry(AttributeWrapper.ARMOR, 2.0),
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, ElderflameChestplate.Companion.STRENGTH - .25),
            ScalarAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, .3),
            AdditiveAttributeEntry(AttributeWrapper.SAFE_FALL, 10.0),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, ElderflameChestplate.Companion.CRIT.toDouble())
        )
    }

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 4 }
}
