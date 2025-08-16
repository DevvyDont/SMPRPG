package xyz.devvydont.smprpg.items.blueprints.debug;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

public class EntityDeleter extends CustomItemBlueprint implements Listener {

    public EntityDeleter(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.EQUIPMENT;
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {

        var mainHandItem = event.getPlayer().getInventory().getItemInMainHand();
        if (mainHandItem.getType().equals(Material.AIR))
            return;

        if (!isItemOfType(mainHandItem))
            return;

        if (event.getRightClicked() instanceof Player)
            return;

        event.getRightClicked().remove();
        event.getPlayer().sendMessage(ComponentUtils.success("Deleted " + event.getRightClicked().getName()));
    }

    @EventHandler
    public void onInteract(CustomEntityDamageByEntityEvent event) {

        if (!(event.dealer instanceof Player player))
            return;

        var mainHandItem = player.getInventory().getItemInMainHand();
        if (mainHandItem.getType().equals(Material.AIR))
            return;

        if (!isItemOfType(mainHandItem))
            return;

        if (event.damaged instanceof Player)
            return;

        event.damaged.remove();
        player.sendMessage(event.damaged.name().append(Component.text(" deleted!", NamedTextColor.GREEN)));
    }
}
