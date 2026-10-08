package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.ScrollColor
import xyz.devvydont.smprpg.enchantments.base.AttributeEnchantment
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeModifierType
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class OneForAllArtificeEnchantment(id: String) : CustomEnchantment(id), AttributeEnchantment {
    override val displayName: Component get() = ComponentUtils.create("One for All I")  // Roman Numeral is hardcoded for this one.
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Multiplies "),
            ComponentUtils.create(AttributeWrapper.STRENGTH.DisplayName, NamedTextColor.GOLD),
            ComponentUtils.create(" by "),
            ComponentUtils.create("10x", NamedTextColor.GREEN),
            ComponentUtils.create(", but no other enchantments may be present on this weapon.")
        )
    override val longDescription: MutableCollection<Component?> get() = mutableListOf(
        ComponentUtils.merge(
            ComponentUtils.create("Multiplies "),
            ComponentUtils.create(AttributeWrapper.STRENGTH.DisplayName, NamedTextColor.GOLD),
            ComponentUtils.create(" by "),
            ComponentUtils.create("10x", NamedTextColor.GREEN),
            ComponentUtils.create(", but no other")
        ),
        ComponentUtils.create("enchantments may be present on this weapon.")
    )
    override val enchantColor: TextColor get()   = ARTIFICE_COLOR
    override val scrollColor: Color get()        = ScrollColor.ARTIFICE.color
    override val scrollBindingColor: Color get() = Color.fromRGB(32, 32, 32)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_WEAPON
    override val maxLevel: Int get()                           = 1
    override val weight: Int get()                             = EnchantmentRarity.ARTIFICE.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.ANY
    override val skillRequirement: Int get()                   = 60

    override val powerRating : Int get() = 1
    override val attributeModifierType : AttributeModifierType get() = AttributeModifierType.ENCHANTMENT
    override fun getHeldAttributes() : MutableCollection<AttributeEntry?>? {
        return mutableListOf(
            MultiplicativeAttributeEntry(AttributeWrapper.STRENGTH, 10.0)
        )
    }
}
