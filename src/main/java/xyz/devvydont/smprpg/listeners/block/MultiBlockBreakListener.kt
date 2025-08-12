package xyz.devvydont.smprpg.listeners.block

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.server.ServerLoadEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.skills.listeners.ForagingExperienceListener.Companion.getBaseExperienceForBlock
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.world.ChunkUtil

class MultiBlockBreakListener : ToggleableListener() {
    private fun stepAdjacentBlocks(
        startBlock: Block,
        blocksLeft: Int,
        checkedBlocks: MutableSet<Block?>,
        player: Player,
        blocksToBreak: MutableSet<Block>
    ) {
        var blocksLeft = blocksLeft
        if (blocksLeft <= 0) {
            return
        }

        for (y in -1..1) {
            for (x in -1..1) {
                for (z in -1..1) {
                    // Don't need to execute on center
                    if (x == 0 && y == 0 && z == 0) continue

                    val adjBlock = startBlock.getRelative(x, y, z)

                    // Exit out if our block was already checked.
                    if (checkedBlocks.contains(adjBlock)) continue
                    checkedBlocks.add(adjBlock)

                    // Exit out if this block is invalid for skill xp.
                    if (ChunkUtil.isBlockSkillInvalid(adjBlock)) continue

                    // Check for material validity
                    // Check for equality first, then go through fuzzy blocks to improve performance.
                    val startType = startBlock.type
                    val fuzzies: MutableSet<Material> = FUZZY_BLOCKS.getOrDefault(startType, DEFAULT_FUZZY_BLOCK_SET)
                    if (adjBlock.type == startBlock.type || fuzzies.contains(adjBlock.type)) {
                        // Success! Reduce our blocks left to chop and delay our chop for a few ticks.
                        blocksLeft--
                        blocksToBreak.add(adjBlock)
                        stepAdjacentBlocks(adjBlock, blocksLeft, checkedBlocks, player, blocksToBreak)
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onLogBreak(event: BlockBreakEvent) {
        val attrInst = instance.getOrCreateAttribute(event.player, AttributeWrapper.LUMBERING)
        val player = event.player
        val checkedBlocks: MutableSet<Block?> = HashSet<Block?>()
        val blocksToBreak: MutableSet<Block> = HashSet<Block>()
        blocksToBreak.add(event.getBlock())

        val numBlocksToBreak = attrInst.getValue().toInt()
        if (MULTI_BREAK_LOGS.contains(event.getBlock().type)) stepAdjacentBlocks(
            event.getBlock(),
            numBlocksToBreak,
            checkedBlocks,
            player,
            blocksToBreak
        )

        // Finished! Go through and schedule our breaks.
        var delay = 2

        for (block in blocksToBreak) {
            Bukkit.getScheduler().runTaskLater(plugin, Runnable runTaskLater@{
                val exp = getBaseExperienceForBlock(block)
                if (exp <= 0) return@runTaskLater
                block.breakNaturally(player.equipment.itemInMainHand, true, true)

                val skill = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
                    .woodcuttingSkill
                skill.addExperience(exp, SkillExperienceGainEvent.ExperienceSource.WOODCUTTING)
            }, delay.toLong())
            delay += 1
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    @Suppress("unused")
    fun registerFuzzyMaterials(event: ServerLoadEvent) {
        initFuzzyBlocks()
    }

    companion object {
        val MULTI_BREAK_LOGS: MutableSet<Material> = mutableSetOf<Material>(
            Material.OAK_LOG,
            Material.OAK_WOOD,
            Material.BIRCH_LOG,
            Material.BIRCH_WOOD,
            Material.SPRUCE_LOG,
            Material.SPRUCE_WOOD,
            Material.JUNGLE_LOG,
            Material.JUNGLE_WOOD,
            Material.DARK_OAK_LOG,
            Material.DARK_OAK_WOOD,
            Material.ACACIA_LOG,
            Material.ACACIA_WOOD,
            Material.MANGROVE_LOG,
            Material.MANGROVE_WOOD,
            Material.CHERRY_LOG,
            Material.CHERRY_WOOD,
            Material.PALE_OAK_LOG,
            Material.PALE_OAK_WOOD,
            Material.CRIMSON_STEM,
            Material.CRIMSON_HYPHAE,
            Material.WARPED_STEM,
            Material.WARPED_HYPHAE
        )

        val FUZZY_BLOCKS: HashMap<Material, MutableSet<Material>> = HashMap<Material, MutableSet<Material>>()
        val DEFAULT_FUZZY_BLOCK_SET: MutableSet<Material> = mutableSetOf<Material>()

        fun initFuzzyBlocks() {
            FUZZY_BLOCKS.put(Material.OAK_LOG, mutableSetOf(Material.OAK_LOG, Material.OAK_WOOD))
            FUZZY_BLOCKS.put(Material.OAK_WOOD, mutableSetOf(Material.OAK_LOG, Material.OAK_WOOD))
            FUZZY_BLOCKS.put(Material.BIRCH_LOG, mutableSetOf(Material.BIRCH_LOG, Material.BIRCH_WOOD))
            FUZZY_BLOCKS.put(Material.BIRCH_WOOD, mutableSetOf(Material.BIRCH_LOG, Material.BIRCH_WOOD))
            FUZZY_BLOCKS.put(Material.SPRUCE_LOG, mutableSetOf(Material.SPRUCE_LOG, Material.SPRUCE_WOOD))
            FUZZY_BLOCKS.put(Material.SPRUCE_WOOD, mutableSetOf(Material.SPRUCE_LOG, Material.SPRUCE_WOOD))
            FUZZY_BLOCKS.put(Material.JUNGLE_LOG, mutableSetOf(Material.JUNGLE_LOG, Material.JUNGLE_WOOD))
            FUZZY_BLOCKS.put(Material.JUNGLE_WOOD, mutableSetOf(Material.JUNGLE_LOG, Material.JUNGLE_WOOD))
        }
    }
}
