package xyz.devvydont.smprpg.ability.handlers;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import xyz.devvydont.smprpg.ability.AbilityContext;
import xyz.devvydont.smprpg.ability.AbilityHandler;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Set;

public class InstantTransmissionAbilityHandler implements AbilityHandler {

    /**
     * How far to teleport.
     */
    public static final int TELEPORT_DISTANCE = 8;

    /**
     * How close to prevent teleporting. (Touching a wall, interacting with stuff right in front of us, etc.)
     */
    public static final int INVALID_DISTANCE_THRESHOLD = 2;

    // Items that will force the teleportation event to not fire in favor of interacting with the block.
    // Not ideal, but Material#isInteractable() is deprecated >_<
    public static final Set<Material> INTERACTABLE_BLOCK_BLACKLIST = Set.of(
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.BARREL,
            Material.ENDER_CHEST,
            Material.CRAFTING_TABLE,
            Material.FURNACE,
            Material.BLAST_FURNACE,
            Material.SMOKER,
            Material.ANVIL,
            Material.ENCHANTING_TABLE,
            Material.GRINDSTONE,
            Material.LECTERN,
            Material.SMITHING_TABLE,
            Material.STONECUTTER,
            Material.CARTOGRAPHY_TABLE,
            Material.LOOM,
            Material.BELL,
            Material.NOTE_BLOCK,
            Material.REPEATER,
            Material.COMPARATOR,
            Material.STONE_BUTTON,
            Material.OAK_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.BIRCH_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.ACACIA_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.CRIMSON_BUTTON,
            Material.WARPED_BUTTON,
            Material.MANGROVE_BUTTON,
            Material.CHERRY_BUTTON,
            Material.BAMBOO_BUTTON,
            Material.IRON_DOOR,
            Material.OAK_DOOR,
            Material.SPRUCE_DOOR,
            Material.BIRCH_DOOR,
            Material.JUNGLE_DOOR,
            Material.ACACIA_DOOR,
            Material.DARK_OAK_DOOR,
            Material.CRIMSON_DOOR,
            Material.WARPED_DOOR,
            Material.MANGROVE_DOOR,
            Material.CHERRY_DOOR,
            Material.BAMBOO_DOOR,
            Material.ACACIA_TRAPDOOR,
            Material.BAMBOO_TRAPDOOR,
            Material.COPPER_TRAPDOOR,
            Material.BIRCH_TRAPDOOR,
            Material.JUNGLE_TRAPDOOR,
            Material.MANGROVE_TRAPDOOR,
            Material.CHERRY_TRAPDOOR,
            Material.OXIDIZED_COPPER_TRAPDOOR,
            Material.EXPOSED_COPPER_TRAPDOOR,
            Material.WARPED_TRAPDOOR,
            Material.SPRUCE_TRAPDOOR,
            Material.IRON_TRAPDOOR,
            Material.LEVER
    );

    /**
     * Attempts to execute the ability.
     *
     * @param ctx The context of the ability.
     * @return True if the ability succeeded and should have cost reduced, false otherwise.
     */
    @Override
    public boolean execute(AbilityContext ctx) {

        // Check if we are looking at a blacklisted block.
        var targetBlock = ctx.caster().getTargetBlockExact(3);
        if (targetBlock != null && INTERACTABLE_BLOCK_BLACKLIST.contains(targetBlock.getType()))
            return false;

        // Teleport.
        var player = ctx.caster();
        Location old = player.getEyeLocation();
        boolean foundSpot = false;
        int distance = TELEPORT_DISTANCE;

        while (!foundSpot && distance > INVALID_DISTANCE_THRESHOLD){

            distance--;

            if (old.getWorld().rayTraceBlocks(old, old.getDirection(), distance) == null)
                foundSpot = true;
        }

        if (!foundSpot){
            player.sendMessage(ComponentUtils.error("No free spot ahead of you!"));
            return false;
        }

        Location newLocation = old.add(old.getDirection().normalize().multiply(distance));
        player.teleport(newLocation);
        player.getWorld().playEffect(old, Effect.ENDER_SIGNAL, 1);
        player.getWorld().playEffect(newLocation, Effect.ENDER_SIGNAL, 0);
        player.getWorld().playSound(newLocation, Sound.ENTITY_ENDER_EYE_DEATH, .4f, 1);
        player.getWorld().playSound(old, Sound.ENTITY_ENDERMAN_TELEPORT, .4f, 1);
        return true;
    }
}
