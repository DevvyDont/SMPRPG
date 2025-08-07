package xyz.devvydont.smprpg.listeners.block;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent;
import xyz.devvydont.smprpg.services.AttributeService;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.skills.SkillInstance;
import xyz.devvydont.smprpg.skills.listeners.ForagingExperienceListener;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;
import xyz.devvydont.smprpg.util.time.TickTime;
import xyz.devvydont.smprpg.util.world.ChunkUtil;

import java.util.HashSet;
import java.util.Set;

public class MultiBlockBreakListener extends ToggleableListener {

    public static final Set<Material> MULTI_BREAK_LOGS = Set.of(
            Material.OAK_LOG,
            Material.BIRCH_LOG,
            Material.SPRUCE_LOG,
            Material.JUNGLE_LOG,
            Material.DARK_OAK_LOG,
            Material.ACACIA_LOG,
            Material.MANGROVE_LOG,
            Material.CHERRY_LOG,
            Material.PALE_OAK_LOG,
            Material.CRIMSON_STEM,
            Material.WARPED_STEM
    );

    private void stepAdjacentBlocks(Block startBlock, int blocksLeft, Set<Block> checkedBlocks, Player player, Set<Block> blocksToBreak) {
        if (blocksLeft <= 0) {
            return;
        }

        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    // Don't need to execute on center
                    if (x == 0 && y == 0 && z == 0)
                        continue;

                    Block adjBlock = startBlock.getRelative(x, y, z);

                    // Exit out if our block was already checked.
                    if (checkedBlocks.contains(adjBlock))
                        continue;
                    checkedBlocks.add(adjBlock);

                    // Exit out if this block is invalid for skill xp.
                    if (ChunkUtil.isBlockSkillInvalid(adjBlock))
                        continue;

                    // Check for material equivalency
                    if (adjBlock.getType() == startBlock.getType()) {
                        // Success! Reduce our blocks left to chop and delay our chop for a few ticks.
                        blocksLeft--;
                        blocksToBreak.add(adjBlock);
                        stepAdjacentBlocks(adjBlock, blocksLeft, checkedBlocks, player, blocksToBreak);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLogBreak(BlockBreakEvent event) {
        var attrInst = AttributeService.getInstance().getOrCreateAttribute(event.getPlayer(), AttributeWrapper.LUMBERING);
        Player player = event.getPlayer();
        Set<Block> checkedBlocks = new HashSet<>();
        Set<Block> blocksToBreak = new HashSet<>();
        blocksToBreak.add(event.getBlock());

        int numBlocksToBreak = (int) attrInst.getValue();
        if (MULTI_BREAK_LOGS.contains(event.getBlock().getType()))
            stepAdjacentBlocks(event.getBlock(), numBlocksToBreak, checkedBlocks, player, blocksToBreak);

        // Finished! Go through and schedule our breaks.
        int delay = 2;

        for (Block block : blocksToBreak) {
            Bukkit.getScheduler().runTaskLater(SMPRPG.getInstance(), () -> {
                var exp = ForagingExperienceListener.getBaseExperienceForBlock(block);
                if (exp <= 0)
                    return;
                block.breakNaturally(player.getEquipment().getItemInMainHand(), true, true);

                SkillInstance skill = SMPRPG.getService(EntityService.class).getPlayerInstance(player).getWoodcuttingSkill();
                skill.addExperience(exp, SkillExperienceGainEvent.ExperienceSource.WOODCUTTING);
            }, delay);
            delay += 1;
        }
    }

}
