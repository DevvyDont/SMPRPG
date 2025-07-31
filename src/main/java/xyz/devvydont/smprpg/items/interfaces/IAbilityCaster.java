package xyz.devvydont.smprpg.items.interfaces;

import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.ability.Ability;
import xyz.devvydont.smprpg.ability.AbilityActivationMethod;
import xyz.devvydont.smprpg.ability.AbilityCost;

import java.util.Collection;

/**
 * Represents an item that can cast an ability.
 */
public interface IAbilityCaster {

    /**
     * Get the abilities this item has, and how they can be cast.
     * @param item The item.
     * @return A list of abilities.
     */
    Collection<AbilityEntry> getAbilities(ItemStack item);

    /**
     * Get the cooldown in between item uses.
     * Keep in mind this is more for preventing strange things from happening via casting on the same tick or teleporting,
     * so it needs to be per item since we use the default cooldown system.
     * @param item The item.
     * @return The cooldown in ticks.
     */
    long getCooldown(ItemStack item);

    record AbilityEntry (Ability ability, AbilityActivationMethod activation, AbilityCost cost){}
}
