package xyz.devvydont.smprpg.ability

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import xyz.devvydont.smprpg.ability.handlers.*
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import java.util.function.Supplier
import kotlin.math.roundToInt

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

    WITHER_SKULL(
        "Wither Skull",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Shoot a "),
                ComponentUtils.create("wither skull", NamedTextColor.DARK_GRAY),
                ComponentUtils.create(" in the direction")
            ),
            ComponentUtils.merge(ComponentUtils.create("you are looking at")),
            ComponentUtils.merge(
                ComponentUtils.create("deals "),
                ComponentUtils.create(WitherSkullAbilityHandler.DAMAGE, NamedTextColor.RED),
                ComponentUtils.create(" damage and applies"),
                ComponentUtils.create(" Wither II (00:15)", NamedTextColor.RED)
            )
        ),
        Supplier { WitherSkullAbilityHandler() }),

    SHARD_STRIKE(
        "Shard Strike",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Launch an "),
                ComponentUtils.create("amethyst shard", NamedTextColor.LIGHT_PURPLE),
                ComponentUtils.create(" in the direction")
            ),
            ComponentUtils.merge(ComponentUtils.create("you are looking that")),
            ComponentUtils.merge(
                ComponentUtils.create("deals "),
                ComponentUtils.create(ShardStrikeAbilityHandler.DAMAGE, NamedTextColor.RED),
                ComponentUtils.create(" damage and deals ")
            ),
            ComponentUtils.merge(
                ComponentUtils.create((ShardStrikeAbilityHandler.DAMAGE * 0.25).roundToInt(), NamedTextColor.RED),
                ComponentUtils.create(" damage one second later.")
            )
        ),
        Supplier { ShardStrikeAbilityHandler() }),

    WIND_STORM(
        "Wind Storm",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Launches a flurry of"),
                ComponentUtils.create(" wind charges", NamedTextColor.AQUA),
                ComponentUtils.create(" around you,")
            ),
            ComponentUtils.merge(ComponentUtils.create("knocking away nearby mobs")),
        ),
        Supplier { WindStormAbilityHandler() }),

    WIND_ATTUNED(
        "Wind Attuned",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Launches a"),
                ComponentUtils.create(" wind charge", NamedTextColor.AQUA),
                ComponentUtils.create(" in front of you,")
            ),
            ComponentUtils.merge(ComponentUtils.create("knocking away nearby mobs")),
        ),
        Supplier { WindAttunedAbilityHandler() }),

    SONIC_SMASH(
        "Sonic Smash",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Charge up a sonic blast that ")
            ),
            ComponentUtils.merge(
                ComponentUtils.create("deals "),
                ComponentUtils.create(WitherSkullAbilityHandler.DAMAGE, NamedTextColor.RED),
                ComponentUtils.create(" damage to nearby enemies.")
            )
        ),
        Supplier { SonicSmashAbilityHandler() }),

    FIREBALL(
        "Fireball",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Launch a "),
                ComponentUtils.create("fireball", NamedTextColor.GOLD),
                ComponentUtils.create(" in the direction")
            ),
            ComponentUtils.merge(ComponentUtils.create("you are looking that")),
            ComponentUtils.merge(
                ComponentUtils.create("deals "),
                ComponentUtils.create(FireballAbilityHandler.DAMAGE, NamedTextColor.RED),
                ComponentUtils.create(" damage and ")
            ),
            ComponentUtils.merge(
                ComponentUtils.create("ignites", NamedTextColor.RED),
                ComponentUtils.create(" enemies in a ${FireballAbilityHandler.ENGULF_RADIUS.toInt()}")
            ),
            ComponentUtils.create("block radius.")
        ),
        Supplier { FireballAbilityHandler() }),

    SYPHON(
        "Syphon",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Cast a beam that deals "),
                ComponentUtils.create(SyphonAbilityHandler.DAMAGE, NamedTextColor.GOLD),
                ComponentUtils.create(" damage"),
            ),
            ComponentUtils.merge(ComponentUtils.create("and heals for "),
                ComponentUtils.create("10%", NamedTextColor.GREEN),
                ComponentUtils.create(" of the dealt damage to the target.")
            ),
            ComponentUtils.EMPTY,
            ComponentUtils.merge(
                ComponentUtils.create("If you do not hit a target,"),
                ComponentUtils.create(" you", NamedTextColor.RED)
            ),
            ComponentUtils.merge(
                ComponentUtils.create("will take "),
                ComponentUtils.create("20%", NamedTextColor.RED),
                ComponentUtils.create(" of the intended damage instead.")
            )
        ),
        Supplier { SyphonAbilityHandler() }),

    FANG_STRIKE(
        "Fang Strike",
        listOf(
            ComponentUtils.create("Summons a line of fangs"),
            ComponentUtils.merge(
                ComponentUtils.create("each dealing "),
                ComponentUtils.create(FangStrikeAbilityHandler.DAMAGE, NamedTextColor.GOLD),
                ComponentUtils.create(" damage.")
            )
        ),
        Supplier { FangStrikeAbilityHandler() }),

    DAMAGE_AURA(
        "Damage Aura",
        listOf(
            ComponentUtils.create("Shoots a projectile, spawning"),
            ComponentUtils.merge(
                ComponentUtils.create("a "),
                ComponentUtils.create("damaging aura", NamedTextColor.RED),
                ComponentUtils.create(" that will hurt")
            ),
            ComponentUtils.create("mobs in its range.")
        ),
        Supplier { DamageAuraAbilityHandler() }),

    HEALING_AURA(
        "Healing Aura",
        listOf(
            ComponentUtils.create("Shoots a projectile, spawning"),
            ComponentUtils.merge(
                ComponentUtils.create("a "),
                ComponentUtils.create("healing aura", NamedTextColor.GREEN),
                ComponentUtils.create(" that will heal")
            ),
            ComponentUtils.create("players in its range"),
            ComponentUtils.merge(
                ComponentUtils.create("for "),
                ComponentUtils.create("+40", NamedTextColor.GREEN),
                ComponentUtils.create(Symbols.HEART, NamedTextColor.RED),
                ComponentUtils.create("/s")
            )
        ),
        Supplier { HealingAuraAbilityHandler() }),

    CONJURE_PLATFORM(
        "Conjure Platform",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Creates a "),
                ComponentUtils.create("5x5", NamedTextColor.GRAY),
                ComponentUtils.create(" platform below")
            ),
            ComponentUtils.create("your feet, consisting of"),
            ComponentUtils.merge(
                ComponentUtils.create("temporary "),
                ComponentUtils.create("conjured blocks", NamedTextColor.LIGHT_PURPLE),
                ComponentUtils.create(".")
            ),
            ComponentUtils.EMPTY,
            ComponentUtils.create("Conjured blocks can be replaced mid-air."),
            ComponentUtils.create("Perfect for building!", NamedTextColor.GOLD)
        ),
        Supplier { ConjurePlatformAbilityHandler() }),

    CONJURE_WALL(
        "Conjure Wall",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Creates a "),
                ComponentUtils.create("5x5", NamedTextColor.GRAY),
                ComponentUtils.create(" wall in")
            ),
            ComponentUtils.create("front of you, consisting of"),
            ComponentUtils.merge(
                ComponentUtils.create("temporary "),
                ComponentUtils.create("conjured blocks", NamedTextColor.LIGHT_PURPLE),
                ComponentUtils.create(".")
            ),
            ComponentUtils.EMPTY,
            ComponentUtils.create("Conjured blocks can be replaced mid-air."),
            ComponentUtils.create("Perfect for a quick get-away!", NamedTextColor.GOLD)
        ),
        Supplier { ConjureWallAbilityHandler() }),

    SPREAD_SHOT(
        "Spread Shot",
        listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Shoots "),
                ComponentUtils.create("9", NamedTextColor.LIGHT_PURPLE),
                ComponentUtils.create(" mage beams")
            ),
            ComponentUtils.create("in a square area in front of you."),
            ComponentUtils.merge(
                ComponentUtils.create("Mobs caught in the beam will take "),
                ComponentUtils.create(SpreadShotAbilityHandler.DAMAGE, NamedTextColor.RED)
            ),
            ComponentUtils.create("damage per beam they get hit by.")
        ),
        Supplier { SpreadShotAbilityHandler() }),

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
