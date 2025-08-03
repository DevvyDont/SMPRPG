package xyz.devvydont.smprpg.items.blueprints.sets.wood;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemPickaxe;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.tools.ItemHatchet;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ToolsUtil;

import java.util.Collection;
import java.util.List;

public class WoodHatchet extends ItemHatchet implements ICraftable, IBreakableEquipment {

    public WoodHatchet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getPowerRating() {
        return ItemSword.getSwordRating(Material.WOODEN_SWORD);
    }

    @Override
    public double getHatchetDamage() { return ItemSword.getSwordDamage(Material.WOODEN_SWORD) - 5; }

    @Override
    public double getHatchetFortune() { return ItemPickaxe.getPickaxeFortune(Material.WOODEN_PICKAXE); }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getInstance(), getCustomItemType().getKey() + "-recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(getRecipeKey(), generate());
        recipe.shape(
                "p ",
                "ps",
                " s"
        );
        recipe.setIngredient('p', new RecipeChoice.MaterialChoice(Material.OAK_PLANKS,
                                                                        Material.SPRUCE_PLANKS,
                                                                        Material.BIRCH_PLANKS,
                                                                        Material.JUNGLE_PLANKS,
                                                                        Material.ACACIA_PLANKS,
                                                                        Material.DARK_OAK_PLANKS,
                                                                        Material.BAMBOO_PLANKS,
                                                                        Material.CHERRY_PLANKS,
                                                                        Material.MANGROVE_PLANKS,
                                                                        Material.CRIMSON_PLANKS,
                                                                        Material.WARPED_PLANKS,
                                                                        Material.PALE_OAK_PLANKS));
        recipe.setIngredient('s', itemService.getCustomItem(Material.STICK));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }

    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(itemService.getCustomItem(Material.OAK_PLANKS));
    }

    @Override
    public int getMaxDurability() {
        return ToolsUtil.WOOD_TOOL_DURABILITY;
    }

}
