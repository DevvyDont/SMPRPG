package xyz.devvydont.smprpg.items.base;


import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.items.blueprints.augment.Recombobulator;
import xyz.devvydont.smprpg.items.blueprints.resources.VanillaResource;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils;

/**
 * Represents an item that shouldn't have any special behavior associated with it. It will simply function as
 * a featureless item that updates according to the item meta and components that is has.
 */
public class VanillaItemBlueprint extends SMPItemBlueprint implements ISellable {

    protected final Material material;

    public VanillaItemBlueprint(ItemService itemService, Material material) {
        super(itemService);
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.resolveVanillaMaterial(material);
    }

    @Override
    public ItemRarity getRarity(ItemStack item) {
        var rarityMod = 0;
        // Add 1 to rarity ordinal if it's recombed.
        if (item.getPersistentDataContainer().getOrDefault(Recombobulator.Companion.getRECOMBOBULATOR_KEY(), PersistentDataType.BOOLEAN, false))
            rarityMod += 1;
        var retRarity = getDefaultRarity().ordinal();
        retRarity += rarityMod;
        return ItemRarity.values()[retRarity];
    }

    @Override
    public ItemRarity getDefaultRarity() {
        return ItemRarity.ofVanillaMaterial(material);
    }

    @Override
    public String getCustomModelDataIdentifier() {
        return "smprpg:vanilla_" + material.name().toLowerCase();
    }

    @Override
    public String getItemName(ItemStack item) {
        return MinecraftStringUtils.getTitledString(material.name());
    }

    @Override
    public boolean isItemOfType(ItemStack itemStack) {
        // A stack only counts as this vanilla type if it carries no custom item key,
        // otherwise custom items sharing the same base material would falsely match.
        return itemService.getItemKey(itemStack) == null && itemStack.getType().equals(material);
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @Override
    public @NonNull ItemStack generate() {
        var item = ItemStack.of(material);
        updateItemData(item);
        return item;
    }

    /**
     * Given this item stack, how much should it be able to sell for?
     * Keep in mind that the size of the stack needs to considered as well!
     *
     * @param item The item that can be sold.
     * @return The worth of the item.
     */
    @Override
    public int getWorth(ItemStack item) {
        return VanillaResource.getMaterialValue(material) * item.getAmount();
    }
}
