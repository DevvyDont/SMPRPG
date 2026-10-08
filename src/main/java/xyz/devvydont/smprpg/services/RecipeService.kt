package xyz.devvydont.smprpg.services

import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.BlastingRecipe
import org.bukkit.inventory.CampfireRecipe
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.RecipeChoice.ExactChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.SmokingRecipe
import org.bukkit.inventory.recipe.CraftingBookCategory
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.base.IRecipeDependentMenu
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.items.search.ItemBrowserCache
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.blueprints.fishing.FishBlueprint
import xyz.devvydont.smprpg.listeners.crafting.CustomCampfireController
import xyz.devvydont.smprpg.listeners.crafting.CustomFurnaceController
import net.momirealms.craftengine.core.util.Key
import xyz.devvydont.smprpg.recipe.campfire.FishTeardown
import xyz.devvydont.smprpg.recipe.CompressionGraph
import xyz.devvydont.smprpg.recipe.core.CompressionRecipe
import xyz.devvydont.smprpg.recipe.core.CustomRecipe
import xyz.devvydont.smprpg.recipe.core.ItemIdentifier
import xyz.devvydont.smprpg.recipe.cookingpot.CookingPotRecipe
import xyz.devvydont.smprpg.recipe.crafting.ShapedDisplayRecipe
import xyz.devvydont.smprpg.recipe.crafting.ShapelessDisplayRecipe
import xyz.devvydont.smprpg.recipe.core.CookingPotRecipe as CoreCookingPotRecipe
import xyz.devvydont.smprpg.recipe.core.RecipeLoader
import xyz.devvydont.smprpg.recipe.core.RecipeRegistry
import xyz.devvydont.smprpg.recipe.core.RecipeStationType
import xyz.devvydont.smprpg.recipe.core.SmeltingCookType
import xyz.devvydont.smprpg.recipe.core.SmeltingRecipe
import xyz.devvydont.smprpg.recipe.cuttingboard.CuttingBoardRecipe
import xyz.devvydont.smprpg.recipe.cuttingboard.CuttingBoardToolTags
import xyz.devvydont.smprpg.recipe.core.CuttingBoardRecipe as CoreCuttingBoardRecipe
import xyz.devvydont.smprpg.recipe.freezer.FreezerRecipe
import xyz.devvydont.smprpg.recipe.core.FreezerRecipe as CoreFreezerRecipe
import xyz.devvydont.smprpg.recipe.core.ShapedRecipe as CoreShapedRecipe
import xyz.devvydont.smprpg.recipe.core.ShapelessRecipe as CoreShapelessRecipe
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import java.io.File
import java.util.jar.JarFile
import kotlin.math.min


/**
 * Handles all recipe logic on the server. This includes crafting, smelting, etc.
 */
class RecipeService : IService, Listener {
    private val listeners: MutableList<ToggleableListener> = ArrayList()

    /**
     * The unified, data-driven recipe registry built from the recipe YAML files. Rebuilt and swapped
     * atomically on [reload]. Nothing consumes this yet — station drivers are migrated onto it incrementally.
     */
    private var registry: RecipeRegistry = RecipeRegistry()

    /**
     * The compression recipes currently registered with Bukkit, keyed by their registry key and mapped to the
     * exact [CompressionRecipe] they were built from. Lets a reload diff against the new registry and only
     * re-register edges whose definition actually changed (each edge owns two derived Bukkit recipes).
     */
    private val registeredCompression: MutableMap<NamespacedKey, CompressionRecipe> = HashMap()

    /**
     * The crafting-table recipes currently registered with Bukkit, keyed by recipe key and mapped to the exact
     * [CustomRecipe] they were built from, so a reload only re-registers recipes whose definition changed.
     */
    private val registeredCrafting: MutableMap<NamespacedKey, CustomRecipe> = HashMap()

    /** The running batched-reload task (and its remaining work), or null when no reload is in progress. */
    private var reloadTask: BukkitTask? = null
    private val reloadQueue: ArrayDeque<() -> Unit> = ArrayDeque()

    fun getRegistry(): RecipeRegistry = registry

    /** Whether a batched reload is currently running. */
    fun isReloading(): Boolean = reloadTask != null

    @Throws(RuntimeException::class)
    override fun setup() {

        saveDefaultRecipes()
        registry = RecipeLoader.load()
        registerCompressionRecipes()
        registerCraftingRecipes()
        Bukkit.updateRecipes()

        // Start listeners.
        listeners.add(CustomFurnaceController())
        listeners.add(CustomCampfireController())
        for (listener in listeners)
            listener.start()
    }

    /**
     * Rebuild the recipe registry from disk WITHOUT blocking the main thread. Parsing every recipe file and
     * re-registering the Bukkit recipes generates thousands of items, so the work is spread across ticks
     * ([RELOAD_UNITS_PER_TICK] units per tick), mirroring the item browser cache.
     *
     * The new registry is built in full, then swapped in atomically — so the custom stations (crafting GUI,
     * furnace, campfire) that read [getRegistry] flip cleanly to it. The vanilla recipe book / 2x2 grid is
     * briefly inconsistent while the Bukkit recipes re-register over the following ticks; a single
     * [Bukkit.updateRecipes] resync runs once at the very end.
     *
     * Furnace and campfire smelting are driven live from the registry, so they need no re-registration here.
     *
     * @param onComplete invoked with the loaded recipe count and a one-line reason for each recipe that failed
     *   to load, on the tick the rebuild finishes.
     * @return false if a reload is already in progress, true if this one was started.
     */
    fun reload(onComplete: (loaded: Int, failures: List<String>) -> Unit = { _, _ -> }): Boolean {
        if (reloadTask != null) return false
        prepareClientsForReload()
        saveDefaultRecipes()
        val itemService = SMPRPG.getService(ItemService::class.java)
        val newRegistry = RecipeRegistry()
        reloadQueue.clear()
        // Single-threaded accumulator collecting the failure reasons across the per-file units.
        val failures = mutableListOf<String>()

        // Phase 1: parse + validate each file into the new registry, one file per unit (the item-generation cost).
        for (file in RecipeLoader.recipeFiles())
            reloadQueue.add { failures += RecipeLoader.loadFileInto(file, newRegistry, itemService) }

        // Phase 2: swap the new registry in, then queue Bukkit recipe mutations ONLY for recipes whose
        // definition actually changed (diff against what's currently registered). Re-registering every recipe
        // each reload is what hammered the recipe manager — and thus the client recipe-book resync — and stalled
        // the server. Each mutation is one recipe per unit so even a large change stays off the TPS loop.
        reloadQueue.add {
            registry = newRegistry
            val changed = enqueueCraftingDiff(newRegistry, itemService) +
                enqueueCompressionDiff(newRegistry, itemService)
            // Phase 3: resync clients only if something actually changed (otherwise a reload is silent + free).
            reloadQueue.add {
                if (changed > 0) {
                    Bukkit.updateRecipes()
                    // The /search item cache bakes each item's recipe tooltip, so it's now stale — rebuild it.
                    ItemBrowserCache.rebuild()
                    Bukkit.broadcast(ComponentUtils.success("Recipes reloaded — crafting is back to normal. Rebuilding the /search index..."))
                } else {
                    Bukkit.broadcast(ComponentUtils.success("Recipes reloaded — no changes detected."))
                }
                SMPRPG.plugin.logger.info("Reloaded custom recipe registry (${registry.size} recipes, $changed changed, ${failures.size} failed).")
                onComplete(registry.size, failures)
            }
        }

        reloadTask = object : BukkitRunnable() {
            override fun run() {
                var processed = 0
                while (processed < RELOAD_UNITS_PER_TICK && reloadQueue.isNotEmpty()) {
                    val unit = reloadQueue.removeFirst()
                    try {
                        unit()
                    } catch (e: Exception) {
                        SMPRPG.plugin.logger.warning("Recipe reload step failed: ${e.message}")
                    }
                    processed++
                }
                if (reloadQueue.isEmpty()) {
                    cancel()
                    reloadTask = null
                }
            }
        }.runTaskTimer(SMPRPG.plugin, 0L, 1L)
        return true
    }

    /**
     * Queue Bukkit mutations for crafting-table recipes that differ between what's currently registered and
     * [newRegistry]. Unchanged recipes are left untouched — that's what keeps a reload off the TPS loop, since
     * each touched recipe churns the recipe manager (and resyncs the client recipe book). One unit per recipe.
     * @return the number of mutation units queued.
     */
    private fun enqueueCraftingDiff(newRegistry: RecipeRegistry, itemService: ItemService): Int {
        val newByKey = newRegistry.byStation(RecipeStationType.CRAFTING_TABLE).associateBy { it.key }
        var changes = 0
        // Removed or redefined recipes: drop the stale Bukkit recipe.
        for ((key, old) in registeredCrafting.toList()) {
            if (newByKey[key] == old) continue
            reloadQueue.add {
                Bukkit.removeRecipe(key, false)
                registeredCrafting.remove(key)
            }
            changes++
        }
        // New or redefined recipes: (re)register the new definition.
        for ((key, new) in newByKey) {
            if (registeredCrafting[key] == new) continue
            reloadQueue.add { registerCraftingRecipe(new, itemService) }
            changes++
        }
        return changes
    }

    /** As [enqueueCraftingDiff], but for compression edges (each owns two derived Bukkit recipes). */
    private fun enqueueCompressionDiff(newRegistry: RecipeRegistry, itemService: ItemService): Int {
        val newByKey = newRegistry.byStation(RecipeStationType.COMPRESSOR)
            .filterIsInstance<CompressionRecipe>().associateBy { it.key }
        var changes = 0
        for ((key, old) in registeredCompression.toList()) {
            if (newByKey[key] == old) continue
            reloadQueue.add { unregisterCompression(key) }
            changes++
        }
        for ((key, new) in newByKey) {
            if (registeredCompression[key] == new) continue
            reloadQueue.add { registerCompressionEdge(new, itemService) }
            changes++
        }
        return changes
    }

    /**
     * Announce the reload to the whole server and close every interface whose contents come from the recipe
     * registry ([IRecipeDependentMenu] menus, plus the vanilla crafting/enchanting screens), so nobody
     * interacts with a half-swapped recipe set. Stations that read the registry live each tick (furnaces, the
     * cooking pot, ...) survive the atomic swap and are intentionally left open.
     */
    private fun prepareClientsForReload() {
        Bukkit.broadcast(
            ComponentUtils.create(
                "Recipes are reloading — crafting, compression, and enchanting may briefly not work...",
                NamedTextColor.YELLOW
            )
        )

        val closedMenus = MenuBase.closeMatching { it is IRecipeDependentMenu }
        var closedVanilla = 0
        for (player in Bukkit.getOnlinePlayers()) {
            val top = player.openInventory.topInventory
            val shouldClose = when (top.type) {
                InventoryType.WORKBENCH, InventoryType.ENCHANTING -> true
                // A player's own inventory screen always reports CRAFTING; only close it if they are mid-craft.
                InventoryType.CRAFTING -> top.contents.any { it != null && !it.isEmpty }
                else -> false
            }
            if (shouldClose) {
                player.closeInventory()
                closedVanilla++
            }
        }

        if (closedMenus + closedVanilla > 0)
            SMPRPG.plugin.logger.info("Closed ${closedMenus + closedVanilla} recipe interface(s) for reload.")
    }

    /**
     * Dump the registry's compression recipes to editable YAML files under recipes/compression/, one file per
     * edge (the compress direction; the reverse is derived on load). Intended to be run once to turn the
     * bridge-derived compression recipes into a hand-editable source before the ICompressible bridge is removed.
     * @return the number of files written.
     */
    fun exportCompressionRecipes(): Int {
        val dir = File(SMPRPG.plugin.dataFolder, "recipes/compression")
        dir.mkdirs()

        var count = 0
        // One file per chain: walk each chain from its root and write its ordered tiers.
        for (root in CompressionGraph.roots()) {
            val flow = CompressionGraph.flowFromRoot(root)
            if (flow.size < 2) continue
            val family = CompressionGraph.family(root)?.takeIf { it.isNotEmpty() } ?: ItemIdentifier.parse(root).path
            // tiers[0] is the base (bare id); each later tier carries how many of the previous tier make one of it.
            val tiers = ArrayList<Any>()
            tiers.add(root)
            for (i in 1 until flow.size)
                tiers.add(linkedMapOf("item" to flow[i].first, "amount" to flow[i - 1].second))

            val cfg = YamlConfiguration()
            cfg.set("type", "compression")
            cfg.set("tiers", tiers)
            cfg.save(File(dir, "${family}_compression.yml"))
            count++
        }
        SMPRPG.plugin.logger.info("Exported $count compression chains to recipes/compression/.")
        return count
    }

    /**
     * Dump the enchantment recipes to editable YAML under recipes/enchanting/, one file per enchantment with
     * a `levels:` map holding each level's `power` + `ingredients`. Faithfully round-trips whatever
     * `CustomEnchantment.getRecipe` currently returns (today, the data-driven registry).
     * @return the number of files written (one per enchantment with at least one recipe).
     */
    fun exportEnchantingRecipes(): Int {
        val dir = File(SMPRPG.plugin.dataFolder, "recipes/enchanting")
        dir.mkdirs()
        val itemService = SMPRPG.getService(ItemService::class.java)
        var count = 0

        for (enchantment in EnchantmentService.CUSTOM_ENCHANTMENTS) {
            val levels = linkedMapOf<String, Any>()
            for (level in 1..enchantment.maxLevel) {
                val recipe = enchantment.getRecipe(level) ?: continue
                levels[level.toString()] = linkedMapOf(
                    "power" to recipe.power,
                    "ingredients" to recipe.ingredients.map {
                        linkedMapOf("item" to itemService.getIdentifier(it), "amount" to it.amount)
                    }
                )
            }
            if (levels.isEmpty()) continue
            val cfg = YamlConfiguration()
            cfg.set("type", "enchanting")
            cfg.set("enchantment", enchantment.id)
            cfg.set("levels", levels)
            cfg.save(File(dir, "${enchantment.id}.yml"))
            count++
        }
        SMPRPG.plugin.logger.info("Exported $count enchanting recipe files to recipes/enchanting/.")
        return count
    }

    /**
     * Seed every bundled recipe file into the data folder on first run only. We only seed when the recipes
     * folder does not yet exist, so admin edits — and deletions — are respected on later restarts.
     */
    private fun saveDefaultRecipes() {
        val dir = File(SMPRPG.plugin.dataFolder, "recipes")
        if (dir.exists())
            return
        copyBundledRecipes(overwrite = false)
    }

    /**
     * Force every bundled recipe file from the jar back into the data folder, overwriting existing copies.
     * Unlike [saveDefaultRecipes], this ignores whether the folder already exists, so it refreshes the
     * hand-authored recipes (shaped/smelting/etc.) to match the current build — the counterpart of
     * [exportCompressionRecipes]/[exportEnchantingRecipes] for the file-authored recipe types.
     * @return the number of files written.
     */
    fun exportBundledRecipes(): Int = copyBundledRecipes(overwrite = true)

    /**
     * Copy every recipe file under the jar's `recipes/` directory into the data folder. With [overwrite] off,
     * existing files are left untouched (first-run seeding); with it on, they are replaced.
     * @return the number of files copied.
     */
    private fun copyBundledRecipes(overwrite: Boolean): Int {
        var count = 0
        try {
            val jar = File(SMPRPG.plugin.javaClass.protectionDomain.codeSource.location.toURI())
            JarFile(jar).use { jf ->
                for (entry in jf.entries()) {
                    if (entry.isDirectory) continue
                    val name = entry.name
                    if (!name.startsWith("recipes/")) continue
                    if (!name.endsWith(".yml") && !name.endsWith(".yaml")) continue
                    try {
                        SMPRPG.plugin.saveResource(name, overwrite)
                        count++
                    } catch (e: IllegalArgumentException) {
                        // Shouldn't happen for a jar entry we just enumerated; ignore defensively.
                    }
                }
            }
        } catch (e: Exception) {
            SMPRPG.plugin.logger.warning("Could not copy bundled recipe files: ${e.message}")
        }
        return count
    }

    override fun cleanup() {
        reloadTask?.cancel()
        reloadTask = null
        reloadQueue.clear()
        for (listener in listeners)
            listener.stop()
    }

    /**
     * Registers the vanilla-grid compression crafting recipes (compress and decompress) from the registry's
     * compression edges. Each edge is stored compress-direction (N -> 1); we build both Bukkit shaped recipes
     * from it. Custom items match exactly; vanilla items match by material. Previously-registered recipes are
     * removed first so this is safe to call again on reload, and both recipes are unlocked by the chain root.
     */
    private fun registerCompressionRecipes() {
        // Mutate quietly (no per-recipe client resend); callers resync once via Bukkit.updateRecipes().
        for (key in registeredCompression.keys.toList())
            unregisterCompression(key)

        val itemService = SMPRPG.getService(ItemService::class.java)
        for (recipe in registry.byStation(RecipeStationType.COMPRESSOR).filterIsInstance<CompressionRecipe>())
            registerCompressionEdge(recipe, itemService)
    }

    /** Remove the two derived Bukkit recipes for a compression edge and forget it. */
    private fun unregisterCompression(key: NamespacedKey) {
        Bukkit.removeRecipe(NamespacedKey("smprpg", key.value() + "_compress"), false)
        Bukkit.removeRecipe(NamespacedKey("smprpg", key.value() + "_decompress"), false)
        registeredCompression.remove(key)
    }

    /** Build and register the compress + decompress Bukkit recipes for one compression edge (quietly). */
    private fun registerCompressionEdge(recipe: CompressionRecipe, itemService: ItemService) {
        // Only build from the compress direction (N -> 1); the decompress recipe is derived here.
        if (recipe.input.amount <= recipe.result.amount) return
        val lowerId = recipe.input.identifier
        val higherId = recipe.result.identifier
        val n = recipe.input.amount
        val lowerStack = itemService.resolveIdentifier(lowerId.asString()) ?: return
        val higherStack = itemService.resolveIdentifier(higherId.asString()) ?: return

        val group = CompressionGraph.baseOf(lowerId.asString())
        val rootStack = itemService.resolveIdentifier(group)

        // Compress: N lower -> 1 higher
        val compressKey = NamespacedKey("smprpg", recipe.key.value() + "_compress")
        val compress = ShapedRecipe(compressKey, higherStack.clone().apply { amount = 1 })
        compress.shape(*compressionShape(n).toTypedArray())
        compress.setIngredient(COMPRESSION_CHAR, compressionChoice(lowerId, lowerStack))
        compress.category = CraftingBookCategory.MISC
        compress.group = group

        // Decompress: 1 higher -> N lower
        val decompressKey = NamespacedKey("smprpg", recipe.key.value() + "_decompress")
        val decompress = ShapedRecipe(decompressKey, lowerStack.clone().apply { amount = n })
        decompress.shape(*compressionShape(1).toTypedArray())
        decompress.setIngredient(COMPRESSION_CHAR, compressionChoice(higherId, higherStack))
        decompress.category = CraftingBookCategory.MISC
        decompress.group = group

        Bukkit.addRecipe(compress, false)
        Bukkit.addRecipe(decompress, false)
        registeredCompression[recipe.key] = recipe
        if (rootStack != null) {
            itemService.addRecipeUnlock(rootStack, compressKey)
            itemService.addRecipeUnlock(rootStack, decompressKey)
        }
    }

    /** Custom items must match exactly (shared base materials are ambiguous); vanilla items match by material. */
    private fun compressionChoice(id: ItemIdentifier, stack: ItemStack): RecipeChoice =
        if (id.namespace == "smprpg") ExactChoice(stack) else MaterialChoice(stack.type)

    /**
     * Registers the data-driven crafting-table recipes with Bukkit so they work in the player's 2x2 grid, the
     * recipe book, the recipe browser, and the transmute-upgrade fix. The custom crafting menu has its own
     * count-aware matching; this Bukkit registration covers everything else. Shaped recipes that use per-slot
     * counts greater than one cannot be represented as a Bukkit recipe and are skipped here (they still craft
     * in the custom menu). Removed and re-added on reload, and unlocked by each recipe's `unlocked_by` items.
     */
    private fun registerCraftingRecipes() {
        // Mutate quietly (no per-recipe client resend); callers resync once via Bukkit.updateRecipes().
        for (key in registeredCrafting.keys.toList())
            Bukkit.removeRecipe(key, false)
        registeredCrafting.clear()

        val itemService = SMPRPG.getService(ItemService::class.java)
        for (recipe in registry.byStation(RecipeStationType.CRAFTING_TABLE))
            registerCraftingRecipe(recipe, itemService)
    }

    /** Build and register the Bukkit crafting recipe for one registry crafting recipe (quietly). */
    private fun registerCraftingRecipe(recipe: CustomRecipe, itemService: ItemService) {
        val result = recipe.outputs.firstOrNull()?.generate() ?: return
        val key = recipe.key
        val bukkit: Recipe = when (recipe) {
            is CoreShapedRecipe -> {
                if (recipe.keyMap.values.any { it.amount > 1 }) {
                    SMPRPG.plugin.logger.info("Crafting recipe ${key.value()} uses per-slot counts; only the custom crafting menu will craft it.")
                    // Still track it (no Bukkit recipe) so the reload diff treats it as registered/unchanged.
                    registeredCrafting[key] = recipe
                    return
                }
                val shaped = ShapedRecipe(key, result)
                shaped.shape(*recipe.pattern.toTypedArray())
                for ((ch, ingredient) in recipe.keyMap)
                    shaped.setIngredient(ch, craftingChoice(ingredient.identifier, itemService) ?: continue)
                shaped.category = CraftingBookCategory.MISC
                shaped
            }
            is CoreShapelessRecipe -> {
                val shapeless = ShapelessRecipe(key, result)
                for (ingredient in recipe.ingredients) {
                    val choice = craftingChoice(ingredient.identifier, itemService) ?: continue
                    repeat(ingredient.amount) { shapeless.addIngredient(choice) }
                }
                shapeless.category = CraftingBookCategory.MISC
                shapeless
            }
            else -> return
        }

        Bukkit.addRecipe(bukkit, false)
        registeredCrafting[key] = recipe
        for (unlock in recipe.unlockedBy) {
            val stack = itemService.resolveIdentifier(unlock.asString()) ?: continue
            itemService.addRecipeUnlock(stack, key)
        }
    }

    /** Resolve a recipe choice for a crafting ingredient: exact for custom items, material for vanilla. */
    private fun craftingChoice(id: ItemIdentifier, itemService: ItemService): RecipeChoice? {
        val stack = itemService.resolveIdentifier(id.asString()) ?: return null
        return if (id.namespace == "smprpg") ExactChoice(stack) else MaterialChoice(stack.type)
    }

    companion object {

        /**
         * How many reload work units (file parses / recipe add+removes) to process per tick. Each unit can
         * generate custom items (validation + result generation) and touch Bukkit's recipe manager, both
         * main-thread only — kept low so a reload never noticeably dents the TPS loop (it just takes longer).
         */
        private const val RELOAD_UNITS_PER_TICK = 8

        /** The grid character used for the single repeated ingredient in a compression recipe. */
        private const val COMPRESSION_CHAR = 'm'

        /** Build a compression crafting grid shape that holds [amount] copies of the ingredient. */
        private fun compressionShape(amount: Int): List<String> = when (amount) {
            1 -> listOf("m")
            2 -> listOf("mm")
            3 -> listOf("mmm")
            4 -> listOf("mm", "mm")
            5 -> listOf("mmm", "mm")
            6 -> listOf("mmm", "mmm")
            7 -> listOf("mmm", "mmm", "m")
            8 -> listOf("mmm", "mmm", "mm")
            else -> listOf("mmm", "mmm", "mmm")
        }

        /** Whether the given item has a data-driven crafting-table recipe that produces it. */
        @JvmStatic
        fun isCraftable(item: ItemStack): Boolean {
            val id = SMPRPG.getService(ItemService::class.java).getIdentifier(item)
            return SMPRPG.getService(RecipeService::class.java).getRegistry()
                .byResult(id).any { it.station == RecipeStationType.CRAFTING_TABLE }
        }

        /** Map a cutting board recipe's `tool` string ("knives"/"axes"/"shovels") to its tool tag. */
        fun toolTagOf(tool: String?): Key = when (tool?.lowercase()) {
            "axes" -> CuttingBoardToolTags.AXES
            "shovels" -> CuttingBoardToolTags.SHOVELS
            else -> CuttingBoardToolTags.KNIVES
        }

        /**
         * Get a list of recipes for a specific item.
         * This operation will filter out vanilla recipes that think they can craft custom items, due to a
         * Material recipe match.
         * @param item
         * @return
         */
        @JvmStatic
        fun getRecipesFor(item: ItemStack): MutableList<Recipe> {
            // Our crafting-table recipes are sourced from the registry below (getCustomRecipesFor), so drop the
            // Bukkit-registered copies here to avoid duplicates — and so count>1 recipes (which have no Bukkit
            // copy) are represented by exactly one entry.
            val craftingKeys = SMPRPG.getService(RecipeService::class.java).getRegistry()
                .byStation(RecipeStationType.CRAFTING_TABLE).map { it.key.asString() }.toSet()
            val allRecipes = Bukkit.getRecipesFor(item)
            // Filter out recipes that have the minecraft namespace that think they can craft custom items.
            // Filter out items that do not match. This function has this lovely behavior of giving us ALL recipes that give us the same underlying vanilla material.
            // Another level of filtering. Filter out custom recipes that craft vanilla items. The only time this should
            // ever really be possible is with compression recipes, and they are annoying to display anyway...
            for (recipe in allRecipes.stream().toList()) {

                // Filter out items that simply do not match. An iron ingot cannot be crafted by a recipe that is a boiling ingot.
                if (!ItemService.isOfSameType(item, recipe.result)) {
                    allRecipes.remove(recipe)
                    continue
                }

                if (recipe !is Keyed)
                    continue

                // Drop our own Bukkit crafting recipes; the registry-built display recipe replaces them.
                if (recipe.key.asString() in craftingKeys) {
                    allRecipes.remove(recipe)
                    continue
                }

                // Filter out a recipe if it is vanilla, but thinks it can craft a custom item.
                //todo commented out to see if recipe browser behaves better with displaying recipes with strange
                //todo ways to make them (vanilla items making custom items)
//                val recipeIsVanilla = recipe.key.namespace == NamespacedKey.MINECRAFT_NAMESPACE
//
//                val resultBlueprint = blueprint(recipe.result)
//                if (recipeIsVanilla && resultBlueprint.isCustom) {
//                    allRecipes.remove(recipe)
//                    continue
//                }
//
//                // Filter out a recipe if it is one of our recipes, but a vanilla item is generated. This could potentially
//                // filter out recipes we want to consider valid, but there are more "lying" recipes if we allow them.
//                if (!recipeIsVanilla && resultBlueprint.isVanilla) {
//                    allRecipes.remove(recipe)
//                }
            }
            allRecipes.addAll(getCustomRecipesFor(item))
            return allRecipes
        }

        /**
         * The registry-sourced display recipes that PRODUCE [item]. Uses the registry's by-result index, so it
         * only builds display recipes for the (usually one or two) recipes that actually make this item — never
         * iterating or generating items for the whole recipe set, which is what made the recipe viewer lag.
         */
        private fun getCustomRecipesFor(item: ItemStack): List<Recipe> {
            val itemService = SMPRPG.getService(ItemService::class.java)
            val registry = SMPRPG.getService(RecipeService::class.java).getRegistry()
            val results = mutableListOf<Recipe>()
            for (core in registry.byResult(itemService.getIdentifier(item)))
                buildDisplayRecipe(core)?.let { results.add(it) }
            results.addAll(fishTeardownDisplayRecipes(item))
            return results
        }

        /**
         * All registry-sourced display recipes that USE [item] as an ingredient — the backend for an item
         * "usages" view. Indexed lookup, so it is as cheap as [getCustomRecipesFor].
         */
        @JvmStatic
        fun getUsagesFor(item: ItemStack): MutableList<Recipe> {
            val itemService = SMPRPG.getService(ItemService::class.java)
            val registry = SMPRPG.getService(RecipeService::class.java).getRegistry()
            val results = mutableListOf<Recipe>()
            for (core in registry.byIngredient(itemService.getIdentifier(item)))
                buildDisplayRecipe(core)?.let { results.add(it) }
            return results
        }

        /**
         * Build the recipe-browser display recipe for one registry recipe (count-aware for crafting), or null
         * for recipes that have no browser display here (compression, which is shown via Bukkit, and enchanting,
         * which produces no item).
         */
        private fun buildDisplayRecipe(core: CustomRecipe): Recipe? = when (core) {
            is CoreCookingPotRecipe -> {
                val resultStack = core.result.generate()?.takeIf { it.type != Material.BARRIER } ?: return null
                val inputs = core.ingredients.mapNotNull { ing -> ing.identifier.resolve()?.also { it.amount = ing.amount } }
                CookingPotRecipe(core.key, inputs, core.time, resultStack, null, core.plating?.resolve())
            }
            is CoreFreezerRecipe -> {
                val resultStack = core.result.generate()?.takeIf { it.type != Material.BARRIER } ?: return null
                val inputStack = core.input.identifier.resolve() ?: return null
                FreezerRecipe(core.key, inputStack, core.time, resultStack)
            }
            is CoreCuttingBoardRecipe -> {
                val outputs = core.results.mapNotNull { out -> out.generate()?.let { it to out.chance } }
                if (outputs.none { (stack, _) -> stack.type != Material.BARRIER }) return null
                val inputStack = core.input.identifier.resolve() ?: return null
                CuttingBoardRecipe(core.key, inputStack, outputs, toolTagOf(core.tool))
            }
            is SmeltingRecipe -> {
                val resultStack = core.result.generate()?.takeIf { it.type != Material.BARRIER } ?: return null
                val inputStack = core.input.identifier.resolve() ?: return null
                val choice: RecipeChoice =
                    if (core.input.identifier.namespace == "smprpg") ExactChoice(inputStack)
                    else MaterialChoice(inputStack.type)
                when (core.cook) {
                    SmeltingCookType.FURNACE -> FurnaceRecipe(core.key, resultStack, choice, core.experience, core.time)
                    SmeltingCookType.BLASTING -> BlastingRecipe(core.key, resultStack, choice, core.experience, core.time)
                    SmeltingCookType.SMOKING -> SmokingRecipe(core.key, resultStack, choice, core.experience, core.time)
                    SmeltingCookType.CAMPFIRE -> CampfireRecipe(core.key, resultStack, choice, core.experience, core.time)
                }
            }
            is CoreShapedRecipe -> {
                val resultStack = core.result.generate()?.takeIf { it.type != Material.BARRIER } ?: return null
                val ingredientItems = HashMap<Char, ItemStack>()
                for ((ch, ingredient) in core.keyMap) {
                    val stack = ingredient.identifier.resolve() ?: continue
                    stack.amount = min(ingredient.amount, stack.maxStackSize)
                    ingredientItems[ch] = stack
                }
                ShapedDisplayRecipe(core.key, core.pattern, ingredientItems, resultStack, core.upgradeChar)
            }
            is CoreShapelessRecipe -> {
                val resultStack = core.result.generate()?.takeIf { it.type != Material.BARRIER } ?: return null
                val items = core.ingredients.mapNotNull { ing -> ing.identifier.resolve()?.also { it.amount = min(ing.amount, it.maxStackSize) } }
                ShapelessDisplayRecipe(core.key, items, resultStack)
            }
            else -> null
        }

        /**
         * Fish teardown is driven by [CustomCampfireController] rather than a Bukkit recipe, so build
         * unregistered campfire display recipes (one per rarity, choosing any fish of that rarity) for the
         * recipe browser when the queried item is the essence that rarity produces.
         */
        private fun fishTeardownDisplayRecipes(item: ItemStack): List<Recipe> {
            val fishByRarity: MutableMap<ItemRarity, MutableList<ItemStack>> = HashMap()
            for (blueprint in SMPRPG.getService(ItemService::class.java).customBlueprints) {
                if (blueprint !is FishBlueprint) continue
                fishByRarity.getOrPut(blueprint.defaultRarity) { ArrayList() }.add(generate(blueprint.customItemType))
            }

            val display = mutableListOf<Recipe>()
            for ((rarity, fish) in fishByRarity) {
                if (fish.isEmpty()) continue
                val essence = generate(FishTeardown.essenceFor(rarity))
                if (!essence.isSimilar(item)) continue
                val key = NamespacedKey(SMPRPG.plugin, "${rarity}_fish_teardown")
                display.add(CampfireRecipe(key, essence, ExactChoice(fish), 0f, FishTeardown.cookTimeTicks(rarity)))
            }
            return display
        }
    }


}
