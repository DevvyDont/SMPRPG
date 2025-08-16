package xyz.devvydont.smprpg.items.blueprints.sets.fishing.nocturnum;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimPattern;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.ITrimmable;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;


public abstract class NocturnumSet extends CustomAttributeItem implements IBreakableEquipment, ITrimmable, ICraftable {

    public static int POWER = 60;

    // Crafting components to be created.
    public static CustomItemType UPGRADE_BINDING = CustomItemType.LUCIFUGOUS_BINDING;
    public static CustomItemType UPGRADE_MATERIAL = CustomItemType.LUCIFUGOUS_THREAD;

    public NocturnumSet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    public abstract int getHealth();

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
                AttributeEntry.additive(AttributeWrapper.HEALTH, getHealth()),
                AttributeEntry.additive(AttributeWrapper.FISHING_CREATURE_CHANCE, 4),
                AttributeEntry.additive(AttributeWrapper.FISHING_RATING, 55),
                AttributeEntry.additive(AttributeWrapper.FISHING_SPEED, 25)
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
        return 60_000;
    }

    @Override
    public TrimPattern getTrimPattern() {
        return TrimPattern.RIB;
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), this.getCustomItemType().getKey() + "_recipe");
    }

    /**
     * A collection of items that will unlock the recipe for this item. Typically, will be one of the components
     * of the recipe itself, but can be set to whatever is desired
     */
    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(ItemService.generate(CustomItemType.NECROPLASM));
    }

    @Override
    public boolean wantNerfedSellPrice() {
        return false;
    }

    @Override
    public int getWorth(ItemStack item) {
        return super.getWorth(item) + (2_000_000 * item.getAmount());
    }
}
