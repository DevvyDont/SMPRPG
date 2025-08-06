package xyz.devvydont.smprpg.util.items;

import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import net.kyori.adventure.util.TriState;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;

public class ToolGlobals {

    public static final int DRAGONSTEEL_TOOL_DURABILITY = 40_000;
    public static final int NETHERITE_TOOL_DURABILITY = 10_000;
    public static final int DIAMOND_TOOL_DURABILITY = 5_000;
    public static final int STEEL_TOOL_DURABILITY = 2_500;
    public static final int GOLD_TOOL_DURABILITY = 1_000;
    public static final int IRON_TOOL_DURABILITY = 1_000;
    public static final int STONE_TOOL_DURABILITY = 750;
    public static final int WOOD_TOOL_DURABILITY = 500;

    public static final int COPPER_TOOL_DURABILITY = 1_000;

    // Power Levels
    public static final int WOOD_TOOL_POWER = 2;
    public static final int STONE_TOOL_POWER = 3;
    public static final int IRON_TOOL_POWER = 7;
    public static final int GOLD_TOOL_POWER = 12;
    public static final int STEEL_TOOL_POWER = 12;
    public static final int DIAMOND_TOOL_POWER = 15;
    public static final int NETHERITE_TOOL_POWER = 25;
    public static final int DRAGONSTEEL_TOOL_POWER = 40;


    // Tool Components
    public static final Registry<BlockType> blockRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BLOCK);

}
