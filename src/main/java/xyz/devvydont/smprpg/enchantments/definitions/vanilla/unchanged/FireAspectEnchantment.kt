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

class FireAspectEnchantment(key: TypedKey<Enchantment>) : UnchangedEnchantment(key) {
    override val displayName: Component get() = ComponentUtils.create("Fire Aspect")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Ignites enemies for "),
            ComponentUtils.create(
                getSecondsOfBurn(level).toString() + "s",
                NamedTextColor.GOLD
            ),
            ComponentUtils.create(" and "),
            ComponentUtils.create("smelts", NamedTextColor.RED),
            ComponentUtils.create(" drops")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(255, 71, 10)

    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_FIRE_ASPECT
    override val weight: Int get()                   = EnchantmentRarity.UNCOMMON.weight
    override val skillRequirement: Int get()         = 16

    companion object {
        fun getSecondsOfBurn(level: Int): Int { return level * 4 }
    }
}
