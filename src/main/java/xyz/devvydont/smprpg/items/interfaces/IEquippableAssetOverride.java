package xyz.devvydont.smprpg.items.interfaces;

import net.kyori.adventure.key.Key;

/**
 * An interface an item should implement if it is meant to contain an equippable asset ID.
 * This is the "asset_id" field on the "equippable" component for items that can be worn, that
 * defines the item model that shows up when an entity wears it.
 * Note, you ONLY should implement this interface if the resource pack has an asset to override it, as if
 * it doesn't, it will just not render at all.
 */
public interface IEquippableAssetOverride {

    Key getAssetId();
}
