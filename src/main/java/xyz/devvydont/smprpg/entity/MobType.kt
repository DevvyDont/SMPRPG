package xyz.devvydont.smprpg.entity

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import xyz.devvydont.smprpg.util.formatting.Symbols

enum class MobType(val symbol: String, val symbolColor: TextColor) {
    AIRBORNE(Symbols.MOB_TYPE_AIRBORNE, NamedTextColor.GRAY),
    ANIMAL(Symbols.MOB_TYPE_ANIMAL, NamedTextColor.GREEN),
    AQUATIC(Symbols.MOB_TYPE_AQUATIC, NamedTextColor.AQUA),
    ARTHROPOD(Symbols.MOB_TYPE_ARTHROPOD, NamedTextColor.DARK_RED),
    BOSS(Symbols.MOB_TYPE_BOSS, NamedTextColor.DARK_PURPLE),
    CONSTRUCT(Symbols.MOB_TYPE_CONSTRUCT, NamedTextColor.GRAY),
    CROP_CRITTER(Symbols.MOB_TYPE_CROP_CRITTER, TextColor.color(0x1E8C00)),
    CUBIC(Symbols.MOB_TYPE_CUBIC, NamedTextColor.GREEN),
    DRACONIC(Symbols.MOB_TYPE_DRACONIC, TextColor.color(10_027_263)),
    ELEMENTAL(Symbols.MOB_TYPE_ELEMENTAL, NamedTextColor.WHITE),
    ENDER(Symbols.MOB_TYPE_ENDER, NamedTextColor.DARK_PURPLE),
    FAE(Symbols.MOB_TYPE_FAE, NamedTextColor.LIGHT_PURPLE),
    HOLY(Symbols.MOB_TYPE_HOLY, TextColor.color(14_933_914)),
    HUMANOID(Symbols.MOB_TYPE_HUMANOID, NamedTextColor.GRAY),
    ILLAGER(Symbols.MOB_TYPE_ILLAGER, NamedTextColor.DARK_GRAY),
    NETHER(Symbols.MOB_TYPE_NETHER, NamedTextColor.DARK_RED),
    PLANT(Symbols.MOB_TYPE_PLANT, NamedTextColor.DARK_GREEN),
    RARE(Symbols.MOB_TYPE_RARE, NamedTextColor.GOLD),
    SCULK(Symbols.MOB_TYPE_SCULK, TextColor.color(338_482)),
    SEA_CREATURE(Symbols.MOB_TYPE_SEA_CREATURE, NamedTextColor.DARK_AQUA),
    UNDEAD(Symbols.MOB_TYPE_UNDEAD, NamedTextColor.DARK_GREEN)
}