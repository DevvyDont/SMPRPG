package xyz.devvydont.smprpg.items.listeners;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.ability.AbilityContext;
import xyz.devvydont.smprpg.items.interfaces.IAbilityCaster;
import xyz.devvydont.smprpg.services.ActionBarService;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;

/**
 * Implements the logic that lets items cast abilities.
 */
public class AbilityCastingListener extends ToggleableListener {

    @EventHandler
    private void __onInteractWithItem(PlayerInteractEvent event) {

        // Retrieve the item involved with the interaction. Do nothing if it doesn't exist.
        var item = event.getItem();
        if (item == null || item.getType().equals(Material.AIR))
            return;

        // Do nothing if the item is not a type of ability caster.
        var _blueprint = ItemService.blueprint(item);
        if (!(_blueprint instanceof IAbilityCaster caster))
            return;

        // Check every ability and see if the click type matches.
        var abilities = caster.getAbilities(item);
        var player = SMPRPG.getService(EntityService.class).getPlayerInstance(event.getPlayer());
        for (var ability : abilities) {

            // Skip if click type isn't correct.
            if (!ability.activation().passes(event.getAction()))
                return;

            // Check if the cost is met.
            if (!ability.cost().canUse(player)) {
                SMPRPG.getService(ActionBarService.class).addActionBarComponent(event.getPlayer(), ActionBarService.ActionBarSource.MISC, ComponentUtils.create("NOT ENOUGH " + ability.cost().resource().name(), NamedTextColor.RED), 1);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, .3f, .5f);
                return;
            }

            // Execute!
            var success = ability.ability().getHandler().execute(new AbilityContext(event.getPlayer()));
            if (!success)
                return;
            ability.cost().spend(player);
            SMPRPG.getService(ActionBarService.class).addActionBarComponent(
                    event.getPlayer(),
                    ActionBarService.ActionBarSource.MISC,
                    ComponentUtils.merge(
                            ComponentUtils.create(ability.ability().getName(), NamedTextColor.GOLD),
                            ComponentUtils.SPACE,
                            ComponentUtils.create("-" + ability.cost().amount() + ability.cost().resource().getSymbol(), ability.cost().resource().getColor())),
                    3);
        }
    }

}
