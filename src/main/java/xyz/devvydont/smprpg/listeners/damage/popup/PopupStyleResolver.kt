package xyz.devvydont.smprpg.listeners.damage.popup

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.damage.DamageType
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason
import xyz.devvydont.smprpg.util.formatting.BreakingPowerFormatting
import xyz.devvydont.smprpg.util.formatting.Symbols
import kotlin.math.max
import kotlin.math.min

/**
 * Turns damage/heal context into a concrete [PopupStyle]. This is the single home for the popup
 * palette and all crit-tier tuning, so re-theming the popups means editing constants here and
 * nothing else.
 *
 * Damage is bucketed primarily by the vanilla [DamageCause] (which cleanly distinguishes poison,
 * fire, fall, etc.) and falls back to the Bukkit [DamageType] to catch ability/magic damage.
 * Critical hits layer a tier-scaled "heat" gradient and escalating decoration on top of the base
 * category color, so a magic crit reads magic-purple-hot while a physical crit reads red-hot.
 */
object PopupStyleResolver {

    // --- Base palette (per damage/heal category) ---
    private val PHYSICAL: TextColor = TextColor.color(180, 100, 100)
    private val MAGIC: TextColor = TextColor.color(180, 120, 255)
    private val FIRE: TextColor = TextColor.color(255, 140, 40)
    private val ICE: TextColor = TextColor.color(120, 220, 255)
    private val LIGHTNING: TextColor = TextColor.color(255, 235, 90)
    private val POISON: TextColor = TextColor.color(120, 200, 60)
    private val WITHER: TextColor = TextColor.color(90, 110, 80)
    private val TRUE: TextColor = TextColor.color(245, 245, 245)
    private val ENVIRONMENTAL: TextColor = NamedTextColor.GRAY
    private val ABSORPTION_LOSS: TextColor = NamedTextColor.GOLD
    private val ABSORPTION_GAIN: TextColor = NamedTextColor.YELLOW
    private val HEAL_NATURAL: TextColor = NamedTextColor.GREEN
    private val HEAL_MAGIC: TextColor = TextColor.color(90, 230, 190)

    // --- Block-breaking feedback palette ---
    private val REQUIRES_TOOL_COLOR: TextColor = TextColor.color(125, 144, 255)

    // --- Critical-hit tuning ---
    // A vivid physical crit start color; other categories reuse their (already vivid) base color.
    private val CRIT_PHYSICAL: TextColor = TextColor.color(255, 70, 70)

    // The escalating "heat" the crit gradient runs toward, one entry per tier.
    private val CRIT_HEAT_TIER_1: TextColor = TextColor.color(255, 138, 0)
    private val CRIT_HEAT_TIER_2: TextColor = TextColor.color(255, 205, 0)
    private val CRIT_HEAT_TIER_3: TextColor = TextColor.color(255, 255, 210)

    // The tier at (and above) which the crit switches to the animated prismatic style.
    private const val PRISMATIC_TIER = 4

    // How much the crit popup grows per tier, and the cap.
    private const val CRIT_SCALE_STEP = 0.12f
    private const val CRIT_MAX_SCALE = 1.6f

    // Crit weight floor, ensuring any crit outranks any normal hit during a merge; tier is added on.
    private const val CRIT_WEIGHT_BASE = 1000

    /**
     * Resolves the style for a damage popup.
     * @param damageType The Bukkit damage type of the source.
     * @param cause The vanilla damage cause.
     * @param critical Whether the hit was a critical.
     * @param criticalTier The crit tier (see [xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent]).
     * @return The style to render.
     */
    fun resolveDamage(damageType: DamageType, cause: DamageCause, critical: Boolean, criticalTier: Int): PopupStyle {
        val category = categorize(damageType, cause)
        if (critical)
            return critStyle(category, max(1, criticalTier))
        return normalStyle(category)
    }

    /**
     * Resolves the style for a healing popup based on why the entity regained health.
     * @param reason The regain reason.
     * @return The style to render.
     */
    fun resolveHeal(reason: RegainReason): PopupStyle {
        return when (reason) {
            RegainReason.MAGIC, RegainReason.MAGIC_REGEN, RegainReason.CUSTOM,
            RegainReason.WITHER_SPAWN, RegainReason.ENDER_CRYSTAL ->
                flat(PopupCategory.HEAL_MAGIC, HEAL_MAGIC, PopupAnimation.HEAL_RISE, trailing = " " + Symbols.SPARKLES)

            else ->
                flat(PopupCategory.HEAL_NATURAL, HEAL_NATURAL, PopupAnimation.HEAL_RISE)
        }
    }

    /**
     * The style used when an entity's absorption (temporary armor) is chipped away.
     */
    fun absorptionLoss(): PopupStyle =
        flat(PopupCategory.ABSORPTION_LOSS, ABSORPTION_LOSS, PopupAnimation.FIRM_SETTLE)

    /**
     * The style used when an entity gains absorption (temporary armor).
     */
    fun absorptionGain(): PopupStyle =
        flat(PopupCategory.ABSORPTION_GAIN, ABSORPTION_GAIN, PopupAnimation.FIRM_SETTLE)

    /**
     * The style for the block-breaking-power feedback popup, colored by the required tier so the
     * warning conveys how strong a tool the block needs.
     * @param breakingPower The block's required breaking power.
     */
    fun miningPower(breakingPower: Float): PopupStyle {
        val color = BreakingPowerFormatting.color(breakingPower.toDouble())
        return flat(PopupCategory.MINING_POWER, color, PopupAnimation.WARNING_SHAKE, leading = Symbols.PICKAXE)
    }

    /**
     * The style for the "requires a better tool" feedback popup.
     */
    fun requiresTool(): PopupStyle =
        flat(PopupCategory.REQUIRES_TOOL, REQUIRES_TOOL_COLOR, PopupAnimation.WARNING_SHAKE)

    /**
     * Buckets a damage source into a [PopupCategory], preferring the vanilla cause and falling back
     * to the damage type for ability/magic damage.
     */
    private fun categorize(damageType: DamageType, cause: DamageCause): PopupCategory {
        return when (cause) {
            DamageCause.POISON -> PopupCategory.POISON
            DamageCause.WITHER -> PopupCategory.WITHER
            DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA,
            DamageCause.HOT_FLOOR, DamageCause.MELTING, DamageCause.CAMPFIRE -> PopupCategory.FIRE
            DamageCause.FREEZE -> PopupCategory.ICE
            DamageCause.LIGHTNING, DamageCause.SONIC_BOOM -> PopupCategory.LIGHTNING
            DamageCause.MAGIC -> PopupCategory.MAGIC
            DamageCause.FALL, DamageCause.DROWNING, DamageCause.SUFFOCATION, DamageCause.VOID,
            DamageCause.STARVATION, DamageCause.CRAMMING, DamageCause.FLY_INTO_WALL,
            DamageCause.DRYOUT, DamageCause.WORLD_BORDER, DamageCause.CONTACT -> PopupCategory.ENVIRONMENTAL
            else -> categorizeByType(damageType)
        }
    }

    private fun categorizeByType(damageType: DamageType): PopupCategory {
        return when (damageType) {
            DamageType.MAGIC, DamageType.INDIRECT_MAGIC -> PopupCategory.MAGIC
            else -> PopupCategory.PHYSICAL
        }
    }

    /**
     * The resting style for a non-critical hit of the given category.
     */
    private fun normalStyle(category: PopupCategory): PopupStyle {
        return when (category) {
            PopupCategory.PHYSICAL -> flat(category, PHYSICAL, PopupAnimation.POP_BOUNCE)
            PopupCategory.MAGIC -> flat(category, MAGIC, PopupAnimation.POP_BOUNCE)
            PopupCategory.FIRE -> flat(category, FIRE, PopupAnimation.POP_BOUNCE, trailing = " " + Symbols.FIRE)
            PopupCategory.ICE -> flat(category, ICE, PopupAnimation.POP_BOUNCE, trailing = " " + Symbols.SNOWFLAKE)
            PopupCategory.LIGHTNING -> flat(category, LIGHTNING, PopupAnimation.POP_BOUNCE)
            PopupCategory.POISON -> flat(category, POISON, PopupAnimation.DOT_TICK)
            PopupCategory.WITHER -> flat(category, WITHER, PopupAnimation.DOT_TICK, trailing = " " + Symbols.SKULL)
            PopupCategory.TRUE -> flat(category, TRUE, PopupAnimation.POP_BOUNCE)
            PopupCategory.ENVIRONMENTAL -> flat(category, ENVIRONMENTAL, PopupAnimation.POP_BOUNCE)
            PopupCategory.ABSORPTION_LOSS -> flat(category, ABSORPTION_LOSS, PopupAnimation.FIRM_SETTLE)
            else -> flat(PopupCategory.PHYSICAL, PHYSICAL, PopupAnimation.POP_BOUNCE)
        }
    }

    /**
     * The style for a critical hit, escalating color, decoration, and size with the tier.
     */
    private fun critStyle(category: PopupCategory, tier: Int): PopupStyle {
        val prismatic = tier >= PRISMATIC_TIER
        val symbol = if (prismatic) Symbols.SPARKLE_STAR else Symbols.POWER
        val count = if (prismatic) 1 else tier
        val decoration = symbol.repeat(count)
        val scale = min(CRIT_MAX_SCALE, 1.0f + (tier - 1) * CRIT_SCALE_STEP)

        return PopupStyle(
            category = category,
            startColor = critBaseColor(category),
            endColor = critHeatColor(tier),
            animation = PopupAnimation.CRIT_BURST,
            leading = "$decoration ",
            trailing = " $decoration",
            scale = scale,
            prismatic = prismatic,
            weight = CRIT_WEIGHT_BASE + tier
        )
    }

    /**
     * The vivid start color of a crit gradient, tinted by the damage category.
     */
    private fun critBaseColor(category: PopupCategory): TextColor {
        return when (category) {
            PopupCategory.PHYSICAL -> CRIT_PHYSICAL
            PopupCategory.MAGIC -> MAGIC
            PopupCategory.FIRE -> FIRE
            PopupCategory.ICE -> ICE
            PopupCategory.LIGHTNING -> LIGHTNING
            PopupCategory.POISON -> POISON
            PopupCategory.WITHER -> WITHER
            PopupCategory.TRUE -> TRUE
            else -> CRIT_PHYSICAL
        }
    }

    /**
     * The "heat" the crit gradient runs toward for the given tier.
     */
    private fun critHeatColor(tier: Int): TextColor {
        return when (tier) {
            1 -> CRIT_HEAT_TIER_1
            2 -> CRIT_HEAT_TIER_2
            else -> CRIT_HEAT_TIER_3
        }
    }

    private fun flat(
        category: PopupCategory,
        color: TextColor,
        animation: PopupAnimation,
        leading: String = "",
        trailing: String = ""
    ): PopupStyle = PopupStyle(category, color, color, animation, leading, trailing)
}
