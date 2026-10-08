package xyz.devvydont.smprpg.items.blueprints.sets.redstone;

import org.bukkit.Color;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.*;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;

import java.util.Collection;
import java.util.List;

public abstract class RedstoneArmorSet extends CustomAttributeItem implements IBreakableEquipment, IDyeable, ITrimmable, IRepairable {

    public static final int POWER = 15;
    public static final int DURABILITY = 12_500;

    public static final double MOVEMENT_BUFF = .05;
    public static final double ATTACK_BUFF = .1;
    public static final double MINING_BUFF = 100.0;

    public static final CustomItemType INGREDIENT = CustomItemType.ENCHANTED_REDSTONE;

    @Override
    public @NotNull Collection<@NotNull ItemStack> getRepairMaterial() {
        return List.of(itemService.getCustomItem(INGREDIENT));
    }


    public RedstoneArmorSet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.ARMOR;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.DEFENSE, getDefense()),
                new ScalarAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, MOVEMENT_BUFF),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, ATTACK_BUFF),
                new AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, MINING_BUFF)
        );
    }

    public abstract int getDefense();

    @Override
    public Color getColor() {
        return Color.fromRGB(0xB02E26);
    }

    @Override
    public TrimMaterial getTrimMaterial() {
        return TrimMaterial.COPPER;
    }

    @Override
    public TrimPattern getTrimPattern() {
        return TrimPattern.TIDE;
    }

    @Override
    public int getPowerRating() {
        return POWER;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY;
    }
}
