package xyz.devvydont.smprpg.listeners.crafting

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Furnace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.FurnaceBurnEvent
import org.bukkit.event.inventory.FurnaceSmeltEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.inventory.FurnaceInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket
import org.bukkit.craftbukkit.entity.CraftPlayer
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineBlueprint
import xyz.devvydont.smprpg.items.interfaces.IFurnaceFuel
import xyz.devvydont.smprpg.recipe.core.RecipeStationType
import xyz.devvydont.smprpg.recipe.core.SmeltingCookType
import xyz.devvydont.smprpg.recipe.core.SmeltingRecipe
import xyz.devvydont.smprpg.recipe.furnace.FurnaceFuel
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.services.RecipeService
import xyz.devvydont.smprpg.util.extensions.transfer
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import kotlin.math.min

/**
 * Drives custom-item smelting on real furnace / blast furnace / smoker blocks.
 *
 * A vanilla furnace can only match recipes by material, so it can neither smelt a custom item that has no
 * vanilla recipe (nothing happens) nor avoid turning a custom item whose base material *does* have a vanilla
 * recipe into a vanilla result. This controller takes over: it suppresses the vanilla loop for custom inputs
 * (cancelling [FurnaceBurnEvent] and [FurnaceSmeltEvent]) and runs its own cook loop against the data-driven
 * [SmeltingRecipe] registry, producing results through the custom item blueprint framework.
 *
 * The real block, its native UI, and hopper automation are all retained. We keep the furnace lit by writing
 * its burn time (vanilla counts it down for us, so the flame animates), while tracking cook progress
 * ourselves. CraftEngine items are left alone — CraftEngine runs its own furnace recipes for those.
 */
class CustomFurnaceController : ToggleableListener() {

    /**
     * Cook progress, in ticks, for every furnace we are actively driving, keyed by block location.
     * Presence in the map marks a furnace as tracked; absence means "not ours to drive".
     */
    private val cookProgress: MutableMap<Location, Int> = HashMap()

    private var task: BukkitTask? = null

    override fun start() {
        super.start()
        if (task == null)
            task = Bukkit.getScheduler().runTaskTimer(SMPRPG.plugin, Runnable { tick() }, TICK_PERIOD, TICK_PERIOD)
    }

    override fun stop() {
        task?.cancel()
        task = null
        cookProgress.clear()
        super.stop()
    }

    private fun tick() {
        if (cookProgress.isEmpty()) return
        val viewers = collectFurnaceViewers()
        // Copy the key set so a furnace removing itself mid-iteration doesn't break the loop.
        for (location in cookProgress.keys.toList())
            processFurnace(location, viewers[location].orEmpty())
    }

    /**
     * Map each furnace location to the players currently viewing it, scanned once per tick. We read viewers
     * from live open inventories rather than a block-state snapshot, which doesn't reliably report them.
     */
    private fun collectFurnaceViewers(): Map<Location, List<Player>> {
        val viewers = HashMap<Location, MutableList<Player>>()
        for (player in Bukkit.getOnlinePlayers()) {
            val top = player.openInventory.topInventory as? FurnaceInventory ?: continue
            val location = (top.holder as? Furnace)?.location ?: continue
            viewers.getOrPut(location) { ArrayList() }.add(player)
        }
        return viewers
    }

    /**
     * Advance (or abandon) one tracked furnace by a single tick: validate it still holds a smeltable custom
     * input, keep it lit while fuel and output room allow, and finish the smelt when the timer elapses.
     */
    private fun processFurnace(location: Location, viewers: List<Player>) {
        val world = location.world
        if (world == null || !world.isChunkLoaded(location.blockX shr 4, location.blockZ shr 4))
            return  // Chunk unloaded; the unload handler prunes it, leave state until then.

        // A live (non-snapshot) state so our input/output/fuel writes apply directly to the world.
        val state = location.block.getState(false) as? Furnace ?: run { cookProgress.remove(location); return }
        val cookType = cookTypeFor(state.type) ?: run { cookProgress.remove(location); return }
        val inventory = state.inventory

        val input = inventory.smelting
        if (input == null || input.isEmpty || !isOurInput(input)) {
            cookProgress.remove(location)
            return
        }

        val recipe = findRecipe(input, cookType)
        if (recipe == null) {
            cookProgress.remove(location)
            return
        }

        val result = recipe.result.generate() ?: run { cookProgress.remove(location); return }
        if (!canAcceptResult(inventory.result, result)) {
            cookProgress[location] = 0  // Output blocked: stall progress but stay tracked.
            pushArrow(viewers, 0, recipe.time)
            return
        }

        var dirty = false

        // Keep the furnace lit. Vanilla decrements burn time each tick, so when it runs out we re-ignite
        // from the next fuel item ourselves (vanilla never ignites for a custom input).
        if (state.burnTime <= 0) {
            val burnTicks = FurnaceFuel.burnTicks(inventory.fuel)
            if (burnTicks <= 0) {
                cookProgress[location] = 0  // No fuel: pause until some is supplied.
                pushArrow(viewers, 0, recipe.time)
                return
            }
            consumeFuel(inventory)
            state.burnTime = burnTicks.toShort()
            dirty = true
        }

        val progress = (cookProgress[location] ?: 0) + COOK_STEP_PER_TICK
        if (progress >= recipe.time) {
            produceResult(inventory, result)
            consumeInput(inventory)
            cookProgress[location] = 0
            pushArrow(viewers, 0, recipe.time)
            dirty = true
        } else {
            cookProgress[location] = progress
            pushArrow(viewers, progress, recipe.time)
        }

        if (dirty)
            state.update(true, false)
    }

    /**
     * Drive the cook-progress arrow for everyone viewing the furnace by sending the container data packet
     * directly. We bypass the Bukkit `setProperty` path because it is keyed to the plain furnace inventory
     * type (so it no-ops for blast furnaces / smokers) and can write the tile-backed data slot that vanilla
     * re-zeroes each tick. Sending the packet ourselves works for every furnace variant: the vanilla loop
     * pins the tile's cook field at 0 for a recipe-less custom input and stops broadcasting it, so the value
     * we send is the one the client keeps. Progress is rescaled onto a fixed display total so any recipe time
     * renders a normal-looking arrow without overflowing the packet's field.
     */
    private fun pushArrow(viewers: List<Player>, progress: Int, total: Int) {
        if (viewers.isEmpty()) return
        val displayProgress =
            if (total <= 0) 0
            else (progress.toLong() * ARROW_DISPLAY_TOTAL / total).toInt().coerceIn(0, ARROW_DISPLAY_TOTAL)
        for (viewer in viewers) {
            val handle = (viewer as CraftPlayer).handle
            val containerId = handle.containerMenu.containerId
            handle.connection.send(ClientboundContainerSetDataPacket(containerId, COOK_PROGRESS_FIELD, displayProgress))
            handle.connection.send(ClientboundContainerSetDataPacket(containerId, COOK_TOTAL_FIELD, ARROW_DISPLAY_TOTAL))
        }
    }

    /**
     * The smelting recipe this furnace type can run for the given input, or null. Matching is type-level so
     * reforged / enchanted custom items still qualify, and the recipe's cook type must suit the block.
     */
    private fun findRecipe(input: ItemStack, cookType: SmeltingCookType): SmeltingRecipe? =
        SMPRPG.getService(RecipeService::class.java).getRegistry()
            .byStation(RecipeStationType.FURNACE)
            .filterIsInstance<SmeltingRecipe>()
            .firstOrNull { it.cook == cookType && it.input.matchesType(input) }

    /** Whether the output slot can take a full copy of the result (empty, or a matching non-full stack). */
    private fun canAcceptResult(current: ItemStack?, result: ItemStack): Boolean {
        if (current == null || current.isEmpty) return true
        if (!current.isSimilar(result)) return false
        return current.amount + result.amount <= current.maxStackSize
    }

    private fun produceResult(inventory: FurnaceInventory, result: ItemStack) {
        val current = inventory.result
        inventory.result =
            if (current == null || current.isEmpty) result.clone()
            else current.asQuantity(current.amount + result.amount)
    }

    private fun consumeInput(inventory: FurnaceInventory) {
        val input = inventory.smelting ?: return
        val remaining = input.amount - 1
        inventory.smelting = if (remaining <= 0) null else input.asQuantity(remaining)
    }

    /** Consume one fuel item, returning an empty bucket when a lava bucket is burnt, as vanilla does. */
    private fun consumeFuel(inventory: FurnaceInventory) {
        val fuel = inventory.fuel ?: return
        if (fuel.type == Material.LAVA_BUCKET && fuel.amount == 1) {
            inventory.fuel = ItemStack(Material.BUCKET)
            return
        }
        val remaining = fuel.amount - 1
        inventory.fuel = if (remaining <= 0) null else fuel.asQuantity(remaining)
    }

    // ---------------------------------------------------------------------------------------------------
    // Vanilla suppression — custom inputs must never be cooked by the vanilla furnace loop.
    // ---------------------------------------------------------------------------------------------------

    /** Stop the vanilla furnace from igniting (and thus cooking) when its input is one of ours. */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onFurnaceBurn(event: FurnaceBurnEvent) {
        val state = event.block.state as? Furnace ?: return
        val input = state.inventory.smelting ?: return
        if (!isOurInput(input)) return
        event.isCancelled = true
        track(state.location)
    }

    /**
     * Custom inputs are handled by our own cook loop, so the vanilla loop must never emit a result from one.
     * For a genuine vanilla smelt, run the result through the item pipeline so it becomes a plugin-standard
     * item (lore, rarity, etc.) instead of a bare vanilla stack.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onFurnaceSmelt(event: FurnaceSmeltEvent) {
        if (isOurInput(event.source)) {
            event.isCancelled = true
            return
        }
        val updated = SMPRPG.getService(ItemService::class.java).ensureItemStackUpdated(event.result)
        if (updated != null)
            event.result = updated
    }

    // ---------------------------------------------------------------------------------------------------
    // Discovery — make sure any furnace that receives a custom input gets picked up by the cook loop.
    // ---------------------------------------------------------------------------------------------------

    /**
     * Vanilla won't shift-click a custom item into the input slot (it isn't a recognised ingredient), so we
     * route it ourselves: smeltable inputs to the cook slot, custom fuels to the fuel slot.
     */
    @EventHandler
    @Suppress("unused")
    private fun onInventoryClick(event: InventoryClickEvent) {
        val furnace = event.view.topInventory as? FurnaceInventory ?: return
        val location = (furnace.holder as? Furnace)?.location ?: return

        if (event.isShiftClick && event.clickedInventory?.type == InventoryType.PLAYER) {
            val clicked = event.currentItem
            if (clicked != null && !clicked.isEmpty && isRoutable(clicked)) {
                event.isCancelled = true
                if (routeIntoFurnace(furnace, clicked))
                    trackLater(location)
                return
            }
        }
        // Any other interaction (manual placement, cursor drop) may have added an input; re-check next tick.
        trackLater(location)
    }

    /** Hoppers and droppers can feed custom inputs straight into the input slot; track the destination. */
    @EventHandler
    @Suppress("unused")
    private fun onInventoryMove(event: InventoryMoveItemEvent) {
        val furnace = event.destination as? FurnaceInventory ?: return
        val location = (furnace.holder as? Furnace)?.location ?: return
        trackLater(location)
    }

    /** Re-discover furnaces with a pending custom input when their chunk loads (e.g. after a restart). */
    @EventHandler
    @Suppress("unused")
    private fun onChunkLoad(event: ChunkLoadEvent) {
        for (state in event.chunk.getTileEntities(false)) {
            if (state !is Furnace) continue
            val input = state.inventory.smelting ?: continue
            if (!input.isEmpty && isOurInput(input))
                track(state.location)
        }
    }

    /** Drop tracking for a chunk's furnaces on unload; cook progress restarts when it loads again. */
    @EventHandler
    @Suppress("unused")
    private fun onChunkUnload(event: ChunkUnloadEvent) {
        val world = event.world
        val chunkX = event.chunk.x
        val chunkZ = event.chunk.z
        cookProgress.keys.removeIf {
            it.world == world && (it.blockX shr 4) == chunkX && (it.blockZ shr 4) == chunkZ
        }
    }

    // ---------------------------------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------------------------------

    /** A custom item the furnace can actually use, so a shift-click of it is ours to route. */
    private fun isRoutable(item: ItemStack): Boolean =
        (isOurInput(item) && hasAnyRecipe(item)) || blueprint(item) is IFurnaceFuel

    /**
     * Move a custom item from the player's clicked stack into the appropriate furnace slot. Smeltable inputs
     * go to the cook slot; custom fuels to the fuel slot. Returns whether anything was moved.
     */
    private fun routeIntoFurnace(inventory: FurnaceInventory, item: ItemStack): Boolean {
        val asInput = isOurInput(item) && hasAnyRecipe(item)
        val asFuel = blueprint(item) is IFurnaceFuel
        val target: ItemStack? = when {
            asInput -> inventory.smelting
            asFuel -> inventory.fuel
            else -> return false
        }

        if (target == null || target.isEmpty) {
            val moved = item.clone()
            item.amount = 0
            if (asInput) inventory.smelting = moved else inventory.fuel = moved
            return true
        }
        if (!target.isSimilar(item)) return false
        val space = target.maxStackSize - target.amount
        if (space <= 0) return false
        return item.transfer(min(space, item.amount), target)
    }

    /** A custom item we are responsible for (custom, but not a CraftEngine item — those cook via CraftEngine). */
    private fun isOurInput(item: ItemStack?): Boolean {
        if (item == null || item.isEmpty) return false
        val blueprint = blueprint(item)
        return blueprint.isCustom && blueprint !is CraftEngineBlueprint
    }

    /** Whether the item is the input of any registered smelting recipe (any cook type). */
    private fun hasAnyRecipe(item: ItemStack): Boolean =
        SMPRPG.getService(RecipeService::class.java).getRegistry()
            .byStation(RecipeStationType.FURNACE)
            .filterIsInstance<SmeltingRecipe>()
            .any { it.input.matchesType(item) }

    /** Begin tracking a furnace immediately if it holds one of our inputs. */
    private fun track(location: Location) {
        val state = location.block.state as? Furnace ?: return
        val input = state.inventory.smelting ?: return
        if (!input.isEmpty && isOurInput(input))
            cookProgress.putIfAbsent(location, 0)
    }

    /** Defer [track] by a tick so the inventory change that triggered it has settled. */
    private fun trackLater(location: Location) {
        Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable { track(location) }, 1L)
    }

    private fun cookTypeFor(material: Material): SmeltingCookType? = when (material) {
        Material.FURNACE -> SmeltingCookType.FURNACE
        Material.BLAST_FURNACE -> SmeltingCookType.BLASTING
        Material.SMOKER -> SmeltingCookType.SMOKING
        else -> null
    }

    companion object {
        /** How often the cook loop runs. Recipes author their time in ticks, so we step once per tick. */
        private const val TICK_PERIOD = 1L
        private const val COOK_STEP_PER_TICK = 1

        /** Display denominator for the cook arrow; progress is rescaled onto this so the arrow always fits. */
        private const val ARROW_DISPLAY_TOTAL = 200

        // Furnace container data-slot indices (litTime=0, litDuration=1, cookProgress=2, cookTotal=3).
        private const val COOK_PROGRESS_FIELD = 2
        private const val COOK_TOTAL_FIELD = 3
    }
}
