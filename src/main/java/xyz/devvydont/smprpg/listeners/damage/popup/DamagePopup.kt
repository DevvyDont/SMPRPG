package xyz.devvydont.smprpg.listeners.damage.popup

import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.AxisAngle4f
import org.joml.Vector3f
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.util.time.TickTime
import java.text.DecimalFormat
import java.util.function.Consumer
import kotlin.math.max
import kotlin.math.roundToLong

/**
 * The single entry point for spawning in-world damage/heal/mining popups. It owns the shared
 * [TextDisplay] spawning + animation lifecycle (so there is exactly one code path), and manages
 * merging so repeated events collapse into a single, re-anchoring label instead of flooding the
 * screen:
 * - Damage/heal accumulate per victim entity + [PopupCategory]; distinct types stay separate.
 * - Mining warnings de-duplicate per viewer + category, so a player spamming a failing block just
 *   replays the existing warning rather than stacking new ones.
 *
 * In both cases a merged label re-anchors to where the latest event occurred so it tracks a moving
 * target.
 */
object DamagePopup {

    private val NUMBER_FORMATTER = DecimalFormat("#,###,###")
    private val TRANSPARENT: Color = Color.fromARGB(0, 0, 0, 0)
    private val NO_OFFSET: Vector = Vector(0.0, 0.0, 0.0)

    // Live, mergeable popups keyed by "<owner uuid>|<popup category>" (owner = victim or viewer).
    private val active: MutableMap<String, ActivePopup> = HashMap()

    /**
     * The mutable state backing one live popup. The animation task reads [style] and [age] each
     * tick, so a merge simply mutates these fields and replays the pop.
     *
     * @property offset The label's fixed positional offset from its anchor, reused when the label
     *                  re-anchors on a merge so it stays put relative to a moving target.
     */
    private class ActivePopup(
        val key: String,
        val display: TextDisplay,
        var accumulated: Double,
        var style: PopupStyle,
        val offset: Vector
    ) {
        var age: Int = 0
        var lastKeyframe: Int = -1

        /**
         * Restarts the animation from the pop frame (skipping the invisible spawn frame) so a merged
         * hit visibly re-pops, and extends the popup's life since expiry is measured from [age].
         */
        fun replay() {
            age = 1
            lastKeyframe = 0
        }
    }

    /**
     * Spawns or merges a numeric popup tied to a victim entity. If a live popup of the same category
     * already exists on this victim, the amounts combine, the stronger style wins, the label
     * re-anchors to where this hit occurred (so it follows a moving target), and the pop replays;
     * otherwise a fresh popup appears near the victim's eyes. Different categories never merge, so
     * distinct damage/heal types stay as separate labels.
     *
     * @param victim The entity the popup belongs to.
     * @param amount The raw amount; rounded, clamped to at least 1, and skipped entirely if it
     *               rounds to 0.
     * @param style The resolved style to render.
     */
    fun spawn(victim: LivingEntity, amount: Double, style: PopupStyle) {
        val contribution = normalize(amount) ?: return
        val key = keyFor(victim.uniqueId.toString(), style)

        val existing = active[key]
        if (existing != null && existing.display.isValid) {
            existing.accumulated += contribution
            if (style.weight >= existing.style.weight)
                existing.style = style
            existing.display.text(existing.style.render(formatAmount(existing.accumulated)))
            existing.display.teleport(victim.eyeLocation.add(existing.offset))
            existing.replay()
            return
        }

        active.remove(key)
        val offset = randomOffset()
        launch(victim.eyeLocation.add(offset), style, contribution, style.render(formatAmount(contribution)), key, offset)
    }

    /**
     * Spawns or refreshes a numeric block-breaking warning (insufficient breaking power) for a
     * player. See [warn].
     * @return True if a fresh label was spawned, false if an existing one was refreshed.
     */
    fun spawnMiningWarning(viewer: Player, location: Location, style: PopupStyle, breakingPower: Double): Boolean =
        warn(viewer, location, style, style.render(formatAmount(breakingPower)))

    /**
     * Spawns or refreshes a text block-breaking warning (wrong tool) for a player. See [warn].
     * @return True if a fresh label was spawned, false if an existing one was refreshed.
     */
    fun spawnMiningWarning(viewer: Player, location: Location, style: PopupStyle, message: String): Boolean =
        warn(viewer, location, style, style.render(message))

    /**
     * De-duplicates block-breaking warnings per viewer + category. If the same warning is already
     * live for this player, its label is moved to the new location and its shake replays; only a
     * genuinely new warning spawns a fresh label. Callers use the return value to gate feedback such
     * as the "denied" sound so spamming a failing action does not spam the effect.
     *
     * @return True if a fresh label was spawned, false if an existing one was refreshed.
     */
    private fun warn(viewer: Player, location: Location, style: PopupStyle, component: Component): Boolean {
        val key = keyFor(viewer.uniqueId.toString(), style)
        val target = location.clone()

        val existing = active[key]
        if (existing != null && existing.display.isValid) {
            existing.style = style
            existing.display.text(component)
            existing.display.teleport(target)
            existing.replay()
            return false
        }

        active.remove(key)
        launch(target, style, 0.0, component, key, NO_OFFSET)
        return true
    }

    private fun launch(
        location: Location,
        style: PopupStyle,
        accumulated: Double,
        component: Component,
        key: String,
        offset: Vector
    ): ActivePopup {
        val display = createDisplay(location, style, component)
        val popup = ActivePopup(key, display, accumulated, style, offset)
        active[key] = popup
        animate(popup)
        return popup
    }

    private fun createDisplay(location: Location, style: PopupStyle, component: Component): TextDisplay {
        val initialScale = if (style.animation == PopupAnimation.STATIC) style.scale else 0.0f
        return location.world.spawn(location, TextDisplay::class.java, Consumer { display ->
            display.isPersistent = false
            display.text(component)
            display.billboard = Display.Billboard.CENTER
            display.isShadowed = true
            display.isSeeThrough = false
            display.backgroundColor = TRANSPARENT
            display.transformation =
                Transformation(Vector3f(), AxisAngle4f(), Vector3f(initialScale), AxisAngle4f())
        })
    }

    private fun animate(popup: ActivePopup) {
        object : BukkitRunnable() {
            override fun run() {
                if (!popup.display.isValid) {
                    active.remove(popup.key)
                    cancel()
                    return
                }

                if (popup.style.prismatic)
                    popup.display.text(popup.style.renderPrismatic(formatAmount(popup.accumulated), popup.age))

                popup.lastKeyframe =
                    popup.style.animation.applyFrame(popup.display, popup.style.scale, popup.age, popup.lastKeyframe)

                if (popup.age >= popup.style.animation.lifetimeTicks) {
                    popup.display.remove()
                    active.remove(popup.key)
                    cancel()
                    return
                }

                popup.age++
            }
        }.runTaskTimer(plugin, TickTime.INSTANTANEOUSLY, TickTime.TICK)
    }

    private fun keyFor(ownerId: String, style: PopupStyle): String =
        "$ownerId|${style.category}"

    /**
     * Rounds an amount, clamps it to at least 1, and returns null if there is nothing to show.
     */
    private fun normalize(amount: Double): Double? {
        val rounded = amount.roundToLong()
        if (rounded == 0L)
            return null
        return max(1.0, rounded.toDouble())
    }

    private fun formatAmount(amount: Double): String =
        NUMBER_FORMATTER.format(max(1L, amount.roundToLong()))

    private fun randomOffset(): Vector =
        Vector(Math.random() - 0.5, Math.random() + 0.3, Math.random() - 0.5)
}
