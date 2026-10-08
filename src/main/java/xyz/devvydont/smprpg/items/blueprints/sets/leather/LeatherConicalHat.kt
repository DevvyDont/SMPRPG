package xyz.devvydont.smprpg.items.blueprints.sets.leather

import io.papermc.paper.datacomponent.item.Equippable
import net.kyori.adventure.key.Key
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.*
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.items.ToolStats
import java.util.List

class LeatherConicalHat(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, IDyeable, IModelOverridden, IEquippableOverride, IRepairable {

    override val itemClassification: ItemClassification get() = ItemClassification.HELMET
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(Material.LEATHER))

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, 5.0),
            AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, 10.0)
        )
    }

    override fun getPowerRating(): Int { return ToolStats.WOOD.power }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.HEAD }

    override fun getMaxDurability(): Int { return (ToolStats.WOOD.getArmorUnitDurability() * 4).toInt() }

    override fun getColor(): Color { return Color.fromRGB(0x9e643f) }

    override fun getDisplayKey(): Key { return IModelOverridden.ofItemType(customItemType) }

    override fun getEquipmentOverride(): Equippable {
        return IEquippableOverride.generateDefault(EquipmentSlot.HEAD)
    }

}
