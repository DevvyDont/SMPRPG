package xyz.devvydont.smprpg.gui.items.search

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.SMPItemBlueprint
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.RecipeService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.time.TickTime
import kotlin.math.min

/**
 * Owns the item browser's display cache (see [MenuItemBrowser][xyz.devvydont.smprpg.gui.items.MenuItemBrowser]).
 *
 * Generating, rendering, and indexing every custom item and vanilla material is expensive enough that doing it all at
 * once stalls the main thread. Instead, [beginBuild] is called once at startup and the work is spread across many
 * ticks ([ITEMS_PER_TICK] items per tick) so the server never hitches. Until the build finishes, [isReady] is false
 * and the browser should be withheld from players with [notReadyMessage].
 *
 * Everything here runs on the main thread (Bukkit item generation is not thread-safe), so no synchronization is needed:
 * the gradual build task and menu queries can never overlap.
 */
object ItemBrowserCache {

    /**
     * The lifecycle of the cache.
     */
    enum class State {
        /** [beginBuild] has not run yet; no work has been queued. */
        UNBUILT,

        /** Items are being generated and indexed in chunks across ticks. */
        BUILDING,

        /** Every item has been indexed; the browser is fully usable. */
        READY
    }

    // How many items to generate and index per tick. Kept conservative so a live rebuild (e.g. after a recipe
    // reload, with players online) never noticeably dents TPS — it just takes a little longer to finish.
    private const val ITEMS_PER_TICK = 12

    // How long to wait after startup before indexing begins, giving dependent systems (recipes, CraftEngine) time to
    // finish loading so generated items render with their final lore and recipe tooltips.
    private val BUILD_START_DELAY = TickTime.seconds(3)

    // Progress is capped here while still building so we never report a misleading 100% before the cache is READY.
    private const val MAX_BUILDING_PERCENT = 99

    // Serializes lore components down to plain text once, so tooltip searches operate on simple strings.
    private val PLAIN_TEXT = PlainTextComponentSerializer.plainText()

    // Vanilla materials we never want to surface in the browser (duplicates/uncraftable variants of custom items).
    private val BLACKLISTED_MATERIALS = setOf(
        Material.STONE_AXE, Material.STONE_PICKAXE, Material.STONE_HOE, Material.STONE_SWORD, Material.STONE_SHOVEL, Material.STONE_SPEAR,
        Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE, Material.DIAMOND_HOE, Material.DIAMOND_SWORD, Material.DIAMOND_SHOVEL, Material.DIAMOND_SPEAR, Material.DIAMOND_HELMET, Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE
    )

    // Fully display-ready items, each with its searchable fields pre-indexed. Building this once lets both page flips
    // and queries avoid all per-item lore/recipe computation.
    private val cache: MutableList<SearchableItem> = ArrayList()

    // Item sources still waiting to be indexed. Each generator is invoked during the gradual build to produce the
    // ItemStack to index. Enumerating sources up front is cheap (just enum/material entries); the expensive generation
    // is deferred into per-tick chunks.
    private val pending: ArrayDeque<() -> ItemStack> = ArrayDeque()

    // Total number of sources queued when the build started, used to compute progress.
    private var totalSources = 0

    // The running gradual-build task, kept so it can be cancelled on shutdown.
    private var buildTask: BukkitTask? = null

    // Identifier sets snapshotted once per build so each item's "craftable"/"used in" hint is an O(1) lookup
    // instead of a per-item recipe scan (which made the live rebuild drag TPS down).
    private var craftableResultIds: Set<String> = emptySet()
    private var usableIngredientIds: Set<String> = emptySet()

    var state: State = State.UNBUILT
        private set

    /**
     * @return true once every item has been indexed and the browser can be opened.
     */
    @JvmStatic
    fun isReady(): Boolean = state == State.READY

    /**
     * @return Build progress as a whole-number percentage in the range 0..100.
     */
    @JvmStatic
    fun progressPercent(): Int {
        if (state == State.READY) return 100
        if (totalSources == 0) return 0
        val done = totalSources - pending.size
        return min(MAX_BUILDING_PERCENT, done * 100 / totalSources)
    }

    /**
     * @return A read-only view of the indexed items, for querying by the browser.
     */
    @JvmStatic
    fun items(): List<SearchableItem> = cache

    /**
     * The message shown to a player who tries to open the browser before the cache has finished building.
     *
     * @return A styled component explaining the registry is still building, including current progress.
     */
    @JvmStatic
    fun notReadyMessage(): Component =
        ComponentUtils.error("Hold on a moment! The item registry is still building... (${progressPercent()}% complete)")

    /**
     * Queues every item source and starts the gradual build. Safe to call once at startup; subsequent calls are
     * ignored while a build is already in progress or complete.
     */
    @JvmStatic
    @JvmOverloads
    fun beginBuild(startDelay: Long = BUILD_START_DELAY) {
        if (state != State.UNBUILT) return
        state = State.BUILDING
        indexRecipeLookups()
        enqueueSources()
        totalSources = pending.size
        scheduleBuildTask(startDelay)
    }

    /**
     * Snapshot, once per build, the set of identifiers that any recipe produces (for the "Left-click to view
     * recipe!" hint) and consumes (for the "Right-click to view usages!" hint). Doing this once — instead of
     * resolving recipes per cached item — turns each item's hint check into an O(1) set lookup.
     */
    private fun indexRecipeLookups() {
        val itemService = SMPRPG.getService(ItemService::class.java)
        val registry = SMPRPG.getService(RecipeService::class.java).getRegistry()
        // Producible identifiers: our registry results plus every Bukkit recipe's result (vanilla, compression).
        val craftable = HashSet(registry.resultIdentifiers())
        val recipes = Bukkit.recipeIterator()
        while (recipes.hasNext())
            runCatching { craftable.add(itemService.getIdentifier(recipes.next().result)) }
        craftableResultIds = craftable
        usableIngredientIds = registry.ingredientIdentifiers().toHashSet()
    }

    /**
     * Discard the current cache and rebuild it from scratch — e.g. after a recipe reload, since each cached item
     * bakes in its recipe tooltip. The rebuild starts immediately (no startup delay) and runs gradually like the
     * initial build, so `/search` is briefly unavailable (showing build progress) until it finishes.
     */
    @JvmStatic
    fun rebuild() {
        shutdown()
        beginBuild(startDelay = 0L)
    }

    /**
     * Cancels any in-progress build and resets the cache. Called on plugin disable so a subsequent enable rebuilds
     * from a clean slate.
     */
    @JvmStatic
    fun shutdown() {
        buildTask?.cancel()
        buildTask = null
        pending.clear()
        cache.clear()
        craftableResultIds = emptySet()
        usableIngredientIds = emptySet()
        totalSources = 0
        state = State.UNBUILT
    }

    /**
     * Enqueues a deferred generator for every custom item and every non-blacklisted vanilla material. The generators
     * are not invoked here; they run later, a chunk at a time, inside the build task.
     */
    private fun enqueueSources() {
        for (type in CustomItemType.entries)
            pending.add { ItemService.generate(type) }

        for (material in Material.entries) {
            if (material.isLegacy || !material.isItem || material == Material.AIR || material in BLACKLISTED_MATERIALS) continue
            pending.add { ItemService.generate(material) }
        }
    }

    /**
     * Starts the repeating task that indexes [ITEMS_PER_TICK] items per tick until the queue is drained.
     */
    private fun scheduleBuildTask(startDelay: Long) {
        val plugin = SMPRPG.plugin
        val itemService = SMPRPG.getService(ItemService::class.java)
        buildTask = object : BukkitRunnable() {
            override fun run() {
                var processed = 0
                while (processed < ITEMS_PER_TICK && pending.isNotEmpty()) {
                    cacheItem(itemService, pending.removeFirst()())
                    processed++
                }

                if (pending.isEmpty()) {
                    state = State.READY
                    buildTask = null
                    cancel()
                    plugin.logger.info("Item browser registry finished indexing ${cache.size} items.")
                }
            }
        }.runTaskTimer(plugin, startDelay, TickTime.TICK)
    }

    /**
     * Bakes the browser tooltip into a generated item and indexes its searchable fields, then stores it in the cache.
     * This mirrors the lore/recipe work that used to happen on every page flip inside render(), but now only ever runs
     * a single time per item.
     */
    private fun cacheItem(itemService: ItemService, item: ItemStack) {
        val blueprint = itemService.getBlueprint(item)
        val renderedLore = itemService.renderItemStackLore(item)

        // Bake clickable hints: left-click to view how it's made, right-click to view what it's used in.
        // Both are O(1) set lookups against the per-build snapshot — no recipe resolution per item.
        val id = itemService.getIdentifier(item)
        val craftable = id in craftableResultIds
        val usable = id in usableIngredientIds
        if (craftable || usable) {
            val displayLore = ArrayList(renderedLore)
            displayLore.addFirst(ComponentUtils.EMPTY)
            if (usable) displayLore.addFirst(ComponentUtils.create("Right-click to view usages!", NamedTextColor.AQUA))
            if (craftable) displayLore.addFirst(ComponentUtils.create("Left-click to view recipe!", NamedTextColor.YELLOW))
            displayLore.addFirst(ComponentUtils.EMPTY)
            item.editMeta { meta -> meta.lore(displayLore) }
        }

        cache.add(buildSearchable(item, blueprint, renderedLore))
    }

    /**
     * Indexes an item's searchable fields (name, tooltip, rarity, classification) so that queries become simple
     * substring checks against pre-normalized strings.
     */
    private fun buildSearchable(
        item: ItemStack,
        blueprint: SMPItemBlueprint,
        renderedLore: List<Component?>
    ): SearchableItem {
        val name = blueprint.getItemName(item)
        val loreText = renderedLore.filterNotNull().joinToString("\n") { PLAIN_TEXT.serialize(it) }
        // The tooltip field includes the name so that '#' searches everything a player can read on the item.
        val tooltip = "$name\n$loreText"
        val rarity = blueprint.getRarity(item).name
        val classification = classificationKeywords(blueprint.itemClassification)

        val fields = mapOf(
            SearchField.NAME to SearchNormalizer.normalize(SearchField.NAME, name),
            SearchField.TOOLTIP to SearchNormalizer.normalize(SearchField.TOOLTIP, tooltip),
            SearchField.RARITY to SearchNormalizer.normalize(SearchField.RARITY, rarity),
            SearchField.CLASSIFICATION to SearchNormalizer.normalize(SearchField.CLASSIFICATION, classification)
        )
        return SearchableItem(item, fields)
    }

    /**
     * Builds the searchable keyword text for an item's classification. Beyond the specific category name (e.g.
     * "sword"), broad groupings ("weapon", "armor", "bow") are appended so a player can search by either.
     */
    private fun classificationKeywords(classification: ItemClassification): String {
        val keywords = StringBuilder(classification.name)
        if (classification.isWeapon) keywords.append(" weapon")
        if (classification.isArmor) keywords.append(" armor")
        if (classification.isBow) keywords.append(" bow")
        return keywords.toString()
    }
}
