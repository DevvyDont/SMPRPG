package xyz.devvydont.smprpg.util.items;

import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import net.kyori.adventure.util.TriState;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.jetbrains.annotations.NotNull;

public class ToolGlobals {

    //<editor-fold desc="Durability">
    public static final int WOOD_TOOL_DURABILITY = 64;

    public static final int COPPER_TOOL_DURABILITY = 160;
    public static final int SILVER_TOOL_DURABILITY = 96;
    public static final int TIN_TOOL_DURABILITY = 128;

    public static final int IRON_TOOL_DURABILITY = 320;
    public static final int BRONZE_TOOL_DURABILITY = 256;
    public static final int GOLD_TOOL_DURABILITY = 192;
    public static final int ROSE_GOLD_TOOL_DURABILITY = 288;

    public static final int STEEL_TOOL_DURABILITY = 512;
    public static final int MITHRIL_TOOL_DURABILITY = 448;
    public static final int ELECTRUM_TOOL_DURABILITY = 576;

    public static final int TITANIUM_TOOL_DURABILITY = 1_024;
    public static final int ADAMANTIUM_TOOL_DURABILITY = 1_536;

    public static final int TUNGSTEN_TOOL_DURABILITY = 768;

    public static final int COBALT_TOOL_DURABILITY = 1_152;
    public static final int ORICHALCUM_TOOL_DURABILITY = 1_280;

    public static final int NETHERITE_TOOL_DURABILITY = 2048;

    public static final int DRAGONSTEEL_TOOL_DURABILITY = 2_560;

    //<editor-fold desc="Unused">
    public static final int STONE_TOOL_DURABILITY = 80;
    public static final int DIAMOND_TOOL_DURABILITY = 1_024;
    //</editor-fold>
    //</editor-fold>
    //<editor-fold desc="Power Levels">
    public static final int WOOD_TOOL_POWER = 2;

    public static final int COPPER_TOOL_POWER = 3;
    public static final int SILVER_TOOL_POWER = 5;
    public static final int TIN_TOOL_POWER = 3;

    public static final int IRON_TOOL_POWER = 7;
    public static final int BRONZE_TOOL_POWER = 8;
    public static final int GOLD_TOOL_POWER = 10;
    public static final int ROSE_GOLD_TOOL_POWER = 11;

    public static final int STEEL_TOOL_POWER = 11;
    public static final int MITHRIL_TOOL_POWER = 12;
    public static final int ELECTRUM_TOOL_POWER = 13;

    public static final int TITANIUM_TOOL_POWER = 15;
    public static final int ADAMANTIUM_TOOL_POWER = 20;

    public static final int TUNGSTEN_TOOL_POWER = 25;
    public static final int COBALT_TOOL_POWER = 30;

    public static final int ORICHALCUM_TOOL_POWER = 30;

    public static final int NETHERITE_TOOL_POWER = 40;

    public static final int DRAGONSTEEL_TOOL_POWER = 60;

    //<editor-fold desc="Unused">
    public static final int STONE_TOOL_POWER = 3;
    public static final int DIAMOND_TOOL_POWER = 15;
    //</editor-fold>
    //</editor-fold>
    //<editor-fold desc="Fortune">
    public static final int WOOD_TOOL_FORTUNE = 5;

    public static final int COPPER_TOOL_FORTUNE = 15;
    public static final int SILVER_TOOL_FORTUNE = 30;
    public static final int TIN_TOOL_FORTUNE = 20;

    public static final int IRON_TOOL_FORTUNE = 30;
    public static final int BRONZE_TOOL_FORTUNE = 50;
    public static final int GOLD_TOOL_FORTUNE = 60;
    public static final int ROSE_GOLD_TOOL_FORTUNE = 70;

    public static final int STEEL_TOOL_FORTUNE = 35;
    public static final int MITHRIL_TOOL_FORTUNE = 15;
    public static final int ELECTRUM_TOOL_FORTUNE = 80;

    public static final int TITANIUM_TOOL_FORTUNE = 45;
    public static final int ADAMANTIUM_TOOL_FORTUNE = 90;

    public static final int TUNGSTEN_TOOL_FORTUNE = 50;
    public static final int COBALT_TOOL_FORTUNE = 60;

    public static final int ORICHALCUM_TOOL_FORTUNE = 80;

    public static final int NETHERITE_TOOL_FORTUNE = 120;

    public static final int DRAGONSTEEL_TOOL_FORTUNE = 150;

    //<editor-fold desc="Unused">
    public static final int STONE_TOOL_FORTUNE = 15;
    public static final int DIAMOND_TOOL_FORTUNE = 45;
    //</editor-fold>
    //</editor-fold>
    //<editor-fold desc="Tool Speeds">
    public static final int WOOD_TOOL_SPEED = 200;

    public static final int COPPER_TOOL_SPEED = 400;
    public static final int SILVER_TOOL_SPEED = 700;
    public static final int TIN_TOOL_SPEED = 200;

    public static final int IRON_TOOL_SPEED = 600;
    public static final int BRONZE_TOOL_SPEED = 400;
    public static final int GOLD_TOOL_SPEED = 800;
    public static final int ROSE_GOLD_TOOL_SPEED = 700;

    public static final int STEEL_TOOL_SPEED = 700;
    public static final int MITHRIL_TOOL_SPEED = 900;
    public static final int ELECTRUM_TOOL_SPEED = 1000;

    public static final int TITANIUM_TOOL_SPEED = 800;
    public static final int ADAMANTIUM_TOOL_SPEED = 1100;

    public static final int TUNGSTEN_TOOL_SPEED = 900;
    public static final int COBALT_TOOL_SPEED = 1400;

    public static final int ORICHALCUM_TOOL_SPEED = 1000;

    public static final int NETHERITE_TOOL_SPEED = 1600;

    public static final int DRAGONSTEEL_TOOL_SPEED = 2000;

    //<editor-fold desc="Unused">
    public static final int STONE_TOOL_SPEED = 400;
    public static final int DIAMOND_TOOL_SPEED = 800;
    //</editor-fold>
    //</editor-fold>
    //<editor-fold desc="Breaking Powers">
    public static final int WOOD_TOOL_MINING_POWER = 1;

    public static final int COPPER_TOOL_MINING_POWER = 2;
    public static final int SILVER_TOOL_MINING_POWER = 2;
    public static final int TIN_TOOL_MINING_POWER = 2;

    public static final int IRON_TOOL_MINING_POWER = 3;
    public static final int BRONZE_TOOL_MINING_POWER = 3;
    public static final int GOLD_TOOL_MINING_POWER = 3;
    public static final int ROSE_GOLD_TOOL_MINING_POWER = 3;

    public static final int STEEL_TOOL_MINING_POWER = 4;
    public static final int MITHRIL_TOOL_MINING_POWER = 4;
    public static final int ELECTRUM_TOOL_MINING_POWER = 4;

    public static final int TITANIUM_TOOL_MINING_POWER = 5;
    public static final int ADAMANTIUM_TOOL_MINING_POWER = 5;

    public static final int TUNGSTEN_TOOL_MINING_POWER = 6;
    public static final int COBALT_TOOL_MINING_POWER = 7;

    public static final int ORICHALCUM_TOOL_MINING_POWER = 7;

    public static final int NETHERITE_TOOL_MINING_POWER = 8;

    public static final int DRAGONSTEEL_TOOL_MINING_POWER = 9;

    //<editor-fold desc="Unused">
    public static final int STONE_TOOL_MINING_POWER = 2;
    public static final int DIAMOND_TOOL_MINING_POWER = 5;
    //</editor-fold>
    //</editor-fold>

    // Attack Cooldowns
    public static final double FISHING_ROD_COOLDOWN = -0.5;


    // Tool Components
    public static final Registry<@NotNull BlockType> blockRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BLOCK);

}
