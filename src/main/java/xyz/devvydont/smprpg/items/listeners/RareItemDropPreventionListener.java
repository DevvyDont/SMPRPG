package xyz.devvydont.smprpg.items.listeners;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
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
 */
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

    /**
     * Listen for when players drop items. Don't let a rare item be dropped unless it was already attempted to
     * drop half a second ago.
     */
    @EventHandler
    private void __onDropItem(PlayerDropItemEvent event) {

        if (!isRareItem(event.getItemDrop().getItemStack()))
            return;

        // We are dropping a rare item. Did they do this previously with a short time?
        var lastTap = lastDropAttempts.getOrDefault(event.getPlayer().getUniqueId(), 0L);
        var now = System.currentTimeMillis();
        lastDropAttempts.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + DROP_COOLDOWN);

        var diff = now - lastTap;
        if (diff < DROP_COOLDOWN)
            return;

        event.setCancelled(true);

        var lastTimeWarned = lastWarning.getOrDefault(event.getPlayer().getUniqueId(), 0L);
        if (now - lastTimeWarned < WARNING_COOLDOWN)
            return;

        lastWarning.put(event.getPlayer().getUniqueId(), now);
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, .5f, 2.0f);
        event.getPlayer().sendMessage(ComponentUtils.alert(ComponentUtils.merge(
                ComponentUtils.create("CAREFUL!", NamedTextColor.RED, TextDecoration.BOLD),
                ComponentUtils.create(" Do you really want to throw out this item? Double tap your drop key if so!")
        ), NamedTextColor.RED));
    }

}
