package xyz.devvydont.smprpg.enchantments.definitions.vanilla.unchanged

import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.UnchangedEnchantment
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class QuickChargeEnchantment(key: TypedKey<Enchantment>) : UnchangedEnchantment(key) {
    override val displayName: Component get() = ComponentUtils.create("Quick Charge")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Sets loading time to "),
            ComponentUtils.create(
                "-" + getChargePercentageReduction(level) + "%",
                if (level >= 5) NamedTextColor.LIGHT_PURPLE else NamedTextColor.GREEN
            )
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(156, 37, 98)

    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_CROSSBOW
    override val weight: Int get() = EnchantmentRarity.UNCOMMON.weight
    override val skillRequirement: Int get() = 37

    companion object {
        fun getChargePercentageReduction(level: Int): Int { return level * 25 }
    }
}
