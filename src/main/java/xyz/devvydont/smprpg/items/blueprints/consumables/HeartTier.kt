package xyz.devvydont.smprpg.items.blueprints.consumables

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import xyz.devvydont.smprpg.items.CustomItemType

/**
 * Defines the dimension-gated tiers of permanent maximum-health consumables ("hearts"),
 * inspired by Terraria's heart crystals.
 *
 * Each tier grants a fixed amount of maximum health per consumption and may be consumed up to
 * [HeartConsumableBlueprint.MAX_CONSUMPTIONS] times. Tiers stack additively, so a fully maxed
 * player receives the sum of every tier's contribution.
 *
 * The intended progression mirrors the server's dimension day-gating: a player can only reach a
 * tier's reagents once they have access to its origin dimension, which keeps the granted health
 * proportional to the player's power at that stage.
 */
enum class HeartTier(
    val itemType: CustomItemType,
    val healthPerConsumption: Int,
    val origin: String,
    val originColor: TextColor
) {
    VITALITY(CustomItemType.VITALITY_HEART, 10, "Overworld", NamedTextColor.GREEN),
    EMBER(CustomItemType.EMBER_HEART, 20, "Nether", NamedTextColor.RED),
    CLOUD(CustomItemType.CLOUDHEART, 30, "Aether", NamedTextColor.AQUA),
    VOID(CustomItemType.VOIDHEART, 40, "End", NamedTextColor.DARK_PURPLE),
    ETERNAL(CustomItemType.ETERNAL_HEART, 50, "?????", NamedTextColor.GOLD);

    companion object {
        /**
         * Resolves the tier associated with a given item type.
         * @throws IllegalArgumentException if the item type is not a registered heart.
         */
        fun fromItemType(itemType: CustomItemType): HeartTier =
            entries.firstOrNull { it.itemType == itemType }
                ?: throw IllegalArgumentException("No heart tier is defined for item type $itemType")
    }
}
