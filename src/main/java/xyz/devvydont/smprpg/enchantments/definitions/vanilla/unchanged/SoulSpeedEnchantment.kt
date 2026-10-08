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

class SoulSpeedEnchantment(key: TypedKey<Enchantment>) : UnchangedEnchantment(key) {
    override val displayName: Component get() = ComponentUtils.create("Soul Speed")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases speed by "),
            ComponentUtils.create(
                "+" + getSoulSpeedPercentage(level) + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" on "),
            ComponentUtils.create("soul sand/soil", NamedTextColor.GOLD)
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(64, 42, 31)

    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_FOOT_ARMOR
    override val weight: Int get() = EnchantmentRarity.RARE.weight
    override val skillRequirement: Int get() = 23


    companion object {
        fun getSoulSpeedPercentage(level: Int): Int { return (((level * 0.105) + .3) * 100).toInt() }
    }
}
