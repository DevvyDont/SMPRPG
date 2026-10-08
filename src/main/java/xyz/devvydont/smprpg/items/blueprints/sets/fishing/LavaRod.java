package xyz.devvydont.smprpg.items.blueprints.sets.fishing;

import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.IFishingRod;
import xyz.devvydont.smprpg.items.interfaces.IRepairable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ToolGlobals;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class LavaRod extends CustomAttributeItem implements IBreakableEquipment, IFishingRod, IRepairable {

    public LavaRod(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.ROD;
    }

    @Override
    public @NotNull Collection<@NotNull ItemStack> getRepairMaterial() {
        return switch (getCustomItemType()) {
            case GOLD_ROD -> List.of(itemService.getCustomItem(Material.GOLD_INGOT));
            case STEEL_ROD -> List.of(itemService.getCustomItem(CustomItemType.STEEL_INGOT));
            case NETHERITE_ROD -> List.of(itemService.getCustomItem(Material.NETHERITE_INGOT));
            case SPITFIRE_ROD -> List.of(itemService.getCustomItem(CustomItemType.BOILING_INGOT));
            default -> List.of();
        };
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                AttributeEntry.additive(AttributeWrapper.STRENGTH, getStrength()),
                AttributeEntry.multiplicative(AttributeWrapper.ATTACK_SPEED, ToolGlobals.FISHING_ROD_COOLDOWN),
                AttributeEntry.additive(AttributeWrapper.FISHING_RATING, getFishingRating()),
                AttributeEntry.additive(AttributeWrapper.FISHING_CREATURE_CHANCE, getChance()),
                AttributeEntry.additive(AttributeWrapper.FISHING_TREASURE_CHANCE, getChance()),
                AttributeEntry.additive(AttributeWrapper.FISHING_SPEED, getSpeed())
        );
    }

    @Override
    public int getPowerRating() {
        return switch (getCustomItemType()) {
            case GOLD_ROD -> ToolGlobals.GOLD_TOOL_POWER;
            case STEEL_ROD -> ToolGlobals.STEEL_TOOL_POWER;
            case NETHERITE_ROD -> ToolGlobals.NETHERITE_TOOL_POWER;
            case SPITFIRE_ROD -> 40;
            default -> 1;
        };
    };

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.HAND;
    }

    @Override
    public int getMaxDurability() {
        return getPowerRating() * 1_000;
    }

    @Override
    public Set<FishingFlag> getFishingFlags() {
        return Set.of(FishingFlag.LAVA);
    }

    private int getFishingRating() {
        return switch (getCustomItemType()) {
            case GOLD_ROD -> 15;
            case STEEL_ROD -> 25;
            case NETHERITE_ROD -> 50;
            case SPITFIRE_ROD -> 80;
            default -> 0;
        };
    };

    private int getStrength() {
        return (int) switch (getCustomItemType()) {
            case GOLD_ROD -> 15;
            case STEEL_ROD -> ItemSword.getSwordDamage(Material.GOLDEN_SWORD) - 10;
            case NETHERITE_ROD -> ItemSword.getSwordDamage(Material.NETHERITE_SWORD) / 2;
            case SPITFIRE_ROD -> ItemSword.getSwordDamage(Material.NETHERITE_SWORD);
            default -> 0;
        };
    };

    private double getChance() {
        return switch (getCustomItemType()) {
            case GOLD_ROD -> 0.5;
            case STEEL_ROD -> 1;
            case NETHERITE_ROD -> 2;
            case SPITFIRE_ROD -> 3;
            default -> 0;
        };
    };

    private int getSpeed() {
        return switch (this.getCustomItemType()) {
            case GOLD_ROD -> 5;
            case STEEL_ROD -> 15;
            case NETHERITE_ROD -> 40;
            case SPITFIRE_ROD -> 60;
            default -> 0;
        };
    }
}
