package xyz.devvydont.smprpg.items.blueprints.sets.emerald;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.IEquippableAssetOverride;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;

import java.util.Collection;
import java.util.List;

public abstract class EmeraldArmorSet extends CustomAttributeItem implements IBreakableEquipment, ICraftable, IEquippableAssetOverride {

    public static int EMERALD_POWER = 20;
    public static Material INGREDIENT = Material.EMERALD_BLOCK;

    private static final Key ASSET_KEY = Key.key("emerald");


    public EmeraldArmorSet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.ARMOR;
    }

    @Override
    public Key getAssetId() {
        return ASSET_KEY;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.DEFENSE, getDefense()),
                new AdditiveAttributeEntry(AttributeWrapper.HEALTH, getHealth()),
                new ScalarAttributeEntry(AttributeWrapper.STRENGTH, getStrength())
        );
    }

    public abstract double getDefense();

    public abstract double getHealth();

    public abstract double getStrength();

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), getCustomItemType().getKey() + "-recipe");
    }

    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(
                itemService.getCustomItem(Material.EMERALD)
        );
    }

    @Override
    public int getPowerRating() {
        return EMERALD_POWER;
    }

    @Override
    public int getMaxDurability() {
        return 20_000;
    }
}
