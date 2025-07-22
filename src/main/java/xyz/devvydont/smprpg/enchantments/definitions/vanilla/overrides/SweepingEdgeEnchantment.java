package xyz.devvydont.smprpg.enchantments.definitions.vanilla.overrides;

import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity;
import xyz.devvydont.smprpg.enchantments.base.AttributeEnchantment;
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeModifierType;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Collection;
import java.util.List;

/**
 * Note; vanilla minecraft already does this for us because of data driven enchantments. There's no point on double
 * stacking a modifier. In the even we figure out how to make the vanilla sweeping edge *not* apply a modifier,
 * we should opt to use our own.
 */
public class SweepingEdgeEnchantment extends VanillaEnchantment implements AttributeEnchantment {

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
    public static int getSweepingEdgeEfficiency(int level) {
        return (int) ((1.0 - (1.0 / (level+1))) * 100);
    }

    public SweepingEdgeEnchantment(TypedKey<Enchantment> key) {
        super(key);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ComponentUtils.create("Sweeping Edge");
    }

    @Override
    public @NotNull Component getDescription() {
        return ComponentUtils.merge(
            ComponentUtils.create("Increases "),
            ComponentUtils.create(AttributeWrapper.SWEEPING.DisplayName, NamedTextColor.GOLD),
            ComponentUtils.create(" by "),
            ComponentUtils.create(getSweepingEdgeEfficiency(getLevel()) + "%", NamedTextColor.GREEN)
        );
    }

    @Override
    public TagKey<ItemType> getItemTypeTag() {
        return ItemTypeTagKeys.ENCHANTABLE_SWORD;
    }

    @Override
    public int getAnvilCost() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getWeight() {
        return EnchantmentRarity.UNCOMMON.getWeight();
    }

    @Override
    public EquipmentSlotGroup getEquipmentSlotGroup() {
        return EquipmentSlotGroup.MAINHAND;
    }

    @Override
    public int getSkillRequirement() {
        return 28;
    }

    @Override
    public AttributeModifierType getAttributeModifierType() {
        return AttributeModifierType.ENCHANTMENT;
    }

    @Override
    public Collection<AttributeEntry> getHeldAttributes() {
        return List.of(
//                AttributeEntry.additive(AttributeWrapper.SWEEPING, getSweepingEdgeEfficiency(getLevel())/100.0)
        );
    }

    @Override
    public int getPowerRating() {
        return 1;
    }
}
