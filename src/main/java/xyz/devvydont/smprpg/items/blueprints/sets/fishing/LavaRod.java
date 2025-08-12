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
import xyz.devvydont.smprpg.items.blueprints.sets.steel.SteelSword;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.IFishingRod;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ToolGlobals;

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
            case GOLD_ROD -> ToolGlobals.GOLD_TOOL_POWER;
            case STEEL_ROD -> ToolGlobals.STEEL_TOOL_POWER;
            case NETHERITE_ROD -> ToolGlobals.NETHERITE_TOOL_POWER;
            case SPITFIRE_ROD -> 40;
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
        return Set.of(FishingFlag.LAVA);
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
            case GOLD_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(Material.GOLD_INGOT));
            case STEEL_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.GOLD_ROD));
            case NETHERITE_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.STEEL_ROD));
            case SPITFIRE_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.NETHERITE_ROD));
            default -> new RecipeChoice.ExactChoice(ItemService.generate(Material.BARRIER));
        };
    }

    /**
     * Get the material used for crafting the rod part of the rod.
     */
    private RecipeChoice getCraftingMaterial() {
        return switch (this.getCustomItemType()) {
            case GOLD_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(Material.GOLD_INGOT));
            case STEEL_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.STEEL_INGOT));
            case NETHERITE_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(Material.NETHERITE_INGOT));
            case SPITFIRE_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.BOILING_INGOT));
            default -> new RecipeChoice.ExactChoice(ItemService.generate(Material.BARRIER));
        };
    }

    /**
     * Get the material used for crafting the rod part of the rod.
     */
    private RecipeChoice getStringMaterial() {
        return switch (this.getCustomItemType()) {
            case GOLD_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(Material.STRING));
            case STEEL_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.PREMIUM_STRING));
            case NETHERITE_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.ENCHANTED_STRING));
            case SPITFIRE_ROD -> new RecipeChoice.ExactChoice(ItemService.generate(CustomItemType.SCORCHING_STRING));
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
        recipe.setGroup("lava_rod");
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
        return List.of(ItemService.generate(Material.GOLD_INGOT));
    }

    private int getFishingRating() {
        return switch (getCustomItemType()) {
            case GOLD_ROD -> 15;
            case STEEL_ROD -> 25;
            case NETHERITE_ROD -> 50;
            case SPITFIRE_ROD -> 80;
            default -> 0;
        };
    };

    private int getStrength() {
        return (int) switch (getCustomItemType()) {
            case GOLD_ROD -> 15;
            case STEEL_ROD -> ItemSword.getSwordDamage(Material.GOLDEN_SWORD) - 10;
            case NETHERITE_ROD -> ItemSword.getSwordDamage(Material.NETHERITE_SWORD) / 2;
            case SPITFIRE_ROD -> ItemSword.getSwordDamage(Material.NETHERITE_SWORD);
            default -> 0;
        };
    };

    private double getChance() {
        return switch (getCustomItemType()) {
            case GOLD_ROD -> 0.5;
            case STEEL_ROD -> 1;
            case NETHERITE_ROD -> 2;
            case SPITFIRE_ROD -> 3;
            default -> 0;
        };
    };

    private int getSpeed() {
        return switch (this.getCustomItemType()) {
            case GOLD_ROD -> 5;
            case STEEL_ROD -> 15;
            case NETHERITE_ROD -> 40;
            case SPITFIRE_ROD -> 60;
            default -> 0;
        };
    }
}
