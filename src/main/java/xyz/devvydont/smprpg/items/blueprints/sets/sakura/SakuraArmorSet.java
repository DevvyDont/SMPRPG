package xyz.devvydont.smprpg.items.blueprints.sets.sakura;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.IRepairable;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;

public abstract class SakuraArmorSet extends CustomAttributeItem implements IBreakableEquipment, IRepairable {

    public static final int POWER = 7;
    public static final int DURABILITY = 1_200;

    @Override
    public @NotNull Collection<@NotNull ItemStack> getRepairMaterial() {
        return List.of(itemService.getCustomItem(Material.CHERRY_LOG));
    }

    public SakuraArmorSet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.ARMOR;
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
