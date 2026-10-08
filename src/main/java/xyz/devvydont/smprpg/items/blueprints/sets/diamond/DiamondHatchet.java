package xyz.devvydont.smprpg.items.blueprints.sets.diamond;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import net.kyori.adventure.util.TriState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemPickaxe;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.tools.ItemHatchet;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ToolGlobals;

public class DiamondHatchet extends ItemHatchet implements IBreakableEquipment {

    public static final Tool TOOL_COMP = Tool.tool()
            .defaultMiningSpeed(1.0f)
            .addRule(Tool.rule(ToolGlobals.blockRegistry.getTag(BlockTypeTagKeys.INCORRECT_FOR_DIAMOND_TOOL), 0.0001f, TriState.FALSE))
            .addRule(Tool.rule(ToolGlobals.blockRegistry.getTag(BlockTypeTagKeys.MINEABLE_AXE), 0.0001f, TriState.TRUE))
            .addRule(Tool.rule(ToolGlobals.blockRegistry.getTag(BlockTypeTagKeys.MINEABLE_HOE), 0.0001f, TriState.TRUE))
            .build();

    public DiamondHatchet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getPowerRating() {
        return ItemSword.getSwordRating(Material.DIAMOND_SWORD);
    }

    @Override
    public double getHatchetMiningPower() { return ToolGlobals.DIAMOND_TOOL_MINING_POWER; }

    @Override
    public double getHatchetDamage() { return ItemSword.getSwordDamage(Material.DIAMOND_SWORD) - 12; }

    @Override
    public double getHatchetFortune() { return ItemPickaxe.getPickaxeFortune(Material.DIAMOND_PICKAXE) * 0.8; }

    @Override
    public double getHatchetSpeed() { return ItemPickaxe.getPickaxeSpeed(Material.DIAMOND_PICKAXE) * 0.8; }

    @Override
    public void updateItemData(ItemStack itemStack) {
        super.updateItemData(itemStack);
        itemStack.setData(DataComponentTypes.TOOL, TOOL_COMP);
    }

    @Override
    public int getMaxDurability() {
        return ToolGlobals.DIAMOND_TOOL_DURABILITY;
    }

}
