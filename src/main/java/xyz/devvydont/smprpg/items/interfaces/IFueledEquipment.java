package xyz.devvydont.smprpg.items.interfaces;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.SMPRPG;

public interface IFueledEquipment {

    int FUEL_OFFSET = 1;

    int getMaxFuel(ItemStack item);
    int getFuelUsed(ItemStack item);
    void setFuelUsed(ItemStack item, int fuel);

    String getBreakSound();

}
