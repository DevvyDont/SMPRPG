package xyz.devvydont.smprpg.items.blueprints.sets.elderflame;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.IEquippableAssetOverride;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;

public abstract class ElderflameArmorSet extends CustomAttributeItem implements IBreakableEquipment, ICraftable, IEquippableAssetOverride {

    private static final Key key = Key.key("elderflame");

    public ElderflameArmorSet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.ARMOR;
    }

    @Override
    public int getPowerRating() {
        return 50;
    }

    @Override
    public int getMaxDurability() {
        return 50_000;
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), getCustomItemType().getKey() + "-recipe");
    }

    @Override
    public Key getAssetId() {
        return key;
    }

    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(itemService.getCustomItem(CustomItemType.DRAGON_SCALES));
    }
}
