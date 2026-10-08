package xyz.devvydont.smprpg.items.blueprints.vanilla

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.base.VanillaAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IMace
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.services.ItemService

class ItemMace(itemService: ItemService, material: Material) : VanillaAttributeItem(itemService, material),
    IBreakableEquipment, IMace, IRepairable {

    override val itemClassification: ItemClassification get() = ItemClassification.MACE
    override val defaultRarity: ItemRarity get() = ItemRarity.EPIC
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(Material.BREEZE_ROD))

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.MAINHAND }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, MACE_ATTACK_DAMAGE.toDouble()),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, MACE_ATTACK_SPEED_DEBUFF)
        )
    }

    override fun getPowerRating(): Int { return MACE_POWER_RATING }

    override fun getMaxDurability(): Int { return MACE_DURABILITY }

    override fun getVelocityMultiplier(): Double { return 0.5 }

    companion object {
        const val MACE_POWER_RATING: Int = 30
        const val MACE_DURABILITY: Int = 10000
        const val MACE_ATTACK_DAMAGE: Int = 100
        const val MACE_ATTACK_SPEED_DEBUFF: Double = -0.85
    }
}
