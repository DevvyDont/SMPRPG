package xyz.devvydont.smprpg.enchantments.definitions;

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.enchantments.CustomEnchantment;
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity;
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil;
import xyz.devvydont.smprpg.enchantments.base.AttributeEnchantment;
import xyz.devvydont.smprpg.events.CustomItemDropRollEvent;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeModifierType;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Collection;
import java.util.List;

public class FortuityEnchantment extends CustomEnchantment implements AttributeEnchantment {

    public static int getChanceIncrease(int level) {
        return switch (level) {
            case 0 -> 0;
            case 1 -> 2;
            case 2 -> 4;
            case 3 -> 6;
            case 4 -> 8;
            case 5 -> 10;
            default -> getChanceIncrease(5) + 2 * (level-5);
        };
    }

    public FortuityEnchantment(String id) {
        super(id);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ComponentUtils.create("Fortuity");
    }

    @Override
    public @NotNull Component getDescription() {
        return ComponentUtils.merge(
            ComponentUtils.create("Increases "),
            ComponentUtils.create(AttributeWrapper.LUCK.DisplayName, NamedTextColor.GOLD),
            ComponentUtils.create(" by "),
            ComponentUtils.create("+" + getChanceIncrease(getLevel()), NamedTextColor.GREEN)
        );
    }

    @Override
    public TagKey<ItemType> getItemTypeTag() {
        return ItemTypeTagKeys.ENCHANTABLE_VANISHING;
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
        return EquipmentSlotGroup.ANY;
    }

    @Override
    public int getSkillRequirement() {
        return 25;
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

    /**
     * What modifiers themselves will be contained on the item if there are no variables to affect them?
     *
     * @return
     */
    @Override
    public Collection<AttributeEntry> getHeldAttributes() {
        return List.of(
                AttributeEntry.additive(AttributeWrapper.LUCK, getChanceIncrease(getLevel()))
        );
    }

    /**
     * How much should we increase the power rating of an item if this container is present?
     *
     * @return
     */
    @Override
    public int getPowerRating() {
        return 0;
    }
}
