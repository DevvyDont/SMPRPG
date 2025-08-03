package xyz.devvydont.smprpg.skills.listeners

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
import org.bukkit.entity.EntityType
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.entity.EntityBreedEvent
import org.bukkit.event.player.PlayerHarvestBlockEvent
import org.bukkit.event.world.StructureGrowEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.services.DropsService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.world.ChunkUtil
import java.util.function.Consumer
import kotlin.math.max

class FarmingExperienceListener : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onHarvestCrop(event: PlayerHarvestBlockEvent) {
        if (event.isCancelled)
            return

        var exp = 0
        for (item in event.itemsHarvested)
            exp += getExperienceValue(item)

        if (exp <= 0)
            return

        val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.getPlayer())
        player.farmingSkill.addExperience(exp, SkillExperienceGainEvent.ExperienceSource.HARVEST)
        val loc = event.harvestedBlock.location
        val expToDrop = max(1, exp / 10)
        loc.getWorld().spawn(
            loc,
            ExperienceOrb::class.java,
            Consumer { orb: ExperienceOrb -> orb.experience = expToDrop })
    }

    private fun getExperienceForDrops(drops: MutableCollection<ItemStack>, environment: World.Environment): Int {
        val multiplier: Double = when (environment) {
            World.Environment.NETHER -> 1.2
            World.Environment.THE_END -> 1.5
            else -> 1.0
        }

        // Loop through every drop from breaking this block and award XP
        var exp = 0
        for (item in drops)
            exp += getExperienceValue(item)
        return (exp * multiplier).toInt()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onHarvestBlock(event: BlockBreakEvent) {
        val isAgeable = event.getBlock().blockData is Ageable

        // If this block is marked as skill invalid, we have some things we need to do.
        if (ChunkUtil.isBlockSkillInvalid(event.getBlock()))
            // If this block does not have age states, we don't have to consider anything. past this point
            if (!isAgeable)
                return
        
        // Loop through every drop from breaking this block and award XP
        val exp = getExperienceForDrops(
            event.getBlock().getDrops(event.player.inventory.itemInMainHand, event.player),
            event.player.world.environment
        )
        if (exp <= 0)
            return

        // If the block is ageable don't give xp unless it is fully matured
        if (event.getBlock().blockData is Ageable) {
            val ageable = event.block.blockData as Ageable
            if (ageable.maximumAge != ageable.age)
                return
        }

        val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.player)
        player.farmingSkill.addExperience(exp, SkillExperienceGainEvent.ExperienceSource.HARVEST)
        val expToDrop = max(1, exp / 10)
        event.expToDrop = expToDrop
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onGrow(event: BlockGrowEvent) {
        ChunkUtil.markBlockSkillValid(event.getBlock())
    }

    /**
     * When a structure grows, (like a tree), mark all blocks as skill valid.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onStructureGrow(event: StructureGrowEvent) {
        for (block in event.blocks) ChunkUtil.markBlockSkillValid(block.block)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onPistonShove(event: BlockPistonExtendEvent) {
        // Carry over the validity of blocks in the direction that the piston is extending.

        for (block in event.getBlocks()) {
            val newPosition = block.getRelative(event.direction)
            // If the current block we are checking is not valid for skills, then carry it over to the position we are
            // extending to. Otherwise, we can just ignore
            if (ChunkUtil.isBlockSkillInvalid(block)) ChunkUtil.markBlockSkillInvalid(newPosition)
        }
    }

    /**
     * Used to give experience to sugar cane/kelp/bamboo breaks. We completely override vanilla behavior
     * so we can properly hook into the "chain break" effect to give experience
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onBreakBambooSugarCane(event: BlockBreakEvent) {
        val blockType = event.getBlock().type

        // Listen to when sugar cane or bamboo is broken
        if (!(blockType == Material.SUGAR_CANE || blockType == Material.BAMBOO || blockType == Material.KELP_PLANT || blockType == Material.CACTUS))
            return

        // We are going to manually break all the blocks.
        event.isCancelled = true
        val farming = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.player).farmingSkill
        val tool = event.player.inventory.itemInMainHand

        // Loop through every block above this and do the same a certain amount of ticks later
        for (y in event.getBlock().y..event.getBlock().world.maxHeight) {
            val yOffset = y - event.getBlock().y

            val block = event.getBlock().getRelative(BlockFace.UP, yOffset)
            // If this block doesn't match the original block, stop checking
            if (block.type != blockType) return

            plugin.server.scheduler.runTaskLater(plugin, Runnable runTaskLater@{
                // Create the items that this block would drop if it were broken properly, and add loot tags.
                val laterDrops: MutableList<ItemStack> = ArrayList<ItemStack>(block.getDrops(tool))
                val dropEntities = ArrayList<Item>()
                for (drop in laterDrops) dropEntities.add(block.world.dropItemNaturally(block.location, drop))

                // Call the event. If it's cancelled, we can stop.
                val newEvent =
                    BlockDropItemEvent(block, block.state, event.player, ArrayList<Item?>(dropEntities))
                if (!newEvent.callEvent()) {
                    for (item in dropEntities) item.remove()
                    return@runTaskLater
                }

                // Check if any items ended up getting removed. They are not going to spawn in the world.
                for (item in dropEntities) if (!newEvent.items.contains(item)) item.remove()

                // This might seem strange, but it's necessary due to our hacky behavior.
                // We now need to remove the item flags from the items so they can be picked up and stack properly.
                for (item in newEvent.items) SMPRPG.getService(DropsService::class.java)
                    .removeAllTags(item.itemStack)

                // Allow the event to happen. Give experience and delete the block.
                if (!ChunkUtil.isBlockSkillInvalid(block)) farming.addExperience(
                    getExperienceForDrops(
                        laterDrops,
                        block.world.environment
                    ), SkillExperienceGainEvent.ExperienceSource.HARVEST
                )

                block.setType(Material.AIR, false)
                block.world.playSound(block.location, block.blockSoundGroup.breakSound, 1f, 1f)
                ChunkUtil.markBlockSkillValid(block)
            }, yOffset.toLong())
        }
    }

    /**
     * When a player breeds animals, we should give them farming experience.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onBreedAnimals(event: EntityBreedEvent) {
        if (event.breeder !is Player)
            return
        val player = event.breeder as Player

        val playerWrapper = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        playerWrapper.farmingSkill.addExperience(
            getExperienceFromBreed(event.entityType),
            SkillExperienceGainEvent.ExperienceSource.BREED
        )
    }

    companion object {
        /**
         * Todo: tweak this a bit more per mob.
         * It would be interesting if certain mobs yielded more rewards, but we need to get an idea of how good this is.
         * @param type The type of entity that was bred.
         * @return The amount of experience to give out.
         */
        fun getExperienceFromBreed(type: EntityType): Int {
            return when (type) {
                EntityType.FROG -> 120
                EntityType.PANDA -> 75
                else -> 50
            }
        }

        fun getExperienceValue(item: ItemStack): Int {
            val experience = when (item.type) {
                Material.FLOWERING_AZALEA, Material.FLOWERING_AZALEA_LEAVES, Material.POPPY, Material.ROSE_BUSH, Material.TALL_GRASS, Material.SHORT_GRASS, Material.SEAGRASS, Material.TALL_SEAGRASS, Material.DANDELION, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET, Material.RED_TULIP, Material.ORANGE_TULIP, Material.PINK_TULIP, Material.WHITE_TULIP, Material.OXEYE_DAISY, Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY, Material.LILY_PAD, Material.PINK_PETALS, Material.LILAC, Material.PEONY, Material.KELP, Material.KELP_PLANT -> 1
                Material.PITCHER_PLANT, Material.PITCHER_CROP, Material.PITCHER_POD -> 2
                Material.COCOA_BEANS -> 1
                Material.GLOW_BERRIES, Material.SWEET_BERRIES -> 3
                Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.DEAD_BUSH -> 3
                Material.SPORE_BLOSSOM -> 7
                Material.BAMBOO -> 2
                Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK -> 21
                Material.SEA_PICKLE -> 3
                Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS -> 4
                Material.WITHER_ROSE -> 6
                Material.MELON -> 5
                Material.MELON_SLICE -> 1
                Material.MELON_SEEDS -> 1
                Material.PUMPKIN -> 5
                Material.PUMPKIN_SEEDS -> 1
                Material.BEETROOT, Material.BEETROOTS -> 3
                Material.BEETROOT_SEEDS -> 1
                Material.TORCHFLOWER, Material.TORCHFLOWER_CROP -> 7
                Material.TORCHFLOWER_SEEDS -> 1
                Material.WHEAT -> 4
                Material.WHEAT_SEEDS -> 1
                Material.CARROT, Material.CARROTS, Material.POTATO, Material.POTATOES -> 3
                Material.NETHER_WART -> 5
                Material.SUGAR_CANE -> 2
                Material.CACTUS -> 5
                else -> 0
            }

            return experience * item.amount
        }
    }
}
