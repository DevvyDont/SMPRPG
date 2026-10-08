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
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class FortuityEnchantment(id: String) : CustomEnchantment(id), AttributeEnchantment {
    override val displayName: Component get() = ComponentUtils.create("Fortuity")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases "),
            ComponentUtils.create(AttributeWrapper.LUCK.DisplayName, NamedTextColor.GOLD),
            ComponentUtils.create(" by "),
            ComponentUtils.create("+" + getChanceIncrease(level), NamedTextColor.GREEN)
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(112, 255, 119)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_VANISHING
    override val maxLevel: Int get()                           = 10
    override val weight: Int get()                             = EnchantmentRarity.RARE.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.ANY
    override val skillRequirement: Int get()                   = 25

    override val powerRating : Int get() = 0
    override val attributeModifierType : AttributeModifierType get() = AttributeModifierType.ENCHANTMENT
    override fun getHeldAttributes() : MutableCollection<AttributeEntry?>? {
        return mutableListOf(
            AttributeEntry.additive(AttributeWrapper.LUCK, getChanceIncrease(level).toDouble())
        )
    }

    override val magicExperience: Int get() = level * 200 * (1 + (level * 4 / maxLevel))

    companion object {
        fun getChanceIncrease(level: Int): Int {
            return when (level) {
                0 -> 0
                1 -> 2
                2 -> 4
                3 -> 6
                4 -> 8
                5 -> 10
                else -> getChanceIncrease(5) + 2 * (level - 5)
            }
        }
    }
}
