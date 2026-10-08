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

class WindBurstEnchantment(key: TypedKey<Enchantment>) : UnchangedEnchantment(key) {
    override val displayName: Component get() = ComponentUtils.create("Wind Burst")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Propel upwards "),
            ComponentUtils.create((level * 5).toString(), NamedTextColor.GREEN),
            ComponentUtils.create(" blocks when dealing damage")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(178, 212, 255)

    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_MACE
    override val weight: Int get() = EnchantmentRarity.RARE.weight
    override val skillRequirement: Int get() = 34
}
