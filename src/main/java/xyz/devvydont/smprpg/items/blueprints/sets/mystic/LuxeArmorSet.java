package xyz.devvydont.smprpg.items.blueprints.sets.mystic;

import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.IRepairable;
import xyz.devvydont.smprpg.items.interfaces.ITrimmable;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;

public abstract class LuxeArmorSet extends CustomAttributeItem implements ITrimmable, IBreakableEquipment, IRepairable {

    public static CustomItemType ingredient = CustomItemType.ENCHANTED_LAPIS;
    public static final int INTELLIGENCE = 50;

    @Override
    public @NotNull Collection<@NotNull ItemStack> getRepairMaterial() {
        return List.of(itemService.getCustomItem(ingredient));
    }

    public LuxeArmorSet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.DEFENSE, getDefense()),
                new AdditiveAttributeEntry(AttributeWrapper.HEALTH, getHealth()),
                new AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, INTELLIGENCE)
        );
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.ARMOR;
    }

    public abstract int getHealth();
    public abstract int getDefense();

    @Override
    public int getPowerRating() {
        return 20;
    }

    @Override
    public int getMaxDurability() {
        return 12_500;
    }

    @Override
    public TrimMaterial getTrimMaterial() {
        return TrimMaterial.LAPIS;
    }

    @Override
    public TrimPattern getTrimPattern() {
        return TrimPattern.SPIRE;
    }
}
