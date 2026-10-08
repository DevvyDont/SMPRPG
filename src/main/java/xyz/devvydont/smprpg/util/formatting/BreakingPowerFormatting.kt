package xyz.devvydont.smprpg.util.formatting

import net.kyori.adventure.text.format.TextColor

/**
 * The single source of truth for coloring a "breaking power" value (the Mining Power attribute).
 * Every surface that shows a breaking power — item lore, the stat menu, and the mining warning
 * popup — routes through here so the same tier maps to the same color everywhere, giving players an
 * at-a-glance read of how strong a tool a block needs (or a tool provides).
 *
 * The ramp mirrors the tool progression: white -> green -> cyan -> blue -> purple -> pink -> gold ->
 * orange -> red, indexed by tier (breaking power 1..9).
 */
object BreakingPowerFormatting {

    private val TIER_COLORS: List<TextColor> = listOf(
        TextColor.color(200, 200, 200), // 1
        TextColor.color(120, 220, 120), // 2
        TextColor.color(90, 220, 220),  // 3
        TextColor.color(100, 140, 255), // 4
        TextColor.color(190, 120, 255), // 5
        TextColor.color(255, 120, 200), // 6
        TextColor.color(255, 205, 70),  // 7
        TextColor.color(255, 140, 40),  // 8
        TextColor.color(255, 70, 70)    // 9
    )

    /**
     * The color representing the given breaking power, clamped to the defined tier range.
     * @param breakingPower The breaking power value (a block's requirement or a tool's stat).
     * @return The tier color to display it in.
     */
    @JvmStatic
    fun color(breakingPower: Double): TextColor {
        val tier = breakingPower.toInt().coerceIn(1, TIER_COLORS.size)
        return TIER_COLORS[tier - 1]
    }
}
