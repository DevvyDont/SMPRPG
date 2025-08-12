package xyz.devvydont.smprpg.items.blueprints.sets.fishing.nocturnum;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.ability.Passive;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.IFishingRod;
import xyz.devvydont.smprpg.items.interfaces.IPassiveProvider;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ToolGlobals;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * The end game sea creature rod. Can fish everywhere, and has the ceiling for base sea creature rod stats.
 */
public class NocturnumRod extends CustomAttributeItem implements IBreakableEquipment, IFishingRod, ICraftable, IPassiveProvider {

    public NocturnumRod(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.ROD;
    }

    /**
     * What modifiers themselves will be contained on the item if there are no variables to affect them?
     * @param item The item that is supposed to be holding the modifiers.
     */
    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                AttributeEntry.additive(AttributeWrapper.STRENGTH, 180),
                AttributeEntry.additive(AttributeWrapper.CRITICAL_DAMAGE, 70),
                AttributeEntry.additive(AttributeWrapper.FISHING_RATING, 150),
                AttributeEntry.additive(AttributeWrapper.FISHING_CREATURE_CHANCE, 12),
                AttributeEntry.multiplicative(AttributeWrapper.ATTACK_SPEED, ToolGlobals.FISHING_ROD_COOLDOWN),
                AttributeEntry.additive(AttributeWrapper.FISHING_SPEED, 25)
        );
    }

    /**
     * How much should we increase the power rating of an item if this container is present?
     */
    @Override
    public int getPowerRating() {
        return NocturnumSet.POWER;
    }

    /**
     * The slot that this item has to be worn in for attributes to kick in.
     */
    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.HAND;
    }

    @Override
    public int getMaxDurability() {
        return 60_000;
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), this.getCustomItemType() + "_recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(this.getRecipeKey(), generate());
        recipe.shape(
                "  r",
                " ts",
                "r s"
        );
        recipe.setIngredient('t', ItemService.generate(CustomItemType.RUINATION_ROD));
        recipe.setIngredient('r', ItemService.generate(NocturnumSet.UPGRADE_BINDING));
        recipe.setIngredient('s', ItemService.generate(NocturnumSet.UPGRADE_MATERIAL));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }

    /**
     * A collection of items that will unlock the recipe for this item. Typically, will be one of the components
     * of the recipe itself, but can be set to whatever is desired
     */
    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(ItemService.generate(CustomItemType.NECROPLASM));
    }

    /**
     * Check what contexts this fishing rod is allowed to fish in. for example, if this rod can catch things in the
     * void then it will contain FishingFlag.VOID.
     * @return A set of fishing flags this rod contains.
     */
    @Override
    public Set<FishingFlag> getFishingFlags() {
        return Set.of(
                FishingFlag.LAVA,
                FishingFlag.VOID
        );
    }

    /**
     * Retrieve the passives this item has.
     *
     * @return A set of passives.
     */
    @Override
    public Set<Passive> getPassives() {
        return Set.of(
                Passive.ANGLER
        );
    }

    @Override
    public boolean wantNerfedSellPrice() {
        return false;
    }

    @Override
    public int getWorth(ItemStack item) {
        return super.getWorth(item) + (1_750_000 * item.getAmount());
    }
}
