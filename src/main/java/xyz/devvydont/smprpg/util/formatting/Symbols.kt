package xyz.devvydont.smprpg.util.formatting

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.TextColor

enum class TooltipStyle(val key : Key) {
    INFO(Key.key("smprpg:info"))
}

object Symbols {
    const val POWER: String = "✦"

    const val HEART: String = "❤"
    const val HEALTH_BAR_FILLED: String = "▰"
    const val HEALTH_BAR_EMPTY: String = "▱"
    const val MANA: String = "\uD83C\uDF1F"
    const val SHIELD: String = "⛊"
    const val SWORD: String = "\uD83D\uDDE1"
    const val SKULL: String = "☠"
    const val SPADE: String = "♠"
    const val CLOVER: String = "☘"

    const val BOOK: String = "\uD83D\uDCD6"

    const val STAR: String = "✯"
    const val ENCLOSED_STAR: String = "✪"
    const val SPARKLE_STAR: String = "✧"

    const val COIN: String = "⛁"

    const val RIGHT_ARROW: String = "➔"
    const val POINT: String = "⏵"

    const val FIRE: String = "\uD83D\uDD25"
    const val SNOWFLAKE: String = "❆"

    const val LOCK: String = "\uD83D\uDD12"

    const val SPARKLES: String = "✨"

    const val CHECK: String = "✔"
    const val X: String = "❌"

    const val PICKAXE: String = "⛏"
    const val AXE: String = "\uD83E\uDE93"

    const val MOB_TYPE_AIRBORNE: String = "✈"
    const val MOB_TYPE_ANIMAL: String = "☮"
    const val MOB_TYPE_AQUATIC: String = "\uD83C\uDF0A"
    const val MOB_TYPE_ARTHROPOD: String = "\uD83D\uDD77"
    const val MOB_TYPE_BOSS: String = "\uD83D\uDC7F"
    const val MOB_TYPE_CONSTRUCT: String = "\uD83E\uDD16"
    const val MOB_TYPE_CROP_CRITTER: String = "\uD83D\uDE9C"
    const val MOB_TYPE_CUBIC: String = "\uD83D\uDD32"
    const val MOB_TYPE_DRACONIC: String = "\uD83D\uDC09"
    const val MOB_TYPE_ELEMENTAL: String = "\uD83E\uDDEA"
    const val MOB_TYPE_ENDER: String = "\uD83E\uDDFF"
    const val MOB_TYPE_FAE: String = "\uD83E\uDDDA"
    const val MOB_TYPE_HOLY: String = "\uD83D\uDD46"
    const val MOB_TYPE_HUMANOID: String = "\uD83D\uDEB9"
    const val MOB_TYPE_ILLAGER: String = "⚔"
    const val MOB_TYPE_NETHER: String = "\uD83D\uDD25"
    const val MOB_TYPE_PLANT: String = "\uD83E\uDD66"
    const val MOB_TYPE_RARE: String = "\uD83E\uDE8E"
    const val MOB_TYPE_SCULK: String = "\uD83D\uDC42"
    const val MOB_TYPE_SEA_CREATURE: String = "⚓"
    const val MOB_TYPE_UNDEAD: String = "☠"

    const val OFFSET_NEG_1: String = "\uF801"
    const val OFFSET_NEG_2: String = "\uF802"
    const val OFFSET_NEG_3: String = "\uF803"
    const val OFFSET_NEG_4: String = "\uF804"
    const val OFFSET_NEG_5: String = "\uF805"
    const val OFFSET_NEG_6: String = "\uF806"
    const val OFFSET_NEG_7: String = "\uF807"
    const val OFFSET_NEG_8: String = "\uF808"

    const val OFFSET_NEG_16: String = "\uF809"
    const val OFFSET_NEG_32: String = "\uF80A"
    const val OFFSET_NEG_64: String = "\uF80B"
    const val OFFSET_NEG_128: String = "\uF80C"
    const val OFFSET_NEG_256: String = "\uF80D"
    const val OFFSET_NEG_512: String = "\uF80E"
    const val OFFSET_NEG_1024: String = "\uF80F"

    const val OFFSET_1: String = "\uF821"
    const val OFFSET_2: String = "\uF822"
    const val OFFSET_3: String = "\uF823"
    const val OFFSET_4: String = "\uF824"
    const val OFFSET_5: String = "\uF825"
    const val OFFSET_6: String = "\uF826"
    const val OFFSET_7: String = "\uF827"
    const val OFFSET_8: String = "\uF828"

    const val OFFSET_16: String = "\uF829"
    const val OFFSET_32: String = "\uF82A"
    const val OFFSET_64: String = "\uF82B"
    const val OFFSET_128: String = "\uF82C"
    const val OFFSET_256: String = "\uF82D"
    const val OFFSET_512: String = "\uF82E"
    const val OFFSET_1024: String = "\uF82F"

    const val NUTRITION_FULL: String = "\ua009"
    const val NUTRITION_HALF: String = "\ua00A"
    const val SATURATION_FULL: String = "\ua00B"
    const val SATURATION_HALF: String = "\ua00C"
    const val MANA_FULL: String = "\ua00D"
    const val MANA_HALF: String = "\ua00E"
    const val MANA_EMPTY: String = "\ua00F"

    val INVENTORY_TITLE_COLOR: TextColor = TextColor.color(63, 63, 63)

    const val OVERLAY_BG_OFFSET_STANDARD: String = OFFSET_NEG_128 + OFFSET_NEG_32 + OFFSET_NEG_2
    const val REFORGE_BACKGROUND: String = "\ub001"
    const val RECIPE_MENU: String = "\ub002"
    const val TRASH_MENU: String = "\ub003"
    const val SHAPED_RECIPE_MENU: String = "\ub004"
    const val SHAPELESS_RECIPE_MENU: String = "\ub005"
    const val SMITHING_RECIPE_MENU: String = "\ub006"
    const val STONECUTTER_RECIPE_MENU: String = "\ub007"
    const val FURNACE_RECIPE_MENU: String = "\ub008"
    const val TOOL_MODIFICATION_MENU: String = "\ub009"
    const val ENCHANTING_MENU: String = "\ub00A"
    const val SLAYER_MAIN_MENU: String = "\ub00B"
    const val SLAYER_SHAMBLING_MENU: String = "\ub00C"
    const val SLAYER_PIGLIN_MENU: String = "\ub00D"
    const val SLAYER_ILLAGER_MENU: String = "\ub00E"
    const val STAT_MAIN_MENU: String = "\ub00F"
    const val STAT_SUB_MENU: String = "\ub010"
    const val FREEZER_MENU: String = "\ub011"
    const val COOKING_POT_MENU: String = "\ub012"
    const val TOME_MENU: String = "\ub013"
    const val COOKING_POT_RECIPE_MENU: String = "\ub014"
    const val ANVIL_MENU: String = "\ub015"
    const val CRAFTING_3X3_MENU: String = "\ub016"

    const val ABILITY: String = "\uc001"
    const val PASSIVE: String = "\uc002"
}
