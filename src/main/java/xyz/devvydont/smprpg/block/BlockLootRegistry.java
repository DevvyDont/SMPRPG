package xyz.devvydont.smprpg.block;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.EnumMap;
import java.util.Map;

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
public class BlockLootRegistry {

    private static final Map<Material, BlockLootEntry> entries = new EnumMap<>(Material.class);

    // Inputs necessary block drop overrides. Keep in mind, we only need to add OVERRIDES. If the vanilla behavior
    // is fine, then you can omit it :)
    // Just keep in mind, vanilla drops will NOT be affected by any fortune stats, so you do need to add all ores etc.
    static {

        // Mining.
        register(Material.STONE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.STONE)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.STONE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.COBBLESTONE)))
                .build()
        );

        register(Material.COBBLESTONE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.STONE)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.STONE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.COBBLESTONE)))
                .build()
        );

        register(Material.DEEPSLATE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.DEEPSLATE)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.DEEPSLATE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.COBBLED_DEEPSLATE)))
                .build()
        );

        register(Material.COBBLED_DEEPSLATE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.DEEPSLATE)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.COBBLED_DEEPSLATE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.COBBLED_DEEPSLATE)))
                .build()
        );

        register(Material.GRANITE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.GRANITE)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.GRANITE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.GRANITE)))
                .build()
        );

        register(Material.DIORITE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.DIORITE)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.DIORITE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.DIORITE)))
                .build()
        );

        register(Material.ANDESITE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.ANDESITE)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.ANDESITE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.ANDESITE)))
                .build()
        );

        register(Material.SAND, BlockLootEntry.builder()
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.GLASS)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.SAND)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.SAND)))
                .uses(AttributeWrapper.MINING_FORTUNE)
                .build()
        );

        register(Material.GRAVEL, BlockLootEntry.builder()
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.GRAVEL)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.GRAVEL)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.GRAVEL)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.FLINT), .2))
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.FLINT), .2))
                .uses(AttributeWrapper.MINING_FORTUNE)
                .build()
        );

        register(Material.END_STONE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.END_STONE)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.END_STONE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.END_STONE)))
                .build()
        );

        register(Material.AMETHYST_BLOCK, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.AMETHYST_BLOCK)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.AMETHYST_BLOCK)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.AMETHYST_BLOCK)))
                .build()
        );

        register(Material.AMETHYST_CLUSTER, BlockLootEntry.builder()
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.AMETHYST_SHARD)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.AMETHYST_CLUSTER)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.AMETHYST_SHARD), 3.5))
                .uses(AttributeWrapper.MINING_FORTUNE)
                .build()
        );

        register(Material.COAL_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.COAL), 1.5))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.COAL_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.COAL), 1.5))
                .build()
        );

        register(Material.DEEPSLATE_COAL_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.COAL), 1.75))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.DEEPSLATE_COAL_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.COAL), 1.75))
                .build()
        );

        register(Material.COPPER_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.COPPER_INGOT), 1.5))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.COPPER_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.RAW_COPPER), 1.5))
                .build()
        );

        register(Material.DEEPSLATE_COPPER_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.COPPER_INGOT), 1.75))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.DEEPSLATE_COPPER_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.RAW_COPPER), 1.75))
                .build()
        );

        register(Material.IRON_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.IRON_INGOT)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.IRON_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.RAW_IRON)))
                .build()
        );

        register(Material.DEEPSLATE_IRON_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.IRON_INGOT)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.DEEPSLATE_IRON_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.RAW_IRON)))
                .build()
        );

        register(Material.GOLD_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.GOLD_INGOT)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.GOLD_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.RAW_GOLD)))
                .build()
        );

        register(Material.DEEPSLATE_GOLD_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.GOLD_INGOT)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.DEEPSLATE_GOLD_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.RAW_GOLD)))
                .build()
        );

        register(Material.REDSTONE_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.REDSTONE), 4))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.REDSTONE_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.REDSTONE), 4))
                .build()
        );

        register(Material.DEEPSLATE_REDSTONE_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.REDSTONE), 5))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.DEEPSLATE_REDSTONE_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.REDSTONE), 5))
                .build()
        );

        register(Material.LAPIS_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.LAPIS_LAZULI), 4))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.LAPIS_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.LAPIS_LAZULI), 4))
                .build()
        );

        register(Material.DEEPSLATE_LAPIS_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.LAPIS_LAZULI), 5))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.DEEPSLATE_LAPIS_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.LAPIS_LAZULI), 5))
                .build()
        );

        register(Material.EMERALD_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.EMERALD)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.EMERALD_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.EMERALD)))
                .build()
        );

        register(Material.DEEPSLATE_EMERALD_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.EMERALD)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.DEEPSLATE_EMERALD_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.EMERALD)))
                .build()
        );

        register(Material.DIAMOND_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.DIAMOND)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.DIAMOND_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.DIAMOND)))
                .build()
        );

        register(Material.DEEPSLATE_DIAMOND_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.DIAMOND)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.DEEPSLATE_DIAMOND_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.DIAMOND)))
                .build()
        );

        register(Material.NETHER_GOLD_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.GOLD_NUGGET)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.NETHER_GOLD_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.GOLD_NUGGET)))
                .build()
        );

        register(Material.NETHER_QUARTZ_ORE, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.QUARTZ)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.NETHER_QUARTZ_ORE)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.QUARTZ)))
                .build()
        );

        register(Material.ANCIENT_DEBRIS, BlockLootEntry.builder(ItemClassification.PICKAXE)
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.NETHERITE_SCRAP)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.ANCIENT_DEBRIS)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.NETHERITE_SCRAP)))
                .build()
        );

        // Woodcutting.
        register(Material.OAK_LOG, BlockLootEntry.builder()
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.CHARCOAL)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.OAK_LOG)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.OAK_LOG)))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.OAK_LOG)))
                .uses(AttributeWrapper.WOODCUTTING_FORTUNE)
                .build()
        );

        register(Material.ACACIA_LOG, BlockLootEntry.builder()
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.CHARCOAL)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.ACACIA_LOG)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.ACACIA_LOG)))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.ACACIA_LOG)))
                .uses(AttributeWrapper.WOODCUTTING_FORTUNE)
                .build()
        );

        register(Material.BIRCH_LOG, BlockLootEntry.builder()
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.CHARCOAL)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.BIRCH_LOG)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.BIRCH_LOG)))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.BIRCH_LOG)))
                .uses(AttributeWrapper.WOODCUTTING_FORTUNE)
                .build()
        );

        register(Material.DARK_OAK_LOG, BlockLootEntry.builder()
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.CHARCOAL)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.DARK_OAK_LOG)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.DARK_OAK_LOG)))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.DARK_OAK_LOG)))
                .uses(AttributeWrapper.WOODCUTTING_FORTUNE)
                .build()
        );

        register(Material.CHERRY_LOG, BlockLootEntry.builder()
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.CHARCOAL)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.CHERRY_LOG)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.CHERRY_LOG)))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.CHERRY_LOG)))
                .uses(AttributeWrapper.WOODCUTTING_FORTUNE)
                .build()
        );

        register(Material.JUNGLE_LOG, BlockLootEntry.builder()
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.CHARCOAL)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.JUNGLE_LOG)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.JUNGLE_LOG)))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.JUNGLE_LOG)))
                .uses(AttributeWrapper.WOODCUTTING_FORTUNE)
                .build()
        );

        register(Material.MANGROVE_LOG, BlockLootEntry.builder()
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.CHARCOAL)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.MANGROVE_LOG)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.MANGROVE_LOG)))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.MANGROVE_LOG)))
                .uses(AttributeWrapper.WOODCUTTING_FORTUNE)
                .build()
        );

        register(Material.SPRUCE_LOG, BlockLootEntry.builder()
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.CHARCOAL)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.SPRUCE_LOG)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.SPRUCE_LOG)))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.SPRUCE_LOG)))
                .uses(AttributeWrapper.WOODCUTTING_FORTUNE)
                .build()
        );

        register(Material.PALE_OAK_LOG, BlockLootEntry.builder()
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.CHARCOAL)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.PALE_OAK_LOG)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.PALE_OAK_LOG)))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.PALE_OAK_LOG)))
                .uses(AttributeWrapper.WOODCUTTING_FORTUNE)
                .build()
        );

        register(Material.WARPED_STEM, BlockLootEntry.builder()
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.WARPED_STEM)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.WARPED_STEM)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.WARPED_STEM)))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.WARPED_STEM)))
                .uses(AttributeWrapper.WOODCUTTING_FORTUNE)
                .build()
        );

        register(Material.CRIMSON_STEM, BlockLootEntry.builder()
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.CRIMSON_STEM)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.CRIMSON_STEM)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.CRIMSON_STEM)))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.CRIMSON_STEM)))
                .uses(AttributeWrapper.WOODCUTTING_FORTUNE)
                .build()
        );

        // Farming. Keep in mind, we actually set farming fortune overrides for a lot of entries.
        // The reason for this is so that fist breaking procs farming fortune.
        register(Material.MELON, BlockLootEntry.builder(ItemClassification.AXE)
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.MELON_SLICE), 3.5))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.MELON_SLICE), 3.5))
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.MELON_SLICE), 3.5))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.MELON)))
                .uses(AttributeWrapper.FARMING_FORTUNE)
                .build()
        );

        register(Material.PUMPKIN, BlockLootEntry.builder(ItemClassification.AXE)
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.PUMPKIN)))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.PUMPKIN)))
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.PUMPKIN)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.PUMPKIN)))
                .uses(AttributeWrapper.FARMING_FORTUNE)
                .build()
        );

        register(Material.WHEAT, BlockLootEntry.builder()
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.WHEAT), 1.5))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.WHEAT), 1.5))
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.WHEAT), 1.5))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.WHEAT), 1.5))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.WHEAT_SEEDS), 1.5))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.WHEAT_SEEDS), 1.5))
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.WHEAT_SEEDS), 1.5))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.WHEAT_SEEDS), 1.5))
                .uses(AttributeWrapper.FARMING_FORTUNE)
                .build()
        );

        register(Material.CARROTS, BlockLootEntry.builder()
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.CARROT), 2.5))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.CARROT), 2.5))
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.CARROT), 2.5))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.CARROT), 2.5))
                .uses(AttributeWrapper.FARMING_FORTUNE)
                .build()
        );

        register(Material.POTATOES, BlockLootEntry.builder()
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.POTATO), 2.5))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.POTATO), 2.5))
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.POTATO), 2.5))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.POTATO), 2.5))
                .uses(AttributeWrapper.FARMING_FORTUNE)
                .build()
        );

        register(Material.NETHER_WART, BlockLootEntry.builder()
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.NETHER_WART), 2.5))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.NETHER_WART), 2.5))
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.NETHER_WART), 2.5))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.NETHER_WART), 2.5))
                .uses(AttributeWrapper.FARMING_FORTUNE)
                .build()
        );

        register(Material.COCOA, BlockLootEntry.builder()
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.COCOA_BEANS), 2.5))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.COCOA_BEANS), 2.5))
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.COCOA_BEANS), 2.5))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.COCOA_BEANS), 2.5))
                .uses(AttributeWrapper.FARMING_FORTUNE)
                .build()
        );

        register(Material.BEETROOTS, BlockLootEntry.builder()
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.BEETROOT)))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.BEETROOT)))
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.BEETROOT)))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.BEETROOT)))
                .add(BlockLootContext.CORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.BEETROOT_SEEDS), 2.5))
                .add(BlockLootContext.INCORRECT_TOOL, BlockLoot.of(ItemService.generate(Material.BEETROOT_SEEDS), 2.5))
                .add(BlockLootContext.AUTO_SMELT, BlockLoot.of(ItemService.generate(Material.BEETROOT_SEEDS), 2.5))
                .add(BlockLootContext.SILK_TOUCH, BlockLoot.of(ItemService.generate(Material.BEETROOT_SEEDS), 2.5))
                .uses(AttributeWrapper.FARMING_FORTUNE)
                .build()
        );
    }

    public static void register(Material material, BlockLootEntry entry) {
        entries.put(material, entry);
    }

    public static @Nullable BlockLootEntry get(Material material) {
        return entries.get(material);
    }

    public static boolean contains(Material material) {
        return entries.containsKey(material);
    }

}
