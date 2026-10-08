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

class KnockbackEnchantment(key: TypedKey<Enchantment>) : UnchangedEnchantment(key) {
    override val displayName: Component get() = ComponentUtils.create("Knockback")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases knockback by "),
            ComponentUtils.create(
                "+" + getKnockbackPower(level) + "%",
                NamedTextColor.GREEN
            )
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(129, 35, 145)

    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_SHARP_WEAPON
    override val weight: Int get()                   = EnchantmentRarity.COMMON.weight
    override val skillRequirement: Int get()         = 3


    companion object {
        fun getKnockbackPower(level: Int): Int {
            return when (level) {
                0 -> 0
                1 -> 105
                2 -> 190
                else -> level * 100
            }
        }
    }
}
