package xyz.devvydont.smprpg.items.blueprints.sets.dragonsteel;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import net.kyori.adventure.util.TriState;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.crafting.builders.PickaxeRecipe;
import xyz.devvydont.smprpg.util.items.ToolGlobals;

import java.util.Collection;
import java.util.List;

import static xyz.devvydont.smprpg.items.blueprints.vanilla.ItemPickaxe.PICKAXE_ATTACK_SPEED_DEBUFF;

public class DragonsteelPickaxe extends CustomAttributeItem implements IBreakableEquipment, ICraftable {

    public static final Tool TOOL_COMP = Tool.tool()
            .defaultMiningSpeed(1.0f)
            .addRule(Tool.rule(ToolGlobals.blockRegistry.getTag(BlockTypeTagKeys.INCORRECT_FOR_DIAMOND_TOOL), 1.0f, TriState.FALSE))
            .addRule(Tool.rule(ToolGlobals.blockRegistry.getTag(BlockTypeTagKeys.MINEABLE_PICKAXE), 11.0f, TriState.TRUE))
            .build();

    public DragonsteelPickaxe(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 40),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, PICKAXE_ATTACK_SPEED_DEBUFF),
                new AdditiveAttributeEntry(AttributeWrapper.MINING_FORTUNE, 100)
        );
    }

    @Override
    public int getPowerRating() {
        return ToolGlobals.DRAGONSTEEL_TOOL_POWER;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.PICKAXE;
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.MAINHAND;
    }

    @Override
    public int getMaxDurability() {
        return ToolGlobals.DRAGONSTEEL_TOOL_DURABILITY;
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
        return new PickaxeRecipe(this,
                itemService.getCustomItem(CustomItemType.DRAGONSTEEL_INGOT),
                itemService.getCustomItem(CustomItemType.OBSIDIAN_TOOL_ROD),
                generate()
        ).build();
    }

    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(
                itemService.getCustomItem(CustomItemType.DRAGONSTEEL_INGOT)
        );
    }
}
