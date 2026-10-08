package xyz.devvydont.smprpg.enchantments.definitions.vanilla.overrides

import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.base.AttributeEnchantment
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeModifierType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Note; vanilla minecraft already does this for us because of data driven enchantments. There's no point on double
 * stacking a modifier. In the even we figure out how to make the vanilla sweeping edge *not* apply a modifier,
 * we should opt to use our own.
 */
class SweepingEdgeEnchantment(key: TypedKey<Enchantment>) : VanillaEnchantment(key), AttributeEnchantment {
    override val displayName: Component get() = ComponentUtils.create("Sweeping Edge")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases "),
            ComponentUtils.create(AttributeWrapper.SWEEPING.DisplayName, NamedTextColor.GOLD),
            ComponentUtils.create(" by "),
            ComponentUtils.create(
                getSweepingEdgeEfficiency(level).toString() + "%",
                NamedTextColor.GREEN
            )
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(201, 218, 255)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_SWEEPING
    override val maxLevel: Int get()                           = 5
    override val weight: Int get()                             = EnchantmentRarity.UNCOMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.MAINHAND
    override val skillRequirement: Int get()                   = 28

    override val powerRating : Int get() = 1
    override val attributeModifierType : AttributeModifierType get() = AttributeModifierType.ENCHANTMENT
    override fun getHeldAttributes(): MutableCollection<AttributeEntry?> { return mutableListOf() }

    companion object {
        //    public static int getSweepingEdgeEfficiency(int level) {
        //        return switch (level) {
        //            case 1 -> 15;
        //            case 2 -> 30;
        //            case 3 -> 45;
        //            case 4 -> 65;
        //            case 5 -> 90;
        //            default -> 0;
        //        };
        //    }
        /*
    Vanilla sweeping edge behavior. Used to display a tooltip.
     */
        fun getSweepingEdgeEfficiency(level: Int): Int { return ((1.0 - (1.0 / (level + 1))) * 100).toInt() }
    }
}
