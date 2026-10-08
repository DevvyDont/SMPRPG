package xyz.devvydont.smprpg.util.persistence;

import xyz.devvydont.smprpg.util.persistence.adapters.*;

/**
 * Holds various PDC adapter instances that can be used for easier PDC interfacing.
 */
public class PDCAdapters {

    public static final FishingGalleryAdapter FISHING_GALLERY = new FishingGalleryAdapter();
    public static final UUIDAdapter UUID = new UUIDAdapter();
    public static final RarityAdapter RARITY = new RarityAdapter();
    public static final SpawnerOptionsAdapter SPAWNER_OPTIONS = new SpawnerOptionsAdapter();
    public static final PlayerSettingsAdapter PLAYER_SETTINGS = new PlayerSettingsAdapter();
    public static final LocationAdapter LOCATION = new LocationAdapter();

    // Adapters for items.
    public static final EquipmentSetAdapter EQUIPMENT_SET_ADAPTER = new EquipmentSetAdapter();
    public static final WardrobeAdapter WARDROBE_ADAPTER = new WardrobeAdapter();

}
