package xyz.devvydont.smprpg.ability;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import xyz.devvydont.smprpg.ability.handlers.passive.AbyssalAnnihilationListener;
import xyz.devvydont.smprpg.ability.handlers.passive.AnglerListener;
import xyz.devvydont.smprpg.entity.fishing.SeaCreature;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

/**
 * Represents a simple "passive ability". These are much more simple due to the fact they simple just "exist" on items
 * rather than actual providing meaningful functionality like its {@link Ability} counterpart.
 */
public enum Passive {

    ANGLER(ComponentUtils.merge(
            ComponentUtils.create("Deals "),
            ComponentUtils.create(AnglerListener.MULTIPLIER + "x damage", NamedTextColor.RED),
            ComponentUtils.create(" to "),
            ComponentUtils.create("Sea Creatures", SeaCreature.NAME_COLOR)
    )),
    ABYSSAL_ANNIHILATION(ComponentUtils.merge(
            ComponentUtils.create("Deals "),
            ComponentUtils.create(AbyssalAnnihilationListener.MULTIPLIER + "x damage", NamedTextColor.RED),
            ComponentUtils.create(" to "),
            ComponentUtils.create("Sea Creatures", SeaCreature.NAME_COLOR)
    )),
    ;

    private final Component description;

    Passive(Component description) {
        this.description = description;
    }

    public Component getDescription() {
        return description;
    }
}
