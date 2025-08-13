package xyz.devvydont.smprpg.ability

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import xyz.devvydont.smprpg.ability.handlers.passive.AbyssalAnnihilationListener
import xyz.devvydont.smprpg.ability.handlers.passive.AnglerListener
import xyz.devvydont.smprpg.entity.fishing.SeaCreature
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Represents a simple "passive ability". These are much more simple due to the fact they simple just "exist" on items
 * rather than actual providing meaningful functionality like its [Ability] counterpart.
 */
enum class Passive(val description: Component) {
    ANGLER(
        ComponentUtils.merge(
            ComponentUtils.create("Deals "),
            ComponentUtils.create(AnglerListener.MULTIPLIER.toString() + "x damage", NamedTextColor.RED),
            ComponentUtils.create(" to "),
            ComponentUtils.create("Sea Creatures", SeaCreature.NAME_COLOR)
        )
    ),
    ABYSSAL_ANNIHILATION(
        ComponentUtils.merge(
            ComponentUtils.create("Deals "),
            ComponentUtils.create(AbyssalAnnihilationListener.MULTIPLIER.toString() + "x damage", NamedTextColor.RED),
            ComponentUtils.create(" to "),
            ComponentUtils.create("Sea Creatures", SeaCreature.NAME_COLOR)
        )
    ),
    ;
}
