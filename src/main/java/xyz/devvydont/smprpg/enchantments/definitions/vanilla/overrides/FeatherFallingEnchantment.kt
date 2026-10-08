package xyz.devvydont.smprpg.enchantments.definitions.vanilla.overrides

import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.base.AttributeEnchantment
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeModifierType
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import kotlin.math.max
import kotlin.math.min

class FeatherFallingEnchantment(key: TypedKey<Enchantment>) : VanillaEnchantment(key), AttributeEnchantment, Listener {
    override val displayName: Component get() = ComponentUtils.create("Feather Falling")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Resists "),
            ComponentUtils.create(
                getFallResistPercent(level).toString() + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" of fall damage")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(255, 255, 255)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_FOOT_ARMOR
    override val maxLevel: Int get()                           = 10
    override val weight: Int get()                             = EnchantmentRarity.COMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.FEET
    override val skillRequirement: Int get()                   = 9

    override val powerRating : Int get() = level / 5
    override val attributeModifierType: AttributeModifierType = AttributeModifierType.ENCHANTMENT
    override fun getHeldAttributes() : MutableCollection<AttributeEntry?>? {
        return mutableListOf(
            MultiplicativeAttributeEntry(AttributeWrapper.FALL_DAMAGE_MULTIPLIER, -getFallResistPercent(level) / 100.0),
            AdditiveAttributeEntry(AttributeWrapper.SAFE_FALL, (level * 2).toDouble())
        )
    }

    companion object {
        fun getFallResistPercent(level: Int): Int { return min(max(0, level * 9), 99) }
    }
}
