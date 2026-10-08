package xyz.devvydont.smprpg.items.blueprints.sets.cobblestone;

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

public class StoneHatchet extends ItemHatchet implements IBreakableEquipment {

    public static final Tool TOOL_COMP = Tool.tool()
            .defaultMiningSpeed(0.0001f)
            .build();

    public StoneHatchet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getPowerRating() {
        return ItemSword.getSwordRating(Material.STONE_SWORD);
    }

    @Override
    public double getHatchetMiningPower() { return ToolGlobals.STONE_TOOL_MINING_POWER; }

    @Override
    public double getHatchetDamage() { return ItemSword.getSwordDamage(Material.STONE_SWORD) - 5; }

    @Override
    public double getHatchetFortune() { return ItemPickaxe.getPickaxeFortune(Material.STONE_PICKAXE) * 0.8; }

    @Override
    public double getHatchetSpeed() { return ItemPickaxe.getPickaxeSpeed(Material.STONE_PICKAXE) * 0.8; }

    @Override
    public void updateItemData(ItemStack itemStack) {
        super.updateItemData(itemStack);
        itemStack.setData(DataComponentTypes.TOOL, TOOL_COMP);
    }

    @Override
    public int getMaxDurability() {
        return ToolGlobals.STONE_TOOL_DURABILITY;
    }

}
