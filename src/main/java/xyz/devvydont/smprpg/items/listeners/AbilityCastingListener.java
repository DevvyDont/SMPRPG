package xyz.devvydont.smprpg.items.listeners;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.ability.AbilityContext;
import xyz.devvydont.smprpg.items.interfaces.IAbilityCaster;
import xyz.devvydont.smprpg.services.ActionBarService;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;

import java.util.Set;

/**
 * Implements the logic that lets items cast abilities.
 */
public class AbilityCastingListener extends ToggleableListener {

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

    @EventHandler(priority = EventPriority.MONITOR)
    private void __onInteractWithItem(PlayerInteractEvent event) {

        // Retrieve the item involved with the interaction. Do nothing if it doesn't exist.
        var item = event.getItem();
        if (item == null || item.getType().equals(Material.AIR))
            return;

        if (event.getPlayer().hasCooldown(item))
            return;

        // Do nothing if the item is not a type of ability caster.
        var _blueprint = ItemService.blueprint(item);
        if (!(_blueprint instanceof IAbilityCaster caster))
            return;

        // Check if we are looking at a blacklisted block.
        var targetBlock = event.getPlayer().getTargetBlockExact(3);
        if (event.getAction().isRightClick() && targetBlock != null && INTERACTABLE_BLOCK_BLACKLIST.contains(targetBlock.getType()))
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
                SMPRPG.getService(ActionBarService.class).addActionBarComponent(event.getPlayer(), ActionBarService.ActionBarSource.MISC, ComponentUtils.create("NOT ENOUGH " + ability.cost().resource.name(), NamedTextColor.RED), 1);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, .3f, .5f);
                return;
            }

            // Execute!
            var success = ability.ability().getHandler().execute(new AbilityContext(event.getPlayer(), event.getHand()));
            if (!success)
                return;
            ability.cost().spend(player);
            SMPRPG.getService(ActionBarService.class).addActionBarComponent(
                    event.getPlayer(),
                    ActionBarService.ActionBarSource.MISC,
                    ComponentUtils.merge(
                            ComponentUtils.create(ability.ability().getFriendlyName(), NamedTextColor.GOLD),
                            ComponentUtils.SPACE,
                            ComponentUtils.create("-" + ability.cost().amount + ability.cost().resource.getSymbol(), ability.cost().resource.getColor())),
                    3);
            player.getPlayer().setCooldown(item, (int) caster.getCooldown(item));
        }
    }

}
