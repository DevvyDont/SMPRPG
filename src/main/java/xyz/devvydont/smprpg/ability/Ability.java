package xyz.devvydont.smprpg.ability;

import net.kyori.adventure.text.Component;
import xyz.devvydont.smprpg.ability.handlers.SugarRush;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static xyz.devvydont.smprpg.util.formatting.ComponentUtils.merge;
import static xyz.devvydont.smprpg.util.formatting.ComponentUtils.create;

/**
 * Represents a general ability. Abilities come with names, descriptions, and handlers.
 */
public enum Ability {

    SUGAR_RUSH(
            "Sugar Rush",
            List.of(merge(
                    create("Increases "),
                    create("speed", GOLD),
                    create(" by "),
                    create("+" + SugarRush.BOOST + "%", GREEN),
                    create(" for "),
                    create(SugarRush.DURATION + "s", GREEN)
            )),
            SugarRush::new)

    ;

    private final String Name;
    private final Collection<Component> Description;
    private final Supplier<? extends AbilityHandler> Handler;

    Ability(String name, Collection<Component> description, Supplier<? extends AbilityHandler> handler) {
        Name = name;
        Description = description;
        Handler = handler;
    }

    public String getName() {
        return Name;
    }

    public Collection<Component> getDescription() {
        return Description;
    }

    public AbilityHandler getHandler() {
        return Handler.get();
    }
}
