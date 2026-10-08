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

class SwiftSneakEnchantment(key: TypedKey<Enchantment>) : UnchangedEnchantment(key) {
    override val displayName: Component get() = ComponentUtils.create("Swift Sneak")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Sneak speed is "),
            ComponentUtils.create(
                getSneakPercent(level).toString() + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" of walk speed")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(9, 69, 62)

    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_LEG_ARMOR
    override val weight: Int get() = EnchantmentRarity.RARE.weight
    override val skillRequirement: Int get() = 49

    companion object {
        fun getSneakPercent(level: Int): Int { return level * 15 + 30 }
    }
}
