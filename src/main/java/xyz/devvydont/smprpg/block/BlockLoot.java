package xyz.devvydont.smprpg.block;

import org.bukkit.inventory.ItemStack;

/**
 * A wrapper over an {@link org.bukkit.inventory.ItemStack}. Contains support for a percentage chance.
 * The chance is the modifier that determines how "fortune" behaves. If you want a 1-to-1 drop chance, then
 * give it a chance of 1.0. This means with fortune boosting, you can get more than 1 drop (1.5 = 2 drops half the time).
 * Some drops like coal ore, will drop 2 coal sometimes. This can be emulated with a chance of 1.5.
 */
public class BlockLoot {

    private final ItemStack loot;

    private final double chance;

    /**
     * Create block loot with a manually defined item and chance.
     * @param loot The loot.
     * @param chance The chance. Can be any positive number that isn't 0.
     */
    public BlockLoot(ItemStack loot, double chance) {
        this.loot = loot;
        this.chance = chance;
    }

    /**
     * Create block loot with the default chance of 1.0.
     * @param loot The loot.
     */
    public BlockLoot(ItemStack loot) {
        this.loot = loot;
        this.chance = 1.0;
    }

    /**
     * Get the loot from this specific entry.
     * @return A cloned ItemStack instance safe for modification.
     */
    public ItemStack getLoot() {
        return loot.clone();
    }

    /**
     * The base chance that this loot can be rolled.
     * @return A percentage chance, that is allowed to go over 1 for multiple drops.
     */
    public double getChance() {
        return chance;
    }

    public static BlockLoot of(ItemStack loot) {
        return new BlockLoot(loot);
    }

    public static BlockLoot of(ItemStack loot, double chance) {
        return new BlockLoot(loot, chance);
    }
}
