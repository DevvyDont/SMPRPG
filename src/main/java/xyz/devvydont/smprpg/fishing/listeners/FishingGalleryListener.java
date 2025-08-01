package xyz.devvydont.smprpg.fishing.listeners;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import xyz.devvydont.smprpg.fishing.events.FishingLootGenerateEvent;
import xyz.devvydont.smprpg.fishing.loot.FishingLootType;
import xyz.devvydont.smprpg.fishing.utils.FishingGallery;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;
import xyz.devvydont.smprpg.util.persistence.KeyStore;
import xyz.devvydont.smprpg.util.persistence.PDCAdapters;

/**
 * Implements the logic for incrementing the fishing gallery when we get certain catches!
 */
public class FishingGalleryListener extends ToggleableListener {

    /**
     * Listens for when a player rolls a fish loot item, and increments the times they caught it in their fishing
     * gallery so they can view their catches in /fishing.
     * @param event The {@link FishingLootGenerateEvent} event.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void __onCatchFishLoot(FishingLootGenerateEvent event) {
        var container = event.getFishingContext().getPlayer().getPersistentDataContainer();
        var gallery = container.getOrDefault(KeyStore.FISHING_GALLERY, PDCAdapters.FISHING_GALLERY, new FishingGallery());
        gallery.increment(event.getCalculationResult().Reward().Element());
        container.set(KeyStore.FISHING_GALLERY, PDCAdapters.FISHING_GALLERY, gallery);
    }

}
