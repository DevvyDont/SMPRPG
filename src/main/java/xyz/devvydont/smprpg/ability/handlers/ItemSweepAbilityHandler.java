package xyz.devvydont.smprpg.ability.handlers;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.ability.AbilityContext;
import xyz.devvydont.smprpg.ability.AbilityHandler;
import xyz.devvydont.smprpg.services.DropsService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

/**
 * Admin ability. Used for an admin item. Teleports and untags any nearby items in a 25 block radius.
 */
public class ItemSweepAbilityHandler implements AbilityHandler {


    /**
     * Attempts to execute the ability.
     *
     * @param ctx The context of the ability.
     * @return True if the ability succeeded and should have cost reduced, false otherwise.
     */
    @Override
    public boolean execute(AbilityContext ctx) {

        var canUse = ctx.caster().permissionValue("smprpg.item.itemsweep").toBooleanOrElse(false) || ctx.caster().isOp();
        if (!canUse) {
            ctx.caster().sendMessage(ComponentUtils.error("You cannot use this item."));
            return false;
        }

        if (!(ctx.caster() instanceof Player player))
            return false;

        var items = 0;
        for (var item : ctx.caster().getLocation().getNearbyEntitiesByType(Item.class, 25)) {
            SMPRPG.getService(DropsService.class).setOwner(item, player);
            item.setOwner(player.getUniqueId());
            item.teleport(ctx.caster().getLocation());
            items++;
        }

        player.sendMessage(ComponentUtils.success("Teleported and untagged " + items + " items to you!"));
        return true;
    }
}
