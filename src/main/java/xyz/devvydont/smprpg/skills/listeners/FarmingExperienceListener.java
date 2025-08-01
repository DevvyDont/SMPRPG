package xyz.devvydont.smprpg.skills.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.entity.player.LeveledPlayer;
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent;
import xyz.devvydont.smprpg.services.DropsService;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.skills.SkillInstance;
import xyz.devvydont.smprpg.util.world.ChunkUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FarmingExperienceListener implements Listener {

    final SMPRPG plugin;

    /**
     * Todo: tweak this a bit more per mob.
     * It would be interesting if certain mobs yielded more rewards, but we need to get an idea of how good this is.
     * @param type The type of entity that was bred.
     * @return The amount of experience to give out.
     */
    public static int getExperienceFromBreed(EntityType type) {
        return switch (type) {
            default -> 100;
        };
    }

    public static int getExperienceValue(ItemStack item) {

        int experience = switch (item.getType()) {

            case FLOWERING_AZALEA, FLOWERING_AZALEA_LEAVES, POPPY, ROSE_BUSH, TALL_GRASS, SHORT_GRASS, SEAGRASS,
                 TALL_SEAGRASS, DANDELION, BLUE_ORCHID, ALLIUM, AZURE_BLUET, RED_TULIP, ORANGE_TULIP, PINK_TULIP,
                 WHITE_TULIP, OXEYE_DAISY, CORNFLOWER, LILY_OF_THE_VALLEY, LILY_PAD, PINK_PETALS, LILAC, PEONY, KELP, KELP_PLANT -> 1;

            case PITCHER_PLANT, PITCHER_CROP, PITCHER_POD -> 2;

            case COCOA_BEANS -> 1;

            case GLOW_BERRIES, SWEET_BERRIES -> 3;

            case BROWN_MUSHROOM, RED_MUSHROOM, DEAD_BUSH -> 3;

            case SPORE_BLOSSOM -> 7;

            case BAMBOO -> 2;

            case BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK -> 21;

            case SEA_PICKLE -> 3;

            case CRIMSON_FUNGUS, WARPED_FUNGUS -> 4;

            case WITHER_ROSE -> 6;

            case MELON -> 5;
            case MELON_SLICE -> 1;
            case MELON_SEEDS -> 1;

            case PUMPKIN -> 5;
            case PUMPKIN_SEEDS -> 1;

            case BEETROOT, BEETROOTS -> 3;
            case BEETROOT_SEEDS -> 1;

            case TORCHFLOWER, TORCHFLOWER_CROP -> 7;
            case TORCHFLOWER_SEEDS -> 1;

            case WHEAT -> 4;
            case WHEAT_SEEDS -> 1;

            case CARROT, CARROTS, POTATO, POTATOES -> 3;

            case NETHER_WART -> 5;

            case SUGAR_CANE -> 2;
            case CACTUS -> 5;

            default -> 0;
        };

        return experience * item.getAmount();

    }

    public FarmingExperienceListener(SMPRPG plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHarvestCrop(PlayerHarvestBlockEvent event) {

        if (event.isCancelled())
            return;

        int exp = 0;
        for (ItemStack item : event.getItemsHarvested())
            exp += getExperienceValue(item);

        if (exp <= 0)
            return;

        LeveledPlayer player = SMPRPG.getService(EntityService.class).getPlayerInstance(event.getPlayer());
        player.getFarmingSkill().addExperience(exp, SkillExperienceGainEvent.ExperienceSource.HARVEST);
        var loc = event.getHarvestedBlock().getLocation();
        var expToDrop = Math.max(1, exp/10);
        loc.getWorld().spawn(loc, ExperienceOrb.class, orb -> orb.setExperience(expToDrop));
    }

    private int getExperienceForDrops(Collection<ItemStack> drops, World.Environment environment) {

        double multiplier = switch (environment) {
            case NETHER -> 1.2;
            case THE_END -> 1.5;
            default -> 1;
        };

        // Loop through every drop from breaking this block and award XP
        int exp = 0;
        for (ItemStack item : drops)
            exp += getExperienceValue(item);
        return (int) (exp * multiplier);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onHarvestBlock(BlockBreakEvent event) {

        boolean isAgeable = event.getBlock().getBlockData() instanceof Ageable ageable;

        // If this block is marked as skill invalid, we have some things we need to do.
        if (ChunkUtil.isBlockSkillInvalid(event.getBlock())) {
            // If this block does not have age states, we don't have to consider anything. past this point
            if (!isAgeable)
                return;
        }

        // Loop through every drop from breaking this block and award XP
        int exp = getExperienceForDrops(event.getBlock().getDrops(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer()), event.getPlayer().getWorld().getEnvironment());
        if (exp <= 0)
            return;

        // If the block is ageable don't give xp unless it is fully matured
        if (event.getBlock().getBlockData() instanceof Ageable ageable)
            if (ageable.getMaximumAge() != ageable.getAge())
                return;

        var player = SMPRPG.getService(EntityService.class).getPlayerInstance(event.getPlayer());
        player.getFarmingSkill().addExperience(exp, SkillExperienceGainEvent.ExperienceSource.HARVEST);
        var expToDrop = Math.max(1, exp/10);
        event.setExpToDrop(expToDrop);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onGrow(BlockGrowEvent event) {
        ChunkUtil.markBlockSkillValid(event.getBlock());
    }

    /**
     * When a structure grows, (like a tree), mark all blocks as skill valid.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onStructureGrow(StructureGrowEvent event) {
        for (var block : event.getBlocks())
            ChunkUtil.markBlockSkillValid(block.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPistonShove(BlockPistonExtendEvent event) {

        // Carry over the validity of blocks in the direction that the piston is extending.
        for (Block block : event.getBlocks()) {

            Block newPosition = block.getRelative(event.getDirection());

            // If the current block we are checking is not valid for skills, then carry it over to the position we are
            // extending to. Otherwise, we can just ignore
            if (ChunkUtil.isBlockSkillInvalid(block))
                ChunkUtil.markBlockSkillInvalid(newPosition);
        }

    }

    /**
     * Used to give experience to sugar cane/kelp/bamboo breaks. We completely override vanilla behavior
     * so we can properly hook into the "chain break" effect to give experience
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreakBambooSugarCane(BlockBreakEvent event) {

        Material blockType = event.getBlock().getType();

        // Listen to when sugar cane or bamboo is broken
        if (!(blockType.equals(Material.SUGAR_CANE) || blockType.equals(Material.BAMBOO) || blockType.equals(Material.KELP_PLANT) || blockType.equals(Material.CACTUS)))
            return;

        // We are going to manually break all the blocks.
        event.setCancelled(true);
        SkillInstance farming = SMPRPG.getService(EntityService.class).getPlayerInstance(event.getPlayer()).getFarmingSkill();
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();

        // Loop through every block above this and do the same a certain amount of ticks later
        for (int y = event.getBlock().getY(); y <= event.getBlock().getWorld().getMaxHeight(); y++) {

            int yOffset = y - event.getBlock().getY();

            Block block = event.getBlock().getRelative(BlockFace.UP, yOffset);
            // If this block doesn't match the original block, stop checking
            if (!block.getType().equals(blockType))
                return;

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                // Create the items that this block would drop if it were broken properly, and add loot tags.
                List<ItemStack> laterDrops = new ArrayList<>(block.getDrops(tool));
                var dropEntities = new ArrayList<Item>();
                for (ItemStack drop : laterDrops)
                    dropEntities.add(block.getWorld().dropItemNaturally(block.getLocation(), drop));

                // Call the event. If it's cancelled, we can stop.
                var newEvent = new BlockDropItemEvent(block, block.getState(), event.getPlayer(), new ArrayList<>(dropEntities));
                if (!newEvent.callEvent()) {
                    for (var item : dropEntities)
                        item.remove();
                    return;
                }

                // Check if any items ended up getting removed. They are not going to spawn in the world.
                for (var item : dropEntities)
                    if (!newEvent.getItems().contains(item))
                        item.remove();

                // This might seem strange, but it's necessary due to our hacky behavior.
                // We now need to remove the item flags from the items so they can be picked up and stack properly.
                for (var item : newEvent.getItems())
                    SMPRPG.getService(DropsService.class).removeAllTags(item.getItemStack());

                // Allow the event to happen. Give experience and delete the block.
                if (!ChunkUtil.isBlockSkillInvalid(block))
                    farming.addExperience(getExperienceForDrops(laterDrops, block.getWorld().getEnvironment()), SkillExperienceGainEvent.ExperienceSource.HARVEST);

                block.setType(Material.AIR, false);
                block.getWorld().playSound(block.getLocation(), block.getBlockSoundGroup().getBreakSound(), 1, 1);
                ChunkUtil.markBlockSkillValid(block);
            }, yOffset);
        }
    }

    /**
     * When a player breeds animals, we should give them farming experience.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreedAnimals(EntityBreedEvent event) {

        if (!(event.getBreeder() instanceof Player player))
            return;

        var playerWrapper = SMPRPG.getService(EntityService.class).getPlayerInstance(player);
        playerWrapper.getFarmingSkill().addExperience(getExperienceFromBreed(event.getEntityType()), SkillExperienceGainEvent.ExperienceSource.BREED);
    }

}
