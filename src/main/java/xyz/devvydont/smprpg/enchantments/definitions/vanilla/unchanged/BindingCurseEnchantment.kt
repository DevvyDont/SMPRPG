package xyz.devvydont.smprpg.enchantments.definitions.vanilla.unchanged

import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.UnchangedEnchantment
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class BindingCurseEnchantment(key: TypedKey<Enchantment>) : UnchangedEnchantment(key) {
    override val displayName: Component get() = ComponentUtils.create("Curse of Binding")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Cannot be "),
            ComponentUtils.create("removed", NamedTextColor.DARK_RED),
            ComponentUtils.create(" once worn")
        )
    override val enchantColor: TextColor get() = NamedTextColor.RED

    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_ARMOR
    override val skillRequirement: Int get()         = 0
    override val skillRequirementToAvoid: Int get()  = 10
    override val weight: Int get()                   = EnchantmentRarity.CURSE.weight
}
