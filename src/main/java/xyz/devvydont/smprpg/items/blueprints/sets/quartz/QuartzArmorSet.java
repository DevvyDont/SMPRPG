package xyz.devvydont.smprpg.items.blueprints.sets.quartz;

import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.IRepairable;
import xyz.devvydont.smprpg.items.interfaces.ITrimmable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;

import java.util.Collection;
import java.util.List;

public abstract class QuartzArmorSet extends CustomAttributeItem implements ITrimmable, IBreakableEquipment, IRepairable {

    @Override
    public @NotNull Collection<@NotNull ItemStack> getRepairMaterial() {
        return List.of(itemService.getCustomItem(CustomItemType.ENCHANTED_QUARTZ));
    }

    public QuartzArmorSet(ItemService itemService, CustomItemType type) {
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
                new ScalarAttributeEntry(AttributeWrapper.STRENGTH, getStrength()),
                new AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 25)
        );
    }

    public abstract int getDefense();

    public abstract double getStrength();

    @Override
    public int getPowerRating() {
        return 20;
    }

    @Override
    public int getMaxDurability() {
        return 12_500;
    }
}
