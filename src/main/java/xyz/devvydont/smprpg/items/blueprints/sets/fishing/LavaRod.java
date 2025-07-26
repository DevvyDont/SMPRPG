package xyz.devvydont.smprpg.items.blueprints.sets.fishing;

import org.bukkit.Material;
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
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.IFishingRod;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class LavaRod extends CustomAttributeItem implements IBreakableEquipment, IFishingRod, ICraftable {

    public LavaRod(ItemService itemService, CustomItemType type) {
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
            case NETHERITE_ROD -> 25;
            case SPITFIRE_ROD -> 40;
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
        return Set.of(FishingFlag.LAVA);
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getInstance(), getCustomItemType().getKey() + "_recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {

        if (this.getCustomItemType() == CustomItemType.NETHERITE_ROD) {
            var recipe = new ShapedRecipe(getRecipeKey(), generate());
            recipe.setCategory(CraftingBookCategory.EQUIPMENT);
            recipe.shape(
                    "  s",
                    " sm",
                    "s m"
            );
            recipe.setIngredient('m', ItemService.generate(CustomItemType.PREMIUM_STRING));
            recipe.setIngredient('s', ItemService.generate(Material.NETHERITE_INGOT));
            return recipe;
        }

        if (this.getCustomItemType() == CustomItemType.SPITFIRE_ROD) {

            var recipe = new ShapedRecipe(getRecipeKey(), generate());
            recipe.setCategory(CraftingBookCategory.EQUIPMENT);
            recipe.shape(
                    "mmm",
                    "mrm",
                    "mmm"
            );
            recipe.setIngredient('m', ItemService.generate(CustomItemType.CINDERITE));
            recipe.setIngredient('r', ItemService.generate(CustomItemType.NETHERITE_ROD));
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
                ItemService.generate(Material.NETHERITE_INGOT)
        );
    }

    private int getFishingRating() {
        return switch (getCustomItemType()) {
            case NETHERITE_ROD -> 120;
            case SPITFIRE_ROD -> 200;
            default -> 0;
        };
    };

    private int getStrength() {
        return (int) switch (getCustomItemType()) {
            case NETHERITE_ROD -> ItemSword.getSwordDamage(Material.NETHERITE_SWORD) / 2;
            case SPITFIRE_ROD -> ItemSword.getSwordDamage(Material.NETHERITE_SWORD);
            default -> 0;
        };
    };

    private int getChance() {
        return switch (getCustomItemType()) {
            case NETHERITE_ROD -> 2;
            case SPITFIRE_ROD -> 3;
            default -> 0;
        };
    };
}
