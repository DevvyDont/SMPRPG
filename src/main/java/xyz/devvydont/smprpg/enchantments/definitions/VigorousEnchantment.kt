package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.set.RegistryKeySet
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.base.AttributeEnchantment
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeModifierType
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class VigorousEnchantment(id: String) : CustomEnchantment(id), AttributeEnchantment {
    override val displayName: Component get() = ComponentUtils.create("Vigorous")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases harvest speed by "),
            ComponentUtils.create("+" + getMiningSpeed(level) + "%", NamedTextColor.GREEN)
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(0, 94, 94)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_MINING
    override val maxLevel: Int get()                           = 3
    override val weight: Int get()                             = EnchantmentRarity.RARE.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.CHEST
    override val skillRequirement: Int get()                   = 45

    override val powerRating : Int get() = 0
    override val attributeModifierType : AttributeModifierType get() = AttributeModifierType.ENCHANTMENT
    override fun getHeldAttributes() : MutableCollection<AttributeEntry?>? {
        return mutableListOf(
            ScalarAttributeEntry(AttributeWrapper.MINING_SPEED, getMiningSpeed(level) / 100.0)
        )
    }

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        /**
         * A set of enchantments that this enchantment conflicts with.
         * If there are none, this enchantment has no conflicts
         *
         * @return
         */
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentService.MINERS_FERVOR.typedKey
        )

    companion object {
        fun getMiningSpeed(level: Int): Int {
            return level * 30
        }
    }
}
