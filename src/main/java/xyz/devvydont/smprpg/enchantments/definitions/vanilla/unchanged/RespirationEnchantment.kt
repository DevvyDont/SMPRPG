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

class RespirationEnchantment(key: TypedKey<Enchantment>) : UnchangedEnchantment(key) {
    override val displayName: Component get() = ComponentUtils.create("Respiration")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases lung capacity by "),
            ComponentUtils.create(
                "+" + getAdditionalBreath(level) + "s",
                NamedTextColor.GREEN
            )
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(37, 116, 156)

    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_HEAD_ARMOR
    override val weight: Int get() = EnchantmentRarity.UNCOMMON.weight
    override val skillRequirement: Int get() = 12

    companion object {
        fun getAdditionalBreath(level: Int): Int { return level * 15 }
    }
}
