package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.base.AttributeEnchantment
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeModifierType
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class VitalityEnchantment(id: String) : CustomEnchantment(id), AttributeEnchantment {
    override val displayName: Component get() = ComponentUtils.create("Vitality")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases strength by "),
            ComponentUtils.create("+" + getStrength(level) + "%", NamedTextColor.GREEN)
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(56, 0, 0)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_ARMOR
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.RARE.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.ARMOR
    override val skillRequirement: Int get()                   = 6

    override val powerRating : Int get() = level / 2
    override val attributeModifierType : AttributeModifierType get() = AttributeModifierType.ENCHANTMENT
    override fun getHeldAttributes() : MutableCollection<AttributeEntry?>? {
        return mutableListOf(
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, getStrength(level) / 100.0)
        )
    }

    companion object {
        fun getStrength(level: Int): Int {
            return when (level) {
                0 -> 0
                1 -> 5
                2 -> 8
                3 -> 12
                4 -> 18
                5 -> 25
                else -> 5 * level + getStrength(5)
            }
        }
    }
}
