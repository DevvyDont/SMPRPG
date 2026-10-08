package xyz.devvydont.smprpg.items.blueprints.sets.phantom;

import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.IRepairable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;

import java.util.Collection;
import java.util.List;

public class PhantomWings extends CustomAttributeItem implements IBreakableEquipment, IRepairable {

    @Override
    public @NotNull Collection<@NotNull ItemStack> getRepairMaterial() {
        return List.of(itemService.getCustomItem(CustomItemType.PREMIUM_MEMBRANE));
    }

    public PhantomWings(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.HEALTH, 10)
        );
    }

    @Override
    public int getPowerRating() {
        return 10;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.CHESTPLATE;
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.CHEST;
    }

    @Override
    public int getMaxDurability() {
        return 160;
    }
}
