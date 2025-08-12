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
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.IFishingRod;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ToolGlobals;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class WaterRod extends CustomAttributeItem implements IBreakableEquipment, IFishingRod, ICraftable {

    public WaterRod(ItemService itemService, CustomItemType type) {
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
                AttributeEntry.additive(AttributeWrapper.FISHING_TREASURE_CHANCE, getChance()),
                AttributeEntry.additive(AttributeWrapper.FISHING_SPEED, getSpeed())
        );
    }

    private int getSpeed() {
        return switch (this.getCustomItemType()) {
            case IRON_ROD -> 10;
            case DIAMOND_ROD -> 25;
            case PRISMARINE_ROD -> 40;
            default -> 0;
        };
    }

    @Override
    public int getPowerRating() {
        return switch (getCustomItemType()) {
            case IRON_ROD -> ToolGlobals.IRON_TOOL_POWER;
            case DIAMOND_ROD -> ToolGlobals.DIAMOND_TOOL_POWER;
            case PRISMARINE_ROD -> ToolGlobals.NETHERITE_TOOL_POWER-5;
            default -> 0;
        };
    };

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.HAND;
    }

    @Override
    public int getMaxDurability() {
        return Math.max(1, getPowerRating() * 1_000);
    }

    @Override
    public Set<FishingFlag> getFishingFlags() {
        return Set.of(FishingFlag.NORMAL);
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
            case IRON_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(Material.FISHING_ROD));
            case DIAMOND_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.IRON_ROD));
            case PRISMARINE_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.DIAMOND_ROD));
            default -> new RecipeChoice.ExactChoice(ItemService.generate(Material.BARRIER));
        };
    }

    /**
     * Get the material used for crafting the rod part of the rod.
     */
    private RecipeChoice getCraftingMaterial() {
        return switch (this.getCustomItemType()) {
            case IRON_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(Material.IRON_INGOT));
            case DIAMOND_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(Material.DIAMOND));
            case PRISMARINE_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.ENCHANTED_PRISMARINE_CRYSTAL));
            default -> new RecipeChoice.ExactChoice(ItemService.generate(Material.BARRIER));
        };
    }

    /**
     * Get the material used for crafting the rod part of the rod.
     */
    private RecipeChoice getStringMaterial() {
        return switch (this.getCustomItemType()) {
            case PRISMARINE_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.PREMIUM_STRING));
            default -> new RecipeChoice.ExactChoice(ItemService.generate(Material.STRING));
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
        recipe.setGroup("water_rod");
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
        return List.of(ItemService.generate(Material.IRON_INGOT));
    }

    private int getFishingRating() {
        return switch (getCustomItemType()) {
            case IRON_ROD -> 10;
            case DIAMOND_ROD -> 25;
            case PRISMARINE_ROD -> 45;
            default -> 0;
        };
    };

    private int getStrength() {
        return (int) switch (getCustomItemType()) {
            case IRON_ROD -> ItemSword.getSwordDamage(Material.IRON_SWORD) / 2;
            case GOLD_ROD -> ItemSword.getSwordDamage(Material.GOLDEN_SWORD) / 2;
            case DIAMOND_ROD -> ItemSword.getSwordDamage(Material.DIAMOND_SWORD) / 2;
            case PRISMARINE_ROD -> ItemSword.getSwordDamage(Material.DIAMOND_SWORD) / 2 + 10;
            default -> 0;
        };
    };

    private double getChance() {
        return switch (getCustomItemType()) {
            case IRON_ROD -> 0.5;
            case DIAMOND_ROD -> 1;
            case PRISMARINE_ROD -> 2;
            default -> 0;
        };
    };
}
