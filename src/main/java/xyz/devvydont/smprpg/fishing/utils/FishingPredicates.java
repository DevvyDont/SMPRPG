package xyz.devvydont.smprpg.fishing.utils;

import net.momirealms.craftengine.bukkit.api.BukkitAdaptor;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Material;
import org.bukkit.World;
import xyz.devvydont.smprpg.util.misc.ILocationPredicate;
import xyz.devvydont.smprpg.util.persistence.KeyStore;

/**
 * Contains various predicates that make fishing definitions easier.
 */
public class FishingPredicates {

    /**
     * Meant to be used as a fallback if no suitable predicate exists. Will always fail, allowing for default behavior.
     */
    public static final ILocationPredicate IMPOSSIBLE_PREDICATE = e -> false;

    /**
     * The predicate that lava rods typically use. Checks if the current block is lava.
     */
    public static final ILocationPredicate LAVA_PREDICATE = e -> e.getBlock().getType().equals(Material.LAVA);

    /**
     * The predicate that void rods typically use. Checks if the current block is in the void.
     */
    public static final ILocationPredicate VOID_PREDICATE = e -> e.getWorld().getEnvironment().equals(World.Environment.THE_END) && e.getBlock().getY() < 0;

    /**
     * The predicate that aerial rods typically use. Checks if the current block is in the void.
     */
    public static final ILocationPredicate AERIAL_PREDICATE = e -> e.getWorld().getKey().equals(KeyStore.DIM_AETHER) && BukkitAdaptor.adapt(e.getBlock()).blockState().hasTag(Key.of("smprpg:aerial_fishing_block"));

    /**
     * Complex predicate allows both lava, aerial, and void fishing to work at the same time.
     */
    public static final ILocationPredicate COMPLEX_PREDICATE = e -> LAVA_PREDICATE.check(e) || VOID_PREDICATE.check(e) || AERIAL_PREDICATE.check(e);

}
