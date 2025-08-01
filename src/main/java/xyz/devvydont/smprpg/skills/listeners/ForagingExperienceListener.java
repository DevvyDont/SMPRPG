package xyz.devvydont.smprpg.skills.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.skills.SkillInstance;
import xyz.devvydont.smprpg.util.world.ChunkUtil;

public class ForagingExperienceListener implements Listener {

    public static int getBaseExperienceForBlock(Block block) {

        int exp = switch (block.getType()) {

            case CHORUS_FLOWER -> 20;
            case CHORUS_PLANT -> 15;

            case CRIMSON_STEM, WARPED_STEM, NETHER_WART_BLOCK, WARPED_WART_BLOCK, STRIPPED_CRIMSON_STEM,
                 STRIPPED_WARPED_STEM -> 14;

            case CHERRY_LOG, ACACIA_LOG, MANGROVE_LOG, MANGROVE_ROOTS, PALE_OAK_LOG -> 12;

            case BIRCH_LOG, OAK_LOG, JUNGLE_LOG, SPRUCE_LOG, DARK_OAK_LOG -> 10;
            case STRIPPED_ACACIA_LOG, STRIPPED_ACACIA_WOOD, STRIPPED_BAMBOO_BLOCK, STRIPPED_BIRCH_LOG, STRIPPED_BIRCH_WOOD, STRIPPED_CHERRY_LOG, STRIPPED_CHERRY_WOOD, STRIPPED_CRIMSON_HYPHAE, STRIPPED_DARK_OAK_LOG, STRIPPED_DARK_OAK_WOOD, STRIPPED_JUNGLE_LOG, STRIPPED_JUNGLE_WOOD, STRIPPED_MANGROVE_LOG, STRIPPED_MANGROVE_WOOD, STRIPPED_OAK_LOG, STRIPPED_OAK_WOOD, STRIPPED_SPRUCE_LOG, STRIPPED_SPRUCE_WOOD, STRIPPED_WARPED_HYPHAE, STRIPPED_PALE_OAK_LOG, STRIPPED_PALE_OAK_WOOD -> 5;
            case ACACIA_PLANKS, BAMBOO_PLANKS, BIRCH_PLANKS, CHERRY_PLANKS, CRIMSON_PLANKS, DARK_OAK_PLANKS, JUNGLE_PLANKS, MANGROVE_PLANKS, OAK_PLANKS, SPRUCE_PLANKS, WARPED_PLANKS -> 2;

            default -> 0;
        };

        return exp;
    }


    final SMPRPG plugin;

    public ForagingExperienceListener(SMPRPG plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * When a player places a block, we need to mark that block as unable to be farmed for experience
     */
    @EventHandler(ignoreCancelled = true)
    private void __onPlaceBlock(BlockPlaceEvent event) {
        ChunkUtil.markBlockSkillInvalid(event.getBlock());
    }

    /**
     * Give players experience for breaking blocks, and mark that position as no longer being skill valid.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void __onGainGeneralWoodcuttingExperience(BlockBreakEvent event) {

        // If this block isn't allowed to retrieve experience
        if (ChunkUtil.isBlockSkillInvalid(event.getBlock()))
            return;

        var exp = getBaseExperienceForBlock(event.getBlock());
        if (exp <= 0)
            return;

        SkillInstance skill = SMPRPG.getService(EntityService.class).getPlayerInstance(event.getPlayer()).getWoodcuttingSkill();
        skill.addExperience(exp, SkillExperienceGainEvent.ExperienceSource.WOODCUTTING);
        event.setExpToDrop(1);
    }

}
