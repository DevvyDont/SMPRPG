package xyz.devvydont.smprpg.items.blueprints.sets.fishing.ruination;

import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.IRepairable;
import xyz.devvydont.smprpg.items.interfaces.ITrimmable;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;


public abstract class RuinationSet extends CustomAttributeItem implements IBreakableEquipment, ITrimmable, IRepairable {

    public static int POWER = 40;

    // Crafting components to be created.
    public static CustomItemType UPGRADE_MATERIAL = CustomItemType.TRIDENTITE;

    @Override
    public @NotNull Collection<@NotNull ItemStack> getRepairMaterial() {
        return List.of(itemService.getCustomItem(UPGRADE_MATERIAL));
    }

    public RuinationSet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    public abstract int getDefense();

    /**
     * The slot that this item has to be worn in for attributes to kick in.
     */
    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.ARMOR;
    }

    /**
     * What modifiers themselves will be contained on the item if there are no variables to affect them?
     * @param item The item that is supposed to be holding the modifiers.
     */
    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                AttributeEntry.additive(AttributeWrapper.DEFENSE, getDefense()),
                AttributeEntry.additive(AttributeWrapper.FISHING_CREATURE_CHANCE, 3),
                AttributeEntry.additive(AttributeWrapper.FISHING_RATING, 35),
                AttributeEntry.additive(AttributeWrapper.FISHING_SPEED, 15)
        );
    }

    /**
     * How much should we increase the power rating of an item if this container is present?
     */
    @Override
    public int getPowerRating() {
        return POWER;
    }

    @Override
    public int getMaxDurability() {
        return 40_000;
    }

    @Override
    public TrimPattern getTrimPattern() {
        return TrimPattern.RIB;
    }


    @Override
    public TrimMaterial getTrimMaterial() {
        return TrimMaterial.NETHERITE;
    }

    @Override
    public int getWorth(ItemStack item) {
        return super.getWorth(item) + (150_000 * item.getAmount());
    }
}
