package xyz.devvydont.smprpg.enchantments.definitions.vanilla.unchanged

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.set.RegistryKeySet
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.UnchangedEnchantment
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class DepthStriderEnchantment(key: TypedKey<Enchantment>) : UnchangedEnchantment(key) {
    override val displayName: Component get() = ComponentUtils.create("Depth Strider")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Walk underwater at "),
            ComponentUtils.create(
                getMovementPercentage(level).toString() + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" speed")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(28, 32, 97)

    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_FOOT_ARMOR
    override val weight: Int get()                   = EnchantmentRarity.UNCOMMON.weight
    override val skillRequirement: Int get()         = 27

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentKeys.FROST_WALKER
        )

    companion object {
        fun getMovementPercentage(level: Int): Int {
            return when (level) {
                0 -> 0
                1 -> 33
                2 -> 66
                else -> 100
            }
        }
    }
}
