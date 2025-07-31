package xyz.devvydont.smprpg.items.blueprints.debug;

import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.ability.Ability;
import xyz.devvydont.smprpg.ability.AbilityActivationMethod;
import xyz.devvydont.smprpg.ability.AbilityCost;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IAbilityCaster;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.Collection;
import java.util.List;

public class ItemMagnet extends CustomItemBlueprint implements IAbilityCaster {

    public ItemMagnet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.EQUIPMENT;
    }

    /**
     * Get the abilities this item has, and how they can be cast.
     *
     * @param item The item.
     * @return A list of abilities.
     */
    @Override
    public Collection<AbilityEntry> getAbilities(ItemStack item) {
        return List.of(
                new AbilityEntry(Ability.ITEM_SWEEP, AbilityActivationMethod.RIGHT_CLICK, AbilityCost.of(AbilityCost.Resource.MANA, 1))
        );
    }

    /**
     * Get the cooldown in between item uses.
     * Keep in mind this is more for preventing strange things from happening via casting on the same tick or teleporting,
     * so it needs to be per item since we use the default cooldown system.
     *
     * @param item The item.
     * @return The cooldown in ticks.
     */
    @Override
    public long getCooldown(ItemStack item) {
        return TickTime.seconds(1);
    }
}
