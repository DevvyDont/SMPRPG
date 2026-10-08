package xyz.devvydont.smprpg.listeners.damage.popup

/**
 * The semantic bucket a popup falls into. This is the single source of truth for "what kind of
 * popup is this", decoupled from how it looks (see [PopupStyle]) and how it moves
 * (see [PopupAnimation]).
 *
 * A popup only ever merges into another popup of the *same category* on the same victim, so
 * different damage/heal types stay as separate labels even when they land in quick succession
 * (e.g. a melee hit and an ability hit never share a label).
 *
 * The [priority] doubles as the default merge/display weight: when two hits of the same category
 * merge, the higher-weighted style is adopted, so a critical hit visually wins over a normal one.
 * Critical hits raise the weight further via [PopupStyle.weight].
 *
 * @property priority The default display weight; higher wins during a merge.
 */
enum class PopupCategory(val priority: Int) {
    // Damage
    ENVIRONMENTAL(10),
    POISON(15),
    WITHER(15),
    PHYSICAL(20),
    FIRE(25),
    ICE(25),
    LIGHTNING(25),
    MAGIC(30),
    ABSORPTION_LOSS(35),
    TRUE(40),

    // Healing
    HEAL_NATURAL(10),
    HEAL_MAGIC(20),
    ABSORPTION_GAIN(10),

    // Block-breaking feedback (reuses the popup infrastructure, never merges).
    MINING_POWER(0),
    REQUIRES_TOOL(0)
}
