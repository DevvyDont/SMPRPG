package xyz.devvydont.smprpg.listeners.damage.popup

import org.bukkit.entity.TextDisplay
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f

/**
 * The motion profile of a popup. Rather than one bespoke routine per category, each animation is a
 * small bundle of numeric parameters fed to a single shared keyframe builder. Motion is driven
 * entirely through Minecraft's client-side [Display][org.bukkit.entity.Display] interpolation (the
 * same [Transformation] + interpolation approach used by the ability visuals), so only a handful of
 * transform updates are ever sent — the client smooths the frames in between.
 *
 * The generic lifecycle is: spawn invisible, pop up to an overshoot scale, settle back to the base
 * scale (optionally with a shake), drift upward, then shrink away. A couple of profiles
 * ([STATIC], [WARNING_SHAKE]) supply their own keyframe sequences instead.
 *
 * @property popTicks Ticks spent scaling from nothing up to the [overshoot] scale.
 * @property overshoot The scale multiplier at the peak of the pop, before settling to 1.0.
 * @property settleTicks Ticks spent easing from the overshoot back to the resting scale.
 * @property riseHeight How far (in blocks) the popup drifts upward over its lifetime.
 * @property shake Random horizontal jitter magnitude applied at the settle frame (0 disables it).
 * @property fadeTicks Ticks spent shrinking away at the end of life.
 * @property lifetimeTicks Total lifetime of the popup in ticks.
 */
enum class PopupAnimation(
    val popTicks: Int,
    val overshoot: Float,
    val settleTicks: Int,
    val riseHeight: Float,
    val shake: Float,
    val fadeTicks: Int,
    val lifetimeTicks: Int
) {
    POP_BOUNCE(popTicks = 3, overshoot = 1.25f, settleTicks = 3, riseHeight = 0.5f, shake = 0.0f, fadeTicks = 5, lifetimeTicks = 26),
    CRIT_BURST(popTicks = 2, overshoot = 1.5f, settleTicks = 4, riseHeight = 0.65f, shake = 0.06f, fadeTicks = 6, lifetimeTicks = 32),
    DOT_TICK(popTicks = 2, overshoot = 1.1f, settleTicks = 2, riseHeight = 0.25f, shake = 0.0f, fadeTicks = 3, lifetimeTicks = 14),
    HEAL_RISE(popTicks = 4, overshoot = 1.0f, settleTicks = 3, riseHeight = 0.75f, shake = 0.0f, fadeTicks = 6, lifetimeTicks = 30),
    FIRM_SETTLE(popTicks = 2, overshoot = 1.12f, settleTicks = 2, riseHeight = 0.15f, shake = 0.0f, fadeTicks = 4, lifetimeTicks = 22),

    // A "denied" motion for block-breaking warnings: pop in, then a damped left-right shake, hold,
    // and shrink away. No upward drift, since it is anchored to the block that was rejected.
    WARNING_SHAKE(popTicks = 1, overshoot = 1.15f, settleTicks = 1, riseHeight = 0.0f, shake = 0.0f, fadeTicks = 6, lifetimeTicks = 30),

    STATIC(popTicks = 0, overshoot = 1.0f, settleTicks = 0, riseHeight = 0.0f, shake = 0.0f, fadeTicks = 0, lifetimeTicks = 40);

    /**
     * A single point in the animation timeline. When the popup's age reaches [atAge] the display's
     * transformation is set to the given [scale] and [sway]/[rise] offsets with an interpolation
     * lasting [duration] ticks, letting the client tween from the previous frame.
     */
    private data class Keyframe(
        val atAge: Int,
        val duration: Int,
        val scale: Float,
        val sway: Float,
        val rise: Float,
        val shaken: Boolean
    )

    private val frames: List<Keyframe> by lazy { buildFrames() }

    private fun buildFrames(): List<Keyframe> {
        return when (this) {
            STATIC -> listOf(Keyframe(0, 0, 1.0f, 0.0f, 0.0f, false))
            WARNING_SHAKE -> buildShakeFrames()
            else -> buildStandardFrames()
        }
    }

    private fun buildStandardFrames(): List<Keyframe> {
        val settleAge = 1 + popTicks
        val driftAge = settleAge + settleTicks
        val fadeAge = lifetimeTicks - fadeTicks
        val driftDuration = maxOf(1, fadeAge - driftAge)
        return listOf(
            Keyframe(0, 0, 0.0f, 0.0f, 0.0f, false),                              // spawn invisible
            Keyframe(1, popTicks, overshoot, 0.0f, 0.0f, false),                  // pop up to the overshoot
            Keyframe(settleAge, settleTicks, 1.0f, 0.0f, riseHeight * SETTLE_RISE_FRACTION, true), // settle (+ shake)
            Keyframe(driftAge, driftDuration, 1.0f, 0.0f, riseHeight * DRIFT_RISE_FRACTION, false), // slow drift up
            Keyframe(fadeAge, fadeTicks, 0.0f, 0.0f, riseHeight, false)           // shrink away
        )
    }

    private fun buildShakeFrames(): List<Keyframe> {
        val frames = ArrayList<Keyframe>()
        frames.add(Keyframe(0, 0, 0.0f, 0.0f, 0.0f, false))       // spawn invisible
        frames.add(Keyframe(1, 1, overshoot, 0.0f, 0.0f, false))  // quick pop-in

        var age = 2
        var sign = 1.0f
        for (swing in SHAKE_SWINGS) {
            frames.add(Keyframe(age, 1, 1.0f, SHAKE_MAX_SWAY * swing * sign, 0.0f, false))
            sign = -sign
            age++
        }

        frames.add(Keyframe(age, 1, 1.0f, 0.0f, 0.0f, false))     // recenter and hold
        val fadeAge = lifetimeTicks - fadeTicks
        frames.add(Keyframe(fadeAge, fadeTicks, 0.0f, 0.0f, 0.0f, false)) // shrink away
        return frames
    }

    /**
     * Advances the popup's animation. If [age] has moved past the last applied keyframe, the newest
     * eligible keyframe is pushed to the display.
     *
     * @param display The text display being animated.
     * @param baseScale The resting scale of the popup, multiplied into every keyframe's scale.
     * @param age The current age of the popup in ticks.
     * @param lastApplied The index of the keyframe applied on the previous call (-1 if none).
     * @return The index of the most recently applied keyframe.
     */
    fun applyFrame(display: TextDisplay, baseScale: Float, age: Int, lastApplied: Int): Int {
        var target = lastApplied
        for (index in frames.indices)
            if (frames[index].atAge <= age)
                target = index

        if (target <= lastApplied)
            return lastApplied

        apply(display, frames[target], baseScale)
        return target
    }

    private fun apply(display: TextDisplay, frame: Keyframe, baseScale: Float) {
        val jitter =
            if (frame.shaken && shake > 0.0f) (Math.random().toFloat() - 0.5f) * shake
            else 0.0f

        display.interpolationDelay = 0
        display.interpolationDuration = frame.duration
        display.transformation = Transformation(
            Vector3f(frame.sway + jitter, frame.rise, 0.0f),
            AxisAngle4f(),
            Vector3f(frame.scale * baseScale),
            AxisAngle4f()
        )
    }

    companion object {
        // What fraction of the total rise the popup has reached by the settle and drift frames.
        private const val SETTLE_RISE_FRACTION = 0.25f
        private const val DRIFT_RISE_FRACTION = 0.85f

        // The damped left-right swing amplitudes (as a fraction of SHAKE_MAX_SWAY) for WARNING_SHAKE.
        private val SHAKE_SWINGS = floatArrayOf(1.0f, 0.75f, 0.5f, 0.3f, 0.15f)

        // The peak horizontal sway of the warning shake, in blocks.
        private const val SHAKE_MAX_SWAY = 0.16f
    }
}
