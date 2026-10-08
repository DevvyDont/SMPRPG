package xyz.devvydont.smprpg.items.blueprints.sets.radiant;

import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.blueprints.sets.mystic.LuxeArmorSet;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.IRepairable;
import xyz.devvydont.smprpg.items.interfaces.ITrimmable;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;

public abstract class RadiantArmorSet extends CustomAttributeItem implements IBreakableEquipment, ITrimmable, IRepairable {

    public RadiantArmorSet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    public abstract double getDefense();

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.DEFENSE, getDefense()),
                new AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, LuxeArmorSet.INTELLIGENCE*2)
        );
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.ARMOR;
    }

    @Override
    public TrimMaterial getTrimMaterial() {
        return TrimMaterial.GOLD;
    }

    @Override
    public int getPowerRating() {
        return 30;
    }

    @Override
    public int getMaxDurability() {
        return 27_500;
    }

    @Override
    public @NotNull Collection<@NotNull ItemStack> getRepairMaterial() {
        return List.of(itemService.getCustomItem(CustomItemType.ENCHANTED_GLOWSTONE));
    }
}
