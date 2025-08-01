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
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.UnchangedEnchantment;
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeModifierType;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Collection;
import java.util.List;

public class LuckOfTheSeaEnchantment extends VanillaEnchantment implements AttributeEnchantment {

    public LuckOfTheSeaEnchantment(TypedKey<Enchantment> key) {
        super(key);
    }

    public static int getRatingIncrease(int level) {
        return switch (level) {
            case 1 -> 10;
            case 2 -> 25;
            case 3 -> 45;
            case 4 -> 70;
            case 5 -> 100;
            default -> 0;
        };
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ComponentUtils.create("Luck of the Sea");
    }

    @Override
    public @NotNull Component getDescription() {
        return ComponentUtils.merge(
                ComponentUtils.create("Increases "),
                ComponentUtils.create(AttributeWrapper.FISHING_RATING.DisplayName, NamedTextColor.GOLD),
                ComponentUtils.create(" rating by "),
                ComponentUtils.create(String.format("+%d", getRatingIncrease(getLevel())), NamedTextColor.GREEN)
        );
    }

    @Override
    public TagKey<ItemType> getItemTypeTag() {
        return ItemTypeTagKeys.ENCHANTABLE_FISHING;
    }

    @Override
    public int getSkillRequirement() {
        return 0;
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
        return EnchantmentRarity.COMMON.getWeight();
    }

    @Override
    public EquipmentSlotGroup getEquipmentSlotGroup() {
        return EquipmentSlotGroup.HAND;
    }

    /**
     * What kind of attribute container is this? Items can have multiple containers of stats that stack
     * to prevent collisions
     *
     * @return
     */
    @Override
    public AttributeModifierType getAttributeModifierType() {
        return AttributeModifierType.ENCHANTMENT;
    }

    @Override
    public Collection<AttributeEntry> getHeldAttributes() {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.FISHING_RATING, getRatingIncrease(getLevel()))
        );
    }

    /**
     * How much should we increase the power rating of an item if this container is present?
     *
     * @return
     */
    @Override
    public int getPowerRating() {
        return getLevel() / 2;
    }
}
