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
import xyz.devvydont.smprpg.util.items.ToolsUtil;

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
                AttributeEntry.additive(AttributeWrapper.FISHING_TREASURE_CHANCE, getChance())
        );
    }

    @Override
    public int getPowerRating() {
        return switch (getCustomItemType()) {
            case IRON_ROD -> ToolsUtil.IRON_TOOL_POWER;
            case GOLD_ROD -> ToolsUtil.GOLD_TOOL_POWER;
            case DIAMOND_ROD -> ToolsUtil.DIAMOND_TOOL_POWER;
            case PRISMARINE_ROD -> ToolsUtil.NETHERITE_TOOL_POWER-5;
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
        return Set.of(FishingFlag.NORMAL);
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), getCustomItemType().getKey() + "_recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {

        if (this.getCustomItemType() == CustomItemType.PRISMARINE_ROD) {
            var recipe = new ShapedRecipe(getRecipeKey(), generate());
            recipe.setCategory(CraftingBookCategory.EQUIPMENT);
            recipe.shape(
                    "sms",
                    "mrm",
                    "sms"
            );
            recipe.setIngredient('m', ItemService.generate(CustomItemType.PREMIUM_PRISMARINE_CRYSTAL));
            recipe.setIngredient('s', ItemService.generate(CustomItemType.PREMIUM_PRISMARINE_SHARD));
            recipe.setIngredient('r', ItemService.generate(CustomItemType.DIAMOND_ROD));
            return recipe;
        }

        var recipe = new ShapedRecipe(getRecipeKey(), generate());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        recipe.shape(
                "  s",
                " sm",
                "s m"
        );
        var mat = getCraftingMaterial();
        recipe.setIngredient('s', mat);
        recipe.setIngredient('m', Material.STRING);
        return recipe;
    }

    private ItemStack getCraftingMaterial() {
        return switch (this.getCustomItemType()) {
            case IRON_ROD -> ItemService.generate(Material.IRON_INGOT);
            case GOLD_ROD -> ItemService.generate(Material.GOLD_INGOT);
            case DIAMOND_ROD -> ItemService.generate(Material.DIAMOND);
            default -> throw new IllegalStateException("Unexpected value: " + this.getCustomItemType());
        };
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
                ItemService.generate(Material.IRON_INGOT)
        );
    }

    private int getFishingRating() {
        return switch (getCustomItemType()) {
            case IRON_ROD -> 20;
            case GOLD_ROD -> 35;
            case DIAMOND_ROD -> 65;
            case PRISMARINE_ROD -> 100;
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
            case IRON_ROD -> 0.25;
            case GOLD_ROD -> 0.5;
            case DIAMOND_ROD -> 1;
            case PRISMARINE_ROD -> 1.5;
            default -> 0;
        };
    };
}
