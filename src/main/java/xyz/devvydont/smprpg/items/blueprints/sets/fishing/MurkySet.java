package xyz.devvydont.smprpg.items.blueprints.sets.fishing;

import org.bukkit.Color;
import org.bukkit.Material;
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

public class MurkySet extends CustomAttributeItem implements ITrimmable, IDyeable, IBreakableEquipment, IRepairable {

    public static final int POWER = 5;
    public static final int CATCH_QUALITY = 15;
    public static final double CHANCE = 0.25;

    public static final int COLOR = 0x5E7C16;
    public static final TrimPattern TRIM = TrimPattern.RIB;
    public static final TrimMaterial TRIM_MATERIAL = TrimMaterial.EMERALD;
    public MurkySet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public ItemClassification getItemClassification() {
        return switch (this.getCustomItemType()) {
            case MURKY_HELMET -> ItemClassification.HELMET;
            case MURKY_CHESTPLATE -> ItemClassification.CHESTPLATE;
            case MURKY_LEGGINGS -> ItemClassification.LEGGINGS;
            case MURKY_BOOTS -> ItemClassification.BOOTS;
            default -> ItemClassification.ITEM;
        };
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                AttributeEntry.additive(AttributeWrapper.DEFENSE, getDefense()),
                AttributeEntry.additive(AttributeWrapper.FISHING_RATING, CATCH_QUALITY),
                AttributeEntry.additive(AttributeWrapper.FISHING_CREATURE_CHANCE, CHANCE),
                AttributeEntry.additive(AttributeWrapper.FISHING_TREASURE_CHANCE, CHANCE),
                AttributeEntry.additive(AttributeWrapper.FISHING_SPEED, 5)
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
            case MURKY_HELMET -> 20;
            case MURKY_CHESTPLATE -> 40;
            case MURKY_LEGGINGS -> 30;
            case MURKY_BOOTS -> 15;
            default -> 0;
        };
    }

    @Override
    public int getMaxDurability() {
        return 5_000;
    }

    @Override
    public @NotNull Collection<@NotNull ItemStack> getRepairMaterial() {
        return List.of(itemService.getCustomItem(Material.LILY_PAD));
    }
}
