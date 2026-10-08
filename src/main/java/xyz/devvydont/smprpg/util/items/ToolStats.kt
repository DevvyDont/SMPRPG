package xyz.devvydont.smprpg.util.items
import xyz.devvydont.smprpg.util.items.ToolGlobals.*

enum class ToolStats(val durability: Int, val power: Int, val fortune: Int, val speed: Int, val miningPower: Int, val skillReqLevel: Int) {
    WOOD(64, 2, 5, 200, 1, 0),

    TIN(128, 3, 20, 200, 2, 2),
    COPPER(160, 4, 15, 400, 2, 4),
    SILVER(96, 5, 30, 700, 2, 5),

    BRONZE(256, 6, 50, 400, 3, 6),
    IRON(320, 7, 30, 600, 3, 7),
    GOLD(192, 10, 60, 800, 3, 10),
    ROSE_GOLD(288, 12, 70, 700, 3, 12),

    STEEL(512, 11, 35, 700, 4, 11),
    MITHRIL(448, 13, 15, 900, 4, 13),

    TITANIUM(1_024, 15, 45, 800, 5, 15),
    ADAMANTIUM(1_536, 18, 90, 1_100, 5, 18),

    TUNGSTEN(768, 24, 50, 900, 6, 24),

    COBALT(1_152, 30, 60, 1_400, 7, 28),
    ORICHALCUM(1_280, 30, 80, 1_000, 7, 30),
    PLATINUM(768, 30, 100, 1_100, 7, 28),
    PALLADIUM(1_024, 30, 50, 1_500, 7, 30),

    NETHERITE(2_048, 40, 120, 1_600, 8, 35),
    AETHERIUM(1_536, 40, 80, 2_400, 8, 35),

    DRAGONSTEEL(2_560, 45, 150, 2_000, 9, 45);

    fun getArmorUnitDurability() : Double {
        return durability / 8.0
    }
}