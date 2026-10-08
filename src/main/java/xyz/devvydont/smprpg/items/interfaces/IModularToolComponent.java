package xyz.devvydont.smprpg.items.interfaces;

import xyz.devvydont.smprpg.items.attribute.AttributeEntry;

import java.util.Collection;

/**
 * Represents an item that can be used as a hotswap tool part in tool workbenches.
 */
public interface IModularToolComponent {
    /**
     * Get the attributes that this component will add to the base tool item.
     * @return Amount of ticks to burn.
     */
    Collection<xyz.devvydont.smprpg.items.attribute.AttributeEntry> getAttributes();

    String getAttrKey();

    String getComponentPrefix();

}
