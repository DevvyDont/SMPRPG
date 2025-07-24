package xyz.devvydont.smprpg.gui.items;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.gui.base.MenuBase;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

public final class MenuTrashItems extends MenuBase {
    public MenuTrashItems(@NotNull Player player) {
        super(player, 6);
    }

    @Override
    protected void handleInventoryOpened(InventoryOpenEvent event) {
        // Prepare the inventory
        event.titleOverride(ComponentUtils.create("Trash Items", NamedTextColor.BLACK));
        this.setMaxStackSize(100);

        // Render the UI
        this.clear();
    }

    @Override
    protected void handleInventoryClicked(InventoryClickEvent event) {
        event.setCancelled(false);

        if (event.getCurrentItem() == null)
            return;

        var itemBlueprint = SMPRPG.getService(ItemService.class).getBlueprint(event.getCurrentItem());
        // If the item clicked is enchanted or reforged, we should prevent the item from being shift clicked.
        if (event.isShiftClick() && (!event.getCurrentItem().getEnchantments().isEmpty() || itemBlueprint.isReforged(event.getCurrentItem()))) {
            var error = ComponentUtils.merge(ComponentUtils.create("WHOA THERE!", NamedTextColor.RED, TextDecoration.BOLD), ComponentUtils.create(" Do you really want to trash this? Manually drag the item in the menu if so."));
            player.sendMessage(ComponentUtils.alert(error, NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_DEATH, 1f, 1.5f);
            event.setCancelled(true);
        }
    }

    @Override
    protected void handleInventoryClosed(InventoryCloseEvent event) {
        var itemCount = 0;
        for (var itemStack : this.getItems()) {
            if (itemStack == null) {
                continue;
            }

            itemCount += itemStack.getAmount();
            itemStack.setAmount(0);
        }

        this.player.sendMessage(ComponentUtils.success(ComponentUtils.merge(
            ComponentUtils.create("You trashed ", NamedTextColor.GREEN),
            ComponentUtils.create(String.valueOf(itemCount), NamedTextColor.AQUA),
            ComponentUtils.create(" items!", NamedTextColor.GREEN)
        )));
    }
}
