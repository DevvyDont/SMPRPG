package xyz.devvydont.smprpg.items.blueprints.resources;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.base.VanillaItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a common crafting ingredient/material that is meant to be a simple vanilla material that can be sold.
 * Can include things such as diamonds, wheat, seeds, and dirt.
 *
 * todo make this into a file.
 */
public class VanillaResource extends VanillaItemBlueprint implements ISellable {

    private static final Map<Material, Integer> materialWorthMap;

    // Instantiate worth of items here. This is how the item service knows to register certain materials to this
    // class.
    static {
        materialWorthMap = new HashMap<>();

        // Materials from general world generation.
        materialWorthMap.put(Material.STONE, 1);
        materialWorthMap.put(Material.STONE_BRICKS, 1);
        materialWorthMap.put(Material.COBBLESTONE, 1);
        materialWorthMap.put(Material.BLACKSTONE, 1);
        materialWorthMap.put(Material.GRANITE, 1);
        materialWorthMap.put(Material.DIORITE, 1);
        materialWorthMap.put(Material.ANDESITE, 1);
        materialWorthMap.put(Material.TUFF, 1);
        materialWorthMap.put(Material.BRICKS, 1);
        materialWorthMap.put(Material.DRIPSTONE_BLOCK, 1);
        materialWorthMap.put(Material.DEEPSLATE, 1);
        materialWorthMap.put(Material.COBBLED_DEEPSLATE, 1);
        materialWorthMap.put(Material.SAND, 1);
        materialWorthMap.put(Material.SANDSTONE, 1);
        materialWorthMap.put(Material.RED_SAND, 1);
        materialWorthMap.put(Material.RED_SANDSTONE, 1);
        materialWorthMap.put(Material.DIRT, 1);
        materialWorthMap.put(Material.GRAVEL, 1);
        materialWorthMap.put(Material.LEAF_LITTER, 1);
        materialWorthMap.put(Material.GLASS, 2);
        materialWorthMap.put(Material.FLINT, 2);
        materialWorthMap.put(Material.OBSIDIAN, 45);
        materialWorthMap.put(Material.CRYING_OBSIDIAN, 60);

        // Decorative blocks.
        materialWorthMap.put(Material.BLACK_WOOL, 5);
        materialWorthMap.put(Material.BLUE_WOOL, 5);
        materialWorthMap.put(Material.CYAN_WOOL, 5);
        materialWorthMap.put(Material.BROWN_WOOL, 5);
        materialWorthMap.put(Material.GREEN_WOOL, 5);
        materialWorthMap.put(Material.LIGHT_BLUE_WOOL, 5);
        materialWorthMap.put(Material.LIGHT_GRAY_WOOL, 5);
        materialWorthMap.put(Material.LIME_WOOL, 5);
        materialWorthMap.put(Material.MAGENTA_WOOL, 5);
        materialWorthMap.put(Material.ORANGE_WOOL, 5);
        materialWorthMap.put(Material.GRAY_WOOL, 5);
        materialWorthMap.put(Material.PINK_WOOL, 5);
        materialWorthMap.put(Material.PURPLE_WOOL, 5);
        materialWorthMap.put(Material.RED_WOOL, 5);
        materialWorthMap.put(Material.YELLOW_WOOL, 5);
        materialWorthMap.put(Material.WHITE_WOOL, 5);

        materialWorthMap.put(Material.BLACK_TERRACOTTA, 5);
        materialWorthMap.put(Material.BLUE_TERRACOTTA, 5);
        materialWorthMap.put(Material.CYAN_TERRACOTTA, 5);
        materialWorthMap.put(Material.BROWN_TERRACOTTA, 5);
        materialWorthMap.put(Material.GREEN_TERRACOTTA, 5);
        materialWorthMap.put(Material.LIGHT_BLUE_TERRACOTTA, 5);
        materialWorthMap.put(Material.LIGHT_GRAY_TERRACOTTA, 5);
        materialWorthMap.put(Material.LIME_TERRACOTTA, 5);
        materialWorthMap.put(Material.MAGENTA_TERRACOTTA, 5);
        materialWorthMap.put(Material.ORANGE_TERRACOTTA, 5);
        materialWorthMap.put(Material.GRAY_TERRACOTTA, 5);
        materialWorthMap.put(Material.PINK_TERRACOTTA, 5);
        materialWorthMap.put(Material.PURPLE_TERRACOTTA, 5);
        materialWorthMap.put(Material.RED_TERRACOTTA, 5);
        materialWorthMap.put(Material.YELLOW_TERRACOTTA, 5);
        materialWorthMap.put(Material.WHITE_TERRACOTTA, 5);

        materialWorthMap.put(Material.BLACK_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.BLUE_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.CYAN_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.BROWN_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.GREEN_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.LIME_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.MAGENTA_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.ORANGE_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.GRAY_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.PINK_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.PURPLE_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.RED_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.YELLOW_GLAZED_TERRACOTTA, 5);
        materialWorthMap.put(Material.WHITE_GLAZED_TERRACOTTA, 5);

        materialWorthMap.put(Material.BLACK_STAINED_GLASS, 5);
        materialWorthMap.put(Material.BLUE_STAINED_GLASS, 5);
        materialWorthMap.put(Material.CYAN_STAINED_GLASS, 5);
        materialWorthMap.put(Material.BROWN_STAINED_GLASS, 5);
        materialWorthMap.put(Material.GREEN_STAINED_GLASS, 5);
        materialWorthMap.put(Material.LIGHT_BLUE_STAINED_GLASS, 5);
        materialWorthMap.put(Material.LIGHT_GRAY_STAINED_GLASS, 5);
        materialWorthMap.put(Material.LIME_STAINED_GLASS, 5);
        materialWorthMap.put(Material.MAGENTA_STAINED_GLASS, 5);
        materialWorthMap.put(Material.ORANGE_STAINED_GLASS, 5);
        materialWorthMap.put(Material.GRAY_STAINED_GLASS, 5);
        materialWorthMap.put(Material.PINK_STAINED_GLASS, 5);
        materialWorthMap.put(Material.PURPLE_STAINED_GLASS, 5);
        materialWorthMap.put(Material.RED_STAINED_GLASS, 5);
        materialWorthMap.put(Material.YELLOW_STAINED_GLASS, 5);
        materialWorthMap.put(Material.WHITE_STAINED_GLASS, 5);

        materialWorthMap.put(Material.BLACK_CARPET, 2);
        materialWorthMap.put(Material.BLUE_CARPET, 2);
        materialWorthMap.put(Material.CYAN_CARPET, 2);
        materialWorthMap.put(Material.BROWN_CARPET, 2);
        materialWorthMap.put(Material.GREEN_CARPET, 2);
        materialWorthMap.put(Material.LIGHT_BLUE_CARPET, 2);
        materialWorthMap.put(Material.LIGHT_GRAY_CARPET, 2);
        materialWorthMap.put(Material.LIME_CARPET, 2);
        materialWorthMap.put(Material.MAGENTA_CARPET, 2);
        materialWorthMap.put(Material.ORANGE_CARPET, 2);
        materialWorthMap.put(Material.GRAY_CARPET, 2);
        materialWorthMap.put(Material.PINK_CARPET, 2);
        materialWorthMap.put(Material.PURPLE_CARPET, 2);
        materialWorthMap.put(Material.RED_CARPET, 2);
        materialWorthMap.put(Material.YELLOW_CARPET, 2);
        materialWorthMap.put(Material.WHITE_CARPET, 2);

        materialWorthMap.put(Material.BLACK_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.BLUE_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.CYAN_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.BROWN_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.GREEN_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.LIME_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.MAGENTA_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.ORANGE_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.GRAY_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.PINK_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.PURPLE_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.RED_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.YELLOW_STAINED_GLASS_PANE, 2);
        materialWorthMap.put(Material.WHITE_STAINED_GLASS_PANE, 2);

        materialWorthMap.put(Material.BLACK_DYE, 2);
        materialWorthMap.put(Material.BLUE_DYE, 2);
        materialWorthMap.put(Material.CYAN_DYE, 2);
        materialWorthMap.put(Material.BROWN_DYE, 2);
        materialWorthMap.put(Material.GREEN_DYE, 2);
        materialWorthMap.put(Material.LIGHT_BLUE_DYE, 2);
        materialWorthMap.put(Material.LIGHT_GRAY_DYE, 2);
        materialWorthMap.put(Material.LIME_DYE, 2);
        materialWorthMap.put(Material.MAGENTA_DYE, 2);
        materialWorthMap.put(Material.ORANGE_DYE, 2);
        materialWorthMap.put(Material.GRAY_DYE, 2);
        materialWorthMap.put(Material.PINK_DYE, 2);
        materialWorthMap.put(Material.PURPLE_DYE, 2);
        materialWorthMap.put(Material.RED_DYE, 2);
        materialWorthMap.put(Material.YELLOW_DYE, 2);
        materialWorthMap.put(Material.WHITE_DYE, 2);

        materialWorthMap.put(Material.BLACK_CONCRETE, 8);
        materialWorthMap.put(Material.BLUE_CONCRETE, 8);
        materialWorthMap.put(Material.CYAN_CONCRETE, 8);
        materialWorthMap.put(Material.BROWN_CONCRETE, 8);
        materialWorthMap.put(Material.GREEN_CONCRETE, 8);
        materialWorthMap.put(Material.LIGHT_BLUE_CONCRETE, 8);
        materialWorthMap.put(Material.LIGHT_GRAY_CONCRETE, 8);
        materialWorthMap.put(Material.LIME_CONCRETE, 8);
        materialWorthMap.put(Material.MAGENTA_CONCRETE, 8);
        materialWorthMap.put(Material.ORANGE_CONCRETE, 8);
        materialWorthMap.put(Material.GRAY_CONCRETE, 8);
        materialWorthMap.put(Material.PINK_CONCRETE, 8);
        materialWorthMap.put(Material.PURPLE_CONCRETE, 8);
        materialWorthMap.put(Material.RED_CONCRETE, 8);
        materialWorthMap.put(Material.YELLOW_CONCRETE, 8);
        materialWorthMap.put(Material.WHITE_CONCRETE, 8);

        materialWorthMap.put(Material.BLACK_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.BLUE_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.CYAN_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.BROWN_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.GREEN_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.LIGHT_BLUE_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.LIGHT_GRAY_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.LIME_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.MAGENTA_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.ORANGE_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.GRAY_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.PINK_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.PURPLE_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.RED_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.YELLOW_CONCRETE_POWDER, 5);
        materialWorthMap.put(Material.WHITE_CONCRETE_POWDER, 5);

        // Materials from trees.
        materialWorthMap.put(Material.BAMBOO, 1);
        materialWorthMap.put(Material.BAMBOO_BLOCK, 2);
        materialWorthMap.put(Material.OAK_LOG, 3);
        materialWorthMap.put(Material.ACACIA_LOG, 3);
        materialWorthMap.put(Material.BIRCH_LOG, 3);
        materialWorthMap.put(Material.SPRUCE_LOG, 4);
        materialWorthMap.put(Material.DARK_OAK_LOG, 4);
        materialWorthMap.put(Material.JUNGLE_LOG, 5);
        materialWorthMap.put(Material.CHERRY_LOG, 5);
        materialWorthMap.put(Material.MANGROVE_LOG, 5);
        materialWorthMap.put(Material.CRIMSON_STEM, 8);
        materialWorthMap.put(Material.WARPED_STEM, 8);

        materialWorthMap.put(Material.BAMBOO_PLANKS, 1);
        materialWorthMap.put(Material.OAK_PLANKS, 1);
        materialWorthMap.put(Material.ACACIA_PLANKS, 1);
        materialWorthMap.put(Material.BIRCH_PLANKS, 1);
        materialWorthMap.put(Material.SPRUCE_PLANKS, 1);
        materialWorthMap.put(Material.DARK_OAK_PLANKS, 1);
        materialWorthMap.put(Material.JUNGLE_PLANKS, 1);
        materialWorthMap.put(Material.CHERRY_PLANKS, 1);
        materialWorthMap.put(Material.MANGROVE_PLANKS, 1);
        materialWorthMap.put(Material.CRIMSON_PLANKS, 1);
        materialWorthMap.put(Material.WARPED_PLANKS, 1);

        materialWorthMap.put(Material.OAK_LEAVES, 3);
        materialWorthMap.put(Material.ACACIA_LEAVES, 3);
        materialWorthMap.put(Material.BIRCH_LEAVES, 3);
        materialWorthMap.put(Material.SPRUCE_LEAVES, 4);
        materialWorthMap.put(Material.DARK_OAK_LEAVES, 4);
        materialWorthMap.put(Material.JUNGLE_LEAVES, 5);
        materialWorthMap.put(Material.CHERRY_LEAVES, 5);
        materialWorthMap.put(Material.MANGROVE_LEAVES, 5);
        materialWorthMap.put(Material.CRIMSON_HYPHAE, 8);
        materialWorthMap.put(Material.WARPED_WART_BLOCK, 8);

        // Materials in the ocean.
        materialWorthMap.put(Material.CLAY_BALL, 1);
        materialWorthMap.put(Material.CLAY, 2);
        materialWorthMap.put(Material.PRISMARINE, 1);
        materialWorthMap.put(Material.PRISMARINE_BRICKS, 1);
        materialWorthMap.put(Material.DARK_PRISMARINE, 1);
        materialWorthMap.put(Material.PRISMARINE_CRYSTALS, 15);

        // Materials from mining. These are used for gear sets so make sure they don't generate infinite money glitch
        materialWorthMap.put(Material.COAL, 10);
        materialWorthMap.put(Material.COAL_ORE, 5);
        materialWorthMap.put(Material.DEEPSLATE_COAL_ORE, 5);
        materialWorthMap.put(Material.COAL_BLOCK, 90);
        materialWorthMap.put(Material.CHARCOAL, 4);

        materialWorthMap.put(Material.COPPER_INGOT, 12);
        materialWorthMap.put(Material.COPPER_BLOCK, 108);
        materialWorthMap.put(Material.RAW_COPPER, 6);
        materialWorthMap.put(Material.COPPER_ORE, 6);
        materialWorthMap.put(Material.DEEPSLATE_COPPER_ORE, 6);

        materialWorthMap.put(Material.IRON_INGOT, 36);
        materialWorthMap.put(Material.IRON_BLOCK, 324);
        materialWorthMap.put(Material.IRON_ORE, 7);
        materialWorthMap.put(Material.DEEPSLATE_IRON_ORE, 7);
        materialWorthMap.put(Material.RAW_IRON, 7);

        materialWorthMap.put(Material.GOLD_INGOT, 70);
        materialWorthMap.put(Material.GOLD_NUGGET, 7);
        materialWorthMap.put(Material.GOLD_BLOCK, 630);
        materialWorthMap.put(Material.DEEPSLATE_GOLD_ORE, 35);
        materialWorthMap.put(Material.GOLD_ORE, 35);
        materialWorthMap.put(Material.RAW_GOLD, 35);

        materialWorthMap.put(Material.AMETHYST_SHARD, 5);
        materialWorthMap.put(Material.AMETHYST_BLOCK, 20);

        materialWorthMap.put(Material.LAPIS_LAZULI, 20);
        materialWorthMap.put(Material.LAPIS_ORE, 10);
        materialWorthMap.put(Material.DEEPSLATE_LAPIS_ORE, 10);
        materialWorthMap.put(Material.LAPIS_BLOCK, 180);

        materialWorthMap.put(Material.REDSTONE, 20);
        materialWorthMap.put(Material.REDSTONE_ORE, 10);
        materialWorthMap.put(Material.DEEPSLATE_REDSTONE_ORE, 10);
        materialWorthMap.put(Material.REDSTONE_BLOCK, 180);

        materialWorthMap.put(Material.DIAMOND, 300);
        materialWorthMap.put(Material.DIAMOND_ORE, 150);
        materialWorthMap.put(Material.DEEPSLATE_DIAMOND_ORE, 150);
        materialWorthMap.put(Material.DIAMOND_BLOCK, 2700);

        materialWorthMap.put(Material.EMERALD_BLOCK, 4500);
        materialWorthMap.put(Material.EMERALD, 500);
        materialWorthMap.put(Material.EMERALD_ORE, 250);
        materialWorthMap.put(Material.DEEPSLATE_EMERALD_ORE, 250);

        materialWorthMap.put(Material.ANCIENT_DEBRIS, 1200);
        materialWorthMap.put(Material.NETHERITE_INGOT, 9000);
        materialWorthMap.put(Material.NETHERITE_BLOCK, 81000);

        // Materials from mobs.
        materialWorthMap.put(Material.ROTTEN_FLESH, 6);
        materialWorthMap.put(Material.BONE, 6);
        materialWorthMap.put(Material.ARROW, 7);
        materialWorthMap.put(Material.STRING, 8);
        materialWorthMap.put(Material.SPIDER_EYE, 4);
        materialWorthMap.put(Material.PHANTOM_MEMBRANE, 35);
        materialWorthMap.put(Material.GUNPOWDER, 9);
        materialWorthMap.put(Material.SLIME_BALL, 12);
        materialWorthMap.put(Material.PRISMARINE_SHARD, 15);
        materialWorthMap.put(Material.NAUTILUS_SHELL, 100);
        materialWorthMap.put(Material.ECHO_SHARD, 250);
        materialWorthMap.put(Material.BLAZE_ROD, 35);
        materialWorthMap.put(Material.BLAZE_POWDER, 16);
        materialWorthMap.put(Material.MAGMA_CREAM, 25);
        materialWorthMap.put(Material.MAGMA_BLOCK, 6);
        materialWorthMap.put(Material.NETHER_STAR, 5000);
        materialWorthMap.put(Material.ENDER_PEARL, 40);
        materialWorthMap.put(Material.SHULKER_SHELL, 290);

        // Materials from farming.
        materialWorthMap.put(Material.WHEAT_SEEDS, 2);
        materialWorthMap.put(Material.MELON_SEEDS, 2);
        materialWorthMap.put(Material.PUMPKIN_SEEDS, 2);
        materialWorthMap.put(Material.BEETROOT_SEEDS, 2);
        materialWorthMap.put(Material.COCOA_BEANS, 2);
        materialWorthMap.put(Material.ACACIA_SAPLING, 2);
        materialWorthMap.put(Material.BAMBOO_SAPLING, 2);
        materialWorthMap.put(Material.BIRCH_SAPLING, 2);
        materialWorthMap.put(Material.OAK_SAPLING, 2);
        materialWorthMap.put(Material.SPRUCE_SAPLING, 2);
        materialWorthMap.put(Material.CHERRY_SAPLING, 2);
        materialWorthMap.put(Material.JUNGLE_SAPLING, 2);
        materialWorthMap.put(Material.BROWN_MUSHROOM, 2);
        materialWorthMap.put(Material.RED_MUSHROOM, 2);
        materialWorthMap.put(Material.RED_MUSHROOM_BLOCK, 8);
        materialWorthMap.put(Material.BROWN_MUSHROOM_BLOCK, 8);
        materialWorthMap.put(Material.POISONOUS_POTATO, 1);
        materialWorthMap.put(Material.POTATOES, 3);
        materialWorthMap.put(Material.COOKIE, 15);

        materialWorthMap.put(Material.WHEAT, 3);
        materialWorthMap.put(Material.BREAD, 7);
        materialWorthMap.put(Material.HAY_BLOCK, 27);
        materialWorthMap.put(Material.CARROT, 3);
        materialWorthMap.put(Material.POTATO, 3);
        materialWorthMap.put(Material.BAKED_POTATO, 7);
        materialWorthMap.put(Material.GOLDEN_CARROT, 15);
        materialWorthMap.put(Material.BEETROOT, 2);
        materialWorthMap.put(Material.SUGAR_CANE, 2);
        materialWorthMap.put(Material.PUMPKIN, 5);
        materialWorthMap.put(Material.MELON, 5);
        materialWorthMap.put(Material.CACTUS, 5);
        materialWorthMap.put(Material.MELON_SLICE, 1);
        materialWorthMap.put(Material.KELP, 1);
        materialWorthMap.put(Material.NETHER_WART, 5);
        materialWorthMap.put(Material.NETHER_WART_BLOCK, 10);
        materialWorthMap.put(Material.NETHER_BRICK, 4);
        materialWorthMap.put(Material.NETHER_BRICK_FENCE, 2);
        materialWorthMap.put(Material.NETHER_BRICK_SLAB, 2);
        materialWorthMap.put(Material.NETHER_BRICK_WALL, 2);
        materialWorthMap.put(Material.NETHER_BRICK_STAIRS, 2);

        materialWorthMap.put(Material.BEEF, 2);
        materialWorthMap.put(Material.COOKED_BEEF, 5);
        materialWorthMap.put(Material.MUTTON, 2);
        materialWorthMap.put(Material.COOKED_MUTTON, 4);
        materialWorthMap.put(Material.CHICKEN, 2);
        materialWorthMap.put(Material.COOKED_CHICKEN, 4);
        materialWorthMap.put(Material.PORKCHOP, 2);
        materialWorthMap.put(Material.COOKED_PORKCHOP, 4);
        materialWorthMap.put(Material.FEATHER, 2);
        materialWorthMap.put(Material.EGG, 2);
        materialWorthMap.put(Material.LEATHER, 5);

        // Materials in the nether.
        materialWorthMap.put(Material.BASALT, 1);
        materialWorthMap.put(Material.SMOOTH_BASALT, 1);
        materialWorthMap.put(Material.NETHERRACK, 1);
        materialWorthMap.put(Material.NETHER_BRICKS, 2);
        materialWorthMap.put(Material.RED_NETHER_BRICKS, 3);
        materialWorthMap.put(Material.SOUL_SAND, 7);
        materialWorthMap.put(Material.SOUL_SOIL, 5);
        materialWorthMap.put(Material.GLOWSTONE_DUST, 12);
        materialWorthMap.put(Material.QUARTZ, 9);
        materialWorthMap.put(Material.QUARTZ_BLOCK, 36);

        materialWorthMap.put(Material.WITHER_SKELETON_SKULL, 5000);

        materialWorthMap.put(Material.MOSSY_STONE_BRICKS, 4);
        materialWorthMap.put(Material.STONE_SLAB, 1);
        materialWorthMap.put(Material.STONE_BRICK_SLAB, 1);
        materialWorthMap.put(Material.STONE_BRICK_STAIRS, 1);
        materialWorthMap.put(Material.STONE_BRICK_WALL, 1);
        materialWorthMap.put(Material.MOSSY_STONE_BRICK_WALL, 1);
        materialWorthMap.put(Material.ANDESITE_WALL, 1);
        materialWorthMap.put(Material.BLACKSTONE_WALL, 1);
        materialWorthMap.put(Material.DEEPSLATE_BRICK_WALL, 1);
        materialWorthMap.put(Material.DEEPSLATE_TILE_WALL, 1);
        materialWorthMap.put(Material.COBBLESTONE_WALL, 1);
        materialWorthMap.put(Material.ANDESITE_SLAB, 1);
        materialWorthMap.put(Material.ANDESITE_STAIRS, 1);
        materialWorthMap.put(Material.POLISHED_ANDESITE, 1);
        materialWorthMap.put(Material.POLISHED_ANDESITE_SLAB, 1);
        materialWorthMap.put(Material.POLISHED_ANDESITE_STAIRS, 1);

        // Materials found in the end.
        materialWorthMap.put(Material.END_STONE, 2);
        materialWorthMap.put(Material.END_STONE_BRICKS, 2);
        materialWorthMap.put(Material.PURPUR_BLOCK, 3);
        materialWorthMap.put(Material.PURPUR_PILLAR, 3);

        // Fishing
        materialWorthMap.put(Material.COD, 3);
        materialWorthMap.put(Material.SALMON, 5);
        materialWorthMap.put(Material.PUFFERFISH, 10);
        materialWorthMap.put(Material.TROPICAL_FISH, 50);
        materialWorthMap.put(Material.LILY_PAD, 2);
        materialWorthMap.put(Material.INK_SAC, 3);
        materialWorthMap.put(Material.STICK, 1);
        materialWorthMap.put(Material.BOWL, 2);
        materialWorthMap.put(Material.TRIPWIRE_HOOK, 10);

        // Treasure items
        materialWorthMap.put(Material.NAME_TAG, 5000);
        materialWorthMap.put(Material.SADDLE, 5);
        materialWorthMap.put(Material.HEART_OF_THE_SEA, 10_000);
        materialWorthMap.put(Material.TURTLE_SCUTE, 5_000);
    }

    /**
     * Retrieves the value of a vanilla material. 0 If the material does not have a value.
     *
     * @param material Vanilla material to check against
     * @return An integer representing a sell price for a vanilla material
     */
    public static int getMaterialValue(Material material) {
        return materialWorthMap.getOrDefault(material, 0);
    }

    public static Map<Material, Integer> getMaterialWorthMap() {
        return materialWorthMap;
    }

    public VanillaResource(ItemService itemService, Material material) {
        super(itemService, material);
    }

    @Override
    public int getWorth(ItemStack itemStack) {
        return getMaterialValue(itemStack.getType()) * itemStack.getAmount();
    }
}
