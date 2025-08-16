package xyz.devvydont.smprpg.items.blueprints.sets.steel;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import net.kyori.adventure.util.TriState;
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
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemAxe;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemPickaxe;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ToolGlobals;

import java.util.Collection;
import java.util.List;

import static xyz.devvydont.smprpg.items.blueprints.vanilla.ItemAxe.AXE_ATTACK_SPEED_DEBUFF;

public class SteelHatchet extends CustomAttributeItem implements ICraftable, IBreakableEquipment {

    public static final Tool TOOL_COMP = Tool.tool()
            .defaultMiningSpeed(1.0f)
            .addRule(Tool.rule(ToolGlobals.blockRegistry.getTag(BlockTypeTagKeys.INCORRECT_FOR_IRON_TOOL), 1.0f, TriState.FALSE))
            .addRule(Tool.rule(ToolGlobals.blockRegistry.getTag(BlockTypeTagKeys.MINEABLE_AXE), 7.0f, TriState.TRUE))
            .addRule(Tool.rule(ToolGlobals.blockRegistry.getTag(BlockTypeTagKeys.MINEABLE_HOE), 5.25f, TriState.TRUE))
            .build();

    public SteelHatchet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 31),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, AXE_ATTACK_SPEED_DEBUFF),
                new AdditiveAttributeEntry(AttributeWrapper.WOODCUTTING_FORTUNE, 28),
                new AdditiveAttributeEntry(AttributeWrapper.FARMING_FORTUNE, 28)
        );
    }

    @Override
    public int getPowerRating() { return ToolGlobals.STEEL_TOOL_POWER; }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.HATCHET;
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.MAINHAND;
    }

    @Override
    public int getMaxDurability() {
        return ToolGlobals.STEEL_TOOL_DURABILITY;
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), getCustomItemType().getKey() + "-recipe");
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
                "c ",
                "cs",
                " s"
        );
        recipe.setIngredient('c', itemService.getCustomItem(CustomItemType.STEEL_INGOT));
        recipe.setIngredient('s', itemService.getCustomItem(Material.STICK));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }

    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(
                itemService.getCustomItem(CustomItemType.STEEL_INGOT)
        );
    }

}
