package xyz.devvydont.smprpg.items.blueprints.sets.wood;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import net.kyori.adventure.util.TriState;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.crafting.CraftingGlobals;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemPickaxe;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.tools.ItemHatchet;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ToolGlobals;

import java.util.Collection;
import java.util.List;

public class WoodHatchet extends ItemHatchet implements ICraftable, IBreakableEquipment {

    public static final Tool TOOL_COMP = Tool.tool()
            .defaultMiningSpeed(1.0f)
            .addRule(Tool.rule(ToolGlobals.blockRegistry.getTag(BlockTypeTagKeys.INCORRECT_FOR_WOODEN_TOOL), 1.0f, TriState.FALSE))
            .addRule(Tool.rule(ToolGlobals.blockRegistry.getTag(BlockTypeTagKeys.MINEABLE_AXE), 2.0f, TriState.TRUE))
            .addRule(Tool.rule(ToolGlobals.blockRegistry.getTag(BlockTypeTagKeys.MINEABLE_HOE), 1.5f, TriState.TRUE))
            .build();

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
    public double getHatchetFortune() { return ItemPickaxe.getPickaxeFortune(Material.WOODEN_PICKAXE) * 0.8; }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getInstance(), getCustomItemType().getKey() + "-recipe");
    }

    @Override
    public void updateItemData(ItemStack itemStack) {
        super.updateItemData(itemStack);
        itemStack.setData(DataComponentTypes.TOOL, TOOL_COMP);
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(getRecipeKey(), generate());
        recipe.shape(
                "p ",
                "ps",
                " s"
        );
        recipe.setIngredient('p', CraftingGlobals.WOODEN_PLANKS_WILDCARD);
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
        return ToolGlobals.WOOD_TOOL_DURABILITY;
    }

}
