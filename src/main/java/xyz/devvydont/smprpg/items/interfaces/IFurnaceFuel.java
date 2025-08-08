package xyz.devvydont.smprpg.items.interfaces;

/**
 * Represents an item that will act as proper furnace fuel.
 * If you want an item to burn as fuel, I recommend it follows these criteria:
 * - The vanilla material is already burnable. (Wood, coal, sticks, you get it.)
 * - The item also doesn't implement {@link ISmeltable}. While you can have an item be both smeltable and fuel, it's meh.
 */
public interface IFurnaceFuel {

    /**
     * Get the time to burn in ticks when a furnace consumes this.
     * @return Amount of ticks to burn.
     */
    long getBurnTime();

}
