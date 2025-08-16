package xyz.devvydont.smprpg.skills.listeners

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.world.ChunkUtil
import kotlin.math.max

class MiningExperienceListener : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    /**
     * When a player places a block, we need to mark that block as unable to be farmed for experience
     *
     * @param event
     */
    @EventHandler
    @Suppress("unused")
    private fun onPlaceBlock(event: BlockPlaceEvent) {
        if (event.isCancelled)
            return

        // When any block is placed, it is no longer able to earn skill experience
        ChunkUtil.markBlockSkillInvalid(event.getBlock())
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onGainGeneralMiningExperience(event: BlockBreakEvent) {
        if (event.isCancelled)
            return

        // If this block isn't allowed to retrieve experience
        if (ChunkUtil.isBlockSkillInvalid(event.getBlock())) return

        val skill = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.player).miningSkill

        var exp = 0
        for (drop in event.getBlock().getDrops(event.player.inventory.itemInMainHand, event.player))
            exp += getBaseExperienceForDrop(drop, event.player.world.environment)
        if (exp <= 0)
            return

        event.expToDrop = max(1, exp / 10)
        skill.addExperience(exp, SkillExperienceGainEvent.ExperienceSource.ORE)
    }

    companion object {
        fun getBaseExperienceForDrop(item: ItemStack, environment: World.Environment): Int {
            val dimensionMultiplier =
                if (environment == World.Environment.THE_END) 1.5 else if (environment == World.Environment.NETHER) 1.2 else 1.0

            return (when (item.type) {
                Material.END_STONE, Material.STONE, Material.COBBLESTONE, Material.COBBLED_DEEPSLATE, Material.SAND, Material.RED_SAND, Material.SANDSTONE, Material.RED_SANDSTONE, Material.CLAY, Material.MYCELIUM, Material.GRASS_BLOCK, Material.DIRT, Material.GRAVEL, Material.DEEPSLATE, Material.TUFF, Material.NETHERRACK, Material.BLACKSTONE, Material.BASALT, Material.SMOOTH_BASALT, Material.CRIMSON_NYLIUM, Material.WARPED_NYLIUM, Material.FLINT -> 1
                Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.CALCITE, Material.BONE_BLOCK, Material.SOUL_SAND, Material.SOUL_SOIL, Material.ICE, Material.PACKED_ICE -> 2
                Material.SEA_LANTERN -> 10
                Material.PRISMARINE -> 2
                Material.PRISMARINE_BRICKS, Material.DARK_PRISMARINE -> 2
                Material.WET_SPONGE -> 25
                Material.BLUE_ICE -> 8
                Material.COAL_ORE, Material.COAL -> 5
                Material.DEEPSLATE_COAL_ORE -> 7
                Material.COPPER_ORE, Material.RAW_COPPER, Material.COPPER_INGOT -> 2
                Material.DEEPSLATE_COPPER_ORE, Material.RAW_COPPER_BLOCK -> 8
                Material.IRON_ORE, Material.RAW_IRON, Material.IRON_INGOT -> 7
                Material.RAW_IRON_BLOCK -> 8
                Material.IRON_BLOCK -> 9
                Material.DEEPSLATE_IRON_ORE -> 12
                Material.GOLD_ORE, Material.RAW_GOLD, Material.GOLD_INGOT -> 12
                Material.RAW_GOLD_BLOCK, Material.DEEPSLATE_GOLD_ORE -> 14
                Material.OBSIDIAN -> 20
                Material.CRYING_OBSIDIAN -> 24
                Material.AMETHYST_BLOCK -> 4
                Material.AMETHYST_SHARD -> 10
                Material.AMETHYST_CLUSTER -> 15
                Material.BUDDING_AMETHYST -> 10
                Material.LARGE_AMETHYST_BUD -> 15
                Material.MEDIUM_AMETHYST_BUD -> 20
                Material.SMALL_AMETHYST_BUD -> 25
                Material.LAPIS_LAZULI -> 3
                Material.REDSTONE -> 4
                Material.REDSTONE_BLOCK -> 15
                Material.LAPIS_ORE, Material.REDSTONE_ORE -> 18
                Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_REDSTONE_ORE -> 21
                Material.DIAMOND_ORE, Material.DIAMOND -> 25
                Material.DEEPSLATE_DIAMOND_ORE -> 30
                Material.EMERALD_ORE, Material.EMERALD -> 100
                Material.DEEPSLATE_EMERALD_ORE -> 120
                Material.GOLD_BLOCK -> 45
                Material.EMERALD_BLOCK -> 200
                Material.MAGMA_BLOCK -> 2
                Material.GOLD_NUGGET -> 5
                Material.NETHER_GOLD_ORE -> 22
                Material.GLOWSTONE_DUST -> 5
                Material.GLOWSTONE -> 15
                Material.NETHER_QUARTZ_ORE -> 25
                Material.QUARTZ -> 10
                Material.QUARTZ_BLOCK -> 15
                Material.ANCIENT_DEBRIS -> 200
                else -> 0
            } * dimensionMultiplier * item.amount).toInt()
        }
    }
}
