package xyz.devvydont.smprpg.items.blueprints.sets.fishing;

import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.*;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ToolGlobals;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class AerialRod extends CustomAttributeItem implements IBreakableEquipment, IFishingRod, ISellable, IRepairable {

    public AerialRod(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.ROD;
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
            case AERCLOUD_ROD -> 20;
            case ENDER_ROD -> 25;
            case COMET_ROD -> 30;
            case NEBULA_ROD -> 45;
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
        return Set.of(FishingFlag.AERIAL);
    }

    private int getFishingRating() {
        return switch (getCustomItemType()) {
            case AERCLOUD_ROD -> 30;
            case ETHER_ROD -> 50;
            case MERCURIAL_ROD -> 75;
            case ZEPHYRUS_ROD -> 125;
            default -> 0;
        };
    };

    private int getStrength() {
        return switch (getCustomItemType()) {
            case AERCLOUD_ROD -> 30;
            case ETHER_ROD -> 50;
            case MERCURIAL_ROD -> 70;
            case ZEPHYRUS_ROD -> 100;
            default -> 0;
        };
    };

    private int getChance() {
        return switch (getCustomItemType()) {
            case AERCLOUD_ROD -> 1;
            case ETHER_ROD -> 2;
            case MERCURIAL_ROD -> 3;
            case ZEPHYRUS_ROD -> 4;
            default -> 0;
        };
    };

    private int getSpeed() {
        return switch (this.getCustomItemType()) {
            case AERCLOUD_ROD -> 5;
            case ETHER_ROD -> 20;
            case MERCURIAL_ROD -> 40;
            case ZEPHYRUS_ROD -> 70;
            default -> 0;
        };
    }

    @Override
    public int getWorth(ItemStack item) {
        var base =  super.getWorth(item);

        return base + switch (this.getCustomItemType()) {
            case AERCLOUD_ROD -> 50;
            case ETHER_ROD -> 500;
            case MERCURIAL_ROD -> 15_000;
            case ZEPHYRUS_ROD -> 500_000;
            default -> 0;
        };
    }

    @Override
    public @NotNull Collection<@NotNull ItemStack> getRepairMaterial() {
        return switch (getCustomItemType()) {
            case AERCLOUD_ROD -> List.of(itemService.getCustomItem(CustomItemType.COLD_AERCLOUD));
            case ETHER_ROD -> List.of(itemService.getCustomItem(CustomItemType.GOLD_AERCLOUD));
            case MERCURIAL_ROD -> List.of(itemService.getCustomItem(CustomItemType.ZANITE));
            case ZEPHYRUS_ROD -> List.of(itemService.getCustomItem(CustomItemType.AETHERIUM_INGOT));
            default -> List.of();
        };
    }
}
