package xyz.devvydont.smprpg.skills.listeners

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import net.momirealms.craftengine.core.util.Key
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.block.CraftEngineBlockEnums
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.craftengine.CraftEngineHelpers
import xyz.devvydont.smprpg.util.world.ChunkUtil

class ForagingExperienceListener : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    /**
     * When a player places a block, we need to mark that block as unable to be farmed for experience
     */
    @EventHandler(ignoreCancelled = true)
    @Suppress("unused")
    private fun onPlaceBlock(event: BlockPlaceEvent) {
        ChunkUtil.markBlockSkillInvalid(event.getBlock())
    }

    /**
     * Give players experience for breaking blocks, and mark that position as no longer being skill valid.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onGainGeneralWoodcuttingExperience(event: BlockBreakEvent) {
        // If this block isn't allowed to retrieve experience

        if (ChunkUtil.isBlockSkillInvalid(event.getBlock()))
            return

        val exp: Int = getBaseExperienceForBlock(event.getBlock())
        if (exp <= 0)
            return

        val skill = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.player).woodcuttingSkill
        skill.addExperience(exp, SkillExperienceGainEvent.ExperienceSource.WOODCUTTING)
        // event.expToDrop = 1
    }

    companion object {

        @JvmStatic
        fun getBaseExperienceForBlock(block: Block): Int {
            if (CraftEngineBlocks.isCustomBlock(block)) {
                val cb = CraftEngineHelpers.getBlockKey(block) ?: return 0
                return when (cb) {
                    CraftEngineBlockEnums.SKYROOT_LOG.key, CraftEngineBlockEnums.SKYROOT_WOOD.key -> 14
                    CraftEngineBlockEnums.GOLDEN_OAK_LOG.key, CraftEngineBlockEnums.GOLDEN_OAK_WOOD.key -> 16
                    CraftEngineBlockEnums.BINARY_LOG.key, CraftEngineBlockEnums.BINARY_WOOD.key -> 24
                    CraftEngineBlockEnums.SKYROOT_PLANKS.key, CraftEngineBlockEnums.GOLDEN_OAK_PLANKS.key -> 2
                    CraftEngineBlockEnums.BINARY_PLANKS.key -> 3
                    else -> 0
                }
            }
            val exp = when (block.type) {
                Material.CHORUS_FLOWER -> 20
                Material.CHORUS_PLANT -> 15
                Material.CRIMSON_STEM, Material.WARPED_STEM, Material.NETHER_WART_BLOCK, Material.WARPED_WART_BLOCK, Material.STRIPPED_CRIMSON_STEM, Material.STRIPPED_WARPED_STEM, Material.CRIMSON_HYPHAE, Material.WARPED_HYPHAE -> 14
                Material.CHERRY_LOG, Material.ACACIA_LOG, Material.MANGROVE_LOG, Material.MANGROVE_ROOTS, Material.PALE_OAK_LOG, Material.CHERRY_WOOD, Material.ACACIA_WOOD, Material.MANGROVE_WOOD, Material.PALE_OAK_WOOD -> 12
                Material.BIRCH_LOG, Material.OAK_LOG, Material.JUNGLE_LOG, Material.SPRUCE_LOG, Material.DARK_OAK_LOG, Material.BIRCH_WOOD, Material.OAK_WOOD, Material.JUNGLE_WOOD, Material.SPRUCE_WOOD, Material.DARK_OAK_WOOD -> 10
                Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_ACACIA_WOOD, Material.STRIPPED_BAMBOO_BLOCK, Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_BIRCH_WOOD, Material.STRIPPED_CHERRY_LOG, Material.STRIPPED_CHERRY_WOOD, Material.STRIPPED_CRIMSON_HYPHAE, Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_DARK_OAK_WOOD, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_JUNGLE_WOOD, Material.STRIPPED_MANGROVE_LOG, Material.STRIPPED_MANGROVE_WOOD, Material.STRIPPED_OAK_LOG, Material.STRIPPED_OAK_WOOD, Material.STRIPPED_SPRUCE_LOG, Material.STRIPPED_SPRUCE_WOOD, Material.STRIPPED_WARPED_HYPHAE, Material.STRIPPED_PALE_OAK_LOG, Material.STRIPPED_PALE_OAK_WOOD -> 5
                Material.ACACIA_PLANKS, Material.BAMBOO_PLANKS, Material.BIRCH_PLANKS, Material.CHERRY_PLANKS, Material.CRIMSON_PLANKS, Material.DARK_OAK_PLANKS, Material.JUNGLE_PLANKS, Material.MANGROVE_PLANKS, Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.WARPED_PLANKS -> 2
                else -> 0
            }

            return exp
        }
    }
}
