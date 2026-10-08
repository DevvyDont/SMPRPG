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

class FrostWalkerEnchantment(key: TypedKey<Enchantment>) : UnchangedEnchantment(key) {
    override val displayName: Component get() = ComponentUtils.create("Frost Walker")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Freezes water "),
            ComponentUtils.create((level + 1).toString(), NamedTextColor.GREEN),
            ComponentUtils.create(" blocks away when walked on")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(166, 255, 245)

    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_FOOT_ARMOR
    override val weight: Int get()                   = EnchantmentRarity.RARE.weight
    override val skillRequirement: Int get()         = 33

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        /**
         * A set of enchantments that this enchantment conflicts with.
         * If there are none, this enchantment has no conflicts
         *
         * @return
         */
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentKeys.DEPTH_STRIDER
        )
}
