package xyz.devvydont.smprpg.enchantments.definitions;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.enchantments.CustomEnchantment;
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity;
import xyz.devvydont.smprpg.enchantments.base.AttributeEnchantment;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeModifierType;
import xyz.devvydont.smprpg.services.EnchantmentService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Collection;
import java.util.List;

import static xyz.devvydont.smprpg.enchantments.definitions.vanilla.overrides.FortuneEnchantment.getFortune;

public class FellingEnchantment extends CustomEnchantment implements AttributeEnchantment {

    public FellingEnchantment(String id) {
        super(id);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ComponentUtils.create("Felling");
    }

    @Override
    public @NotNull Component getDescription() {
        return ComponentUtils.merge(
                ComponentUtils.create("Increases "),
                ComponentUtils.create(AttributeWrapper.WOODCUTTING_FORTUNE.DisplayName, NamedTextColor.GOLD),
                ComponentUtils.create(" by "),
                ComponentUtils.create(String.format("+%d", getFortune(getLevel())), NamedTextColor.GREEN)
        );
    }

    @Override
    public TagKey<ItemType> getItemTypeTag() {
        return ItemTypeTagKeys.AXES;
    }

    @Override
    public int getAnvilCost() {
        return 0;
    }

    @Override
    public int getMaxLevel() {
        return 10;
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
        return 0;
    }

    /**
     * A set of enchantments that this enchantment conflicts with.
     * If there are none, this enchantment has no conflicts
     *
     * @return
     */
    @NotNull
    public RegistryKeySet<@NotNull Enchantment> getConflictingEnchantments() {
        return RegistrySet.keySet(RegistryKey.ENCHANTMENT, EnchantmentKeys.SILK_TOUCH, EnchantmentKeys.FORTUNE, EnchantmentService.HARVESTING.getTypedKey());
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
                AttributeEntry.additive(AttributeWrapper.FARMING_FORTUNE, getFortune(getLevel()))
        );
    }

    /**
     * How much should we increase the power rating of an item if this container is present?
     *
     * @return
     */
    @Override
    public int getPowerRating() {
        return getLevel() / 3;
    }
}
