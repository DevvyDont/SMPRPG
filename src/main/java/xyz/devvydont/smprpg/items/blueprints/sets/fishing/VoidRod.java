package xyz.devvydont.smprpg.items.blueprints.sets.fishing;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.IFishingRod;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class VoidRod extends CustomAttributeItem implements IBreakableEquipment, IFishingRod, ICraftable {

    public VoidRod(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.ROD;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                AttributeEntry.additive(AttributeWrapper.STRENGTH, getStrength()),
                AttributeEntry.multiplicative(AttributeWrapper.ATTACK_SPEED, -.5),
                AttributeEntry.additive(AttributeWrapper.FISHING_RATING, getFishingRating()),
                AttributeEntry.additive(AttributeWrapper.FISHING_CREATURE_CHANCE, getChance()),
                AttributeEntry.additive(AttributeWrapper.FISHING_TREASURE_CHANCE, getChance())
        );
    }

    @Override
    public int getPowerRating() {
        return switch (getCustomItemType()) {
            case COMET_ROD -> 40;
            case NEBULA_ROD -> 50;
            default -> 0;
        };
    };

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.HAND;
    }

    @Override
    public int getMaxDurability() {
        return getPowerRating() * 1_000;
    }

    @Override
    public Set<FishingFlag> getFishingFlags() {
        return Set.of(FishingFlag.VOID);
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), getCustomItemType().getKey() + "_recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {

        if (this.getCustomItemType() == CustomItemType.COMET_ROD) {
            var recipe = new ShapedRecipe(getRecipeKey(), generate());
            recipe.setCategory(CraftingBookCategory.EQUIPMENT);
            recipe.shape(
                    "  s",
                    " sm",
                    "s m"
            );
            recipe.setIngredient('m', ItemService.generate(CustomItemType.ENCHANTED_STRING));
            recipe.setIngredient('s', ItemService.generate(CustomItemType.DRACONIC_CRYSTAL));
            return recipe;
        }

        if (this.getCustomItemType() == CustomItemType.NEBULA_ROD) {

            var recipe = new ShapedRecipe(getRecipeKey(), generate());
            recipe.setCategory(CraftingBookCategory.EQUIPMENT);
            recipe.shape(
                    "mmm",
                    "mrm",
                    "mmm"
            );
            recipe.setIngredient('m', ItemService.generate(CustomItemType.ECHO_MEMBRANE));
            recipe.setIngredient('r', ItemService.generate(CustomItemType.COMET_ROD));
            return recipe;
        }

        throw new IllegalStateException("Unexpected value: " + this.getCustomItemType());

    }

    /**
     * A collection of items that will unlock the recipe for this item. Typically will be one of the components
     * of the recipe itself, but can be set to whatever is desired
     *
     * @return
     */
    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(
                ItemService.generate(CustomItemType.DRACONIC_CRYSTAL)
        );
    }

    private int getFishingRating() {
        return switch (getCustomItemType()) {
            case COMET_ROD -> 200;
            case NEBULA_ROD -> 300;
            default -> 0;
        };
    };

    private int getStrength() {
        return switch (getCustomItemType()) {
            case COMET_ROD -> 100;
            case NEBULA_ROD -> 150;
            default -> 0;
        };
    };

    private int getChance() {
        return switch (getCustomItemType()) {
            case COMET_ROD -> 6;
            case NEBULA_ROD -> 8;
            default -> 0;
        };
    };
}
