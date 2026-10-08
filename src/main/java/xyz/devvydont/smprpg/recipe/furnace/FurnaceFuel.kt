package xyz.devvydont.smprpg.recipe.furnace

import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.interfaces.IFurnaceFuel
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint

/**
 * Resolves how long a fuel item burns, in ticks, for the custom furnace controller.
 *
 * The vanilla furnace's own fuel handling is unavailable to us when we drive smelting ourselves (it never
 * ignites for a custom input), so this is our independent source of truth. Custom fuels declare their time
 * via [IFurnaceFuel]; vanilla fuels use the named constants below, matched first by an explicit material map
 * and then by item tag (so every plank/log/sapling variant resolves without enumerating each one).
 *
 * Items not recognised here return 0 and simply cannot fuel a custom smelt. Vanilla smelting of vanilla
 * items is unaffected — that still runs through the vanilla furnace, which keeps its own fuel table.
 */
object FurnaceFuel {

    // Burn times in ticks, mirroring vanilla values. (20 ticks = 1 second.)
    private const val LAVA_BUCKET_TICKS = 20_000
    private const val COAL_BLOCK_TICKS = 16_000
    private const val DRIED_KELP_BLOCK_TICKS = 4_000
    private const val SCAFFOLDING_TICKS = 400
    private const val BLAZE_ROD_TICKS = 2_400
    private const val COAL_TICKS = 1_600
    private const val WOOD_TICKS = 300       // planks, logs, stairs, fences, bamboo planks, etc.
    private const val WOODEN_TOOL_TICKS = 200
    private const val SLAB_TICKS = 150       // wooden slabs burn for half a plank
    private const val SAPLING_TICKS = 100
    private const val STICK_TICKS = 100
    private const val WOOL_TICKS = 100
    private const val BAMBOO_TICKS = 50

    /** Bukkit block states store burn time as a short, so a single fuel can never exceed this many ticks. */
    private const val MAX_BURN_TICKS = Short.MAX_VALUE.toInt()

    /** High-value and non-wood fuels that don't belong to a convenient item tag. */
    private val EXPLICIT_FUEL: Map<Material, Int> = buildMap {
        put(Material.LAVA_BUCKET, LAVA_BUCKET_TICKS)
        put(Material.COAL_BLOCK, COAL_BLOCK_TICKS)
        put(Material.DRIED_KELP_BLOCK, DRIED_KELP_BLOCK_TICKS)
        put(Material.BLAZE_ROD, BLAZE_ROD_TICKS)
        put(Material.COAL, COAL_TICKS)
        put(Material.CHARCOAL, COAL_TICKS)
        put(Material.SCAFFOLDING, SCAFFOLDING_TICKS)
        put(Material.BAMBOO_BLOCK, WOOD_TICKS)
        put(Material.STICK, STICK_TICKS)
        put(Material.BAMBOO, BAMBOO_TICKS)
        for (tool in listOf(
            Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_HOE,
            Material.WOODEN_SHOVEL, Material.WOODEN_SWORD
        )) put(tool, WOODEN_TOOL_TICKS)
    }

    /**
     * The number of ticks the given stack burns for, or 0 if it is not a usable fuel.
     */
    fun burnTicks(stack: ItemStack?): Int {
        if (stack == null || stack.isEmpty) return 0

        val blueprint = blueprint(stack)
        if (blueprint is IFurnaceFuel)
            return blueprint.getBurnTime().toInt().coerceIn(0, MAX_BURN_TICKS)

        return vanillaBurnTicks(stack.type).coerceIn(0, MAX_BURN_TICKS)
    }

    /** Whether the given stack can be used as fuel for a custom smelt. */
    fun isFuel(stack: ItemStack?): Boolean = burnTicks(stack) > 0

    private fun vanillaBurnTicks(material: Material): Int {
        EXPLICIT_FUEL[material]?.let { return it }
        return when {
            Tag.PLANKS.isTagged(material) -> WOOD_TICKS
            Tag.LOGS.isTagged(material) -> WOOD_TICKS
            Tag.WOODEN_STAIRS.isTagged(material) -> WOOD_TICKS
            Tag.WOODEN_FENCES.isTagged(material) -> WOOD_TICKS
            Tag.WOODEN_SLABS.isTagged(material) -> SLAB_TICKS
            Tag.SAPLINGS.isTagged(material) -> SAPLING_TICKS
            Tag.WOOL.isTagged(material) -> WOOL_TICKS
            else -> 0
        }
    }
}
