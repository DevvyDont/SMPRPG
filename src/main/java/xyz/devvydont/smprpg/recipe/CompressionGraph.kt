package xyz.devvydont.smprpg.recipe

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.blueprints.resources.VanillaResource
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.recipe.core.CompressionRecipe
import xyz.devvydont.smprpg.recipe.core.RecipeRegistry
import xyz.devvydont.smprpg.recipe.core.RecipeStationType
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.RecipeService

/**
 * A read-only view over the registry's compression recipes that answers the chain questions the economy,
 * lore, recipe-discovery, and bazaar need: what an item decompresses into, its base material, the total
 * compression ratio, and the forward chain.
 *
 * The source of truth is the data-driven recipe registry (compression recipe files). It is rebuilt
 * automatically whenever the registry instance is swapped (i.e. on reload).
 */
object CompressionGraph {

    /** higher-tier id -> (lower-tier id, ratio): one of the higher item decompresses into `ratio` lower items. */
    private data class Down(val lower: String, val ratio: Int)

    /** lower-tier id -> (higher-tier id, inputAmount): `inputAmount` lower items compress into one higher item. */
    private data class Up(val higher: String, val inputAmount: Int)

    private var lastRegistry: RecipeRegistry? = null
    private val downMap = HashMap<String, Down>()
    private val upMap = HashMap<String, Up>()
    private val familyMap = HashMap<String, String>()

    private fun ensureBuilt() {
        val registry = SMPRPG.getService(RecipeService::class.java).getRegistry()
        if (registry === lastRegistry) return

        downMap.clear()
        upMap.clear()
        familyMap.clear()
        for (recipe in registry.byStation(RecipeStationType.COMPRESSOR)) {
            if (recipe !is CompressionRecipe) continue
            val inId = recipe.input.identifier.asString()
            val inAmt = recipe.input.amount
            val outId = recipe.result.identifier.asString()
            val outAmt = recipe.result.amount
            if (recipe.family.isNotEmpty()) {
                familyMap[inId] = recipe.family
                familyMap[outId] = recipe.family
            }
            // A compression edge is always N:1. Each recipe (in either direction) fully describes the edge, so we
            // derive both the compress and decompress mappings from it. This keeps the graph correct whether the
            // registry holds both directions (the bridge) or just the compress direction (exported YAML).
            if (inAmt > outAmt) {
                // Compress: inAmt of input -> 1 output. input compresses up; output decompresses down by inAmt.
                upMap[inId] = Up(outId, inAmt)
                downMap[outId] = Down(inId, inAmt)
            } else {
                // Decompress: 1 input -> outAmt output. input decompresses down; output compresses up by outAmt.
                downMap[inId] = Down(outId, outAmt)
                upMap[outId] = Up(inId, outAmt)
            }
        }
        lastRegistry = registry
    }

    /** The compression chain "family" this item belongs to (from its chain file name), or null if it is in none. */
    fun family(id: String): String? {
        ensureBuilt()
        return familyMap[id]
    }

    /** True if this item decompresses into something (i.e. it is not the base of its chain). */
    fun isCompressed(id: String): Boolean {
        ensureBuilt()
        return downMap.containsKey(id)
    }

    /** True if this item participates in any compression chain (as a compressible or decompressible member). */
    fun inChain(id: String): Boolean {
        ensureBuilt()
        return downMap.containsKey(id) || upMap.containsKey(id)
    }

    /** The compress step for this item: (higher-tier id, how many of this item compress into one higher). */
    fun compressStep(id: String): Pair<String, Int>? {
        ensureBuilt()
        return upMap[id]?.let { it.higher to it.inputAmount }
    }

    /** The decompress step for this item: (lower-tier id, how many lower items one of this yields). */
    fun decompressStep(id: String): Pair<String, Int>? {
        ensureBuilt()
        return downMap[id]?.let { it.lower to it.ratio }
    }

    /** The base (root) identifier of this item's compression chain — what it ultimately decompresses into. */
    fun baseOf(id: String): String {
        ensureBuilt()
        var current = id
        val seen = HashSet<String>()
        while (downMap.containsKey(current) && seen.add(current))
            current = downMap[current]!!.lower
        return current
    }

    /** Total base items represented by one of this item (product of decompression ratios). 1 if it is a base. */
    fun ratioToBase(id: String): Int {
        ensureBuilt()
        var current = id
        var ratio = 1
        val seen = HashSet<String>()
        while (downMap.containsKey(current) && seen.add(current)) {
            ratio *= downMap[current]!!.ratio
            current = downMap[current]!!.lower
        }
        return ratio
    }

    /** Chain roots: items that compress into something but do not decompress (the base of each chain). */
    fun roots(): List<String> {
        ensureBuilt()
        return upMap.keys.filter { !downMap.containsKey(it) }
    }

    /**
     * Walk the compression chain forward from a root, returning each member as (identifier, inputAmount) where
     * inputAmount is how many of that member compress into the next tier (defaulting to 9 for the final tier).
     */
    fun flowFromRoot(rootId: String): List<Pair<String, Int>> {
        ensureBuilt()
        val flow = ArrayList<Pair<String, Int>>()
        var current: String? = rootId
        val seen = HashSet<String>()
        while (current != null && seen.add(current)) {
            flow.add(current to (upMap[current]?.inputAmount ?: 9))
            current = upMap[current]?.higher
        }
        return flow
    }

    /**
     * Worth of a (possibly compressed) item, reproducing the former ICompressible.calculateCompressedWorth:
     * walk down to the base, multiply the per-item base worth by the total compression ratio and the stack size.
     * Non-compressed items fall back to their stack amount, matching the original behavior.
     */
    fun worth(itemStack: ItemStack): Int {
        ensureBuilt()
        val itemService = SMPRPG.getService(ItemService::class.java)
        val id = itemService.getIdentifier(itemStack)
        if (!downMap.containsKey(id)) return itemStack.amount

        val ratio = ratioToBase(id)
        val baseWorth = baseItemWorth(baseOf(id)) ?: return itemStack.amount
        return ratio * baseWorth * itemStack.amount
    }

    /** Per-item worth of a base identifier: vanilla materials via [VanillaResource], custom items via [ISellable]. */
    private fun baseItemWorth(baseId: String): Int? {
        val itemService = SMPRPG.getService(ItemService::class.java)
        if (baseId.startsWith("minecraft:")) {
            val material = Material.matchMaterial(baseId) ?: return null
            return VanillaResource.getMaterialValue(material)
        }
        val stack = itemService.resolveIdentifier(baseId) ?: return null
        val blueprint = itemService.getBlueprint(stack)
        return if (blueprint is ISellable) (blueprint as ISellable).getWorth(blueprint.generate()) else null
    }
}
