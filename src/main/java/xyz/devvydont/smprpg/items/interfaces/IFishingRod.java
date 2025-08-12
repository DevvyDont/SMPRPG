package xyz.devvydont.smprpg.items.interfaces;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import xyz.devvydont.smprpg.entity.fishing.SeaCreature;
import xyz.devvydont.smprpg.gui.enchantments.EnchantmentSortMode;

import java.util.Set;

/**
 * An item that has the ability to cast lines. Every single material of this item should be a FISHING_ROD.
 * For our plugin, the only thing that fishing rods have custom logic for is what contexts it can fish in.
 */
public interface IFishingRod {

    enum FishingFlag {
        NORMAL("Water", SeaCreature.NAME_COLOR),
        LAVA("Lava", TextColor.color(255, 100, 28)),
        VOID("Void", TextColor.color(69, 56, 94)),
        ;

        /**
         * Retrieve the next fishing flag after this one. If this is the last, use the first one.
         * Useful for GUIs.
         * @return A new fishing flag enum.
         */
        public FishingFlag next() {
            int desiredFlag = this.ordinal() + 1;
            if (desiredFlag >= FishingFlag.values().length)
                desiredFlag = 0;
            return FishingFlag.values()[desiredFlag];
        }

        /**
         * Fishing rods can be complex. Return a proper prefix for the rod when displaying an item tag.
         * @param flags The flags on the item.
         * @return A prefix.
         */
        public static String prefix(Set<FishingFlag> flags) {

            if (flags.isEmpty())
                return "";

            var listified = flags.stream().toList();

            // If there's only one, use that.
            if (listified.size() == 1)
                return listified.getFirst().Display;

            // If there's not 2, use multi.
            if (listified.size() > 2)
                return "Multi";

            // If this rod can do normal and lava
            if (flags.contains(FishingFlag.NORMAL) && flags.contains(FishingFlag.LAVA))
                return "Fluid";

            // If this rod can do normal and void
            if (flags.contains(FishingFlag.NORMAL) && flags.contains(FishingFlag.VOID))
                return "Drift";

            // If this rod can do lava and void
            if (flags.contains(FishingFlag.LAVA) && flags.contains(FishingFlag.VOID))
                return "Warped";

            return "Unknown";
        }

        /**
         * Used to construct the fishing rod prefix on the item type.
         */
        public final String Display;
        public final TextColor Color;

        FishingFlag(String display, TextColor color) {
            Display = display;
            Color = color;
        }

        /**
         * Get the material to display in GUIs.
         * @return The material.
         */
        public Material getMaterial() {
            return switch (this) {
                case NORMAL -> Material.WATER_BUCKET;
                case LAVA -> Material.LAVA_BUCKET;
                case VOID -> Material.SCULK;
            };
        }

    }

    /**
     * Check what contexts this fishing rod is allowed to fish in. for example, if this rod can catch things in the
     * void then it will contain FishingFlag.VOID.
     * @return A set of fishing flags this rod contains.
     */
    Set<FishingFlag> getFishingFlags();

}
