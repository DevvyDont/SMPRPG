package xyz.devvydont.smprpg.fishing.loot.requirements;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.block.Biome;
import xyz.devvydont.smprpg.fishing.utils.FishingContext;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils;

import java.util.Collection;
import java.util.List;

public record BiomeChoiceRequirement(BiomeGroup biome) implements FishingLootRequirement {

    public enum BiomeGroup {
        OCEAN(NamedTextColor.AQUA, Biome.OCEAN, Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN, Biome.DEEP_OCEAN, Biome.DEEP_LUKEWARM_OCEAN, Biome.WARM_OCEAN),
        ;

        private final TextColor color;
        private final Collection<Biome> biomes;

        BiomeGroup(TextColor color, Biome...biomes) {
            this.color = color;
            this.biomes = List.of(biomes);
        }

        public TextColor getColor() {
            return color;
        }

        public Collection<Biome> getBiomes() {
            return biomes;
        }
    }

    /**
     * Checks if the given fishing context passes this requirement.
     *
     * @param context The fishing context.
     * @return true if the context allows this loot to exist, false otherwise.
     */
    @Override
    public boolean passes(FishingContext context) {
        return biome().getBiomes().contains(context.getLocation().getBlock().getBiome());
    }

    /**
     * Displays this requirement as a component. Very useful if you want to lay out requirements in a GUI.
     *
     * @return The component that represents this requirement.
     */
    @Override
    public Component display() {
        return ComponentUtils.create("Any ").append(ComponentUtils.create(MinecraftStringUtils.getTitledString(biome.name()), biome.getColor()));
    }

}
