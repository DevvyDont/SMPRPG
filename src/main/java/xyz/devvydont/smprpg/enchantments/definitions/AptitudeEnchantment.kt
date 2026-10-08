package xyz.devvydont.smprpg.enchantments.definitions

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
import xyz.devvydont.smprpg.util.persistence.KeyStore

class AptitudeEnchantment(id: String) : CustomEnchantment(id), AttributeEnchantment {
    override val displayName: Component get() = ComponentUtils.create("Aptitude")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases "),
            ComponentUtils.create(AttributeWrapper.INTELLIGENCE.DisplayName, NamedTextColor.GOLD),
            ComponentUtils.create(" by "),
            ComponentUtils.create(
                "+" + getIntelligenceIncrease(level),
                NamedTextColor.AQUA
            )
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(0, 255, 255)

    override val itemTypeTag: TagKey<ItemType> get()           = KeyStore.ENCHANTABLE_APTITUDE
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.COMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.ANY
    override val skillRequirement: Int get()                   = 10

    override val powerRating : Int get() = level / 2 + 1
    override val attributeModifierType : AttributeModifierType get() = AttributeModifierType.ENCHANTMENT
    override fun getHeldAttributes() : MutableCollection<AttributeEntry?>? {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, getIntelligenceIncrease(level).toDouble())
        )
    }

    companion object {
        fun getIntelligenceIncrease(level: Int): Int {
            return when (level) {
                0 -> 0
                1 -> 40
                2 -> 80
                3 -> 120
                4 -> 160
                5 -> 200
                else -> getIntelligenceIncrease(5) + 40 * (level - 5)
            }
        }
    }
}
