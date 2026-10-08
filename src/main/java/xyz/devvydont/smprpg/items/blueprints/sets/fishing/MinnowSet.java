package xyz.devvydont.smprpg.items.blueprints.sets.fishing;

import org.bukkit.Color;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.*;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;

public class MinnowSet extends CustomAttributeItem implements ITrimmable, IDyeable, IBreakableEquipment, IRepairable {

    public static final int POWER = 15;
    public static final int CATCH_QUALITY = 10;
    public static final double CHANCE = 1;
    public static final int COLOR = 0x9B9B9B;
    public static final TrimPattern TRIM = TrimPattern.RIB;
    public static final TrimMaterial TRIM_MATERIAL = TrimMaterial.DIAMOND;

    public MinnowSet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public boolean wantNerfedSellPrice() {
        return false;
    }

    @Override
    public int getWorth(ItemStack item) {
        return (CustomItemType.MINNOW_SCALE.Worth * 3) * item.getAmount();
    }

    @Override
    public ItemClassification getItemClassification() {
        return switch (this.getCustomItemType()) {
            case MINNOW_HELMET -> ItemClassification.HELMET;
            case MINNOW_CHESTPLATE -> ItemClassification.CHESTPLATE;
            case MINNOW_LEGGINGS -> ItemClassification.LEGGINGS;
            case MINNOW_BOOTS -> ItemClassification.BOOTS;
            default -> ItemClassification.ITEM;
        };
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                AttributeEntry.additive(AttributeWrapper.DEFENSE, getDefense()),
                AttributeEntry.additive(AttributeWrapper.FISHING_RATING, CATCH_QUALITY),
                AttributeEntry.additive(AttributeWrapper.FISHING_CREATURE_CHANCE, CHANCE)
        );
    }

    @Override
    public int getPowerRating() {
        return POWER;
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.ARMOR;
    }

    @Override
    public Color getColor() {
        return Color.fromRGB(COLOR);
    }

    @Override
    public TrimMaterial getTrimMaterial() {
        return TRIM_MATERIAL;
    }

    @Override
    public TrimPattern getTrimPattern() {
        return TRIM;
    }

    private int getDefense() {
        return switch (this.getCustomItemType()) {
            case MINNOW_HELMET -> 35;
            case MINNOW_CHESTPLATE -> 60;
            case MINNOW_LEGGINGS -> 45;
            case MINNOW_BOOTS -> 25;
            default -> 0;
        };
    }

    @Override
    public int getMaxDurability() {
        return 12_000;
    }

    @Override
    public @NotNull Collection<@NotNull ItemStack> getRepairMaterial() {
        return List.of(itemService.getCustomItem(CustomItemType.MINNOW_SCALE));
    }
}
