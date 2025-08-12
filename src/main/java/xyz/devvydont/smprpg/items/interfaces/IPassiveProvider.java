package xyz.devvydont.smprpg.items.interfaces;

import xyz.devvydont.smprpg.ability.Passive;

import java.util.Set;

/**
 * Represents an item that contains "passive" abilities. These abilities aren't actively used, and "always" present
 * when the item is worn/held. For now, we check if a passive is active simply by doing manual listener checks.
 * This system should be revamped eventually to be more future-proofed and support things like stacking.
 */
public interface IPassiveProvider {

    /**
     * Retrieve the passives this item has.
     * @return A set of passives.
     */
    Set<Passive> getPassives();
}
