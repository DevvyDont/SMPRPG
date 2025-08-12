package xyz.devvydont.smprpg.items.blueprints.sets.fishing;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
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
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ToolGlobals;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class VoidRod extends CustomAttributeItem implements IBreakableEquipment, IFishingRod, ICraftable, ISellable {

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
                AttributeEntry.multiplicative(AttributeWrapper.ATTACK_SPEED, ToolGlobals.FISHING_ROD_COOLDOWN),
                AttributeEntry.additive(AttributeWrapper.FISHING_RATING, getFishingRating()),
                AttributeEntry.additive(AttributeWrapper.FISHING_CREATURE_CHANCE, getChance()),
                AttributeEntry.additive(AttributeWrapper.FISHING_TREASURE_CHANCE, getChance()),
                AttributeEntry.additive(AttributeWrapper.FISHING_SPEED, getSpeed())
        );
    }

    @Override
    public int getPowerRating() {
        return switch (getCustomItemType()) {
            case ENDSTONE_ROD -> 25;
            case ENDER_ROD -> 35;
            case COMET_ROD -> 45;
            case NEBULA_ROD -> 60;
            default -> 1;
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

    /**
     * Work out which fishing rod this rod will be crafted from.
     */
    private RecipeChoice getTransmuteComponent() {
        return switch (this.getCustomItemType()) {
            case ENDSTONE_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(Material.END_STONE));
            case ENDER_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.ENDSTONE_ROD));
            case COMET_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.ENDER_ROD));
            case NEBULA_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.COMET_ROD));
            default -> new RecipeChoice.ExactChoice(ItemService.generate(Material.BARRIER));
        };
    }

    /**
     * Get the material used for crafting the rod part of the rod.
     */
    private RecipeChoice getCraftingMaterial() {
        return switch (this.getCustomItemType()) {
            case ENDSTONE_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(Material.END_STONE));
            case ENDER_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.PREMIUM_ENDER_PEARL));
            case COMET_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.OBSIDIAN_TOOL_ROD));
            case NEBULA_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.DRACONIC_CRYSTAL));
            default -> new RecipeChoice.ExactChoice(ItemService.generate(Material.BARRIER));
        };
    }

    /**
     * Get the material used for crafting the rod part of the rod.
     */
    private RecipeChoice getStringMaterial() {
        return switch (this.getCustomItemType()) {
            case ENDSTONE_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.PREMIUM_STRING));
            case ENDER_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.ENCHANTED_STRING));
            case COMET_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.ASTRAL_FILAMENT));
            case NEBULA_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.ETHEREAL_FIBER));
            default -> new RecipeChoice.ExactChoice(ItemService.generate(Material.BARRIER));
        };
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(getRecipeKey(), generate());
        recipe.shape(
                "  m",
                " ts",
                "m s"
        );
        recipe.setIngredient('m', getCraftingMaterial());
        recipe.setIngredient('t', getTransmuteComponent());
        recipe.setIngredient('s', getStringMaterial());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        recipe.setGroup("void_rod");
        return recipe;
    }

    /**
     * A collection of items that will unlock the recipe for this item. Typically will be one of the components
     * of the recipe itself, but can be set to whatever is desired
     *
     * @return
     */
    @Override
    public Collection<ItemStack> unlockedBy() {
        if (getCraftingMaterial() instanceof RecipeChoice.ExactChoice exact)
            return List.of(exact.getItemStack());
        return List.of(ItemService.generate(Material.END_STONE));
    }

    private int getFishingRating() {
        return switch (getCustomItemType()) {
            case ENDSTONE_ROD -> 45;
            case ENDER_ROD -> 65;
            case COMET_ROD -> 100;
            case NEBULA_ROD -> 170;
            default -> 0;
        };
    };

    private int getStrength() {
        return switch (getCustomItemType()) {
            case ENDSTONE_ROD -> 50;
            case ENDER_ROD -> 65;
            case COMET_ROD -> 90;
            case NEBULA_ROD -> 120;
            default -> 0;
        };
    };

    private int getChance() {
        return switch (getCustomItemType()) {
            case ENDSTONE_ROD -> 1;
            case ENDER_ROD -> 2;
            case COMET_ROD -> 3;
            case NEBULA_ROD -> 4;
            default -> 0;
        };
    };

    private int getSpeed() {
        return switch (this.getCustomItemType()) {
            case ENDSTONE_ROD -> 5;
            case ENDER_ROD -> 25;
            case COMET_ROD -> 50;
            case NEBULA_ROD -> 80;
            default -> 0;
        };
    }

    @Override
    public int getWorth(ItemStack item) {
        var base =  super.getWorth(item);

        return base + switch (this.getCustomItemType()) {
            case ENDSTONE_ROD -> 50;
            case ENDER_ROD -> 500;
            case COMET_ROD -> 15_000;
            case NEBULA_ROD -> 500_000;
            default -> 0;
        };
    }
}
