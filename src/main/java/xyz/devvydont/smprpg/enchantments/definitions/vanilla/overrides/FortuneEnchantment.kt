package xyz.devvydont.smprpg.enchantments.definitions.vanilla.overrides

import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.base.AttributeEnchantment
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeModifierType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class FortuneEnchantment(key: TypedKey<Enchantment>) : VanillaEnchantment(key), AttributeEnchantment {
    override val displayName: Component get() = ComponentUtils.create("Fortune")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases "),
            ComponentUtils.create(AttributeWrapper.MINING_FORTUNE.DisplayName, NamedTextColor.GOLD),
            ComponentUtils.create(" by "),
            ComponentUtils.create(
                String.format("+%d", getFortune(level)),
                NamedTextColor.GREEN
            )
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(131, 255, 120)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.PICKAXES
    override val maxLevel: Int get()                           = 10
    override val weight: Int get()                             = EnchantmentRarity.UNCOMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.MAINHAND
    override val skillRequirement: Int get()                   = 0

    override val powerRating : Int get() = level / 3
    override val attributeModifierType : AttributeModifierType get() = AttributeModifierType.ENCHANTMENT
    override fun getHeldAttributes(): MutableCollection<AttributeEntry?>{
        return mutableListOf(
            AttributeEntry.additive(AttributeWrapper.MINING_FORTUNE, getFortune(level).toDouble())
        )
    }

    companion object {
        @JvmStatic
        fun getFortune(level: Int): Int {
            return when (level) {
                0 -> 0
                1 -> 10
                2 -> 25
                3 -> 40
                4 -> 55
                5 -> 70
                6 -> 85
                7 -> 100
                8 -> 115
                9 -> 130
                10 -> 150
                else -> level * 25 + getFortune(10)
            }
        }
    }
}
