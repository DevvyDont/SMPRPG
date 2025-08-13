package xyz.devvydont.smprpg.items.listeners;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This listener will prevent players from dropping items that are considered expensive.
 * This listener does not work as intended, but some day in the future should be refactored so it works correctly
 * as double tapping behavior is desirable. Currently, it is near impossible to implement this feature without
 * introducing some possible item deletion bug due to the nature of how item dropping behavior works.
 */
@Deprecated
public class RareItemDropPreventionListener extends ToggleableListener {

    private static final long DROP_COOLDOWN = 500L;
    private static final long WARNING_COOLDOWN = 5000L;
    private final Map<UUID, Long> lastDropAttempts = new HashMap<>();
    private final Map<UUID, Long> lastWarning = new HashMap<>();

    private boolean isRareItem(ItemStack item) {

        // All legendary items.
        var rarity = ItemService.blueprint(item).getRarity(item);
        if (rarity.ordinal() >= ItemRarity.LEGENDARY.ordinal())
            return true;

        // All rare+ items with enchantments.
        if (rarity.ordinal() >= ItemRarity.RARE.ordinal() && !item.getEnchantments().isEmpty())
            return true;

        return false;
    }

    private void performRareDropCheck(Player player, ItemStack item, Cancellable event) {

        if (!isRareItem(item))
            return;

        // We are dropping a rare item. Did they do this previously with a short time?
        var lastTap = lastDropAttempts.getOrDefault(player.getUniqueId(), 0L);
        var now = System.currentTimeMillis();
        lastDropAttempts.put(player.getUniqueId(), System.currentTimeMillis() + DROP_COOLDOWN);

        var diff = now - lastTap;
        if (diff < DROP_COOLDOWN)
            return;

        event.setCancelled(true);

        var lastTimeWarned = lastWarning.getOrDefault(player.getUniqueId(), 0L);
        if (now - lastTimeWarned < WARNING_COOLDOWN)
            return;

        lastWarning.put(player.getUniqueId(), now);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, .5f, 2.0f);
        player.sendMessage(ComponentUtils.alert(ComponentUtils.merge(
                ComponentUtils.create("CAREFUL!", NamedTextColor.RED, TextDecoration.BOLD),
                ComponentUtils.create(" Do you really want to throw out this item? Double tap your drop key if so!")
        ), NamedTextColor.RED));

    }

    /**
     * Listen for when players attempt to drop an item from an inventory.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void __onDropFromInventory(InventoryClickEvent event) {

        var isDropEvent = switch (event.getAction()) {
            case DROP_ALL_CURSOR, DROP_ALL_SLOT, DROP_ONE_CURSOR, DROP_ONE_SLOT -> true;
            default -> false;
        };

        if (!isDropEvent)
            return;

        if (event.getCurrentItem() == null)
            return;

        performRareDropCheck((Player) event.getWhoClicked(), event.getCurrentItem(), event);
    }

    /**
     * Listen for when players drop items. Don't let a rare item be dropped unless it was already attempted to
     * drop half a second ago.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void __onDropItem(PlayerDropItemEvent event) {
        performRareDropCheck(event.getPlayer(), event.getItemDrop().getItemStack(), event);
    }

}
