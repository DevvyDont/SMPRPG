package xyz.devvydont.smprpg.block

import org.bukkit.inventory.ItemStack

/**
 * A wrapper over an [ItemStack]. Contains support for a percentage chance.
 * The chance is the modifier that determines how "fortune" behaves. If you want a 1-to-1 drop chance, then
 * give it a chance of 1.0. This means with fortune boosting, you can get more than 1 drop (1.5 = 2 drops half the time).
 * Some drops like coal ore, will drop 2 coal sometimes. This can be emulated with a chance of 1.5.
 */
class BlockLoot {
    private val loot: ItemStack

    /**
     * The base chance that this loot can be rolled.
     * @return A percentage chance, that is allowed to go over 1 for multiple drops.
     */
    val chance: Double

    /**
     * Create block loot with a manually defined item and chance.
     * @param loot The loot.
     * @param chance The chance. Can be any positive number that isn't 0.
     */
    constructor(loot: ItemStack, chance: Double) {
        this.loot = loot
        this.chance = chance
    }

    /**
     * Create block loot with the default chance of 1.0.
     * @param loot The loot.
     */
    constructor(loot: ItemStack) {
        this.loot = loot
        this.chance = 1.0
    }

    /**
     * Get the loot from this specific entry.
     * @return A cloned ItemStack instance safe for modification.
     */
    fun getLoot(): ItemStack {
        return loot.clone()
    }

    companion object {
        fun of(loot: ItemStack): BlockLoot {
            return BlockLoot(loot)
        }

        fun of(loot: ItemStack, chance: Double): BlockLoot {
            return BlockLoot(loot, chance)
        }
    }
}
