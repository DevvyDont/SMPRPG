package xyz.devvydont.smprpg.listeners.damage.popup

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * A fully resolved description of how one popup should look and animate. This is the only place
 * colors, decorations, and animation choices are bound together — [PopupStyleResolver] produces
 * these, and [DamagePopup] renders them.
 *
 * A flat color is expressed as [startColor] equal to [endColor]; a gradient uses two different
 * colors. Text is composed as `leading + value + trailing`, letting the resolver add tier symbols
 * (e.g. `✦✦`) or elemental glyphs without any special-casing here.
 *
 * @property category The semantic bucket, providing the merge family and default [weight].
 * @property startColor The left/only color of the text.
 * @property endColor The right color of the text (equal to [startColor] for a flat color).
 * @property animation The motion profile to play.
 * @property leading Text prepended to the value (symbols, glyphs).
 * @property trailing Text appended to the value (symbols, glyphs).
 * @property scale The resting scale of the popup.
 * @property prismatic When true, the color cycles through the spectrum each tick (top-tier crits).
 * @property weight The merge/display weight; a higher-weighted style wins when popups combine.
 */
data class PopupStyle(
    val category: PopupCategory,
    val startColor: TextColor,
    val endColor: TextColor,
    val animation: PopupAnimation,
    val leading: String = "",
    val trailing: String = "",
    val scale: Float = 1.0f,
    val prismatic: Boolean = false,
    val weight: Int = category.priority
) {

    /**
     * Renders the popup text for the given value string.
     * @param value The already-formatted value (number or message).
     * @return The decorated component.
     */
    fun render(value: String): Component {
        if (prismatic)
            return renderPrismatic(value, 0)

        val full = leading + value + trailing
        return if (startColor == endColor) ComponentUtils.create(full, startColor)
        else ComponentUtils.gradient(full, startColor, endColor)
    }

    /**
     * Renders the popup text with a spectrum gradient whose hue advances with [age], producing the
     * animated rainbow used by the highest crit tier.
     * @param value The already-formatted value.
     * @param age The current age of the popup in ticks.
     * @return The decorated component.
     */
    fun renderPrismatic(value: String, age: Int): Component {
        val full = leading + value + trailing
        val hue = (age % PRISMATIC_PERIOD_TICKS) / PRISMATIC_PERIOD_TICKS.toFloat()
        val start = hueColor(hue)
        val end = hueColor((hue + PRISMATIC_SPREAD) % 1.0f)
        return ComponentUtils.gradient(full, start, end)
    }

    companion object {
        // How many ticks it takes the prismatic hue to complete one full cycle.
        private const val PRISMATIC_PERIOD_TICKS = 20

        // How far apart (in hue) the two ends of the prismatic gradient sit.
        private const val PRISMATIC_SPREAD = 0.35f

        // Saturation/brightness used when converting a hue to a display color.
        private const val PRISMATIC_SATURATION = 0.85f
        private const val PRISMATIC_BRIGHTNESS = 1.0f

        // Masks off the alpha byte returned by the HSB conversion, leaving 0xRRGGBB.
        private const val RGB_MASK = 0xFFFFFF

        private fun hueColor(hue: Float): TextColor {
            val rgb = java.awt.Color.HSBtoRGB(hue, PRISMATIC_SATURATION, PRISMATIC_BRIGHTNESS)
            return TextColor.color(rgb and RGB_MASK)
        }
    }
}
