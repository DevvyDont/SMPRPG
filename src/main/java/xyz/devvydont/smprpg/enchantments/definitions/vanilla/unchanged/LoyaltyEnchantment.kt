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

class LoyaltyEnchantment(key: TypedKey<Enchantment>) : UnchangedEnchantment(key) {
    override val displayName: Component get() = ComponentUtils.create("Loyalty")
    override val description: Component get() = ComponentUtils.create("Returns when thrown ", NamedTextColor.GRAY).append(getSpeedModifier(level))
    override val scrollBindingColor: Color get() = Color.fromRGB(80, 12, 153)

    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_TRIDENT
    override val weight: Int get()                   = EnchantmentRarity.UNCOMMON.weight
    override val skillRequirement: Int get()         = 31

    companion object {
        fun getSpeedModifier(level: Int): Component {
            return when (level) {
                0 -> ComponentUtils.create("")
                1 -> ComponentUtils.create("slowly", NamedTextColor.YELLOW)
                2 -> ComponentUtils.create("quickly", NamedTextColor.GREEN)
                3 -> ComponentUtils.create("very quickly", NamedTextColor.LIGHT_PURPLE)
                else -> ComponentUtils.create("AT LIGHTSPEED", NamedTextColor.LIGHT_PURPLE)
            }
        }
    }
}
