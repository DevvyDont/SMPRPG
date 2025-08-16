package xyz.devvydont.smprpg.ability

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import xyz.devvydont.smprpg.ability.handlers.*
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import java.util.function.Supplier

/**
 * Represents a general ability. Abilities come with names, descriptions, and handlers.
 */
enum class Ability(val friendlyName: String, val description: List<Component>,
                   private val handler: Supplier<out AbilityHandler?>
) {
    SUGAR_RUSH(
        "Sugar Rush",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Increases "),
                ComponentUtils.create("speed", NamedTextColor.GOLD),
                ComponentUtils.create(" by "),
                ComponentUtils.create("+" + SugarRushAbilityHandler.BOOST + "%", NamedTextColor.GREEN),
                ComponentUtils.create(" for "),
                ComponentUtils.create(SugarRushAbilityHandler.DURATION.toString() + "s", NamedTextColor.GREEN)
            )
        ),
        Supplier { SugarRushAbilityHandler() }),

    MELON_MEND(
        "Melon Mend",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Heal for "),
                ComponentUtils.create(
                    "+" + HealingHandler.SMALL_HEAL_AMOUNT * HealingHandler.SMALL_HEAL_SECONDS * 2,
                    NamedTextColor.GREEN
                ), ComponentUtils.create(Symbols.HEART, NamedTextColor.RED),
                ComponentUtils.create(" over "),
                ComponentUtils.create("+" + HealingHandler.SMALL_HEAL_SECONDS + "s", NamedTextColor.GREEN)
            )
        ),
        Supplier { HealingHandler(HealingHandler.SMALL_HEAL_AMOUNT, HealingHandler.SMALL_HEAL_SECONDS) }
    ),

    FRUITFUL_REMEDY(
        "Fruitful Remedy",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Heal for "),
                ComponentUtils.create(
                    "+" + HealingHandler.NORMAL_HEAL_AMOUNT * HealingHandler.SMALL_HEAL_SECONDS * 2,
                    NamedTextColor.GREEN
                ), ComponentUtils.create(Symbols.HEART, NamedTextColor.RED),
                ComponentUtils.create(" over "),
                ComponentUtils.create("+" + HealingHandler.SMALL_HEAL_SECONDS + "s", NamedTextColor.GREEN)
            )
        ),
        Supplier { HealingHandler(HealingHandler.NORMAL_HEAL_AMOUNT, HealingHandler.SMALL_HEAL_SECONDS) }
    ),

    NATURES_RESPITE(
        "Nature's Respite",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Heal for "),
                ComponentUtils.create(
                    "+" + HealingHandler.BIG_HEAL_AMOUNT * HealingHandler.NORMAL_HEAL_SECONDS * 2,
                    NamedTextColor.GREEN
                ), ComponentUtils.create(Symbols.HEART, NamedTextColor.RED),
                ComponentUtils.create(" over "),
                ComponentUtils.create("+" + HealingHandler.NORMAL_HEAL_SECONDS + "s", NamedTextColor.GREEN)
            )
        ),
        Supplier { HealingHandler(HealingHandler.BIG_HEAL_AMOUNT, HealingHandler.NORMAL_HEAL_SECONDS) }
    ),

    FULL_BLOOM(
        "Full Bloom",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Heal for "),
                ComponentUtils.create(
                    "+" + HealingHandler.HEFTY_HEAL_AMOUNT * HealingHandler.NORMAL_HEAL_SECONDS * 2,
                    NamedTextColor.GREEN
                ), ComponentUtils.create(Symbols.HEART, NamedTextColor.RED),
                ComponentUtils.create(" over "),
                ComponentUtils.create("+" + HealingHandler.NORMAL_HEAL_SECONDS + "s", NamedTextColor.GREEN)
            )
        ),
        Supplier { HealingHandler(HealingHandler.HEFTY_HEAL_AMOUNT, HealingHandler.NORMAL_HEAL_SECONDS) }
    ),

    REJUVENATION_BURST(
        "Rejuvenation Burst",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Heal for "),
                ComponentUtils.create(
                    "+" + HealingHandler.COLOSSAL_HEAL_AMOUNT * HealingHandler.BIG_HEAL_SECONDS * 2,
                    NamedTextColor.GREEN
                ), ComponentUtils.create(Symbols.HEART, NamedTextColor.RED),
                ComponentUtils.create(" over "),
                ComponentUtils.create("+" + HealingHandler.BIG_HEAL_SECONDS + "s", NamedTextColor.GREEN)
            )
        ),
        Supplier { HealingHandler(HealingHandler.COLOSSAL_HEAL_AMOUNT, HealingHandler.BIG_HEAL_SECONDS) }
    ),

    INSTANT_TRANSMISSION(
        "Instant Transmission",
        listOf(
            ComponentUtils.create("Instantly teleport"),
            ComponentUtils.create("where you're looking!")
        ),
        Supplier { InstantTransmissionAbilityHandler() }),

    HOT_SHOT(
        "Hot Shot",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Shoot a "),
                ComponentUtils.create("fireball", NamedTextColor.RED),
                ComponentUtils.create(" in the direction")
            ),
            ComponentUtils.merge(ComponentUtils.create("you are looking that")),
            ComponentUtils.merge(
                ComponentUtils.create("deals "),
                ComponentUtils.create(HotShotAbilityHandler.DAMAGE, NamedTextColor.RED),
                ComponentUtils.create(" damage")
            )
        ),
        Supplier { HotShotAbilityHandler() }),

    // Admin abilities.
    ITEM_SWEEP(
        "Item Sweep",
        listOf(
            ComponentUtils.merge(ComponentUtils.create("Pick up every nearby item")),
            ComponentUtils.merge(ComponentUtils.create("regardless of owner status"))
        ),
        Supplier { ItemSweepAbilityHandler() }),
    ;

    fun getHandler(): AbilityHandler {
        return handler.get()
    }
}
