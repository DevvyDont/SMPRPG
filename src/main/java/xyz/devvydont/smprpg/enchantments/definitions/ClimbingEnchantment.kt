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

class ClimbingEnchantment(id: String) : CustomEnchantment(id), AttributeEnchantment {
    override val displayName: Component get() = ComponentUtils.create("Climbing")
    override val description: Component
        get() = ComponentUtils.merge(
            ComponentUtils.create("Increases block step height by "),
            ComponentUtils.create(
                String.format("+%d%%", (getStepIncrease(level) * 100).toInt()),
                NamedTextColor.GREEN
            )
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(79, 112, 33)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_FOOT_ARMOR
    override val maxLevel: Int get()                           = 3
    override val weight: Int get()                             = EnchantmentRarity.RARE.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.FEET
    override val skillRequirement: Int get()                   = 38

    override val powerRating : Int get() = 0
    override val attributeModifierType : AttributeModifierType get() = AttributeModifierType.ENCHANTMENT
    override fun getHeldAttributes() : MutableCollection<AttributeEntry?>? {
        return mutableListOf(
            ScalarAttributeEntry(AttributeWrapper.STEP, getStepIncrease(level))
        )
    }

    companion object {
        fun getStepIncrease(level: Int): Double { return .9 * level }
    }
}
