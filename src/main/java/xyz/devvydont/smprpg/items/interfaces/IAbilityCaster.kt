package xyz.devvydont.smprpg.items.interfaces

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.ability.Ability
import xyz.devvydont.smprpg.ability.AbilityActivationMethod
import xyz.devvydont.smprpg.ability.AbilityCost
import xyz.devvydont.smprpg.services.ItemService

/**
 * Represents an item that can cast an ability.
 */
interface IAbilityCaster {
    /**
     * Get the abilities this item has, and how they can be cast.
     * @param item The item.
     * @return A list of abilities.
     */
    fun getAbilities(item: ItemStack): Collection<AbilityEntry>

    /**
     * Get the cooldown in between item uses.
     * Keep in mind this is more for preventing strange things from happening via casting on the same tick or teleporting,
     * so it needs to be per item since we use the default cooldown system.
     * @param item The item.
     * @return The cooldown in ticks.
     */
    fun getCooldown(item: ItemStack): Long
    fun getCooldownGroup(item: ItemStack): NamespacedKey {
        return NamespacedKey(SMPRPG.plugin, item.persistentDataContainer.getOrDefault(
            SMPRPG.getService(ItemService::class.java).itemTypeKey,
            PersistentDataType.STRING,
            "unknown"))
    }

    @JvmRecord
    data class AbilityEntry(@JvmField val ability: Ability, @JvmField val activation: AbilityActivationMethod, @JvmField val cost: AbilityCost)
}
