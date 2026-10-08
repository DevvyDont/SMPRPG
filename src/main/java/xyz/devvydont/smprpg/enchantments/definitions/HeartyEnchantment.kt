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
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeModifierType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols

class HeartyEnchantment(id: String) : CustomEnchantment(id), AttributeEnchantment {
    override val displayName: Component get() = ComponentUtils.create("Hearty")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases max HP by "),
            ComponentUtils.create("+" + getHealthIncrease(level), NamedTextColor.GREEN),
            ComponentUtils.create(Symbols.HEART, NamedTextColor.RED)
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(255, 25, 68)

   override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_ARMOR
   override val maxLevel: Int get()                           = 10
   override val weight: Int get()                             = EnchantmentRarity.COMMON.getWeight()
   override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.ARMOR
   override val skillRequirement: Int get()                   = 1

    override val powerRating : Int get() = level / 2 + 1
    override val attributeModifierType : AttributeModifierType get() = AttributeModifierType.ENCHANTMENT
    override fun getHeldAttributes() : MutableCollection<AttributeEntry?>? {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.HEALTH, getHealthIncrease(level).toDouble())
        )
    }

    override val magicExperience: Int get() = level * 200 * (1 + (level * 4 / maxLevel))

    companion object {
        fun getHealthIncrease(level: Int): Int {
            return when (level) {
                0 -> 0
                1 -> 10
                2 -> 20
                3 -> 30
                4 -> 45
                5 -> 60
                6 -> 75
                7 -> 90
                8 -> 105
                9 -> 125
                10 -> 150
                else -> getHealthIncrease(10) + 25 * (level - 10)
            }
        }
    }
}
