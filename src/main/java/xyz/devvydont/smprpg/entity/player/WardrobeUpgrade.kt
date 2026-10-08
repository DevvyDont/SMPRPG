package xyz.devvydont.smprpg.entity.player

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material

enum class UpgradeCategory(val displayName: String, val icon: Material, val color: NamedTextColor) {
    LEVEL("Skill Milestones", Material.EXPERIENCE_BOTTLE, NamedTextColor.GREEN),
    COIN("Coin Purchases", Material.GOLD_INGOT, NamedTextColor.GOLD),
    TOKEN("Wardrobe Tokens", Material.ARMOR_STAND, NamedTextColor.AQUA),
    SPECIAL("Special", Material.NETHER_STAR, NamedTextColor.LIGHT_PURPLE)
}

enum class WardrobeUpgrade(val displayName: String, val category: UpgradeCategory) {

    // Skill average milestones (10 slots)
    LEVEL_10("Skill Average 10", UpgradeCategory.LEVEL),
    LEVEL_20("Skill Average 20", UpgradeCategory.LEVEL),
    LEVEL_30("Skill Average 30", UpgradeCategory.LEVEL),
    LEVEL_40("Skill Average 40", UpgradeCategory.LEVEL),
    LEVEL_50("Skill Average 50", UpgradeCategory.LEVEL),
    LEVEL_60("Skill Average 60", UpgradeCategory.LEVEL),
    LEVEL_70("Skill Average 70", UpgradeCategory.LEVEL),
    LEVEL_80("Skill Average 80", UpgradeCategory.LEVEL),
    LEVEL_90("Skill Average 90", UpgradeCategory.LEVEL),
    LEVEL_100("Skill Average 100", UpgradeCategory.LEVEL),

    // Coin purchases (5 slots)
    COIN_TIER_1("Coin Purchase I", UpgradeCategory.COIN),
    COIN_TIER_2("Coin Purchase II", UpgradeCategory.COIN),
    COIN_TIER_3("Coin Purchase III", UpgradeCategory.COIN),
    COIN_TIER_4("Coin Purchase IV", UpgradeCategory.COIN),
    COIN_TIER_5("Coin Purchase V", UpgradeCategory.COIN),

    // Wardrobe tokens (5 slots)
    TOKEN_COMMON("Common Wardrobe Token", UpgradeCategory.TOKEN),
    TOKEN_UNCOMMON("Uncommon Wardrobe Token", UpgradeCategory.TOKEN),
    TOKEN_RARE("Rare Wardrobe Token", UpgradeCategory.TOKEN),
    TOKEN_EPIC("Epic Wardrobe Token", UpgradeCategory.TOKEN),
    TOKEN_LEGENDARY("Legendary Wardrobe Token", UpgradeCategory.TOKEN),

    // Special achievements (5 slots)
    SPECIAL_ALL_ADVANCEMENTS("All Advancements", UpgradeCategory.SPECIAL),
    SPECIAL_2("???", UpgradeCategory.SPECIAL),
    SPECIAL_3("???", UpgradeCategory.SPECIAL),
    SPECIAL_4("???", UpgradeCategory.SPECIAL),
    SPECIAL_5("???", UpgradeCategory.SPECIAL);

    companion object {
        const val DEFAULT_SLOTS = 3

        val LEVEL_THRESHOLDS = listOf(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)

        val COIN_COSTS = listOf(1_000L, 10_000L, 100_000L, 500_000L, 2_500_000L)

        val LEVEL_UPGRADES = listOf(
            LEVEL_10, LEVEL_20, LEVEL_30, LEVEL_40, LEVEL_50,
            LEVEL_60, LEVEL_70, LEVEL_80, LEVEL_90, LEVEL_100
        )

        val COIN_UPGRADES = listOf(COIN_TIER_1, COIN_TIER_2, COIN_TIER_3, COIN_TIER_4, COIN_TIER_5)

        val TOKEN_UPGRADES = listOf(TOKEN_COMMON, TOKEN_UNCOMMON, TOKEN_RARE, TOKEN_EPIC, TOKEN_LEGENDARY)

        fun byCategory(category: UpgradeCategory): List<WardrobeUpgrade> =
            entries.filter { it.category == category }

        fun getLevelThreshold(upgrade: WardrobeUpgrade): Int =
            LEVEL_THRESHOLDS[LEVEL_UPGRADES.indexOf(upgrade)]

        fun getCoinCost(upgrade: WardrobeUpgrade): Long =
            COIN_COSTS[COIN_UPGRADES.indexOf(upgrade)]
    }
}
