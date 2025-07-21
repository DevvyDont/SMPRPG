package xyz.devvydont.smprpg.ability;

import net.kyori.adventure.text.Component;
import xyz.devvydont.smprpg.ability.handlers.HotShotAbilityHandler;
import xyz.devvydont.smprpg.ability.handlers.InstantTransmissionAbilityHandler;
import xyz.devvydont.smprpg.ability.handlers.SugarRushAbilityHandler;
import xyz.devvydont.smprpg.ability.handlers.HealingHandler;
import xyz.devvydont.smprpg.util.formatting.Symbols;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static net.kyori.adventure.text.format.NamedTextColor.*;
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
                    create("+" + SugarRushAbilityHandler.BOOST + "%", GREEN),
                    create(" for "),
                    create(SugarRushAbilityHandler.DURATION + "s", GREEN)
            )),
            SugarRushAbilityHandler::new),

    MELON_MEND(
            "Melon Mend",
            List.of(merge(
                    create("Heal for "),
                    create("+" + HealingHandler.SMALL_HEAL_AMOUNT*HealingHandler.SMALL_HEAL_SECONDS*2, GREEN), create(Symbols.HEART, RED),
                    create(" over "),
                    create("+" + HealingHandler.SMALL_HEAL_SECONDS + "s", GREEN)
            )),
            () -> new HealingHandler(HealingHandler.SMALL_HEAL_AMOUNT, HealingHandler.SMALL_HEAL_SECONDS)
    ),

    FRUITFUL_REMEDY(
            "Fruitful Remedy",
            List.of(merge(
                    create("Heal for "),
                    create("+" + HealingHandler.NORMAL_HEAL_AMOUNT*HealingHandler.SMALL_HEAL_SECONDS*2, GREEN), create(Symbols.HEART, RED),
                    create(" over "),
                    create("+" + HealingHandler.SMALL_HEAL_SECONDS + "s", GREEN)
            )),
            () -> new HealingHandler(HealingHandler.NORMAL_HEAL_AMOUNT, HealingHandler.SMALL_HEAL_SECONDS)
    ),

    NATURES_RESPITE(
            "Nature's Respite",
            List.of(merge(
                    create("Heal for "),
                    create("+" + HealingHandler.BIG_HEAL_AMOUNT*HealingHandler.NORMAL_HEAL_SECONDS*2, GREEN), create(Symbols.HEART, RED),
                    create(" over "),
                    create("+" + HealingHandler.NORMAL_HEAL_SECONDS + "s", GREEN)
            )),
            () -> new HealingHandler(HealingHandler.BIG_HEAL_AMOUNT, HealingHandler.NORMAL_HEAL_SECONDS)
    ),

    FULL_BLOOM(
            "Full Bloom",
            List.of(merge(
                    create("Heal for "),
                    create("+" + HealingHandler.HEFTY_HEAL_AMOUNT*HealingHandler.NORMAL_HEAL_SECONDS*2, GREEN), create(Symbols.HEART, RED),
                    create(" over "),
                    create("+" + HealingHandler.NORMAL_HEAL_SECONDS + "s", GREEN)
            )),
            () -> new HealingHandler(HealingHandler.HEFTY_HEAL_AMOUNT, HealingHandler.NORMAL_HEAL_SECONDS)
    ),

    REJUVENATION_BURST(
            "Rejuvenation Burst",
            List.of(merge(
                    create("Heal for "),
                    create("+" + HealingHandler.COLOSSAL_HEAL_AMOUNT*HealingHandler.BIG_HEAL_SECONDS*2, GREEN), create(Symbols.HEART, RED),
                    create(" over "),
                    create("+" + HealingHandler.BIG_HEAL_SECONDS + "s", GREEN)
            )),
            () -> new HealingHandler(HealingHandler.COLOSSAL_HEAL_AMOUNT, HealingHandler.BIG_HEAL_SECONDS)
    ),

    INSTANT_TRANSMISSION(
            "Instant Transmission",
            List.of(
                    create("Instantly teleport"),
                    create("where you're looking!")
            ),
            InstantTransmissionAbilityHandler::new),

    HOT_SHOT(
            "Hot Shot",
            List.of(
                    merge(create("Shoot a "), create("fireball", RED), create(" in the direction")),
                    merge(create("you are looking that")),
                    merge(create("deals "), create(HotShotAbilityHandler.DAMAGE, RED), create(" damage"))
            ),
            HotShotAbilityHandler::new)

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
