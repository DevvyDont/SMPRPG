package xyz.devvydont.smprpg.items.blueprints.sets.elderflame

import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.services.ItemService

class ElderflameChestplate(itemService: ItemService, type: CustomItemType) : ElderflameArmorSet(itemService, type) {

    override val itemClassification: ItemClassification get() = ItemClassification.CHESTPLATE

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, DEFENSE.toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.HEALTH, HEALTH.toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.ARMOR, 4.0),
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, STRENGTH),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, .25),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, CRIT.toDouble())
        )
    }

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        itemStack.setData(DataComponentTypes.GLIDER)
    }

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 8 }

    companion object {
        const val DEFENSE: Int = 375
        const val HEALTH: Int = 60
        const val STRENGTH: Double = .75
        const val CRIT: Int = 25
    }
}
