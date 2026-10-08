package xyz.devvydont.smprpg.items.interfaces;

import org.bukkit.NamespacedKey;

public interface ITrackedConsumable extends IConsumable {

    NamespacedKey getConumableTrackerKey();
    int getMaxUses();

}
