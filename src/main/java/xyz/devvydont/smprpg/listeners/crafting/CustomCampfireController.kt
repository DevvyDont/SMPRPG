package xyz.devvydont.smprpg.listeners.crafting

import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.block.Campfire
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockCookEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import java.util.UUID
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineBlueprint
import xyz.devvydont.smprpg.items.blueprints.fishing.FishBlueprint
import xyz.devvydont.smprpg.recipe.campfire.FishTeardown
import xyz.devvydont.smprpg.recipe.core.RecipeRewards
import xyz.devvydont.smprpg.recipe.core.RecipeStationType
import xyz.devvydont.smprpg.recipe.core.SmeltingCookType
import xyz.devvydont.smprpg.recipe.core.SmeltingRecipe
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.services.RecipeService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * Drives custom-item cooking on real campfire / soul campfire blocks.
 *
 * Vanilla won't place a custom item on a campfire (no vanilla cooking recipe for it), so this controller
 * intercepts the right-click, drops the item into a free cook slot itself, and runs its own per-slot cook
 * loop against two recipe sources: data-driven [SmeltingRecipe]s with `cook: campfire` (e.g. latticed
 * xenomatter) and fish-essence teardown ([FishTeardown]). When a slot finishes, the custom result pops off
 * as a dropped item, mirroring vanilla campfire behavior.
 *
 * Vanilla's own cook timer for the slot is parked at a sentinel so it never produces or drops the raw item;
 * we own completion. None of the current custom inputs collide with a vanilla campfire recipe, but as a
 * safeguard the vanilla cook event is cancelled for custom sources. CraftEngine items are left alone.
 */
class CustomCampfireController : ToggleableListener() {

    /** Per-slot cook progress (ticks) for every campfire we are driving, keyed by block location. */
    private val cookProgress: MutableMap<Location, IntArray> = HashMap()

    /** Per-slot UUID of the player who placed each cooking item, so completion rewards reach them. */
    private val placers: MutableMap<Location, Array<UUID?>> = HashMap()

    private var task: BukkitTask? = null

    private data class CampfireResult(val result: ItemStack, val cookTime: Int, val rewards: RecipeRewards)

    override fun start() {
        super.start()
        if (task == null)
            task = Bukkit.getScheduler().runTaskTimer(SMPRPG.plugin, Runnable { tick() }, TICK_PERIOD, TICK_PERIOD)
    }

    override fun stop() {
        task?.cancel()
        task = null
        cookProgress.clear()
        placers.clear()
        super.stop()
    }

    private fun tick() {
        if (cookProgress.isEmpty()) return
        for (location in cookProgress.keys.toList())
            processCampfire(location)
    }

    /** Advance every cooking slot of one campfire by a tick, completing and dropping any that finish. */
    private fun processCampfire(location: Location) {
        val world = location.world
        if (world == null || !world.isChunkLoaded(location.blockX shr 4, location.blockZ shr 4))
            return  // Chunk unloaded; the unload handler prunes it.

        val campfire = location.block.getState(false) as? Campfire ?: run { cookProgress.remove(location); return }
        val progress = cookProgress[location] ?: return
        var anyActive = false
        var dirty = false

        for (slot in 0 until campfire.size) {
            val item = campfire.getItem(slot)
            if (item == null || item.isEmpty || !isOurInput(item)) {
                progress[slot] = 0
                continue
            }
            val recipe = resolveResult(item)
            if (recipe == null) {
                progress[slot] = 0
                continue
            }
            anyActive = true

            // Keep vanilla from ever popping our item itself; we drive completion.
            if (campfire.getCookTimeTotal(slot) < VANILLA_COOK_BLOCK_TICKS) {
                campfire.setCookTimeTotal(slot, VANILLA_COOK_BLOCK_TICKS)
                dirty = true
            }

            val advanced = progress[slot] + COOK_STEP_PER_TICK
            if (advanced >= recipe.cookTime) {
                dropResult(world, location, recipe.result)
                campfire.setItem(slot, null)
                campfire.setCookTime(slot, 0)
                progress[slot] = 0
                spawnCookEffects(world, location)
                grantRewards(location, slot, recipe.rewards)
                dirty = true
            } else {
                progress[slot] = advanced
            }
        }

        if (dirty) campfire.update(true, false)
        if (!anyActive) cookProgress.remove(location)
    }

    /** The campfire result for an item: a `cook: campfire` registry recipe, then fish teardown, else null. */
    private fun resolveResult(item: ItemStack): CampfireResult? {
        val registryRecipe = SMPRPG.getService(RecipeService::class.java).getRegistry()
            .byStation(RecipeStationType.FURNACE)
            .filterIsInstance<SmeltingRecipe>()
            .firstOrNull { it.cook == SmeltingCookType.CAMPFIRE && it.input.matchesType(item) }
        if (registryRecipe != null) {
            val result = registryRecipe.result.generate() ?: return null
            return CampfireResult(result, registryRecipe.time, registryRecipe.rewards)
        }

        val blueprint = blueprint(item)
        if (blueprint is FishBlueprint) {
            val rarity = blueprint.getRarity(item)
            // Fish teardown is dynamic (not a registry recipe), so it carries no configurable rewards.
            return CampfireResult(ItemService.generate(FishTeardown.essenceFor(rarity)), FishTeardown.cookTimeTicks(rarity), RecipeRewards())
        }
        return null
    }

    // ---------------------------------------------------------------------------------------------------
    // Placement & rules
    // ---------------------------------------------------------------------------------------------------

    /** Place a custom campfire input into a free slot ourselves, since vanilla refuses to. */
    @EventHandler(priority = EventPriority.LOW)
    @Suppress("unused")
    private fun onInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        val block = event.clickedBlock ?: return
        val soul = when (block.type) {
            Material.CAMPFIRE -> false
            Material.SOUL_CAMPFIRE -> true
            else -> return
        }
        val handSlot = event.hand ?: return
        val item = event.item ?: return
        if (item.isEmpty || !isOurInput(item)) return

        // Fish need a soul campfire above a certain rarity; reject (with flavor) on a normal one.
        val blueprint = blueprint(item)
        if (blueprint is FishBlueprint && FishTeardown.requiresSoulCampfire(blueprint.getRarity(item)) && !soul) {
            event.isCancelled = true
            rejectSoulFish(event.player, block.location)
            return
        }

        if (resolveResult(item) == null) return  // Not a campfire input; let vanilla handle it normally.

        val campfire = block.getState(false) as? Campfire ?: return
        val slot = (0 until campfire.size).firstOrNull {
            val existing = campfire.getItem(it); existing == null || existing.isEmpty
        } ?: return  // All cook slots busy.

        event.isCancelled = true
        campfire.setItem(slot, item.asQuantity(1))
        campfire.setCookTime(slot, 0)
        campfire.setCookTimeTotal(slot, VANILLA_COOK_BLOCK_TICKS)
        campfire.update(true, false)

        consumeFromHand(event.player, handSlot)
        track(block.location)
        placers.getOrPut(block.location) { arrayOfNulls(CAMPFIRE_SLOTS) }[slot] = event.player.uniqueId
        block.world.playSound(block.location, Sound.BLOCK_CAMPFIRE_CRACKLE, 1f, 1f)
        if (handSlot == EquipmentSlot.HAND) event.player.swingMainHand() else event.player.swingOffHand()
    }

    private fun rejectSoulFish(player: Player, location: Location) {
        val phrase = SOUL_REQUIRED_MESSAGES[(Math.random() * SOUL_REQUIRED_MESSAGES.size).toInt()]
        player.sendMessage(ComponentUtils.error(phrase))
        player.world.playSound(location, Sound.BLOCK_SOUL_SAND_BREAK, 1f, 1.5f)
        ParticleBuilder(Particle.SOUL_FIRE_FLAME)
            .location(location.toCenterLocation())
            .count(SOUL_PARTICLE_COUNT)
            .offset(0.2, 0.1, 0.2)
            .spawn()
    }

    // ---------------------------------------------------------------------------------------------------
    // Vanilla suppression & vanilla-output conversion
    // ---------------------------------------------------------------------------------------------------

    /**
     * Custom inputs are handled by our own cook loop, so the vanilla loop must never emit a result from one.
     * For a genuine vanilla campfire cook, run the result through the item pipeline so it becomes a
     * plugin-standard item, matching the furnace behavior.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onBlockCook(event: BlockCookEvent) {
        if (event.block.type != Material.CAMPFIRE && event.block.type != Material.SOUL_CAMPFIRE) return
        if (isOurInput(event.source)) {
            event.isCancelled = true
            return
        }
        val updated = SMPRPG.getService(ItemService::class.java).ensureItemStackUpdated(event.result)
        if (updated != null) event.result = updated
    }

    // ---------------------------------------------------------------------------------------------------
    // Discovery
    // ---------------------------------------------------------------------------------------------------

    /** Re-track campfires holding our items when their chunk loads (e.g. after a restart). */
    @EventHandler
    @Suppress("unused")
    private fun onChunkLoad(event: ChunkLoadEvent) {
        for (state in event.chunk.getTileEntities(false)) {
            if (state !is Campfire) continue
            var hasOurItem = false
            var dirty = false
            for (slot in 0 until state.size) {
                val item = state.getItem(slot) ?: continue
                if (item.isEmpty || !isOurInput(item) || resolveResult(item) == null) continue
                hasOurItem = true
                if (state.getCookTimeTotal(slot) < VANILLA_COOK_BLOCK_TICKS) {
                    state.setCookTimeTotal(slot, VANILLA_COOK_BLOCK_TICKS)
                    dirty = true
                }
            }
            if (dirty) state.update(true, false)
            if (hasOurItem) track(state.location)
        }
    }

    /** Drop tracking for a chunk's campfires on unload; cook progress restarts when it loads again. */
    @EventHandler
    @Suppress("unused")
    private fun onChunkUnload(event: ChunkUnloadEvent) {
        val world = event.world
        val chunkX = event.chunk.x
        val chunkZ = event.chunk.z
        val inChunk = { loc: Location -> loc.world == world && (loc.blockX shr 4) == chunkX && (loc.blockZ shr 4) == chunkZ }
        cookProgress.keys.removeIf(inChunk)
        placers.keys.removeIf(inChunk)
    }

    // ---------------------------------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------------------------------

    private fun consumeFromHand(player: Player, hand: EquipmentSlot) {
        val current = player.inventory.getItem(hand)
        if (current == null || current.isEmpty) return
        val remaining = current.amount - 1
        player.inventory.setItem(hand, if (remaining <= 0) null else current.asQuantity(remaining))
    }

    private fun dropResult(world: World, location: Location, result: ItemStack) {
        val drop = location.toCenterLocation().add(0.0, DROP_HEIGHT, 0.0)
        world.dropItem(drop, result) { it.velocity = Vector(0.0, DROP_UPWARD_VELOCITY, 0.0) }
    }

    /** Grant a completed slot's rewards to the player who placed it (if still online), then clear the placer. */
    private fun grantRewards(location: Location, slot: Int, rewards: RecipeRewards) {
        val placerId = placers[location]?.getOrNull(slot)
        placers[location]?.set(slot, null)
        if (rewards.isEmpty || placerId == null) return
        val placer = Bukkit.getPlayer(placerId) ?: return
        rewards.grant(placer, SkillExperienceGainEvent.ExperienceSource.COOK)
    }

    private fun spawnCookEffects(world: World, location: Location) {
        world.playSound(location, Sound.BLOCK_CAMPFIRE_CRACKLE, 1f, 1.2f)
        ParticleBuilder(Particle.SMOKE)
            .location(location.toCenterLocation().add(0.0, DROP_HEIGHT, 0.0))
            .count(COOK_PARTICLE_COUNT)
            .offset(0.1, 0.1, 0.1)
            .spawn()
    }

    /** A custom item we are responsible for (custom, but not a CraftEngine item, which CraftEngine cooks). */
    private fun isOurInput(item: ItemStack?): Boolean {
        if (item == null || item.isEmpty) return false
        val blueprint = blueprint(item)
        return blueprint.isCustom && blueprint !is CraftEngineBlueprint
    }

    private fun track(location: Location) {
        cookProgress.putIfAbsent(location, IntArray(CAMPFIRE_SLOTS))
    }

    companion object {
        /** How often the cook loop runs. Recipes author their time in ticks, so we step once per tick. */
        private const val TICK_PERIOD = 1L
        private const val COOK_STEP_PER_TICK = 1

        /** Number of cook slots on a vanilla campfire. */
        private const val CAMPFIRE_SLOTS = 4

        /** Parked vanilla cook total so vanilla never finishes (and pops) a slot we are driving ourselves. */
        private const val VANILLA_COOK_BLOCK_TICKS = Int.MAX_VALUE

        private const val DROP_HEIGHT = 0.5
        private const val DROP_UPWARD_VELOCITY = 0.2
        private const val COOK_PARTICLE_COUNT = 5
        private const val SOUL_PARTICLE_COUNT = 10

        private val SOUL_REQUIRED_MESSAGES: Array<String> = arrayOf(
            "This flame rejects your fishy friend.",
            "This flame lacks the whisper of souls.",
            "Only a flame touched by the beyond can unbind essence.",
            "The embers know nothing of the deep.",
            "The essence remains bound - The flame is wrong.",
            "This fire burns, but it does not understand.",
            "This flame does not resonate with the tune of its essence."
        )
    }
}
