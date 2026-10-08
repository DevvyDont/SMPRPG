package xyz.devvydont.smprpg.blockbreaking;

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.event.CraftEngineReloadEvent;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.block.BlockSound;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.util.craftengine.CraftEngineHelpers;

import java.util.*;

/**
 * The loot dropped from blocks is a very complex system. When blocks are dropped, there's conditions
 * that can be met that completely shift the result of what is dropped.
 * The idea with our plugin, is that we hook into specific events to override the drops of such events, and assume
 * vanilla (unchanged) behavior when our plugin doesn't have a case defined. Think of events as follows:
 * - Breaking a block with a preferred tool. (pickaxe breaking stone)
 * - Breaking a block with silk touch.
 * - Breaking a block with auto smelt.
 * - Breaking a block with incorrect tool. (fist and stone, or axe and stone)
 */
public class BlockPropertiesRegistry implements Listener {

    private static final Map<Material, BlockPropertiesEntry> entries = new EnumMap<>(Material.class);
    private static final Map<Key, BlockPropertiesEntry> specialEntries = new HashMap<>();

    public BlockPropertiesRegistry() {
        SMPRPG.getPlugin().getServer().getPluginManager().registerEvents(this, SMPRPG.getPlugin());

        registerVanillaMaterials();
        registerCraftEngineMaterials();
    }

    // Maps every block type to specified mining properties. Keep in mind, EVERY block needs to be defined here.
    private static void registerVanillaMaterials() {
        //<editor-fold desc="Shovelable blocks">
        //<editor-fold desc="Concrete powders">
        register(Material.CLAY, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(60)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.COARSE_DIRT, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WHITE_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ORANGE_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.MAGENTA_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIGHT_BLUE_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.YELLOW_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIME_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PINK_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GRAY_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIGHT_GRAY_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CYAN_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PURPLE_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BLUE_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BROWN_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GREEN_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.RED_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BLACK_CONCRETE_POWDER, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>

        register(Material.DIRT, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DIRT_PATH, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(65)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.FARMLAND, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GRAVEL, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(60)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GRASS_BLOCK, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(60)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.MUD, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.MYCELIUM, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PODZOL, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.RED_SAND, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ROOTED_DIRT, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SAND, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SNOW, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SNOW_BLOCK, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POWDER_SNOW, BlockPropertiesEntry.builder()
                .hardness(25)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ICE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PACKED_ICE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BLUE_ICE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(280)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SOUL_SAND, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SOUL_SOIL, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SUSPICIOUS_GRAVEL, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(25)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SUSPICIOUS_SAND, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL)
                .hardness(25)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>

        //<editor-fold desc="Pickaxe blocks">
        //<editor-fold desc="Ores">
        register(Material.AMETHYST_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.AMETHYST_CLUSTER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SMALL_AMETHYST_BUD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.MEDIUM_AMETHYST_BUD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LARGE_AMETHYST_BUD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ANCIENT_DEBRIS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(3000)
                .breakingPower(7)
                .softRequirement(false)
                .build());

        register(Material.BUDDING_AMETHYST, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(600)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.COAL_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.COPPER_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DIAMOND_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(4)
                .softRequirement(false)
                .build());

        register(Material.EMERALD_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(4)
                .softRequirement(false)
                .build());

        register(Material.GOLD_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.IRON_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.LAPIS_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.NETHER_GOLD_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.NETHER_QUARTZ_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.REDSTONE_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_COAL_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(450)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_COPPER_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(450)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_DIAMOND_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(450)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_EMERALD_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(450)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_GOLD_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(450)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_IRON_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(450)
                .breakingPower(4)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_LAPIS_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(450)
                .breakingPower(4)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_REDSTONE_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(450)
                .breakingPower(5)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //<editor-fold desc="Non-Ore">
        register(Material.BEDROCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(-1)
                .breakingPower(-1)
                .softRequirement(false)
                .build());

        register(Material.STONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.STONE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.STONE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.STONE_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.STONE_BUTTON, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.COBBLESTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.COBBLESTONE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.COBBLESTONE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.COBBLESTONE_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.MOSSY_COBBLESTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.MOSSY_COBBLESTONE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.MOSSY_COBBLESTONE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.MOSSY_COBBLESTONE_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.SMOOTH_STONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.SMOOTH_STONE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.STONE_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.STONE_BRICK_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.STONE_BRICK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.STONE_BRICK_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.CRACKED_STONE_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.CHISELED_STONE_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.MOSSY_STONE_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.MOSSY_STONE_BRICK_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.MOSSY_STONE_BRICK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.MOSSY_STONE_BRICK_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.INFESTED_COBBLESTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.INFESTED_STONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(75)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.INFESTED_MOSSY_STONE_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(75)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.INFESTED_CRACKED_STONE_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(75)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.INFESTED_CHISELED_STONE_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(75)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.INFESTED_DEEPSLATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.INFESTED_COBBLESTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DEEPSLATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.COBBLED_DEEPSLATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.COBBLED_DEEPSLATE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.COBBLED_DEEPSLATE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.COBBLED_DEEPSLATE_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHISELED_DEEPSLATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_DEEPSLATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_DEEPSLATE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_DEEPSLATE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_DEEPSLATE_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_BRICK_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_BRICK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_BRICK_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CRACKED_DEEPSLATE_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_TILES, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_TILES, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_TILE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.DEEPSLATE_TILE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CRACKED_DEEPSLATE_TILES, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.REINFORCED_DEEPSLATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(-1)
                .breakingPower(-1)
                .softRequirement(false)
                .build());

        register(Material.CALCITE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(75)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.GRANITE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.GRANITE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.GRANITE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.GRANITE_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_GRANITE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_GRANITE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_GRANITE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.ANDESITE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.ANDESITE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.ANDESITE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.ANDESITE_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_ANDESITE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_ANDESITE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_ANDESITE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DIORITE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DIORITE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DIORITE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DIORITE_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_DIORITE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_DIORITE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_DIORITE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.TUFF, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.TUFF_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.TUFF_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.TUFF_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.TUFF_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.TUFF_BRICK_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.TUFF_BRICK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.TUFF_BRICK_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHISELED_TUFF, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_TUFF, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_TUFF_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_TUFF_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_TUFF_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.TUFF_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.TUFF_BRICK_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.TUFF_BRICK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.TUFF_BRICK_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHISELED_TUFF_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.DRIPSTONE_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POINTED_DRIPSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PRISMARINE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PRISMARINE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PRISMARINE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PRISMARINE_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PRISMARINE_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PRISMARINE_BRICK_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PRISMARINE_BRICK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DARK_PRISMARINE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DARK_PRISMARINE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DARK_PRISMARINE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.SEA_LANTERN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.MAGMA_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.CRYING_OBSIDIAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(5000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.OBSIDIAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(5000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.NETHERRACK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(40)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.CRIMSON_NYLIUM, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(40)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WARPED_NYLIUM, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(40)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BONE_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BLACKSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BLACKSTONE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BLACKSTONE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BLACKSTONE_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.GILDED_BLACKSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.CHISELED_POLISHED_BLACKSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_BLACKSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_BLACKSTONE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_BLACKSTONE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_BLACKSTONE_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_BLACKSTONE_BUTTON, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POLISHED_BLACKSTONE_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POLISHED_BLACKSTONE_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_BLACKSTONE_BRICK_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_BLACKSTONE_BRICK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_BLACKSTONE_BRICK_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.BRICK_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.BRICK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.BRICK_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.PACKED_MUD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.MUD_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.MUD_BRICK_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.MUD_BRICK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.MUD_BRICK_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.RESIN_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.RESIN_BRICK_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.RESIN_BRICK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.RESIN_BRICK_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.SANDSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.SANDSTONE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.SANDSTONE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.SANDSTONE_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.CUT_SANDSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(90)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.CUT_SANDSTONE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.CHISELED_SANDSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.SMOOTH_SANDSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.SMOOTH_SANDSTONE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.SMOOTH_SANDSTONE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.RED_SANDSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.RED_SANDSTONE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.RED_SANDSTONE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.RED_SANDSTONE_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.CUT_RED_SANDSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(90)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.CUT_RED_SANDSTONE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.CHISELED_RED_SANDSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.SMOOTH_RED_SANDSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.SMOOTH_RED_SANDSTONE_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.SMOOTH_RED_SANDSTONE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.NETHER_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.NETHER_BRICK_FENCE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.NETHER_BRICK_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.NETHER_BRICK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.NETHER_BRICK_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.CRACKED_NETHER_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.RED_NETHER_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.RED_NETHER_BRICK_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.RED_NETHER_BRICK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.RED_NETHER_BRICK_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BASALT, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.SMOOTH_BASALT, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.POLISHED_BASALT, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.END_STONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(900)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.END_STONE_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.END_STONE_BRICK_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.END_STONE_BRICK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.END_STONE_BRICK_WALL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PURPUR_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PURPUR_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PURPUR_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PURPUR_PILLAR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //<editor-fold desc="Metal/Gem/Dust Blocks and Manufactured"
        register(Material.COAL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        //<editor-fold desc="No oxidation Copper">
        register(Material.COPPER_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.CHISELED_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.COPPER_GRATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.CUT_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.CUT_COPPER_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.CUT_COPPER_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.COPPER_DOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.COPPER_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.COPPER_BULB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.COPPER_CHAIN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.COPPER_CHEST, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.COPPER_GOLEM_STATUE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.COPPER_LANTERN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.COPPER_BARS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //<editor-fold desc="Exposed Copper">
        register(Material.EXPOSED_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.EXPOSED_CHISELED_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.EXPOSED_COPPER_GRATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.EXPOSED_CUT_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.EXPOSED_CUT_COPPER_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.EXPOSED_CUT_COPPER_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.EXPOSED_COPPER_DOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.EXPOSED_COPPER_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.EXPOSED_COPPER_BULB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.EXPOSED_COPPER_CHAIN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.EXPOSED_COPPER_CHEST, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.EXPOSED_COPPER_GOLEM_STATUE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.EXPOSED_COPPER_LANTERN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.EXPOSED_COPPER_BARS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //<editor-fold desc="Weathered Copper">
        register(Material.WEATHERED_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WEATHERED_CHISELED_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WEATHERED_COPPER_GRATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WEATHERED_CUT_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WEATHERED_CUT_COPPER_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WEATHERED_CUT_COPPER_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WEATHERED_COPPER_DOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WEATHERED_COPPER_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WEATHERED_COPPER_BULB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WEATHERED_COPPER_CHAIN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WEATHERED_COPPER_CHEST, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WEATHERED_COPPER_GOLEM_STATUE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WEATHERED_COPPER_LANTERN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WEATHERED_COPPER_BARS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //<editor-fold desc="Oxidized Copper">
        register(Material.OXIDIZED_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.OXIDIZED_CHISELED_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.OXIDIZED_COPPER_GRATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.OXIDIZED_CUT_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.OXIDIZED_CUT_COPPER_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.OXIDIZED_CUT_COPPER_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.OXIDIZED_COPPER_DOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.OXIDIZED_COPPER_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.OXIDIZED_COPPER_BULB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.OXIDIZED_COPPER_CHAIN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.OXIDIZED_COPPER_CHEST, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.OXIDIZED_COPPER_GOLEM_STATUE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.OXIDIZED_COPPER_LANTERN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.OXIDIZED_COPPER_BARS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());
        //</editor-fold>

        //<editor-fold desc="Waxed Copper">
        register(Material.WAXED_COPPER_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_CHISELED_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_COPPER_GRATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_CUT_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_CUT_COPPER_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_CUT_COPPER_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_COPPER_DOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_COPPER_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_COPPER_BULB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_COPPER_CHAIN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_COPPER_CHEST, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_COPPER_GOLEM_STATUE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_COPPER_LANTERN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_COPPER_BARS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //<editor-fold desc="Waxed Exposed Copper">
        register(Material.WAXED_EXPOSED_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_EXPOSED_CHISELED_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_EXPOSED_COPPER_GRATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_EXPOSED_CUT_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_EXPOSED_CUT_COPPER_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_EXPOSED_CUT_COPPER_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_EXPOSED_COPPER_DOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_EXPOSED_COPPER_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_EXPOSED_COPPER_BULB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_EXPOSED_COPPER_CHAIN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_EXPOSED_COPPER_CHEST, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_EXPOSED_COPPER_GOLEM_STATUE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_EXPOSED_COPPER_LANTERN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_EXPOSED_COPPER_BARS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //<editor-fold desc="Waxed Weathered Copper">
        register(Material.WAXED_WEATHERED_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_WEATHERED_CHISELED_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_WEATHERED_COPPER_GRATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_WEATHERED_CUT_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_WEATHERED_CUT_COPPER_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_WEATHERED_CUT_COPPER_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_WEATHERED_COPPER_DOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_WEATHERED_COPPER_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_WEATHERED_COPPER_BULB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_WEATHERED_COPPER_CHAIN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_WEATHERED_COPPER_CHEST, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_WEATHERED_COPPER_GOLEM_STATUE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_WEATHERED_COPPER_LANTERN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_WEATHERED_COPPER_BARS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //<editor-fold desc="Waxed Oxidized Copper">
        register(Material.WAXED_OXIDIZED_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_OXIDIZED_CHISELED_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_OXIDIZED_COPPER_GRATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_OXIDIZED_CUT_COPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_OXIDIZED_CUT_COPPER_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_OXIDIZED_CUT_COPPER_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_OXIDIZED_COPPER_DOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_OXIDIZED_COPPER_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_OXIDIZED_COPPER_BULB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_OXIDIZED_COPPER_CHAIN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_OXIDIZED_COPPER_CHEST, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_OXIDIZED_COPPER_GOLEM_STATUE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_OXIDIZED_COPPER_LANTERN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WAXED_OXIDIZED_COPPER_BARS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());
        //</editor-fold>

        //<editor-fold desc="Redstone Components">
        register(Material.REDSTONE, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.REDSTONE_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.REPEATER, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.COMPARATOR, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LEVER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.TRIPWIRE, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.TRIPWIRE_HOOK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DAYLIGHT_DETECTOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PISTON, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PISTON_HEAD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.STICKY_PISTON, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.MOVING_PISTON, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DISPENSER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DROPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.CRAFTER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.HOPPER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.OBSERVER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.RAIL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(70)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ACTIVATOR_RAIL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(70)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DETECTOR_RAIL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(70)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POWERED_RAIL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(70)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>

        register(Material.RAW_COPPER_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DIAMOND_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(4)
                .softRequirement(false)
                .build());

        register(Material.EMERALD_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(4)
                .softRequirement(false)
                .build());

        register(Material.IRON_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.RAW_IRON_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.IRON_BARS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.IRON_DOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.IRON_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.IRON_CHAIN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.GOLD_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.RAW_GOLD_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LAPIS_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.GLOWSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.NETHERITE_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(5000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.RESIN_BLOCK, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(false)
                .build());

        register(Material.QUARTZ_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.QUARTZ_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.QUARTZ_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.CHISELED_QUARTZ_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.QUARTZ_BRICKS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.QUARTZ_PILLAR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.SMOOTH_QUARTZ, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.SMOOTH_QUARTZ_SLAB, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.SMOOTH_QUARTZ_STAIRS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(80)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.LANTERN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SOUL_LANTERN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.REDSTONE_LAMP, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.STONECUTTER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.GRINDSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.FURNACE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.SMOKER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BLAST_FURNACE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.ANVIL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.CHIPPED_ANVIL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DAMAGED_ANVIL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.ENCHANTING_TABLE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(5000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.BREWING_STAND, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CAULDRON, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BELL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BEACON, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(5000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.CONDUIT, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.LODESTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(350)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.LIGHTNING_ROD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(300)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.DECORATED_POT, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ENDER_CHEST, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(5000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.RESPAWN_ANCHOR, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(5000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.SKELETON_SKULL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.SKELETON_WALL_SKULL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.WITHER_SKELETON_SKULL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.WITHER_SKELETON_WALL_SKULL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.PLAYER_HEAD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.PLAYER_WALL_HEAD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.ZOMBIE_HEAD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.ZOMBIE_WALL_HEAD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.CREEPER_HEAD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.CREEPER_WALL_HEAD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.PIGLIN_HEAD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.PIGLIN_WALL_HEAD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.DRAGON_HEAD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.DRAGON_WALL_HEAD, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(100)
                .breakingPower(1)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Concrete Blocks">
        register(Material.WHITE_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.LIGHT_GRAY_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.GRAY_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BLACK_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BROWN_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.RED_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.ORANGE_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.YELLOW_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.LIME_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.GREEN_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.CYAN_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.LIGHT_BLUE_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BLUE_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PURPLE_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.MAGENTA_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PINK_CONCRETE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(180)
                .breakingPower(1)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //<editor-fold desc="Terracotta Blocks">
        register(Material.TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WHITE_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.LIGHT_GRAY_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.GRAY_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BLACK_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BROWN_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.RED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.ORANGE_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.YELLOW_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.LIME_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.GREEN_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.CYAN_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.LIGHT_BLUE_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BLUE_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PURPLE_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.MAGENTA_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PINK_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(125)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.WHITE_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.GRAY_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BLACK_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BROWN_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.RED_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.ORANGE_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.YELLOW_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.LIME_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.GREEN_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.CYAN_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BLUE_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PURPLE_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.MAGENTA_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.PINK_GLAZED_TERRACOTTA, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(140)
                .breakingPower(1)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //<editor-fold desc="Glass Blocks">
        register(Material.GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WHITE_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIGHT_GRAY_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.GRAY_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.BLACK_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.BROWN_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.RED_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.ORANGE_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.YELLOW_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.LIME_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.GREEN_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.CYAN_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.LIGHT_BLUE_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.BLUE_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.PURPLE_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.MAGENTA_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.PINK_STAINED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WHITE_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIGHT_GRAY_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.GRAY_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.BLACK_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.BROWN_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.RED_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.ORANGE_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.YELLOW_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.LIME_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.GREEN_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.CYAN_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.LIGHT_BLUE_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.BLUE_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.PURPLE_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.MAGENTA_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.PINK_STAINED_GLASS_PANE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.TINTED_GLASS, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(30)
                .breakingPower(1)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Shulker Boxes">
        register(Material.SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WHITE_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIGHT_GRAY_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GRAY_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BLACK_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BROWN_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.RED_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ORANGE_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.YELLOW_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIME_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GREEN_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CYAN_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIGHT_BLUE_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BLUE_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PURPLE_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.MAGENTA_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PINK_SHULKER_BOX, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Coral">
        register(Material.TUBE_CORAL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BRAIN_CORAL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.BUBBLE_CORAL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.FIRE_CORAL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.HORN_CORAL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.TUBE_CORAL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.BRAIN_CORAL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.BUBBLE_CORAL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.FIRE_CORAL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.HORN_CORAL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.TUBE_CORAL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BRAIN_CORAL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BUBBLE_CORAL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.FIRE_CORAL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.HORN_CORAL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.TUBE_CORAL_WALL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BRAIN_CORAL_WALL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BUBBLE_CORAL_WALL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.FIRE_CORAL_WALL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.HORN_CORAL_WALL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DEAD_TUBE_CORAL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DEAD_BRAIN_CORAL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DEAD_BUBBLE_CORAL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DEAD_FIRE_CORAL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DEAD_HORN_CORAL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.DEAD_TUBE_CORAL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.DEAD_BRAIN_CORAL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.DEAD_BUBBLE_CORAL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.DEAD_FIRE_CORAL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.DEAD_HORN_CORAL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(1)
                .softRequirement(true)
                .build());

        register(Material.DEAD_TUBE_CORAL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DEAD_BRAIN_CORAL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DEAD_BUBBLE_CORAL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DEAD_FIRE_CORAL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DEAD_HORN_CORAL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DEAD_TUBE_CORAL_WALL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DEAD_BRAIN_CORAL_WALL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DEAD_BUBBLE_CORAL_WALL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DEAD_FIRE_CORAL_WALL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DEAD_HORN_CORAL_WALL_FAN, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //</editor-fold>

        //<editor-fold desc="Axe Blocks">
        //<editor-fold desc="Banners">
        register(Material.WHITE_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.LIGHT_GRAY_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.GRAY_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BLACK_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BROWN_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.RED_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ORANGE_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.YELLOW_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.LIME_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.GREEN_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.CYAN_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.LIGHT_BLUE_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BLUE_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PURPLE_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MAGENTA_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PINK_BANNER, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());
        //</editor-fold>
        //<editor-fold desc="Beds">
        register(Material.WHITE_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.LIGHT_GRAY_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.GRAY_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BLACK_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BROWN_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.RED_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ORANGE_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.YELLOW_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.LIME_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.GREEN_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.CYAN_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.LIGHT_BLUE_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BLUE_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PURPLE_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MAGENTA_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PINK_BED, BlockPropertiesEntry.builder(ItemClassification.AXE)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());
        //</editor-fold>
        //<editor-fold desc="Logs/Wood">
        //<editor-fold desc="Oak">
        register(Material.OAK_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_OAK_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_OAK_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_PLANKS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_SLAB, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_DOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_FENCE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_FENCE_GATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_BUTTON, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_WALL_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_SHELF, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.OAK_LEAVES, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.SHEARS)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Spruce">
        register(Material.SPRUCE_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_SPRUCE_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_SPRUCE_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_PLANKS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_SLAB, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_DOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_FENCE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_FENCE_GATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_BUTTON, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_WALL_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_SHELF, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SPRUCE_LEAVES, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.SHEARS)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Birch">
        register(Material.BIRCH_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_BIRCH_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_BIRCH_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_PLANKS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_SLAB, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_STAIRS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_DOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_FENCE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_FENCE_GATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(62)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_BUTTON, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(62)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(62)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(62)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_WALL_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(62)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_SHELF, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BIRCH_LEAVES, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.SHEARS)
                .hardness(25)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Jungle">
        register(Material.JUNGLE_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_JUNGLE_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_JUNGLE_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_PLANKS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_SLAB, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_DOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_FENCE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_FENCE_GATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(62)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_BUTTON, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(62)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(62)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(62)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_WALL_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(62)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_SHELF, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(1)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUNGLE_LEAVES, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.SHEARS)
                .hardness(25)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Acacia">
        register(Material.ACACIA_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_ACACIA_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_ACACIA_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_PLANKS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_SLAB, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_STAIRS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_DOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_FENCE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_FENCE_GATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_BUTTON, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_WALL_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_SHELF, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.ACACIA_LEAVES, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.SHEARS)
                .hardness(35)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Dark Oak">
        register(Material.DARK_OAK_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_DARK_OAK_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_DARK_OAK_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_PLANKS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_SLAB, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_DOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_FENCE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_FENCE_GATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_BUTTON, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_WALL_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_SHELF, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.DARK_OAK_LEAVES, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.SHEARS)
                .hardness(35)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Bamboo">
        register(Material.BAMBOO, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_BLOCK, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.STRIPPED_BAMBOO_BLOCK, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_PLANKS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_MOSAIC, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_SLAB, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_MOSAIC_SLAB, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_STAIRS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_MOSAIC_STAIRS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_FENCE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_FENCE_GATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_DOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_BUTTON, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BAMBOO_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BAMBOO_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_WALL_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(87)
                .breakingPower(2)
                .softRequirement(false)
                .build());

        register(Material.BAMBOO_SHELF, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(350)
                .breakingPower(2)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //<editor-fold desc="Cherry">
        register(Material.CHERRY_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHERRY_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.STRIPPED_CHERRY_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.STRIPPED_CHERRY_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHERRY_PLANKS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHERRY_SLAB, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHERRY_STAIRS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHERRY_DOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHERRY_FENCE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHERRY_FENCE_GATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHERRY_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHERRY_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(125)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CHERRY_BUTTON, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(125)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CHERRY_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(125)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHERRY_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(125)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHERRY_WALL_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(125)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHERRY_SHELF, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .build());

        register(Material.CHERRY_LEAVES, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.SHEARS)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Mangrove">
        register(Material.MANGROVE_ROOTS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(175)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MUDDY_MANGROVE_ROOTS, BlockPropertiesEntry.builder(ItemClassification.SHOVEL, ItemClassification.DRILL, ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(175)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_MANGROVE_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_MANGROVE_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_PLANKS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_SLAB, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_STAIRS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_DOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_FENCE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_FENCE_GATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(125)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_BUTTON, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(125)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(125)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(125)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_WALL_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(125)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_SHELF, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(125)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MANGROVE_LEAVES, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.SHEARS)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Pale Oak">
        register(Material.PALE_OAK_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(750)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(750)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_PALE_OAK_LOG, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(750)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.STRIPPED_PALE_OAK_WOOD, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(750)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_PLANKS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(750)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_SLAB, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(750)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_STAIRS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(750)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_DOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(750)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_FENCE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(750)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_FENCE_GATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(750)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(750)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(187)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_BUTTON, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(187)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(187)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(187)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_WALL_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(187)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_SHELF, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(187)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.CREAKING_HEART, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(2000)
                .breakingPower(4)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.PALE_OAK_LEAVES, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.SHEARS)
                .hardness(75)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Crimson">
        register(Material.CRIMSON_STEM, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.CRIMSON_HYPHAE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.STRIPPED_CRIMSON_STEM, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.STRIPPED_CRIMSON_HYPHAE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.CRIMSON_PLANKS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.CRIMSON_SLAB, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.CRIMSON_STAIRS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.CRIMSON_DOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.CRIMSON_FENCE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.CRIMSON_FENCE_GATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.CRIMSON_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.CRIMSON_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CRIMSON_BUTTON, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CRIMSON_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.CRIMSON_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.CRIMSON_WALL_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.CRIMSON_SHELF, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.NETHER_WART_BLOCK, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(100)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.SHROOMLIGHT, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(100)
                .breakingPower(5)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //<editor-fold desc="Warped">
        register(Material.WARPED_STEM, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.WARPED_HYPHAE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.STRIPPED_WARPED_STEM, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.STRIPPED_WARPED_HYPHAE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.WARPED_PLANKS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.WARPED_SLAB, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.WARPED_STAIRS, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.WARPED_DOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.WARPED_FENCE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.WARPED_FENCE_GATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.WARPED_TRAPDOOR, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.WARPED_PRESSURE_PLATE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WARPED_BUTTON, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WARPED_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.WARPED_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.WARPED_WALL_HANGING_SIGN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.WARPED_SHELF, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1000)
                .breakingPower(5)
                .softRequirement(false)
                .build());

        register(Material.WARPED_WART_BLOCK, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(100)
                .breakingPower(5)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //<editor-fold desc="Large Mushrooms">
        register(Material.BROWN_MUSHROOM_BLOCK, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.RED_MUSHROOM_BLOCK, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MUSHROOM_STEM, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(3)
                .softRequirement(false)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());
        //</editor-fold>
        //<editor-fold desc="Azalea">
        register(Material.AZALEA_LEAVES, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.SHEARS)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        register(Material.FLOWERING_AZALEA_LEAVES, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.SHEARS)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //</editor-fold>
        //<editor-fold desc="Functional">
        register(Material.CRAFTING_TABLE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.CARTOGRAPHY_TABLE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.FLETCHING_TABLE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SMITHING_TABLE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(250)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.LOOM, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.CAMPFIRE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SOUL_CAMPFIRE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.COMPOSTER, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(60)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JUKEBOX, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(200)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.LADDER, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(40)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BOOKSHELF, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(150)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.CHISELED_BOOKSHELF, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.LECTERN, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.CHEST, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.TRAPPED_CHEST, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BARREL, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(250)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());
        //</editor-fold>
        register(Material.PUMPKIN, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.CARVED_PUMPKIN, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.JACK_O_LANTERN, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.MELON, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(100)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.COCOA_BEANS, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BEE_NEST, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(30)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.BEEHIVE, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(60)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.CHORUS_PLANT, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1600)
                .breakingPower(6)
                .softRequirement(false)
                .build());

        register(Material.CHORUS_FLOWER, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(1600)
                .breakingPower(6)
                .softRequirement(false)
                .build());
        //</editor-fold>

        //<editor-fold desc="Hoe Blocks">
        //<editor-fold desc="Moss Type Blocks">
        register(Material.MOSS_BLOCK, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.MOSS_CARPET, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PALE_MOSS_BLOCK, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PALE_MOSS_CARPET, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PALE_HANGING_MOSS, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GLOW_LICHEN, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.VINE, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.TWISTING_VINES, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.TWISTING_VINES_PLANT, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WEEPING_VINES, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WEEPING_VINES_PLANT, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Sculk Blocks">
        register(Material.SCULK, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(20)
                .breakingPower(5)
                .softRequirement(true)
                .build());

        register(Material.SCULK_VEIN, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(20)
                .breakingPower(5)
                .softRequirement(true)
                .build());

        register(Material.SCULK_CATALYST, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(6)
                .softRequirement(false)
                .build());

        register(Material.SCULK_SENSOR, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(6)
                .softRequirement(false)
                .build());

        register(Material.CALIBRATED_SCULK_SENSOR, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(500)
                .breakingPower(6)
                .softRequirement(false)
                .build());

        register(Material.SCULK_SHRIEKER, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(1500)
                .breakingPower(7)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //<editor-fold desc="Sponges">
        register(Material.SPONGE, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(60)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WET_SPONGE, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(60)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Crops">
        register(Material.WHEAT, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.HAY_BLOCK, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CARROTS, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTATOES, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BEETROOTS, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SUGAR_CANE, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SWEET_BERRIES, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GLOW_BERRIES, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CACTUS, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(40)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.MELON_STEM, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ATTACHED_MELON_STEM, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PUMPKIN_STEM, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ATTACHED_PUMPKIN_STEM, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.NETHER_WART, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.COCOA, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(20)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        register(Material.TARGET, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DRIED_KELP_BLOCK, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>

        //<editor-fold desc="Other/No Tool Blocks">
        //<editor-fold desc="Wool Blocks">
        register(Material.COBWEB, BlockPropertiesEntry.builder(ItemClassification.SHEARS, ItemClassification.SWORD)
                .hardness(800)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WHITE_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIGHT_GRAY_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GRAY_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BLACK_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BROWN_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.RED_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ORANGE_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.YELLOW_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIME_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GREEN_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CYAN_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIGHT_BLUE_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BLUE_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PURPLE_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.MAGENTA_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PINK_WOOL, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WHITE_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIGHT_GRAY_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GRAY_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BLACK_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BROWN_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.RED_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ORANGE_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.YELLOW_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIME_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GREEN_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CYAN_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIGHT_BLUE_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BLUE_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PURPLE_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.MAGENTA_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PINK_CARPET, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Candles">
        register(Material.CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WHITE_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIGHT_GRAY_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GRAY_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BLACK_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BROWN_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.RED_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ORANGE_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.YELLOW_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIME_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GREEN_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CYAN_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LIGHT_BLUE_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BLUE_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PURPLE_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.MAGENTA_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PINK_CANDLE, BlockPropertiesEntry.builder()
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Froglights">
        register(Material.OCHRE_FROGLIGHT, BlockPropertiesEntry.builder()
                .hardness(30)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.VERDANT_FROGLIGHT, BlockPropertiesEntry.builder()
                .hardness(30)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PEARLESCENT_FROGLIGHT, BlockPropertiesEntry.builder()
                .hardness(30)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Honey related">
        register(Material.HONEYCOMB_BLOCK, BlockPropertiesEntry.builder()
                .hardness(60)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.HONEY_BLOCK, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Grasses/Plants">
        //<editor-fold desc="Saplings">
        register(Material.OAK_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BIRCH_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SPRUCE_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.JUNGLE_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ACACIA_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DARK_OAK_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CHERRY_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.MANGROVE_PROPAGULE, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PALE_OAK_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CRIMSON_FUNGUS, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WARPED_FUNGUS, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.AZALEA, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.FLOWERING_AZALEA, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Flowers">
        //<editor-fold desc="Potted">
        register(Material.FLOWER_POT, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        register(Material.POTTED_ACACIA_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_ALLIUM, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_AZALEA_BUSH, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_BAMBOO, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_AZURE_BLUET, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_BIRCH_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_BLUE_ORCHID, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_BROWN_MUSHROOM, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_CACTUS, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_CHERRY_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_CLOSED_EYEBLOSSOM, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_CORNFLOWER, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_CRIMSON_FUNGUS, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_CRIMSON_ROOTS, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_DANDELION, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_GOLDEN_DANDELION, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_DARK_OAK_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_DEAD_BUSH, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_FERN, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_JUNGLE_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_LILY_OF_THE_VALLEY, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_MANGROVE_PROPAGULE, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_OAK_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_OPEN_EYEBLOSSOM, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_ORANGE_TULIP, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_OXEYE_DAISY, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_PALE_OAK_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_POPPY, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_PINK_TULIP, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_RED_MUSHROOM, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_RED_TULIP, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_SPRUCE_SAPLING, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_TORCHFLOWER, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_WARPED_FUNGUS, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_WARPED_ROOTS, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_WHITE_TULIP, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POTTED_WITHER_ROSE, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE, ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        register(Material.BROWN_MUSHROOM, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.RED_MUSHROOM, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DANDELION, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.GOLDEN_DANDELION, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.POPPY, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BLUE_ORCHID, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ALLIUM, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.AZURE_BLUET, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.RED_TULIP, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ORANGE_TULIP, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WHITE_TULIP, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PINK_TULIP, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.OXEYE_DAISY, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CORNFLOWER, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LILY_OF_THE_VALLEY, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.TORCHFLOWER, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CACTUS_FLOWER, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.CLOSED_EYEBLOSSOM, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.OPEN_EYEBLOSSOM, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WITHER_ROSE, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PINK_PETALS, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.WILDFLOWERS, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LEAF_LITTER, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SPORE_BLOSSOM, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SUNFLOWER, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LILAC, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.ROSE_BUSH, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PEONY, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PITCHER_PLANT, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.PITCHER_POD, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SWEET_BERRY_BUSH, BlockPropertiesEntry.builder(ItemClassification.HATCHET, ItemClassification.HOE)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        register(Material.KELP_PLANT, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.KELP, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SEA_PICKLE, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SEAGRASS, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.TALL_SEAGRASS, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LILY_PAD, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.HANGING_ROOTS, BlockPropertiesEntry.builder(ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SHORT_GRASS, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SHORT_DRY_GRASS, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.TALL_GRASS, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.TALL_DRY_GRASS, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.FERN, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.LARGE_FERN, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BUSH, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DEAD_BUSH, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.FIREFLY_BUSH, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BIG_DRIPLEAF, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.BIG_DRIPLEAF_STEM, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SMALL_DRIPLEAF, BlockPropertiesEntry.builder(ItemClassification.HOE, ItemClassification.HATCHET, ItemClassification.SHEARS)
                .hardness(10)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Functional">
        register(Material.SLIME_BLOCK, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.TURTLE_EGG, BlockPropertiesEntry.builder()
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.DRIED_GHAST, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SNIFFER_EGG, BlockPropertiesEntry.builder()
                .hardness(50)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.TORCH, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.WALL_TORCH, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SOUL_TORCH, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.SOUL_WALL_TORCH, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.COPPER_TORCH, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.COPPER_WALL_TORCH, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .blockSound(BlockSound.WOOD_GENERIC)
                .build());

        register(Material.REDSTONE_TORCH, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.REDSTONE_WALL_TORCH, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.END_ROD, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.SCAFFOLDING, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.TNT, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
        //<editor-fold desc="Special generated (Portals, spawners)">
        register(Material.AIR, BlockPropertiesEntry.builder()
                .hardness(-1)
                .breakingPower(-1)
                .softRequirement(false)
                .build());

        register(Material.FIRE, BlockPropertiesEntry.builder()
                .hardness(0)
                .breakingPower(-1)
                .softRequirement(true)
                .build());

        register(Material.SPAWNER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(500)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.TRIAL_SPAWNER, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(5000)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.NETHER_PORTAL, BlockPropertiesEntry.builder()
                .hardness(-1)
                .breakingPower(-1)
                .softRequirement(false)
                .build());

        register(Material.DRAGON_EGG, BlockPropertiesEntry.builder()
                .hardness(300)
                .breakingPower(0)
                .softRequirement(true)
                .build());

        register(Material.VAULT, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
                .hardness(5000)
                .breakingPower(1)
                .softRequirement(false)
                .build());

        register(Material.END_PORTAL, BlockPropertiesEntry.builder()
                .hardness(-1)
                .breakingPower(-1)
                .softRequirement(false)
                .build());

        register(Material.END_PORTAL_FRAME, BlockPropertiesEntry.builder()
                .hardness(-1)
                .breakingPower(-1)
                .softRequirement(false)
                .build());

        register(Material.END_GATEWAY, BlockPropertiesEntry.builder()
                .hardness(-1)
                .breakingPower(-1)
                .softRequirement(false)
                .build());
        //</editor-fold>
        //</editor-fold>

        //<editor-fold desc="Noteblocks">
        register(Material.NOTE_BLOCK, BlockPropertiesEntry.builder(ItemClassification.AXE, ItemClassification.HATCHET)
                .hardness(80)
                .breakingPower(0)
                .softRequirement(true)
                .build());
        //</editor-fold>
    }

    // Custom Block Registration
    private static void registerCraftEngineMaterials() {

        for (var blockEntry : CraftEngineBlocks.loadedBlocks().entrySet()) {
            var customBlock = blockEntry.getValue();

            var blockSettings = customBlock.defaultState().settings();
            int hardness = (int) (blockSettings.hardness() * 100);
            int breakingPower = 0;
            for (var i=0; i<=9; i++) {
                if (blockSettings.tags().contains(Key.of("smprpg", String.format("breaking_power_%d", i)))) {
                    breakingPower = i;
                    break;
                }
            }
            boolean requiresTool = blockSettings.requireCorrectTool();

            HashSet<ItemClassification> tools = new HashSet<>();
            var key = blockSettings.tags().contains(Key.of("minecraft", "mineable/pickaxe"));
            if (key) {
                tools.add(ItemClassification.PICKAXE);
                tools.add(ItemClassification.DRILL);
            }

            key = blockSettings.tags().contains(Key.of("minecraft", "mineable/hoe"));
            if (key) {
                tools.add(ItemClassification.HOE);
                tools.add(ItemClassification.HATCHET);
            }

            key = blockSettings.tags().contains(Key.of("minecraft", "mineable/axe"));
            if (key) {
                tools.add(ItemClassification.AXE);
                tools.add(ItemClassification.HATCHET);
            }

            key = blockSettings.tags().contains(Key.of("minecraft", "mineable/shovel"));
            if (key) {
                tools.add(ItemClassification.SHOVEL);
                tools.add(ItemClassification.DRILL);
            }

            key = blockSettings.tags().contains(Key.of("smprpg", "mineable/shears"));
            if (key) {
                tools.add(ItemClassification.SHEARS);
            }

            register(customBlock.id(), BlockPropertiesEntry.builder(tools.toArray(new ItemClassification[]{}))
                    .hardness(hardness)
                    .breakingPower(breakingPower)
                    .softRequirement(!requiresTool)
                    .build());
        }

        //register(CustomBlock.REFORGE_TABLE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.IRON)
        //        .softRequirement(false)
        //        .build());
//
        ////<editor-fold desc="Ores & Material Blocks">
        //register(CustomBlock.SILVER_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(300)
        //        .breakingPower(2)
        //        .blockSound(BlockSound.STONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.DEEPSLATE_SILVER_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(450)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.DEEPSLATE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RAW_SILVER_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(2)
        //        .blockSound(BlockSound.STONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.SILVER_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(2)
        //        .blockSound(BlockSound.METAL_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.TIN_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(300)
        //        .breakingPower(1)
        //        .blockSound(BlockSound.STONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.DEEPSLATE_TIN_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(450)
        //        .breakingPower(3)
        //        .blockSound(BlockSound.DEEPSLATE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RAW_TIN_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(2)
        //        .blockSound(BlockSound.STONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.TIN_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(2)
        //        .blockSound(BlockSound.METAL_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.BRONZE_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(3)
        //        .blockSound(BlockSound.COPPER)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.ROSE_GOLD_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(3)
        //        .blockSound(BlockSound.METAL_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.STEEL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.IRON)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.SPARSE_MITHRIL_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(625)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.MITHRIL_ORE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.MITHRIL_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(1250)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.MITHRIL_ORE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.DENSE_MITHRIL_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(2500)
        //        .breakingPower(5)
        //        .blockSound(BlockSound.MITHRIL_ORE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RAW_MITHRIL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(800)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.MITHRIL_ORE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.MITHRIL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(800)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.METAL_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.TITANIUM_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(7500)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.MITHRIL_ORE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RAW_TITANIUM_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(2500)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.MITHRIL_ORE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.TITANIUM_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(2500)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.TITANIUM_METAL)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.ADAMANTIUM_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(15000)
        //        .breakingPower(5)
        //        .blockSound(BlockSound.ADAMANTIUM_ORE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RAW_ADAMANTIUM_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(5000)
        //        .breakingPower(5)
        //        .blockSound(BlockSound.ADAMANTIUM_ORE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.ADAMANTIUM_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(5000)
        //        .breakingPower(5)
        //        .blockSound(BlockSound.TITANIUM_METAL)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.SULFUR_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(600)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.BLACKSTONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.SULFUR_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(600)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.STONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RAW_TUNGSTEN_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(750)
        //        .breakingPower(5)
        //        .blockSound(BlockSound.BLACKSTONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.TUNGSTEN_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(750)
        //        .breakingPower(5)
        //        .blockSound(BlockSound.METAL_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.TUNGSTEN_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(750)
        //        .breakingPower(5)
        //        .blockSound(BlockSound.BLACKSTONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RAW_COBALT_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(900)
        //        .breakingPower(6)
        //        .blockSound(BlockSound.BLACKSTONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.COBALT_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(900)
        //        .breakingPower(6)
        //        .blockSound(BlockSound.METAL_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.COBALT_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(900)
        //        .breakingPower(6)
        //        .blockSound(BlockSound.BLACKSTONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RAW_ORICHALCUM_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(900)
        //        .breakingPower(6)
        //        .blockSound(BlockSound.BLACKSTONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.ORICHALCUM_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(900)
        //        .breakingPower(6)
        //        .blockSound(BlockSound.METAL_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.ORICHALCUM_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(900)
        //        .breakingPower(6)
        //        .blockSound(BlockSound.BLACKSTONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.DRAGONSTEEL_BLOCK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(6)
        //        .blockSound(BlockSound.IRON)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.GRIMSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(600)
        //        .breakingPower(5)
        //        .blockSound(BlockSound.BLACKSTONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.COBBLED_GRIMSTONE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(650)
        //        .breakingPower(5)
        //        .blockSound(BlockSound.BLACKSTONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.GRIMSTONE_DIAMOND_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(750)
        //        .breakingPower(5)
        //        .blockSound(BlockSound.BLACKSTONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.GRIMSTONE_IRON_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(750)
        //        .breakingPower(5)
        //        .blockSound(BlockSound.BLACKSTONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.GRIMSTONE_GOLD_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(750)
        //        .breakingPower(5)
        //        .blockSound(BlockSound.BLACKSTONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.GRIMSTONE_SILVER_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(750)
        //        .breakingPower(5)
        //        .blockSound(BlockSound.BLACKSTONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.GRIMSTONE_LAPIS_ORE, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(750)
        //        .breakingPower(5)
        //        .blockSound(BlockSound.BLACKSTONE_GENERIC)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RUNE_BLANK, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.RUNE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RUNE_POTENTIAL, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.RUNE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RUNE_AMBITION, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.RUNE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RUNE_MEMORIZATION, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.RUNE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RUNE_GREED, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.RUNE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RUNE_INSIGHT, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.RUNE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RUNE_FORTUITY, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.RUNE)
        //        .softRequirement(false)
        //        .build());
//
        //register(CustomBlock.RUNE_DIVINITY, BlockPropertiesEntry.builder(ItemClassification.PICKAXE, ItemClassification.DRILL)
        //        .hardness(500)
        //        .breakingPower(4)
        //        .blockSound(BlockSound.RUNE)
        //        .softRequirement(false)
        //        .build());
        //</editor-fold>

    }

    public static void register(Material material, BlockPropertiesEntry entry) {
        entries.put(material, entry);
    }

    public static void register(Key block, BlockPropertiesEntry entry) { specialEntries.put(block, entry); }

    //public static void register(CustomBlock material, BlockPropertiesEntry entry) { specialEntries.put(material, entry); }

    public static @Nullable BlockPropertiesEntry get(Block block) {
        BlockPropertiesEntry entry;
        if (CraftEngineBlocks.isCustomBlock(block)) {
            var resourceKey = CraftEngineHelpers.Companion.getBlockKey(block);
            entry = specialEntries.getOrDefault(resourceKey, null);
        }
        else
            entry = entries.get(block.getType());
        return entry;
    }

    @EventHandler
    public void __onCraftEngineReload(CraftEngineReloadEvent event) {
        registerVanillaMaterials();
        registerCraftEngineMaterials();
    }
}
