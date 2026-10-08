package xyz.devvydont.smprpg.items.blueprints.sets.emerald

import net.kyori.adventure.key.Key
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IEquippableAssetOverride
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.services.ItemService

abstract class EmeraldArmorSet(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, IEquippableAssetOverride, IRepairable {

    abstract val defense: Double
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(INGREDIENT))

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.ARMOR }

    override fun getAssetId(): Key { return ASSET_KEY }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, this.defense),
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, 0.1)
        )
    }

    override fun getPowerRating(): Int { return EMERALD_POWER }

    companion object {
        const val ARMOR_DURABILITY_UNIT = 48
        const val EMERALD_POWER: Int = 20
        @JvmField
        var INGREDIENT: Material = Material.EMERALD_BLOCK

        private val ASSET_KEY = Key.key("emerald")
    }
}
