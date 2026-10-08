package xyz.devvydont.smprpg.items.blueprints.sets.araxys

import io.papermc.paper.datacomponent.item.Equippable
import net.kyori.adventure.key.Key
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IEquippableOverride
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.services.ItemService
import java.util.List

class AraxysHelmet(itemService: ItemService, type: CustomItemType) : AraxysArmorSet(itemService, type),
    IBreakableEquipment, IModelOverridden, IEquippableOverride {

    override val itemClassification: ItemClassification get() = ItemClassification.HELMET

    override fun getDisplayKey(): Key? { return IModelOverridden.ofMaterial(Material.SPAWNER) }

    override fun getEquipmentOverride(): Equippable { return Equippable.equippable(EquipmentSlot.HEAD).build() }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.HEAD }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.DEFENSE, 225.0),
            AdditiveAttributeEntry(AttributeWrapper.HEALTH, 90.0),
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, AraxysChestplate.Companion.STRENGTH),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, AraxysChestplate.Companion.CRIT.toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, 200.0)
        )
    }

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 5 }
}
